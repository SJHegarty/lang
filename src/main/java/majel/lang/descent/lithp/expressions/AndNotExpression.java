package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;

public record AndNotExpression(LithpExpression expr0, LithpExpression expr1) implements LithpExpression{
	public static final char HEAD_TOKEN = '-';

	@Override
	public TokenStream_Obj<Token$Char> decompose(){
		return null;
	}
}
