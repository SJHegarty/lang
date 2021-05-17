import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class FSA {

	public static final char LAMBDA = '^';
	public static final int TABLE_SIZE = 0x100;

	final Node entryPoint;

	private FSA(){
		this(new SimpleNode());
	}
	private FSA(Node entryPoint){
		this.entryPoint = entryPoint;
	}

	private FSA(CharPredicate predicate, String id) {
		this();

		final var node = new SimpleNode(id);
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

			Set<Node> terminating = node.nodes().stream()
				.filter(Node::terminating)
				.collect(Collectors.toSet());

			for(Node t: terminating){
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
		final Map<Node, Node> map = nodes.stream()
			.collect(
				Collectors.toMap(
					n -> n,
					n -> {
						var identifiers = n.identifiers().stream()
							.map(SimpleNode.Identifier::negate)
							.collect(
								Collectors.toCollection(TreeSet::new)
							);

						return new SimpleNode(identifiers);
					}
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

	private FSA and(FSA...elements){
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
		var sink = new SimpleNode();
		for(var node: nodes()){
			for(char c = 0; c < TABLE_SIZE; c++){
				if(c != LAMBDA){
					var transitions = node.transitions(c);
					if(transitions.isEmpty()){
						transitions.add(sink);
					}
				}
			}
		}
	}

	private FSA dfa(){
		class MetaNode{

		}
		throw new UnsupportedOperationException("NYI");
	}

	Set<Node> nodes(){
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

		return rv;
	}
}