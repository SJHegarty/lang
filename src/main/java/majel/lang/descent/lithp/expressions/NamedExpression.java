package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.TokenStream$Char;
import majel.lang.util.TokenStream$Obj;
import majel.stream.Token$Char;
import majel.util.functional.TokenStreamBuilder;

public record NamedExpression(String name, LithpExpression wrapped) implements LithpExpression{
	public static final char HEAD_TOKEN = '<';

	public String reconstitute(){
		return TokenStream$Char.of(decompose()).remaining();
	}

	@Override
	public TokenStream$Obj<Token$Char> decompose(){
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
