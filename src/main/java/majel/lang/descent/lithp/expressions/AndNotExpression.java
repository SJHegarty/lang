package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;

public record AndNotExpression(LithpExpression expr0, LithpExpression expr1) implements LithpExpression{
	public static final char HEAD_TOKEN = '-';

	@Override
	public String reconstitute(){
		return null;
	}

}
