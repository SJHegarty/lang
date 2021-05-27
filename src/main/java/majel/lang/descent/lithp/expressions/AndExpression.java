package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;

import java.util.List;

public record AndExpression(List<LithpExpression> expressions) implements LithpExpression{
	public static final char HEAD_TOKEN = '&';

	@Override
	public String reconstitute(){
		return HEAD_TOKEN + LithpExpression.reconstituteList(expressions);
	}

}
