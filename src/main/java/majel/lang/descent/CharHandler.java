package majel.lang.descent;

import majel.lang.util.TokenStream;

public interface CharHandler<T> extends Handler<T>{

	@Override
	default boolean supportsHead(TokenStream tokens){
		return tokens.peek() == headToken();
	}

	char headToken();
}
