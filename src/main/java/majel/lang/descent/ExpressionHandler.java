package majel.lang.descent;

import majel.lang.automata.fsa.StringProcessor;
import majel.lang.descent.context.NullContext;
import majel.lang.util.TokenStream$Char;
import majel.lang.util.TokenStream;
import majel.stream.Token$Char;
import majel.stream.Token;

public interface ExpressionHandler<T extends Token> extends Handler<NullContext, Token$Char, T>{
	@Override
	default boolean supportsHead(TokenStream<Token$Char> tokens){
		return headProcessor().process(TokenStream$Char.of(tokens)).node().terminating();
	}

	StringProcessor headProcessor();
}
