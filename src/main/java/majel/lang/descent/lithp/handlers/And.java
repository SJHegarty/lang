package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.AndExpression;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

public class And implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return AndExpression.HEAD_TOKEN;
	}

	@Override
	public LithpExpression parse(
		TokenStream<SimpleToken> tokens,
		TokenStream<LithpExpression> parsed
	){
		checkHead(tokens);
		var elements = LithpExpression.readList(tokens, parsed);
		return new AndExpression(elements);
	}
}
