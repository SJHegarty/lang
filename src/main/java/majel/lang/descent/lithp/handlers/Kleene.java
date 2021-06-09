package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.context.NullContext;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.KleenExpression;
import majel.lang.util.TokenStream$Obj;
import majel.stream.Token$Char;

public class Kleene implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return KleenExpression.HEAD_TOKEN;
	}

	@Override
	public LithpExpression parse(
		NullContext ignored,
		TokenStream$Obj<Token$Char> tokens,
		TokenStream$Obj<LithpExpression> expressions
	){
		checkHead(tokens);
		return new KleenExpression(expressions.poll());
	}
}
