package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;
import majel.util.functional.TokenStreamBuilder;

public record RangeExpression(char c0, char cN) implements LithpExpression{

	public static final char OPENING_BRACKET = '[';
	public static final char CLOSING_BRACKET = ']';
	public static final String DELIMITER = "...";

	public String reconstitute(){
		return SimpleTokenStream.of(decompose()).remaining();
	}

	@Override
	public TokenStream<SimpleToken> decompose(){
		var builder = new TokenStreamBuilder();
		builder
			.feed(OPENING_BRACKET)
			.feed(c0)
			.feed(DELIMITER)
			.feed(cN)
			.feed(CLOSING_BRACKET);

		return builder.immutableView().wrap();
	}
}
