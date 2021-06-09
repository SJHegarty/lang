package majel.lang.descent;

import majel.lang.descent.context.NullContext;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;
import majel.stream.Token;
import majel.util.functional.CharPredicate;

public interface PredicateHandler<T extends Token> extends Handler<NullContext, Token$Char, T>{

	@Override
	default boolean supportsHead(TokenStream_Obj<Token$Char> tokens){
		return headPredicate().test(tokens.peek().value());
	}

	CharPredicate headPredicate();
}
