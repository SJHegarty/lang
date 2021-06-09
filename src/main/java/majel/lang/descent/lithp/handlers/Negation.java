package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.context.NullContext;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.NegationExpression;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;

public class Negation implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return NegationExpression.HEAD_TOKEN;
	}

	@Override
	public LithpExpression parse(
		NullContext ignored,
		TokenStream_Obj<Token$Char> tokens,
		TokenStream_Obj<LithpExpression> parsed
	){
		checkHead(tokens);
		return new NegationExpression(parsed.poll());
	}
}
