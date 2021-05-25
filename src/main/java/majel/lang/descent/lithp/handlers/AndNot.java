package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.CharHandler;
import majel.lang.descent.Expression;
import majel.lang.descent.RecursiveDescentBuildContext;
import majel.lang.descent.RecursiveDescentParser;
import majel.lang.err.IllegalExpression;
import majel.lang.util.TokenStream;

import static majel.lang.descent.lithp.Lithp.*;

public class AndNot implements CharHandler<FSA>{

	private static final char HEAD_TOKEN = '-';

	@Override
	public char headToken(){
		return HEAD_TOKEN;
	}

	@Override
	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
		checkHead(tokens);
		var list = parser.parseList(
			tokens,
			OPENING_PARENTHESIS,
			CLOSING_PARENTHESIS,
			DELIMITER
		);
		if(list.size() != 2){
			throw new IllegalExpression(tokens);
		}
		return new Expression<>(){
			@Override
			public String reconstitute(){
				return HEAD_TOKEN + parser.reconstituteList(
					list,
					OPENING_PARENTHESIS,
					CLOSING_PARENTHESIS,
					DELIMITER
				);
			}

			@Override
			public FSA build(RecursiveDescentBuildContext<FSA> context){
				return FSA.and(
					list.get(0).build(context),
					list.get(1).build(context).negate()
				);
			}
		};
	}
}
