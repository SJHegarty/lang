package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;
import majel.util.functional.TokenStreamBuilder;

import java.util.List;

public record ParenthesisExpression(List<LithpExpression> elements) implements LithpExpression{

	public String reconstitute(){
		return SimpleTokenStream.of(regress()).remaining();
	}

	@Override
	public TokenStream<SimpleToken> regress(){
		var builder = new TokenStreamBuilder();
		builder.feed(OPENING_PARENTHESIS);
		for(var expr: elements){
			builder.feed(expr.regress());
		}
		builder.feed(CLOSING_PARENTHESIS);
		return builder.immutableView().wrap();
	}
}
