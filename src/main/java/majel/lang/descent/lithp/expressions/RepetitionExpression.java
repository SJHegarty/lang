package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;
import majel.util.functional.TokenStreamBuilder;

public record RepetitionExpression(int lower, int upper, LithpExpression base) implements LithpExpression{

	public static final char HEAD_TOKEN = '#';
	public static final char UNBOUND = '+';
	public static final String CONTINUATION = "...";

	@Override
	public TokenStream_Obj<Token$Char> decompose(){
		var builder = new TokenStreamBuilder();
		builder
			.feed(HEAD_TOKEN)
			.feed(LithpExpression.OPENING_PARENTHESIS)
			.feed(Integer.toString(lower));

		if(upper != lower){
			if(upper == Integer.MAX_VALUE){
				builder.feed(UNBOUND);
			}
			else{
				builder
					.feed(CONTINUATION)
					.feed(Integer.toString(upper));
			}
		}

		builder
			.feed(LithpExpression.DELIMITER)
			.feed(base.decompose())
			.feed(LithpExpression.CLOSING_PARENTHESIS);

		return builder.immutableView().wrap();
	}

}
