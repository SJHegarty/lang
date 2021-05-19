package majel.lang.automata.fsa;

import majel.util.ObjectUtils;
import majel.util.functional.CharPredicate;
import majel.util.functional.ObjectIntFunction;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static majel.util.functional.CharPredicate.*;

public class FSA {

	public static void main(String...args){
	//	testOr();
		System.err.println();
		testOr();
	}

	public static void testOr(){
		var lower = new FSA(CharPredicate.inclusiveRange('a', 'z'), "lower");
		var upper = new FSA(c -> c >= 'A' && c <= 'Z', "upper");
		var alpha = FSA.concatenate("test", lower, upper);

		var examples = new String[]{
			"a",
			"aa",
			"1",
			"b",
			"Z",
			"Za",
			"aZ"
		};

		var machines = new FSA[]{
			lower,
			lower.negate(),
			upper,
			upper.negate(),
			alpha,
			FSA.literal("aZ", "yo")
		};

		for(var m: machines) {
			System.err.println(m);
			var processor = new StringProcessor(m);
			for (String s : examples) {
				var res = processor.process(s);
				System.err.println(s + " " + res.terminating() + " " + res.labels());
			}
			System.err.println();
		}
	}

	public static void testAnd(){
		var lower = new FSA(inclusiveRange('a', (char)('a' + 20)), "lower");
		var upper = new FSA(inclusiveRange((char)('z' - 20), 'z'), "upper");
		var neglw = lower.negate();
		var negup = upper.negate();
		var orneg = FSA.or(neglw, negup);


		var andlu = orneg.negate();

		var examples = IntStream.rangeClosed('a', 'z')
			.mapToObj(i -> "" + (char)i)
			.toArray(String[]::new);

		var machines = new FSA[]{
			lower,
			upper,
			neglw,
			negup,
			orneg,
			orneg.negate()
		};

		for(var m: machines) {
			System.err.println(m);
			var processor = new StringProcessor(m);
			for (String s : examples) {
				var res = processor.process(s);
				System.err.println(s + " " + res.terminating() + " " + res.labels());
			}
			System.err.println();
		}

	}
	public static final char LAMBDA = '^';
	public static final int TABLE_SIZE = 0x100;

	final SimpleNode entryPoint;

	private FSA(){
		this(new SimpleNode(false));
	}

	private FSA(SimpleNode entryPoint){
		this.entryPoint = entryPoint;
	}



	public FSA(CharPredicate predicate, String label) {
		this();
		final var node = new SimpleNode(label, true);
		for (char c = 0; c < TABLE_SIZE; c++) {
			if(predicate.test(c)){
				entryPoint.transitions(c).add(node);;
			}
		}
	}

	public static FSA literal(String label, String value){
		var rv = new FSA();
		var node = rv.entryPoint;
		final int limit = value.length() - 1;
		for(int i = 0; i < limit; i++){
			var next = new SimpleNode(false);
			node.transitions(value.charAt(i)).add(next);
			node = next;
		}
		node.transitions(value.charAt(limit))
			.add(new SimpleNode(label, true));

		return rv;
	}

	public static FSA concatenate(String label, FSA...elements){
		int limit = elements.length - 1;
		return concatenate(
			label,
			i -> i == limit,
			elements
		);
	}

	public FSA repeating(int lowerBound){
		var machines = ObjectUtils.repeating(this, lowerBound);
		final int limit = lowerBound - 1;
		machines[limit] = machines[limit].kleene();
		return concatenate(
			null,
			layer -> layer == limit,
			machines
		);
	}
	public FSA repeating(int lowerBound, int upperBound){
		/*
			lowerBound - 1 non-terminating copies
			1 + upperBound - lowerBound terminating copies

			the easiest way to implement this is by modifying the concatenate method to take an IntPredicate on the layer index.
			That predicate (given n) defines whether or not the nth element is terminating or not.
		 */
		if((lowerBound|upperBound) < 0 || upperBound < lowerBound){
			throw new IllegalArgumentException(
				String.format("[%s, %s]", lowerBound, upperBound)
			);
		}
		final FSA[] machines = ObjectUtils.repeating(this, upperBound);
		int limit = lowerBound - 1;
		return concatenate(
			null,
			layer -> layer >= limit,
			machines
		);
	}

	public static FSA concatenate(String label, IntPredicate elementTerminates, FSA...elements){
		elements = elements.clone();

		final int limit = elements.length - 1;

		//(Node src, int layer) -> (Node generated) nodeBuilder
		ObjectIntFunction<SimpleNode, SimpleNode> nodeBuilder = (src, layer) -> {
			boolean terminating = src.terminating() && elementTerminates.test(layer);
			return terminating ? new SimpleNode(label, true) : new SimpleNode(false);
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
		for(var e: elements){
			e.complete();
		}
		var not = or(
			Stream.of(elements)
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

	Set<SimpleNode> nodes(){
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

		return rv;
	}
}