package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.AndNotExpression;
import majel.lang.util.TokenStream;
import majel.stream.Token$Char;

public class AndNot implements CharHandler<LithpExpression>{

	private static final char HEAD_TOKEN = '-';

	@Override
	public char headToken(){
		return HEAD_TOKEN;
	}

	@Override
	public LithpExpression parse(TokenStream<Token$Char> tokens, TokenStream<LithpExpression> parsed){
		checkHead(tokens);
		var elements = LithpExpression.readList(tokens, parsed);
		if(elements.size() != 2){
			throw new IllegalStateException();
		}
		return new AndNotExpression(elements.get(0), elements.get(1));
	}

}
