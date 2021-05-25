package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.Expression;
import majel.lang.descent.CharHandler;
import majel.lang.descent.RecursiveDescentBuildContext;
import majel.lang.descent.RecursiveDescentParser;
import majel.lang.descent.lithp.Lithp;
import majel.lang.util.TokenStream;

public class Range implements CharHandler<FSA>{

	private static final char OPENING_BRACKET = '[';
	private static final char CLOSING_BRACKET = ']';
	private static final String DELIMITER = "...";

	@Override
	public char headToken(){
		return OPENING_BRACKET;
	}

	@Override
	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
		checkHead(tokens);
		char c0 = Lithp.parseLiteral(tokens);
		tokens.read(DELIMITER);
		char c1 = Lithp.parseLiteral(tokens);
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
