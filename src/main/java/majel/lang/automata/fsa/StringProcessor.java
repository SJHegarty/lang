package majel.lang.automata.fsa;

public class StringProcessor{
	private final FSA automaton;

	StringProcessor(FSA automaton){
		this.automaton = automaton.dfa();
	}

	public Node process(String value){
		var node = automaton.entryPoint;
		for(char c: value.toCharArray()){
			var next = node.next(c);
			if(next.size() != 1){
				throw new IllegalStateException("Unreachable? The automaton is deterministic and complete.");
			}
			for(var n: next){
				node = n;
			}
		}
		return node;
	}
}
