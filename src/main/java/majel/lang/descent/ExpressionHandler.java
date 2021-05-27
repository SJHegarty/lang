package majel.lang.descent;

import majel.lang.automata.fsa.StringProcessor;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;
import majel.stream.Token;

public interface ExpressionHandler<T extends Token> extends Handler<SimpleToken, T>{
	@Override
	default boolean supportsHead(TokenStream<SimpleToken> tokens){
		return headProcessor().process(SimpleTokenStream.of(tokens)).node().terminating();
	}

	StringProcessor headProcessor();
}
