package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.TokenStream$Char;
import majel.lang.util.TokenStream;
import majel.stream.Token$Char;
import majel.util.functional.TokenStreamBuilder;

import java.util.List;

public record ParenthesisExpression(List<LithpExpression> elements) implements LithpExpression{

	public String reconstitute(){
		return TokenStream$Char.of(decompose()).remaining();
	}

	@Override
	public TokenStream<Token$Char> decompose(){
		var builder = new TokenStreamBuilder();
		builder.feed(OPENING_PARENTHESIS);
		for(var expr: elements){
			builder.feed(expr.decompose());
		}
		builder.feed(CLOSING_PARENTHESIS);
		return builder.immutableView().wrap();
	}
}
