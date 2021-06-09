package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.context.NullContext;
import majel.lang.descent.lithp.Lithp1;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.LiteralExpression;
import majel.lang.util.TokenStream_Char;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;

public class Literal implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return LiteralExpression.ENCLOSING_TOKEN;
	}

	@Override
	public LithpExpression parse(NullContext ignored, TokenStream_Obj<Token$Char> tokens, TokenStream_Obj<LithpExpression> parsed){
		checkHead(tokens);
		final String str;
		outer:{
			var builder = new StringBuilder();
			var simple = TokenStream_Char.of(tokens);
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
