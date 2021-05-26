package majel.lang.err;

import majel.lang.util.TokenStream;

public class IllegalToken extends ParseException{

	public IllegalToken(TokenStream tokens){
		super(
			String.format(
				"Illegal token '%s' at start of expression:%s",
				tokens.peek(),
				tokens.remaining()
			)
		);
	}
}
