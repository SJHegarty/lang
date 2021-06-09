package majel.lang.err;

import majel.lang.util.TokenStream$Obj;

public class IllegalToken extends ParseException{

	public IllegalToken(TokenStream$Obj tokens){
		super(tokens.peek().toString());
	}
}
