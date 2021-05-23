package majel.lang.err;

import majel.lang.util.TokenStream;

public class IllegalToken extends ParseException{

	public IllegalToken(TokenStream tokens){
		super(
			String.format(
				"Illegal token '%s' at index:%s of expression:%s",
				tokens.peek(),
				tokens.index(),
				tokens.expression()
			)
		);
	}
}
