package majel.lang.descent;

import majel.lang.util.TokenStream;
import majel.stream.Token$Char;
import majel.stream.Token;

public interface CharHandler<T extends Token> extends Handler<Token$Char, T>{

	@Override
	default boolean supportsHead(TokenStream<Token$Char> tokens){
		return tokens.peek().value() == headToken();
	}

	char headToken();
}
