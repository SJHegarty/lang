package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;

public record LookupExpression(String identifier) implements LithpExpression{
	public static final char HEAD_TOKEN = '@';
	public static final char TAIL_TOKEN = ';';
	@Override
	public String reconstitute(){
		return HEAD_TOKEN + identifier + TAIL_TOKEN;
	}

}
