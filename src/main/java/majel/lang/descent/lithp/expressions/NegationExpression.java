package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.PrefixLithpExpression;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

public record NegationExpression(LithpExpression wrapped) implements PrefixLithpExpression{
	public static final char HEAD_TOKEN = '!';


	@Override
	public char headToken(){
		return HEAD_TOKEN;
	}
}
