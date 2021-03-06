package majel.lang.automata.fsa;

import majel.lang.descent.context.NullContext;
import majel.lang.err.IllegalToken;
import majel.lang.util.Mark;
import majel.lang.util.Pipe;
import majel.lang.util.TokenStream_Char;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;
import majel.stream.StringToken;

public class StringProcessor implements Pipe<NullContext, Token$Char, StringToken>{
	private final FSA automaton;

	public StringProcessor(FSA automaton){
		this.automaton = automaton.dfa();
		if(this.automaton.entryPoint.terminating()){
			throw new IllegalStateException();
		}
	}

	public Result process(TokenStream_Char tokens){
		var last = automaton.entryPoint;
		var node = automaton.entryPoint;
		var builder = new StringBuilder();
		var buffer = new StringBuilder();
		var mark = tokens.mark();
		loop: while(!tokens.empty() && node.terminable()){
			char c = tokens.poll();
			buffer.append(c);
			var next = node.next(c);

			switch(next.size()){
				case 0:{
					//break loop;
					throw new IllegalStateException("Unreachable? The automaton is deterministic and complete.");
				}
				case 1:{
					for(var n: next){
						node = n;
						break;
					}
				}
				case 2:{
					/*
						Implement code for non-deterministic execution,
						despite the fact it never actually happens.

						I'm pretty sure that this "should" work, unless something is using the identity of the Node somehow
						I was tired and stupid when I wrote some of that code, and I don't have and IDE to properly navigate this shit
					*/
					node = new MetaNode(next);//Wrong. Probably misses the Lambda transitions from the source.
					throw new IllegalStateException("Unreachable? The automaton is deterministic and complete.");
				}
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
		return process(TokenStream_Char.from(value));
	}

	@Override
	public TokenStream_Obj<StringToken> parse(NullContext ignored, TokenStream_Obj<Token$Char> tokens){
		var simple = TokenStream_Char.of(tokens);
		return new TokenStream_Obj<>(){
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
