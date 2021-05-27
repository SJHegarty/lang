package majel.lang.descent;

import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;
import majel.stream.Token;

import java.util.function.Supplier;

import static majel.lang.automata.fsa.FSA.TABLE_SIZE;

public class LA1Selector<T extends Token> implements HandlerSelector<SimpleToken, T>{
	private final Handler<SimpleToken, T>[] handlers;

	public LA1Selector(){
		this.handlers = new Handler[256];
	}

	@Override
	public Handler<SimpleToken, T> handlerFor(TokenStream<SimpleToken> tokens){
		return handlers[tokens.poll().character()];
	}

	public void registerHandler(Supplier<Handler<SimpleToken, T>> builder){
		Handler<SimpleToken, T> h = builder.get();
		for(int i = 0; i < TABLE_SIZE; i++){
			char headToken = (char)i;
			if(h.supportsHead(SimpleTokenStream.of(headToken).wrap())){
				if(handlers[headToken] != null){
					throw new UnsupportedOperationException(
						String.format(
							"%s already defined for head-token '%s'",
							Handler.class.getSimpleName(),
							headToken
						)
					);
				}
				handlers[headToken] = h;
			}
		}
	}
}
