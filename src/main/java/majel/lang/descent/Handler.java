package majel.lang.descent;

import majel.lang.err.IllegalToken;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token;

public interface Handler<Context, S extends Token, T extends Token>{

	default void checkHead(TokenStream_Obj<S> tokens){
		if(!supportsHead(tokens)){
			throw new IllegalToken(tokens);
		}
		tokens.poll();
	}

	boolean supportsHead(TokenStream_Obj<S> tokens);

	T parse(Context c, TokenStream_Obj<S> tokens, TokenStream_Obj<T> parsed);

}
