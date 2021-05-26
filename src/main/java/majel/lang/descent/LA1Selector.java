package majel.lang.descent;

import majel.lang.util.TokenStream;

import java.util.function.Supplier;

import static majel.lang.automata.fsa.FSA.TABLE_SIZE;

public class LA1Selector<T> implements HandlerSelector<T>{
	private final Handler<T>[] handlers;

	public LA1Selector(){
		this.handlers = new Handler[256];
	}

	@Override
	public Handler<T> handlerFor(TokenStream tokens){
		return handlers[tokens.poll()];
	}

	public void registerHandler(Supplier<Handler<T>> builder){
		Handler<T> h = builder.get();
		for(int i = 0; i < TABLE_SIZE; i++){
			char headToken = (char)i;
			if(h.supportsHead(TokenStream.of(headToken))){
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
