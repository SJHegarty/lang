package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;
import majel.util.functional.TokenStreamBuilder;

public record NamedExpression(String name, LithpExpression wrapped) implements LithpExpression{
	public static final char HEAD_TOKEN = '<';

	public String reconstitute(){
		return SimpleTokenStream.of(decompose()).remaining();
	}

	@Override
	public TokenStream<SimpleToken> decompose(){
		var builder = new TokenStreamBuilder();
		builder
			.feed(HEAD_TOKEN)
			.feed(LithpExpression.OPENING_PARENTHESIS)
			.feed(name)
			.feed(DELIMITER)
			.feed(wrapped.decompose())
			.feed(CLOSING_PARENTHESIS);

		return builder.immutableView().wrap();
	}
}
