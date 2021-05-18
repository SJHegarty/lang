package majel.lang.automata.fsa;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.IntStream;

interface Node{

	Set<? extends Node> transitions(char c);

	default Set<? extends Node> next(){
		return next(c -> true);
	}

	default Set<? extends Node> next(char c){
		return next(ch -> ch == c);
	}

	default Set<? extends Node> next(CharPredicate filter) {
		Set<Node> result = IntStream.range(0, FSA.TABLE_SIZE)
			.filter(i -> filter.test((char)i))
			.mapToObj(i -> (Set<Node>)transitions((char)i))
			.reduce(
				(s0, s1) -> {
					var rv = new HashSet<Node>();
					rv.addAll(s0);
					rv.addAll(s1);
					return rv;
				}
			)
			.orElseGet(HashSet::new);

		return result;
	}

	boolean terminating();
	SortedSet<String> labels();
}