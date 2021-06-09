package majel.lang.descent;

import majel.lang.err.IllegalToken;
import majel.lang.util.TokenStream;
import majel.stream.Token;

public interface Handler<Context, S extends Token, T extends Token>{

	default void checkHead(TokenStream<S> tokens){
		if(!supportsHead(tokens)){
			throw new IllegalToken(tokens);
		}
		tokens.poll();
	}

	boolean supportsHead(TokenStream<S> tokens);

	T parse(Context c, TokenStream<S> tokens, TokenStream<T> parsed);

}
