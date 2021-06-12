package majel.lang.automata.fsa;

import majel.util.functional.CharPredicate;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.IntStream;

public interface Node{

	Set<Node> transitions(char c);

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

	default boolean terminable(){
		var explored = new HashSet<Node>();
		var queue = new ArrayDeque<Node>();
		queue.add(this);
		while(!queue.isEmpty()){
			var node = queue.poll();
			if(explored.add(node)){
				if(node.terminating()){
					return true;
				}
				queue.addAll(node.next());
			}
		}
		return false;
	}

	CharPredicate alphabet();
}