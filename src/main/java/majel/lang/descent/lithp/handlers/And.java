package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.Expression;
import majel.lang.descent.CharHandler;
import majel.lang.descent.RecursiveDescentBuildContext;
import majel.lang.descent.RecursiveDescentParser;
import majel.lang.util.TokenStream;

import static majel.lang.descent.lithp.Lithp.*;

public class And implements CharHandler<FSA>{

	private static final char HEAD_TOKEN = '&';

	@Override
	public char headToken(){
		return HEAD_TOKEN;
	}

	@Override
	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
		checkHead(tokens);
		var elements = parser.parseList(
			tokens,
			OPENING_PARENTHESIS,
			CLOSING_PARENTHESIS,
			DELIMITER
		);
		return new Expression<>(){
			@Override
			public String reconstitute(){
				return new StringBuilder()
					.append(HEAD_TOKEN)
					.append(
						parser.reconstituteList(
							elements,
							OPENING_PARENTHESIS,
							CLOSING_PARENTHESIS,
							DELIMITER
						)
					)
					.toString();
			}

			@Override
			public FSA build(RecursiveDescentBuildContext<FSA> context){
				return FSA.and(
					elements.stream()
						.map(expr -> expr.build(context))
						.toArray(FSA[]::new)
				);
			}
		};
	}
}
