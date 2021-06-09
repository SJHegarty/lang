package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.context.NullContext;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.OrExpression;
import majel.lang.util.TokenStream;
import majel.stream.Token$Char;

public class Or implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return OrExpression.HEAD_TOKEN;
	}

	@Override
	public LithpExpression parse(NullContext ignored, TokenStream<Token$Char> tokens, TokenStream<LithpExpression> parsed){
		checkHead(tokens);
		return new OrExpression(LithpExpression.readList(tokens, parsed));
	}
}
