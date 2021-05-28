package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

import java.util.List;

public record AndExpression(List<LithpExpression> expressions) implements LithpExpression{
	public static final char HEAD_TOKEN = '&';

	@Override
	public TokenStream<SimpleToken> regress(){
		return SimpleTokenStream.of(HEAD_TOKEN).wrap()
			.concat(() -> LithpExpression.streamList(expressions));
	}
}
