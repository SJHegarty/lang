package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.OptionalExpression;
import majel.lang.util.TokenStream;
import majel.stream.Token$Char;

public class Optional implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return OptionalExpression.HEAD_TOKEN;
	}

	@Override
	public LithpExpression parse(TokenStream<Token$Char> tokens, TokenStream<LithpExpression> parsed){
		checkHead(tokens);
		return new OptionalExpression(parsed.poll());
	}
}
