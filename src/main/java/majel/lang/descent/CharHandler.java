package majel.lang.descent;

import majel.lang.descent.context.NullContext;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;
import majel.stream.Token;

public interface CharHandler<T extends Token> extends Handler<NullContext, Token$Char, T>{

	@Override
	default boolean supportsHead(TokenStream_Obj<Token$Char> tokens){
		return tokens.peek().value() == headToken();
	}

	char headToken();
}
