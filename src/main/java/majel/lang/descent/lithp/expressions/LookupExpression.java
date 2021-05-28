package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;
import majel.util.functional.TokenStreamBuilder;

public record LookupExpression(String identifier) implements LithpExpression{
	public static final char HEAD_TOKEN = '@';
	public static final char TAIL_TOKEN = ';';

	public String reconstitute(){
		return HEAD_TOKEN + identifier + TAIL_TOKEN;
	}

	@Override
	public TokenStream<SimpleToken> regress(){
		var builder = new TokenStreamBuilder();
		builder
			.feed(HEAD_TOKEN)
			.feed(identifier)
			.feed(TAIL_TOKEN);

		return builder.immutableView().wrap();
	}
}
