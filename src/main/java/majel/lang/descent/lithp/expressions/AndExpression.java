package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.TokenStream$Char;
import majel.lang.util.TokenStream;
import majel.stream.Token$Char;

import java.util.List;

public record AndExpression(List<LithpExpression> expressions) implements LithpExpression{
	public static final char HEAD_TOKEN = '&';

	@Override
	public TokenStream<Token$Char> decompose(){
		return TokenStream$Char.of(HEAD_TOKEN).wrap()
			.concat(() -> LithpExpression.streamList(expressions));
	}
}
