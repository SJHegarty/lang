package majel.lang.descent;

import majel.lang.util.TokenStream;

public interface Handler<T>{

	default void checkHead(TokenStream tokens){
		tokens.read(headToken());
	}

	char headToken();

	T parse(RecursiveDescentTokenStream<T> context);
}
