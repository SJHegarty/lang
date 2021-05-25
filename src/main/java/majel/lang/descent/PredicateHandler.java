package majel.lang.descent;

import majel.lang.util.TokenStream;
import majel.util.functional.CharPredicate;

public interface PredicateHandler<T> extends Handler<T>{

	@Override
	default boolean supportsHead(TokenStream tokens){
		return headPredicate().test(tokens.peek());
	}

	CharPredicate headPredicate();
}
