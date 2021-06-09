package majel.lang.descent;

import majel.lang.util.TokenStream;
import majel.stream.Token;

public interface HandlerSelector<Context, S extends Token, T extends Token>{

	Handler<Context, S, T> handlerFor(TokenStream<S> tokens);

	default Handler<Context, S, T> markedHandlerFor(TokenStream<S> tokens){
		var mark = tokens.mark();
		var rv = handlerFor(tokens);
		mark.reset();
		return rv;
	}
}
