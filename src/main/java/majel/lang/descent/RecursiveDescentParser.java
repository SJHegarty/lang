package majel.lang.descent;

import majel.lang.util.Pipe;
import majel.lang.err.IllegalToken;
import majel.lang.util.Mark;
import majel.lang.util.TokenStream$Obj;
import majel.stream.Token;

/*
	TODO:
 		Split the context into ExpressionContext and ParseContext.
 		The context type is somewhat conflated
 			There're expression contexts, e.g.: <(some-name, *.) should parse to:
 				record NamedExpression(String name, Expression wrapped);
 			However the name here still has no meaning - it's premature to actually lookup the definition.
 			Likewise @some-name; should not lookup some-name until later in the process.
 			The context containing a Map<name:String, Expression> should not be accessible until semantic processing is occurring.
 */
public class RecursiveDescentParser<Context, S extends Token, T extends Token> implements Pipe<Context, S, T>{

	private final HandlerSelector<Context, S, T> selector;
	public RecursiveDescentParser(HandlerSelector<Context, S, T> selector){
		this.selector = selector;
	}

	@Override
	public TokenStream$Obj<T> parse(Context context, TokenStream$Obj<S> tokens){
		return new TokenStream$Obj<T>(){
			@Override
			public T peek(){
				var mark = tokens.mark();
				var rv = poll();
				mark.reset();
				return rv;
			}

			@Override
			public T poll(){
				var handler = selector.markedHandlerFor(tokens);
				if(handler == null){
					throw new IllegalToken(tokens);
				}
				return handler.parse(context, tokens, this);
			}

			@Override
			public boolean touched(){
				return false;
			}

			@Override
			public boolean empty(){
				return tokens.empty();
			}

			@Override
			public Mark mark(){
				return tokens.mark();
			}
		};
	}
}
