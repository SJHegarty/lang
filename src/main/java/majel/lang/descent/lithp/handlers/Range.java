package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.lithp.Lithp1;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.RangeExpression;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

import static majel.lang.descent.lithp.expressions.RangeExpression.*;

public class Range implements CharHandler<LithpExpression>{


	@Override
	public char headToken(){
		return OPENING_BRACKET;
	}

//	@Override
//	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
//		checkHead(tokens);
//		char c0 = Lithp.parseLiteral(tokens);
//		tokens.read(DELIMITER);
//		char c1 = Lithp.parseLiteral(tokens);
//		tokens.read(CLOSING_BRACKET);
//
//		return new Expression<>(){
//			@Override
//			public String reconstitute(){
//				return new StringBuilder()
//					.append(OPENING_BRACKET)
//					.append(c0)
//					.append(DELIMITER)
//					.append(c1)
//					.append(CLOSING_BRACKET)
//					.toString();
//			}
//
//			/*@Override
//			public FSA build(RecursiveDescentBuildContext<FSA> context){
//				return new FSA(c -> c >= c0 && c <= c1);
//			}*/
//		};
//	}

	@Override
	public LithpExpression parse(TokenStream<SimpleToken> tokens, TokenStream<LithpExpression> parsed){
		checkHead(tokens);
		var simple = SimpleTokenStream.of(tokens);
		char c0 = Lithp1.parseLiteral(simple);
		simple.read(DELIMITER);
		char c1 = Lithp1.parseLiteral(simple);
		simple.read(CLOSING_BRACKET);

		return new RangeExpression(c0, c1);
	}
}
