package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.Expression;
import majel.lang.descent.CharHandler;
import majel.lang.descent.RecursiveDescentBuildContext;
import majel.lang.descent.RecursiveDescentParser;
import majel.lang.util.TokenStream;

import static majel.lang.descent.lithp.Lithp.*;

public class Parenthesis implements CharHandler<FSA>{

	@Override
	public char headToken(){
		return OPENING_PARENTHESIS;
	}

	@Override
	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
		checkHead(tokens);
		var expressions = parser.parseUntil(tokens, CLOSING_PARENTHESIS);
		tokens.poll();
		return new Expression<>(){
			@Override
			public String reconstitute(){
				var builder = new StringBuilder().append(OPENING_PARENTHESIS);
				for(var expr: expressions){
					builder.append(expr.reconstitute());
				}
				return builder.append(CLOSING_PARENTHESIS).toString();
			}

			@Override
			public FSA build(RecursiveDescentBuildContext<FSA> context){
				return FSA.concatenate(
					expressions.stream()
						.map(expr -> expr.build(context))
						.toArray(FSA[]::new)
				);
			}
		};
	}
}
