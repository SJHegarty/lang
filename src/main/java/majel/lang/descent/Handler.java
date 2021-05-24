package majel.lang.descent;

import majel.lang.util.TokenStream;

public interface Handler<T>{

	default void checkHead(TokenStream tokens){
		tokens.read(headToken());
	}

	char headToken();

	Expression<T> parse(RecursiveDescentParser<T> parser, TokenStream tokens);


}
