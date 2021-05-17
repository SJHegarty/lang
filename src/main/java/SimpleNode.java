import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

class SimpleNode implements Node{

	final Set<Node>[] transitions;
	final SortedSet<Identifier> identifiers;

	SimpleNode() {
		this.transitions = new Set[256];
		this.identifiers = new TreeSet<>();
	}

	SimpleNode(String id){
		this();
		addIdentifier(id);
	}

	SimpleNode(Set<Identifier> identifiers){
		this();
		this.identifiers.addAll(identifiers);
	}

	@Override
	public SortedSet<Identifier> identifiers(){
		return identifiers;
	}

	@Override
	public Set<Node> transitions(char c){
		if(transitions[c] == null){
			return new MaskingSet<>(
				() -> {
					var rv = new HashSet<Node>();
					transitions[c] = rv;
					return rv;
				}
			);
		};
		return transitions[c];
	}

	void addIdentifier(String id){
		identifiers.add(new Identifier(id, true));
	}
}