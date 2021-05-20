package majel.lang.automata.fsa;

import majel.lang.descent.lithp.TokenStream;

public class StringProcessor{
	private final FSA automaton;

	public StringProcessor(FSA automaton){
		this.automaton = automaton.dfa();
	}

	public Blah process(TokenStream tokens){
		Node terminating = null;
		var node = automaton.entryPoint;
		var builder = new StringBuilder();
		var buffer = new StringBuilder();
		while(!tokens.empty() && node.terminable()){
			char c = tokens.poll();
			buffer.append(c);
			var next = node.next(c);
			if(next.size() != 1){
				throw new IllegalStateException("Unreachable? The automaton is deterministic and complete.");
			}
			for(var n: next){
				node = n;
			}
			if(node.terminating()){
				builder.append(buffer);
				buffer.delete(0, buffer.length());
				tokens.mark();
				terminating = node;
			}
		}
		tokens.reset();
		return new Blah(builder.toString(), terminating);

	}

	public Blah process(String value){
		return process(new TokenStream(value));
	}

	public record Blah(String value, Node node){

	}
}
