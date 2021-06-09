package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.context.NullContext;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.WildCardExpression;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;

public class WildCard implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return WildCardExpression.TOKEN;
	}

	@Override
	public LithpExpression parse(NullContext context, TokenStream_Obj<Token$Char> tokens, TokenStream_Obj<LithpExpression> parsed){
		return new WildCardExpression();
	}
}
