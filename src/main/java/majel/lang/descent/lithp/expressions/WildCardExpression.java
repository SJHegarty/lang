package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;

public class WildCardExpression implements LithpExpression{
	public static final char TOKEN = '.';
	@Override
	public String reconstitute(){
		return Character.toString(TOKEN);
	}

	@Override
	public boolean equals(Object o){
		return o instanceof WildCardExpression;
	}
}
