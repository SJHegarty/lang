package majel.lang.descent;

import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;
import majel.stream.Token;

public interface CharHandler<T extends Token> extends Handler<SimpleToken, T>{

	@Override
	default boolean supportsHead(TokenStream<SimpleToken> tokens){
		return tokens.peek().character() == headToken();
	}

	char headToken();
}
