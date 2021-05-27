package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;

public record RepetitionExpression(int lower, int upper, LithpExpression base) implements LithpExpression{

	public static final char HEAD_TOKEN = '#';
	public static final char UNBOUND = '+';
	public static final String CONTINUATION = "...";

	@Override
	public String reconstitute(){
		var builder = new StringBuilder()
			.append(HEAD_TOKEN)
			.append(LithpExpression.OPENING_PARENTHESIS)
			.append(lower);

		if(upper != lower){
			if(upper == Integer.MAX_VALUE){
				builder.append(UNBOUND);
			}
			else{
				builder.append(CONTINUATION).append(upper);
			}
		}

		return builder
			.append(LithpExpression.DELIMITER)
			.append(base.reconstitute())
			.append(LithpExpression.CLOSING_PARENTHESIS)
			.toString();
	}
}
