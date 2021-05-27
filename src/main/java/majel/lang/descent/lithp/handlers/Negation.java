package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.NegationExpression;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

public class Negation implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return NegationExpression.HEAD_TOKEN;
	}
//
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
//				return base.build(context).negate();
//			}*/
//		};
//	}

	@Override
	public LithpExpression parse(TokenStream<SimpleToken> tokens, TokenStream<LithpExpression> parsed){
		checkHead(tokens);
		return new NegationExpression(parsed.poll());
	}
}
