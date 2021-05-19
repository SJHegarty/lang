package majel.lang.automata.fsa;

import majel.util.functional.CharPredicate;

import java.util.*;

class SimpleNode implements Node{
	/*
	TODO:
		Don't just return the ProxySet, allow a SetBuilder to be passed into the constructor
		This will allow for a clean construction mechanism for immutable views.

	TODO:
		Language Feature:
			![
				@target-version :~ 0.5;
			]
			Autotypes: ProxySet<Node> and Set<Node>Builder are to be generated using Autotypes.
				This means for example, that Set<Node>Builder is a builder that constructs a set of Nodes.
				(I haven't wrapped my head around how to handle proxies yet.)
	 */
	private final Set<SimpleNode>[] transitions;
	private final SortedSet<String> labels;
	private final boolean terminating;

	public SimpleNode(boolean terminating) {
		this.transitions = new Set[256];
		this.terminating = terminating;
		this.labels = new TreeSet<>();
	}

	public SimpleNode(String label, boolean terminating){
		this(terminating);
		if(label != null){
			labels.add(label);
		}
	}

	public SimpleNode(SortedSet<String> labels, boolean terminating){
		this(terminating);
		this.labels.addAll(labels);
	}

	@Override
	public boolean terminating(){
		return terminating;
	}

	@Override
	public Set<SimpleNode> transitions(char c){
		if(transitions[c] == null){
			return new MaskingSet<>(
				() -> {
					var rv = new HashSet<SimpleNode>();
					transitions[c] = rv;
					return rv;
				}
			);
		};
		return transitions[c];
	}

	@Override
	public Set<SimpleNode> next(){
		return (Set<SimpleNode>)Node.super.next();
	}

	@Override
	public Set<SimpleNode> next(char c){
		return (Set<SimpleNode>)Node.super.next(c);
	}

	@Override
	public Set<SimpleNode> next(CharPredicate filter){
		return (Set<SimpleNode>)Node.super.next(filter);
	}

	@Override
	public SortedSet<String> labels(){
		return Collections.unmodifiableSortedSet(labels);
	}

}