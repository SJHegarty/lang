package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.context.NullContext;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.AndExpression;
import majel.lang.util.TokenStream;
import majel.stream.Token$Char;

public class And implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return AndExpression.HEAD_TOKEN;
	}

	@Override
	public LithpExpression parse(
		NullContext ignored,
		TokenStream<Token$Char> tokens,
		TokenStream<LithpExpression> parsed
	){
		checkHead(tokens);
		var elements = LithpExpression.readList(tokens, parsed);
		return new AndExpression(elements);
	}
}
