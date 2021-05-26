package majel.lang.descent;

import majel.lang.automata.fsa.StringProcessor;
import majel.lang.util.TokenStream;

public interface ExpressionHandler<T> extends Handler<T>{
	@Override
	default boolean supportsHead(TokenStream tokens){
		return headProcessor().process(tokens).node().terminating();
	}

	StringProcessor headProcessor();
}
