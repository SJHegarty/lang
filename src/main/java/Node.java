import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.IntStream;

interface Node{

	record Identifier(String name, boolean enabled) implements Comparable<Identifier>{
		Identifier negate(){
			return new Identifier(name, !enabled);
		}

		@Override
		public int compareTo(Identifier o) {
			return name.compareTo(o.name);
		}
	}

	SortedSet<Identifier> identifiers();

	Set<? extends Node> transitions(char c);

	default Set<? extends Node> next(){
		return next(c -> true);
	}

	default Set<? extends Node> next(char c){
		return next(ch -> ch == c);
	}

	default Set<? extends Node> next(CharPredicate filter) {
		return IntStream.range(0, FSA.TABLE_SIZE)
			.filter(i -> filter.test((char)i))
			.mapToObj(i -> transitions((char)i))
			.reduce(
				(s0, s1) -> {
					var rv = new HashSet<Node>();
					rv.addAll(s0);
					rv.addAll(s1);
					return (Set)rv;
				}
			)
			.orElseGet(Set::of);
	}

	default boolean terminating(){
		return identifiers().stream().anyMatch(i -> i.enabled);
	}

}