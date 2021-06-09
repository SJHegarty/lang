package majel.lang.descent;

import majel.lang.descent.context.NullContext;
import majel.lang.util.TokenStream_Char;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;
import majel.stream.Token;

import java.util.function.Supplier;

import static majel.lang.automata.fsa.FSA.TABLE_SIZE;

public class LA1Selector<T extends Token> implements HandlerSelector<NullContext, Token$Char, T>{
	private final Handler<NullContext, Token$Char, T>[] handlers;

	public LA1Selector(){
		this.handlers = new Handler[256];
	}

	@Override
	public Handler<NullContext, Token$Char, T> handlerFor(TokenStream_Obj<Token$Char> tokens){
		return handlers[tokens.poll().value()];
	}

	public void registerHandler(Supplier<Handler<NullContext, Token$Char, T>> builder){
		Handler<NullContext, Token$Char, T> h = builder.get();
		for(int i = 0; i < TABLE_SIZE; i++){
			char headToken = (char)i;
			if(h.supportsHead(TokenStream_Char.of(headToken).wrap())){
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
