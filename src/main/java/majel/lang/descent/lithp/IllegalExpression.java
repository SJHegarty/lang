package majel.lang.descent.lithp;

public class IllegalExpression extends ParseException{
	public IllegalExpression(TokenStream tokens){
		super(tokens.expression());
	}
}
