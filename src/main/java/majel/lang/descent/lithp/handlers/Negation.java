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

	@Override
	public LithpExpression parse(TokenStream<SimpleToken> tokens, TokenStream<LithpExpression> parsed){
		checkHead(tokens);
		return new NegationExpression(parsed.poll());
	}
}
