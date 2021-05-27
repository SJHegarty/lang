package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;

public record KleenExpression(LithpExpression wrapped) implements LithpExpression{
	public static final char HEAD_TOKEN = '*';

	@Override
	public String reconstitute(){
		return HEAD_TOKEN + wrapped.reconstitute();
	}

}
