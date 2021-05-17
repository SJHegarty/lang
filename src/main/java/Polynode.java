import java.util.*;
import java.util.stream.Collectors;

public class Polynode implements Node{
	private final Set<Node> nodes = new HashSet<>();

	Polynode(Node src){
		this(Set.of(src));
	}

	Polynode(Set<Node> nodes){
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

	@Override
	public Set<Node> transitions(char c){
		return nodes.stream()
			.flatMap(n -> n.transitions(c).stream())
			.collect(Collectors.toSet());
	}

	public boolean equals(Object o){
		return o instanceof Polynode p && nodes.equals(p.nodes);
	}

}
