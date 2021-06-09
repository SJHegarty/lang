package majel.lang.automata.fsa;

import majel.lang.descent.context.NullContext;
import majel.lang.err.IllegalToken;
import majel.lang.util.Mark;
import majel.lang.util.Pipe;
import majel.lang.util.TokenStream$Char;
import majel.lang.util.TokenStream$Obj;
import majel.stream.Token$Char;
import majel.stream.StringToken;

public class StringProcessor implements Pipe<NullContext, Token$Char, StringToken>{
	private final FSA automaton;

	public StringProcessor(FSA automaton){
		this.automaton = automaton.dfa();
	}

	public Result process(TokenStream$Char tokens){
		var last = automaton.entryPoint;
		var node = automaton.entryPoint;
		var builder = new StringBuilder();
		var buffer = new StringBuilder();
		var mark = tokens.mark();
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
				mark = tokens.mark();
				last = node;
			}
		}
		mark.reset();
		return new Result(builder.toString(), last);
	}


	public record Result(String value, Node node){

	}

	public Result process(String value){
		return process(TokenStream$Char.from(value));
	}

	@Override
	public TokenStream$Obj<StringToken> parse(NullContext ignored, TokenStream$Obj<Token$Char> tokens){
		var simple = TokenStream$Char.of(tokens);
		return new TokenStream$Obj<>(){
			@Override
			public StringToken poll(){
				var result = process(simple);
				if(!result.node.terminating()){
					throw new IllegalToken(tokens);
				}
				return new StringToken(
					result.value,
					result.node.labels()
				);
			}

			boolean touched;
			@Override
			public boolean touched(){
				return touched;
			}

			@Override
			public boolean empty(){
				return tokens.empty();
			}

			@Override
			public Mark mark(){
				var m0 = touched;
				var m1 = tokens.mark();
				return () -> {
					touched = m0;
					m1.reset();
				};
			}
		};
	}


	public FSA automaton(){
		return automaton;
	}
}
