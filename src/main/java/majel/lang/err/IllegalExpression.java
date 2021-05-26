package majel.lang.err;

import majel.lang.util.TokenStream;

public class IllegalExpression extends ParseException{
	public IllegalExpression(TokenStream tokens){
		super(tokens.remaining());
	}
}
