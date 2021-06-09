package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.TokenStream_Char;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;
import majel.util.functional.TokenStreamBuilder;

public record LiteralExpression(String value) implements LithpExpression{

	public static final char ENCLOSING_TOKEN = '\'';

	public String reconstitute(){
		return TokenStream_Char.of(decompose()).remaining();
	}

	@Override
	public TokenStream_Obj<Token$Char> decompose(){
		var builder = new TokenStreamBuilder();
		builder.feed(ENCLOSING_TOKEN);
		for(char c : value.toCharArray()){
			builder.feed(
				switch(c){
					case '\t' -> "\\t";
					case '\n' -> "\\n";
					case '\\' -> "\\\\";
					default -> Character.toString(c);
				}
			);
		}
		builder.feed(ENCLOSING_TOKEN);
		return builder.immutableView().wrap();
	}
}
