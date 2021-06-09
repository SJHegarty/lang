package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.TokenStream$Char;
import majel.lang.util.TokenStream$Obj;
import majel.stream.Token$Char;

public class WildCardExpression implements LithpExpression{
	public static final char TOKEN = '.';

	public String reconstitute(){
		return Character.toString(TOKEN);
	}

	@Override
	public boolean equals(Object o){
		return o instanceof WildCardExpression;
	}

	@Override
	public TokenStream$Obj<Token$Char> decompose(){
		return TokenStream$Char.of('.').wrap();
	}
}
