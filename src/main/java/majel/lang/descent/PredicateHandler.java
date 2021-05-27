package majel.lang.descent;

import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;
import majel.stream.Token;
import majel.util.functional.CharPredicate;

public interface PredicateHandler<T extends Token> extends Handler<SimpleToken, T>{

	@Override
	default boolean supportsHead(TokenStream<SimpleToken> tokens){
		return headPredicate().test(tokens.peek().character());
	}

	CharPredicate headPredicate();
}
