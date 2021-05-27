package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.KleenExpression;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

public class Kleene implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return KleenExpression.HEAD_TOKEN;
	}
//
//	@Override
//	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
//		checkHead(tokens);
//		var base = parser.parse(tokens);
//		return new Expression(){
//			@Override
//			public String reconstitute(){
//				return headToken() + base.reconstitute();
//			}
//
//			/*@Override
//			public FSA build(RecursiveDescentBuildContext<FSA> context){
//				return base.build(context).kleene();
//			}*/
//		};
//	}
	@Override
	public LithpExpression parse(
		TokenStream<SimpleToken> tokens,
		TokenStream<LithpExpression> expressions
	){
		checkHead(tokens);
		return new KleenExpression(expressions.poll());
	}
}
