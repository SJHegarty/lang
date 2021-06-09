package majel.lang.descent;

import majel.lang.util.TokenStream;
import majel.stream.Token$Char;
import majel.stream.Token;
import majel.util.functional.CharPredicate;

public interface PredicateHandler<T extends Token> extends Handler<Token$Char, T>{

	@Override
	default boolean supportsHead(TokenStream<Token$Char> tokens){
		return headPredicate().test(tokens.peek().value());
	}

	CharPredicate headPredicate();
}
