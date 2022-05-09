package majel.lang.automata.fsa;

import majel.stream.Token;
import majel.util.ObjectUtils;
import majel.util.Opt;
import majel.util.functional.CharPredicate;
import majel.util.functional.ObjectIntFunction;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class FSA implements Token{

	public static final char LAMBDA = '^';
	public static final CharPredicate NOT_LAMBDA = c -> c != LAMBDA;
	final Node entryPoint;

	public FSA(){
		this(new SimpleNode(false));
	}

	private FSA(Node entryPoint){
		this.entryPoint = entryPoint;
	}

	public FSA(char c){
		this();
		entryPoint.transitions(c).add(new SimpleNode(true));
	}

	public FSA(CharPredicate predicate) {
		this();
		final var node = new SimpleNode(true);
		NOT_LAMBDA.and(predicate).forEach(
			c -> entryPoint.transitions(c).add(node)
		);
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

		ObjectIntFunction<Node, SimpleNode> nodeBuilder = (src, layer) -> {
			return new SimpleNode(src.terminating() && elementTerminates.test(layer));
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

	private FSA process(UnaryOperator<Node> nodeProcessor){
		final var nodes = nodes();
		final Map<Node, Node> map = nodes.stream()
			.collect(
				Collectors.toMap(n -> n, nodeProcessor)
			);

		for(var src: nodes){
			var dst = map.get(src);
			src.alphabet().forEach(
				c -> {
					var srcTransitions = src.transitions(c);
					var dstTransitions = dst.transitions(c);

					for(var srcNext: srcTransitions){
						dstTransitions.add(map.get(srcNext));
					}
				}
			);
		}

		return new FSA(map.get(entryPoint));

	}
	private Set<Node> terminating() {
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
		if(new MetaNode(entryPoint).terminating()){
			return this;
		}
		var rv = copy();
		rv.entryPoint
			.transitions(LAMBDA)
			.add(new SimpleNode(true));

		return rv;
	}

	public FSA kleene(){
		//TODO: if each of the terminating nodes has a lambda transition to the entry-point, return this
		var rv = copy();
		rv.terminating().forEach(
			n -> n.transitions(LAMBDA).add(rv.entryPoint)
		);

		return rv;
	}

	protected FSA copy(){
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
		//TODO: if every node can already reach a sink via lambda transition, return this
		var sink = new SimpleNode(false);

		Consumer<Node> nodeOp = node -> NOT_LAMBDA.forEach(
			c -> Opt.Gen.of(node.transitions(c))
				.retain(Set::isEmpty)
				.ifPresent(transitions -> transitions.add(sink))
		);
		nodes().forEach(nodeOp);
		nodeOp.accept(sink);
	}

	public FSA dfa(){
		this.complete();
		return new FSA(new MetaNode(this.entryPoint)).copy();
	}

	public Set<Node> nodes(){
		final var rv = new HashSet<Node>();
		final var queue = new ArrayDeque<Node>();

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

	/*
	public boolean equals(Object o){
		BinaryOperator<FSA> xor = (f0, f1) -> {
			BinaryOperator<FSA> orNot = (fi, fu) -> FSA.or(fi, fu.negate());
			return FSA.and(
				orNot.apply(f0, f1),
				orNot.apply(f1, f0)
			);
		}
		return o instanceof FSA f && !xor.apply(this, f).isTerminable();
	}
	*/
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
