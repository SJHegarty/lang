package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.context.NullContext;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.ParenthesisExpression;
import majel.lang.util.TokenStream_Char;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;

import java.util.ArrayList;
import java.util.Collections;

public class Parenthesis implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return LithpExpression.OPENING_PARENTHESIS;
	}

	@Override
	public LithpExpression parse(NullContext ignored, TokenStream_Obj<Token$Char> tokens, TokenStream_Obj<LithpExpression> parsed){
		checkHead(tokens);
		var simple = TokenStream_Char.of(tokens);
		var elements = new ArrayList<LithpExpression>();
		while(simple.peek() != LithpExpression.CLOSING_PARENTHESIS){
			elements.add(parsed.poll());
		}
		simple.poll();
		return new ParenthesisExpression(Collections.unmodifiableList(elements));
	}
}
