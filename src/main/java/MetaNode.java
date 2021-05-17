import java.util.*;
import java.util.stream.Collectors;

public class MetaNode implements Node{
	private final Set<SimpleNode> nodes = new HashSet<>();

	MetaNode(SimpleNode src){
		this(Set.of(src));
	}

	MetaNode(Set<SimpleNode> nodes){
		var queue = new ArrayDeque<>(nodes);
		while(!queue.isEmpty()){
			var node = queue.poll();
			if(nodes.add(node)){
				queue.addAll(node.transitions(FSA.LAMBDA));
			}
		}
	}

	@Override
	public SortedSet<Identifier> identifiers() {
		return nodes.stream()
			.flatMap(n -> n.identifiers().stream())
			.collect(Collectors.toCollection(TreeSet::new));
	}

	public MetaNode transition(char c) {
		if (c == FSA.LAMBDA) {
			throw new UnsupportedOperationException();
		}
		return new MetaNode(
			nodes.stream()
				.flatMap(n -> n.transitions(c).stream())
				.collect(Collectors.toSet())
		);
	}

	@Override
	public Set<MetaNode> transitions(char c){
		return Set.of(transition(c));
	}

	public boolean equals(Object o){
		return o instanceof MetaNode p && nodes.equals(p.nodes);
	}

	public int hashCode(){
		return getClass().hashCode() ^ nodes.hashCode();
	}
}
