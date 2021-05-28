package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.PrefixLithpExpression;

public record OptionalExpression(LithpExpression wrapped) implements PrefixLithpExpression{
	public static final char HEAD_TOKEN = '?';

	@Override
	public char headToken(){
		return HEAD_TOKEN;
	}
}
