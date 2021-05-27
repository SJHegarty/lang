package majel.lang.descent;

import majel.lang.util.TokenStream;
import majel.stream.Token;

public interface HandlerSelector<S extends Token, T extends Token>{

	Handler<S, T> handlerFor(TokenStream<S> tokens);

	default Handler<S, T> markedHandlerFor(TokenStream<S> tokens){
		var mark = tokens.mark();
		var rv = handlerFor(tokens);
		mark.reset();
		return rv;
	}
}
