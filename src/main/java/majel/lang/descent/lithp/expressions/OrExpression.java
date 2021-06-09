package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.TokenStream_Char;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;

import java.util.List;

public record OrExpression(List<LithpExpression> elements) implements LithpExpression{
	public static final char HEAD_TOKEN = '+';

	public String reconstitute(){
		return TokenStream_Char.of(decompose()).remaining();
	}

	@Override
	public TokenStream_Obj<Token$Char> decompose(){
		return TokenStream_Char.of(HEAD_TOKEN).wrap()
			.concat(() -> LithpExpression.streamList(elements));
	}
}
