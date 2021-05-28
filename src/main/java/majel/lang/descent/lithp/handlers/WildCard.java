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

	@Override
	public LithpExpression parse(TokenStream<SimpleToken> tokens, TokenStream<LithpExpression> parsed){
		return new WildCardExpression();
	}
}
