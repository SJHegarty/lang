package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.ParenthesisExpression;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

import java.util.ArrayList;
import java.util.Collections;

public class Parenthesis implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return LithpExpression.OPENING_PARENTHESIS;
	}

	@Override
	public LithpExpression parse(TokenStream<SimpleToken> tokens, TokenStream<LithpExpression> parsed){
		checkHead(tokens);
		var simple = SimpleTokenStream.of(tokens);
		var elements = new ArrayList<LithpExpression>();
		while(simple.peek() != LithpExpression.CLOSING_PARENTHESIS){
			elements.add(parsed.poll());
		}
		simple.poll();
		return new ParenthesisExpression(Collections.unmodifiableList(elements));
	}
}
