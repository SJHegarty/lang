package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;
import majel.util.functional.TokenStreamBuilder;

import java.util.List;

public record OrExpression(List<LithpExpression> elements) implements LithpExpression{
	public static final char HEAD_TOKEN = '+';

	public String reconstitute(){
		return SimpleTokenStream.of(regress()).remaining();
	}

	@Override
	public TokenStream<SimpleToken> regress(){
		return SimpleTokenStream.of(HEAD_TOKEN).wrap()
			.concat(() -> LithpExpression.streamList(elements));
	}
}
