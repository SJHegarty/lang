package lang;

import java.util.*;

class SimpleNode implements Node{

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