package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;
import majel.util.functional.TokenStreamBuilder;

public record LiteralExpression(String value) implements LithpExpression{

	public static final char ENCLOSING_TOKEN = '\'';

	public String reconstitute(){
		return SimpleTokenStream.of(decompose()).remaining();
	}

	@Override
	public TokenStream<SimpleToken> decompose(){
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
