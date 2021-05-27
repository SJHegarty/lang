package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;

public record OptionalExpression(LithpExpression optional) implements LithpExpression{
	public static final char HEAD_TOKEN = '?';
	@Override
	public String reconstitute(){
		return HEAD_TOKEN + optional.reconstitute();
	}

}
