package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.context.NullContext;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.AndNotExpression;
import majel.lang.descent.lithp.expressions.OrExpression;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;

public class AndNot implements CharHandler<LithpExpression>{

	private static final char HEAD_TOKEN = '-';

	@Override
	public char headToken(){
		return HEAD_TOKEN;
	}

	@Override
	public LithpExpression parse(
		NullContext ignored,
		TokenStream_Obj<Token$Char> tokens,
		TokenStream_Obj<LithpExpression> parsed
	){
		checkHead(tokens);
		var elements = LithpExpression.readList(tokens, parsed);
		return new AndNotExpression(
			elements.get(0),
			new OrExpression(elements.subList(1, elements.size()))
		);
	}

}
