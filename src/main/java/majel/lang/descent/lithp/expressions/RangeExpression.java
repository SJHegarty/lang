package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;

public record RangeExpression(char c0, char cN) implements LithpExpression{

	public static final char OPENING_BRACKET = '[';
	public static final char CLOSING_BRACKET = ']';
	public static final String DELIMITER = "...";

	@Override
	public String reconstitute(){
		return new StringBuilder()
			.append(OPENING_BRACKET)
			.append(c0)
			.append(DELIMITER)
			.append(cN)
			.append(CLOSING_BRACKET)
			.toString();
	}
}
