package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.TokenStream_Char;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;

public class WildCardExpression implements LithpExpression{
	public static final char TOKEN = '.';
	public static final WildCardExpression INSTANCE = new WildCardExpression();

	private WildCardExpression(){

	}
	public String reconstitute(){
		return Character.toString(TOKEN);
	}

	@Override
	public boolean equals(Object o){
		return o instanceof WildCardExpression;
	}

	@Override
	public TokenStream_Obj<Token$Char> decompose(){
		return TokenStream_Char.of('.').wrap();
	}
}
