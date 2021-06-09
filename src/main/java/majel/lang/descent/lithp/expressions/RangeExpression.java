package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.TokenStream_Char;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;
import majel.util.functional.TokenStreamBuilder;

public record RangeExpression(char c0, char cN) implements LithpExpression{

	public static final char OPENING_BRACKET = '[';
	public static final char CLOSING_BRACKET = ']';
	public static final String DELIMITER = "...";

	public String reconstitute(){
		return TokenStream_Char.of(decompose()).remaining();
	}

	@Override
	public TokenStream_Obj<Token$Char> decompose(){
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
