package majel.lang.automata.fsa;

import majel.stream.Token;
import majel.util.ObjectUtils;
import majel.util.functional.CharPredicate;
import majel.util.functional.ObjectIntFunction;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FSA implements Token{

	public static final char LAMBDA = '^';
	public static final int TABLE_SIZE = 0x100;

	final SimpleNode entryPoint;

	private FSA(){
		this(new SimpleNode(false));
	}

	private FSA(SimpleNode entryPoint){
		this.entryPoint = entryPoint;
	}

	public FSA(char c){
		this();
		entryPoint.transitions(c).add(new SimpleNode(true));
	}

	public FSA(CharPredicate predicate) {
		this();
		final var node = new SimpleNode(true);
		for (char c = 0; c < TABLE_SIZE; c++) {
			if(predicate.test(c)){
				entryPoint.transitions(c).add(node);;
			}
		}
	}

	public static FSA literal(String value){
		var rv = new FSA();
		var node = rv.entryPoint;
		final int limit = value.length() - 1;
		for(int i = 0; i < limit; i++){
			var next = new SimpleNode(false);
			node.transitions(value.charAt(i)).add(next);
			node = next;
		}
		node.transitions(value.charAt(limit))
			.add(new SimpleNode(true));

		return rv;
	}

	public static FSA concatenate(List<FSA> elements){
		return concatenate(elements.toArray(FSA[]::new));
	}
	public static FSA concatenate(FSA...elements){
		return switch(elements.length){
			case 0 -> new FSA();
			case 1 -> elements[0];
			default -> {
				int limit = elements.length - 1;
				yield concatenate(
					i -> i == limit,
					elements
				);
			}
		};
	}

	public FSA repeating(int lowerBound){
		return repeating(lowerBound, Integer.MAX_VALUE);
	}

	public FSA repeating(int lowerBound, int upperBound){
		if((lowerBound|upperBound) < 0 || upperBound < lowerBound){
			throw new IllegalArgumentException(
				String.format("[%s, %s]", lowerBound, upperBound)
			);
		}
		if(upperBound == Integer.MAX_VALUE){
			var machines = ObjectUtils.repeating(this, lowerBound);
			final int limit = lowerBound - 1;
			machines[limit] = machines[limit].kleene();
			return concatenate(
				layer -> layer == limit,
				machines
			);
		}
		else{
			final FSA[] machines = ObjectUtils.repeating(this, upperBound);
			int limit = lowerBound - 1;
			return concatenate(
				layer -> layer >= limit,
				machines
			);
		}
	}

	public static FSA concatenate(IntPredicate elementTerminates, FSA...elements){
		elements = elements.clone();

		final int limit = elements.length - 1;

		//(Node src, int layer) -> (Node generated) nodeBuilder
		ObjectIntFunction<SimpleNode, SimpleNode> nodeBuilder = (src, layer) -> {
			boolean terminating = src.terminating() && elementTerminates.test(layer);
			return terminating ? new SimpleNode(true) : new SimpleNode(false);
		};

		elements[limit] = elements[limit].process(
			n -> nodeBuilder.apply(n, limit)
		);

		for(int i = limit - 1; i >= 0; i--){
			var next = elements[i + 1].entryPoint;
			int fi = i;
			elements[i] = elements[i].process(
				n -> {
					var rv = nodeBuilder.apply(n, fi);
					if(n.terminating()){
						rv.transitions(LAMBDA).add(next);
					}
					return rv;
				}
			);
		}
		return new FSA(elements[0].entryPoint);
	}

	private FSA process(UnaryOperator<SimpleNode> nodeProcessor){
		final var nodes = nodes();
		final Map<SimpleNode, SimpleNode> map = nodes.stream()
			.collect(
				Collectors.toMap(n -> n, nodeProcessor)
			);

		for(var src: nodes){
			var dst = map.get(src);

			for(char c = 0; c < 0x100; c++){
				var srcTransitions = src.transitions(c);
				var dstTransitions = dst.transitions(c);

				for(var srcNext: srcTransitions){
					dstTransitions.add(map.get(srcNext));
				}
			}
		}

		return new FSA(map.get(entryPoint));

	}
	private Set<SimpleNode> terminating() {
		return nodes().stream()
			.filter(Node::terminating)
			.collect(Collectors.toSet());
	}

	public static FSA or(FSA... elements){
		return or(Arrays.asList(elements));
	}

	public static FSA or(List<FSA> elements){
		var rv = new FSA();
		var l = rv.entryPoint.transitions(LAMBDA);

		for(var e: elements){
			l.add(e.entryPoint);
		}

		return rv;
	}

	public FSA optional(){
		var rv = clone();
		rv.entryPoint
			.transitions(LAMBDA)
			.add(new SimpleNode(true));

		return rv;
	}

	public FSA kleene(){
		var rv = clone();
		rv.terminating().forEach(
			n -> n.transitions(LAMBDA).add(rv.entryPoint)
		);

		return rv;
	}

	protected FSA clone(){
		return process(
			n -> new SimpleNode(n.labels(), n.terminating())
		);
	}

	public FSA negate(){
		return dfa().process(
			n -> new SimpleNode(n.labels(), !n.terminating())
		);
	}

	public static FSA and(FSA...elements){
		return and(Arrays.asList(elements));
	}

	public static FSA and(List<FSA> elements){
		for(var e: elements){
			e.complete();
		}
		var not = or(
			elements.stream()
				.map(FSA::negate)
				.toArray(FSA[]::new)
		);
		not.complete();
		return not.negate();
	}

	private void complete(){
		var sink = new SimpleNode(false);

		Consumer<SimpleNode> nodeOp = node -> {
			for(char c = 0; c < TABLE_SIZE; c++){
				if(c != LAMBDA){
					var transitions = node.transitions(c);
					if(transitions.isEmpty()){
						transitions.add(sink);
					}
				}
			}
		};

		nodes().forEach(nodeOp);
		nodeOp.accept(sink);
	}

	/*
	TODO:
		Rename clone() as copy(),
		Genericise FSA on <T extends Node>
		Add a method .deterministic() returning an FSA<MetaNode>
		Make copy() always return a FSA<SimpleNode>
		this method should be implementable as:
			return new FSA<>(new MetaNode(entryPoint))
				.deterministic()
				.copy()
	 */
	public FSA dfa(){
		complete();
		record Lookup(MetaNode meta, SimpleNode built){}
		var lookup = new HashMap<MetaNode, Lookup>();

		Function<MetaNode, Lookup> deAliaser = meta -> {
			var rv = lookup.get(meta);
			if(rv == null){
				rv = new Lookup(meta, new SimpleNode(meta.labels(), meta.terminating()));
				lookup.put(meta, rv);
			}
			return rv;
		};
		var root = deAliaser.apply(new MetaNode(entryPoint));
		var rv = new FSA(root.built);
		var queue = new ArrayDeque<MetaNode>();
		var explored = new HashSet<MetaNode>();
		queue.add(root.meta);

		while(!queue.isEmpty()){
			var meta = queue.poll();
			if(explored.add(meta)) {
				var simple = deAliaser.apply(meta).built;
				for (char c = 0; c < TABLE_SIZE; c++) {
					if (c != LAMBDA) {
						var next = meta.transition(c);
						queue.add(next);
						simple.next(c).add(
							deAliaser.apply(next).built
						);
					}
				}
			}
		}

		return rv;
	}

	public Set<SimpleNode> nodes(){
		final var rv = new HashSet<SimpleNode>();
		final var queue = new ArrayDeque<SimpleNode>();

		rv.add(entryPoint);
		queue.add(entryPoint);

		while(!queue.isEmpty()){
			var node = queue.poll();

			node.next().stream()
				.filter(rv::add)
				.forEach(queue::add);
		}

		return Collections.unmodifiableSet(rv);
	}

	public FSA named(String name){
		var rv = process(
			n -> {
				var labels = new TreeSet<String>();
				labels.addAll(n.labels());
				if(n.terminating()){
					labels.add(name);
				}
				return new SimpleNode(
					labels,
					n.terminating()
				);
			}
		);

		return rv;
	}
}