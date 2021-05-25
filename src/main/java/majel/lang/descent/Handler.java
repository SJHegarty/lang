package majel.lang.descent;

import majel.lang.err.IllegalToken;
import majel.lang.util.TokenStream;

public interface Handler<T>{

	default void checkHead(TokenStream tokens){
		if(!supportsHead(tokens)){
			throw new IllegalToken(tokens);
		}
		tokens.poll();
	}

	boolean supportsHead(TokenStream tokens);

	Expression<T> parse(RecursiveDescentParser<T> parser, TokenStream tokens);


}
