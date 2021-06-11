package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.TokenStream_Char;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;
import majel.util.functional.TokenStreamBuilder;

public record NamedExpression(char headToken, String name, LithpExpression wrapped) implements LithpExpression{

	public String reconstitute(){
		return TokenStream_Char.of(decompose()).remaining();
	}

	@Override
	public TokenStream_Obj<Token$Char> decompose(){
		var builder = new TokenStreamBuilder();
		builder
			.feed(headToken)
			.feed(LithpExpression.OPENING_PARENTHESIS)
			.feed(name)
			.feed(DELIMITER)
			.feed(wrapped.decompose())
			.feed(CLOSING_PARENTHESIS);

		return builder.immutableView().wrap();
	}
}
