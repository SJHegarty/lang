package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;

public record NamedExpression(String name, LithpExpression wrapped) implements LithpExpression{
	public static final char HEAD_TOKEN = '<';

	@Override
	public String reconstitute(){
		return new StringBuilder()
			.append(HEAD_TOKEN)
			.append(LithpExpression.OPENING_PARENTHESIS)
			.append(name)
			.append(LithpExpression.DELIMITER)
			.append(wrapped.reconstitute())
			.append(LithpExpression.CLOSING_PARENTHESIS)
			.toString();
	}

}
