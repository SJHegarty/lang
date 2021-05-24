package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.Expression;
import majel.lang.descent.Handler;
import majel.lang.descent.RecursiveDescentBuildContext;
import majel.lang.descent.RecursiveDescentParser;
import majel.lang.util.TokenStream;

import java.util.List;

import static majel.lang.descent.lithp.Lithp.*;
public class Or implements Handler<FSA>{

	private static final char HEAD_TOKEN = '+';

	@Override
	public char headToken(){
		return HEAD_TOKEN;
	}

	@Override
	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
		checkHead(tokens);
		List<Expression<FSA>> elements = parser.parseList(
			tokens,
			OPENING_PARENTHESIS,
			CLOSING_PARENTHESIS,
			DELIMITER
		);
		return new Expression<>(){
			@Override
			public String reconstitute(){
				return HEAD_TOKEN + parser.reconstituteList(
					elements,
					OPENING_PARENTHESIS,
					CLOSING_PARENTHESIS,
					DELIMITER
				);
			}

			@Override
			public FSA build(RecursiveDescentBuildContext<FSA> context){
				return FSA.or(
					elements.stream()
						.map(expr -> expr.build(context))
						.toArray(FSA[]::new)
				);
			}
		};
	}
}
