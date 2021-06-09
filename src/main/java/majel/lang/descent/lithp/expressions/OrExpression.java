package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.TokenStream$Char;
import majel.lang.util.TokenStream;
import majel.stream.Token$Char;

import java.util.List;

public record OrExpression(List<LithpExpression> elements) implements LithpExpression{
	public static final char HEAD_TOKEN = '+';

	public String reconstitute(){
		return TokenStream$Char.of(decompose()).remaining();
	}

	@Override
	public TokenStream<Token$Char> decompose(){
		return TokenStream$Char.of(HEAD_TOKEN).wrap()
			.concat(() -> LithpExpression.streamList(elements));
	}
}
