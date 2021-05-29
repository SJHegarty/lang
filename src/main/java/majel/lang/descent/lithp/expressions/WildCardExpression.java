package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

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
	public TokenStream<SimpleToken> decompose(){
		return SimpleTokenStream.of('.').wrap();
	}
}
