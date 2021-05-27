package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;

import java.util.List;

public record ParenthesisExpression(List<LithpExpression> elements) implements LithpExpression{

	@Override
	public String reconstitute(){
		var builder = new StringBuilder()
			.append(OPENING_PARENTHESIS);

		for(var expr: elements){
			builder.append(expr.reconstitute());
		}

		return builder
			.append(CLOSING_PARENTHESIS)
			.toString();
	}
}
