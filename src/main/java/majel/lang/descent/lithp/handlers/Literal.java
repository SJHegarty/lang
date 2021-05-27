package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.lithp.Lithp1;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.LiteralExpression;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

public class Literal implements CharHandler<LithpExpression>{


	@Override
	public char headToken(){
		return LiteralExpression.ENCLOSING_TOKEN;
	}

	@Override
	public LithpExpression parse(TokenStream<SimpleToken> tokens, TokenStream<LithpExpression> parsed){
		checkHead(tokens);
		final String str;
		outer:{
			var builder = new StringBuilder();
			var simple = SimpleTokenStream.of(tokens);
			for(;;){
				switch(simple.peek()){
					case LiteralExpression.ENCLOSING_TOKEN -> {
						str = builder.toString();
						break outer;
					}
					default -> {
						builder.append(Lithp1.parseLiteral(simple));
					}
				}
			}
		}
		tokens.poll();
		return new LiteralExpression(str);
	}
}
