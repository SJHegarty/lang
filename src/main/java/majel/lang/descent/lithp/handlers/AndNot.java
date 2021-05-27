package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.AndNotExpression;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

public class AndNot implements CharHandler<LithpExpression>{

	private static final char HEAD_TOKEN = '-';

	@Override
	public char headToken(){
		return HEAD_TOKEN;
	}

	@Override
	public LithpExpression parse(TokenStream<SimpleToken> tokens, TokenStream<LithpExpression> parsed){
		checkHead(tokens);
		var elements = LithpExpression.readList(tokens, parsed);
		if(elements.size() != 2){
			throw new IllegalStateException();
		}
		return new AndNotExpression(elements.get(0), elements.get(1));
	}

//	@Override
//	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
//		checkHead(tokens);
//		var list = parser.parseList(
//			tokens,
//			LithpExpression.OPENING_PARENTHESIS,
//			LithpExpression.CLOSING_PARENTHESIS,
//			LithpExpression.DELIMITER
//		);
//		if(list.size() != 2){
//			throw new IllegalExpression(tokens);
//		}
//		return new Expression<>(){
//			@Override
//			public String reconstitute(){
//				return HEAD_TOKEN + parser.reconstituteList(
//					list,
//					LithpExpression.OPENING_PARENTHESIS,
//					LithpExpression.CLOSING_PARENTHESIS,
//					LithpExpression.DELIMITER
//				);
//			}
//
//			/*@Override
//			public FSA build(RecursiveDescentBuildContext<FSA> context){
//				return FSA.and(
//					list.get(0).build(context),
//					list.get(1).build(context).negate()
//				);
//			}*/
//		};
//	}
}
