package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.context.NullContext;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.EscapeExpression;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;

public class Escape implements CharHandler<LithpExpression>{
	@Override
	public char headToken(){
		return '\\';
	}

	@Override
	public LithpExpression parse(NullContext c, TokenStream_Obj<Token$Char> tokens, TokenStream_Obj<LithpExpression> parsed){
		checkHead(tokens);
		return new EscapeExpression(tokens.poll().value());
	}
}
