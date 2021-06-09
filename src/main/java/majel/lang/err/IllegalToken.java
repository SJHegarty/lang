package majel.lang.err;

import majel.lang.util.TokenStream_Obj;

public class IllegalToken extends ParseException{

	public IllegalToken(TokenStream_Obj tokens){
		super(tokens.peek().toString());
	}
}
