package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;
import majel.util.functional.TokenStreamBuilder;

public record LookupExpression(String identifier) implements LithpExpression{
	public static final char HEAD_TOKEN = '@';
	public static final char TAIL_TOKEN = ';';

	public String reconstitute(){
		return HEAD_TOKEN + identifier + TAIL_TOKEN;
	}

	@Override
	public TokenStream_Obj<Token$Char> decompose(){
		var builder = new TokenStreamBuilder();
		builder
			.feed(HEAD_TOKEN)
			.feed(identifier)
			.feed(TAIL_TOKEN);

		return builder.immutableView().wrap();
	}
}
