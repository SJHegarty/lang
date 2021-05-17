package lang;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

class SimpleNode implements Node{

	final Set<SimpleNode>[] transitions;
	//final SortedSet<Identifier> identifiers;
	final boolean terminating;

	SimpleNode(boolean terminating) {
		this.transitions = new Set[256];
		this.terminating = terminating;
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

}