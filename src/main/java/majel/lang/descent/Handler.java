package majel.lang.descent;

import majel.lang.err.IllegalToken;
import majel.lang.util.TokenStream$Obj;
import majel.stream.Token;

public interface Handler<Context, S extends Token, T extends Token>{

	default void checkHead(TokenStream$Obj<S> tokens){
		if(!supportsHead(tokens)){
			throw new IllegalToken(tokens);
		}
		tokens.poll();
	}

	boolean supportsHead(TokenStream$Obj<S> tokens);

	T parse(Context c, TokenStream$Obj<S> tokens, TokenStream$Obj<T> parsed);

}
