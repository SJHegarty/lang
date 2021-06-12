package majel.lang.automata.fsa;

import majel.util.functional.CharPredicate;

import java.util.*;
import java.util.stream.Collectors;

import static majel.lang.automata.fsa.FSA.LAMBDA;
import static majel.lang.automata.fsa.FSA.NOT_LAMBDA;

public class MetaNode implements Node{
	private final Set<Node> nodes = new HashSet<>();

	MetaNode(Node src){
		this(new HashSet<>(Set.of(src)));
	}

	MetaNode(Set<Node> nodes){
		if(nodes.isEmpty()){
			throw new IllegalArgumentException();
		}
		var queue = new ArrayDeque<>(nodes);
		while(!queue.isEmpty()){
			var node = queue.poll();
			if(this.nodes.add(node)){
				queue.addAll(node.transitions(LAMBDA));
			}
		}
	}

	Set<Node> nodes(){
		return nodes;
	}

	@Override
	public boolean terminating() {
		return nodes().stream().anyMatch(Node::terminating);
	}

	@Override
	public SortedSet<String> labels() {
		return nodes.stream()
			.flatMap(n -> n.labels().stream())
			.collect(Collectors.toCollection(TreeSet::new));
	}

	@Override
	public CharPredicate alphabet(){
		return NOT_LAMBDA;
	}

	public MetaNode transition(char c) {
		if (c == LAMBDA) {
			throw new UnsupportedOperationException();
		}
		return new MetaNode(
			nodes.stream()
				.flatMap(n -> n.transitions(c).stream())
				.collect(Collectors.toSet())
		);
	}

	@Override
	public Set<? extends Node> next(CharPredicate filter) {
		return Node.super.next(NOT_LAMBDA.and(filter));
	}

	@Override
	public Set<Node> transitions(char c){
		return Set.of(transition(c));
	}

	public boolean equals(Object o){
		return o instanceof MetaNode p && nodes.equals(p.nodes);
	}

	public int hashCode(){
		return getClass().hashCode() ^ nodes.hashCode();
	}
}
