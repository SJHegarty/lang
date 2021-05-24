package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.*;
import majel.lang.util.TokenStream;

public class Range implements Handler<FSA>{

	private static final char OPENING_BRACKET = '[';
	private static final char CLOSING_BRACKET = ']';
	private static final String DELIMITER = "...";

	@Override
	public char headToken(){
		return OPENING_BRACKET;
	}

	//TODO: utilise character extraction from Literal
	@Override
	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
		checkHead(tokens);
		char c0 = tokens.poll();
		tokens.read(DELIMITER);
		char c1 = tokens.poll();
		tokens.read(CLOSING_BRACKET);

		return new Expression<>(){
			@Override
			public String reconstitute(){
				return new StringBuilder()
					.append(OPENING_BRACKET)
					.append(c0)
					.append(DELIMITER)
					.append(c1)
					.append(CLOSING_BRACKET)
					.toString();
			}

			@Override
			public FSA build(RecursiveDescentBuildContext<FSA> context){
				return new FSA(c -> c >= c0 && c <= c1);
			}
		};
	}
}
