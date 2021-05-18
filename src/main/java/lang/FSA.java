package lang;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static lang.CharPredicate.*;

class FSA {

	public static void main(String...args){
	//	testOr();
		System.err.println();
		testAnd();
	}

	public static void testOr(){
		var lower = new FSA(CharPredicate.inclusiveRange('a', 'z'), "lower");
		var upper = new FSA(c -> c >= 'A' && c <= 'Z', "upper");
		var alpha = FSA.or(lower, upper);

		var examples = new String[]{
			"a",
			"aa",
			"1",
			"b",
			"Z",
			"Za"
		};

		var machines = new FSA[]{
			lower,
			lower.negate(),
			upper,
			upper.negate(),
			alpha
		};

		for(var m: machines) {
			System.err.println(m);
			var processor = new StringProcessor(m);
			for (String s : examples) {
				System.err.println(s + " " + processor.process(s));
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

	private FSA(CharPredicate predicate, String label) {
		this();
		final var node = new SimpleNode(label, true);
		for (char c = 0; c < TABLE_SIZE; c++) {
			if(predicate.test(c)){
				entryPoint.transitions(c).add(node);;
			}
		}
	}

	private static FSA concatenate(FSA...elements){

		var rv = new FSA(elements[0].entryPoint);

		for(int i = 1; i < elements.length; i++){
			var node = elements[i - 1];
			var next = elements[i];

			Set<SimpleNode> terminating = node.nodes().stream()
				.filter(Node::terminating)
				.collect(Collectors.toSet());

			for(var t: terminating){
				t.transitions(LAMBDA).add(next.entryPoint);
			}
		}
		return rv;
	}

	private static FSA or(FSA...elements){
		var rv = new FSA();
		var l = rv.entryPoint.transitions(LAMBDA);

		for(var e: elements){
			l.add(e.entryPoint);
		}

		return rv;
	}

	private FSA negate(){
		complete();
		final var deterministic = dfa();
		final var nodes = deterministic.nodes();
		final Map<SimpleNode, SimpleNode> map = nodes.stream()
			.collect(
				Collectors.toMap(
					n -> n,
					n -> new SimpleNode(n.labels(), !n.terminating())
				)
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

		return new FSA(map.get(deterministic.entryPoint));
	}

	private static FSA and(FSA...elements){
		for(var e: elements){
			e.complete();
		}
		var not = or(
			Stream.of(elements)
				.map(lang.FSA::negate)
				.toArray(lang.FSA[]::new)
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

	FSA dfa(){
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