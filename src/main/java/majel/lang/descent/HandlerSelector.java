package majel.lang.descent;

import majel.lang.util.TokenStream;

public interface HandlerSelector<T>{

	Handler<T> handlerFor(TokenStream tokens);

	default Handler<T> markedHandlerFor(TokenStream tokens){
		var mark = tokens.mark();
		var rv = handlerFor(tokens);
		mark.reset();
		return rv;
	}
}
