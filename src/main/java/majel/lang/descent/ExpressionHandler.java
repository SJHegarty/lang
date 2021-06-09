package majel.lang.descent;

import majel.lang.automata.fsa.StringProcessor;
import majel.lang.descent.context.NullContext;
import majel.lang.util.TokenStream_Char;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;
import majel.stream.Token;

public interface ExpressionHandler<T extends Token> extends Handler<NullContext, Token$Char, T>{
	@Override
	default boolean supportsHead(TokenStream_Obj<Token$Char> tokens){
		return headProcessor().process(TokenStream_Char.of(tokens)).node().terminating();
	}

	StringProcessor headProcessor();
}
