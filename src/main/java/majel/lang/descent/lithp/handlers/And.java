package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.AndExpression;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

public class And implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return AndExpression.HEAD_TOKEN;
	}
//
//	@Override
//	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
//		checkHead(tokens);
//		var elements = parser.parseList(
//			tokens,
//			LithpExpression.OPENING_PARENTHESIS,
//			LithpExpression.CLOSING_PARENTHESIS,
//			LithpExpression.DELIMITER
//		);
//		return new Expression<>(){
//			@Override
//			public String reconstitute(){
//				return new StringBuilder()
//					.append(HEAD_TOKEN)
//					.append(
//						parser.reconstituteList(
//							elements,
//							LithpExpression.OPENING_PARENTHESIS,
//							LithpExpression.CLOSING_PARENTHESIS,
//							LithpExpression.DELIMITER
//						)
//					)
//					.toString();
//			}
//
//			/*@Override
//			public FSA build(RecursiveDescentBuildContext<FSA> context){
//				return FSA.and(
//					elements.stream()
//						.map(expr -> expr.build(context))
//						.toArray(FSA[]::new)
//				);
//			}*/
//		};
//	}


	@Override
	public LithpExpression parse(
		TokenStream<SimpleToken> tokens,
		TokenStream<LithpExpression> parsed
	){
		checkHead(tokens);
		var elements = LithpExpression.readList(tokens, parsed);
		return new AndExpression(elements);
	}
}
