package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.TokenStream_Char;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;

import java.util.List;

public record AndExpression(List<LithpExpression> expressions) implements LithpExpression{
	public static final char HEAD_TOKEN = '&';

	@Override
	public TokenStream_Obj<Token$Char> decompose(){
		return TokenStream_Char.of(HEAD_TOKEN).wrap()
			.concat(() -> LithpExpression.streamList(expressions));
	}
}
