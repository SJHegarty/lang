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

	@Override
	public LithpExpression parse(
		TokenStream<SimpleToken> tokens,
		TokenStream<LithpExpression> expressions
	){
		checkHead(tokens);
		return new KleenExpression(expressions.poll());
	}
}
