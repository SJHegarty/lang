import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Foo {
	public static void main(String... args) {

	}

	@FunctionalInterface
	interface CharPredicate {
		boolean test(char c);
	}

	static class FiniteStateAutomaton {

		static class Node {

			record Identifier(String name, boolean enabled) implements Comparable<Identifier>{
				Identifier negate(){
					return new Identifier(name, !enabled);
				}

				@Override
				public int compareTo(Identifier o) {
					return name.compareTo(o.name);
				}
			}

			final Set<Node>[] next;
			final Set<Identifier> identifiers;

			Node() {
				this.next = new Set[256];
				this.identifiers = new TreeSet<>();
			}

			Node(String id){
				this();
				addIdentifier(id);
			}

			Node(Set<Identifier> identifiers){
				this();
				this.identifiers.addAll(identifiers);
			}

			void addIdentifier(String id){
				identifiers.add(new Identifier(id, true));
			}

			public boolean terminating(){
				return identifiers.stream().anyMatch(i -> i.enabled);
			}

			public Set<Node> next() {
				return Stream.of(next)
					.reduce(
						(s0, s1) -> {
							var rv = new HashSet<Node>();
							rv.addAll(s0);
							rv.addAll(s1);
							return rv;
						}
					)
					.orElseGet(Set::of);
			}
		}

		final Node entryPoint;

		private FiniteStateAutomaton(){
			this(new Node());
		}
		private FiniteStateAutomaton(Node entryPoint){
			this.entryPoint = entryPoint;
		}

		private FiniteStateAutomaton(CharPredicate predicate, String id) {
			this();

			final var next = new HashSet<>(Set.of(new Node(id)));

			for (char c = 0; c < entryPoint.next.length; c++) {
				if(predicate.test(c)){
					entryPoint.next[c] = next;
				}
			}
		}


		public static final char LAMBDA = '^';

		private static FiniteStateAutomaton concatenate(FiniteStateAutomaton...elements){

			var rv = new FiniteStateAutomaton(elements[0].entryPoint);

			for(int i = 1; i < elements.length; i++){
				var node = elements[i - 1];
				var next = elements[i];

				Set<Node> terminating = node.nodes().stream()
					.filter(Node::terminating)
					.collect(Collectors.toSet());

				for(Node t: terminating){
					if(t.next[LAMBDA] == null){
						t.next[LAMBDA] = new HashSet<>();
					}
					t.next[LAMBDA].add(next.entryPoint);
				}
			}
			return rv;
		}

		private static FiniteStateAutomaton or(FiniteStateAutomaton...elements){
			var rv = new FiniteStateAutomaton();
			var l = rv.entryPoint.next[LAMBDA] = new HashSet<>();

			for(var e: elements){
				l.add(e.entryPoint);
			}

			return rv;
		}

		private FiniteStateAutomaton negate(String id){
			complete();
			final Map<Node, Node> map = nodes().stream()
				.collect(
					Collectors.toMap(
						n -> n,
						n -> n.identifiers.isEmpty() ? new Node(id) : new Node()
					)
				);

			for(var src: nodes()){
				var dst = map.get(src);
				for(char c = 0; c < 0x100; c++){
					for(var srcNext: src.next[c]){
						if(dst.next[c] == null){
							dst.next[c] = new HashSet<>();
						}
						dst.next[c].add(map.get(srcNext));
					}
				}
			}

			return new FiniteStateAutomaton(map.get(entryPoint));
		}

		private FiniteStateAutomaton and(FiniteStateAutomaton...elements){
			for(var e: elements){
				e.complete();
			}
			return or().negate()
		}
		private void complete(){
			var sink = new Node();
			for(var node: nodes()){
				for(char c = 0; c < 0x100; c++){
					if(c != LAMBDA && node.next[c] == null){
						node.next[c] = new HashSet<>(Set.of(sink));
					}
				}
			}
		}

		private FiniteStateAutomaton dfa(){
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
}
