package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.OrExpression;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

public class Or implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return OrExpression.HEAD_TOKEN;
	}

//	@Override
//	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
//		checkHead(tokens);
//		List<Expression<FSA>> elements = parser.parseList(
//			tokens,
//			LithpExpression.OPENING_PARENTHESIS,
//			LithpExpression.CLOSING_PARENTHESIS,
//			LithpExpression.DELIMITER
//		);
//		return new Expression<>(){
//			@Override
//			public String reconstitute(){
//				return HEAD_TOKEN + parser.reconstituteList(
//					elements,
//					LithpExpression.OPENING_PARENTHESIS,
//					LithpExpression.CLOSING_PARENTHESIS,
//					LithpExpression.DELIMITER
//				);
//			}
//
//			/*@Override
//			public FSA build(RecursiveDescentBuildContext<FSA> context){
//				return FSA.or(
//					elements.stream()
//						.map(expr -> expr.build(context))
//						.toArray(FSA[]::new)
//				);
//			}*/
//		};
//	}

	@Override
	public LithpExpression parse(TokenStream<SimpleToken> tokens, TokenStream<LithpExpression> parsed){
		checkHead(tokens);
		return new OrExpression(LithpExpression.readList(tokens, parsed));
	}
}
