package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.WildCardExpression;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

public class WildCard implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return WildCardExpression.TOKEN;
	}

//	@Override
//	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
//		checkHead(tokens);
//		return new Expression<>(){
//			@Override
//			public String reconstitute(){
//				return Character.toString(headToken());
//			}
//
//			/*@Override
//			public FSA build(RecursiveDescentBuildContext<FSA> context){
//				return new FSA(c -> true);
//			}*/
//		};
//	}

	@Override
	public LithpExpression parse(TokenStream<SimpleToken> tokens, TokenStream<LithpExpression> parsed){
		return new WildCardExpression();
	}
}
