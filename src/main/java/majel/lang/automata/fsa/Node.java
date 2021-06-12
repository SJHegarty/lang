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
		final Set<Node> rv = new HashSet<>();
		filter.toStream()
			.mapToObj(this::transitions)
			.forEach(rv::addAll);

		return rv;
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