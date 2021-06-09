package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.lithp.Lithp1;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.RangeExpression;
import majel.lang.util.TokenStream$Char;
import majel.lang.util.TokenStream;
import majel.stream.Token$Char;

import static majel.lang.descent.lithp.expressions.RangeExpression.*;

public class Range implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return OPENING_BRACKET;
	}

	@Override
	public LithpExpression parse(TokenStream<Token$Char> tokens, TokenStream<LithpExpression> parsed){
		checkHead(tokens);
		var simple = TokenStream$Char.of(tokens);
		char c0 = Lithp1.parseLiteral(simple);
		simple.read(DELIMITER);
		char c1 = Lithp1.parseLiteral(simple);
		simple.read(CLOSING_BRACKET);

		return new RangeExpression(c0, c1);
	}
}
