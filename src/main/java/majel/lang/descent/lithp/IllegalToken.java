package majel.lang.descent.lithp;

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
