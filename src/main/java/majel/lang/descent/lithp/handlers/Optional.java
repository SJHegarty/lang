package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.OptionalExpression;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

public class Optional implements CharHandler<LithpExpression>{


	@Override
	public char headToken(){
		return OptionalExpression.HEAD_TOKEN;
	}

//	@Override
//	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
//		checkHead(tokens);
//		var base = parser.parse(tokens);
//		return new Expression<>(){
//			@Override
//			public String reconstitute(){
//				return HEAD_TOKEN + base.reconstitute();
//			}
//
//			/*@Override
//			public FSA build(RecursiveDescentBuildContext<FSA> context){
//				return base.build(context).optional();
//			}*/
//		};
//	}

	@Override
	public LithpExpression parse(TokenStream<SimpleToken> tokens, TokenStream<LithpExpression> parsed){
		checkHead(tokens);
		return new OptionalExpression(parsed.poll());
	}
}
