package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;

public record LiteralExpression(String value) implements LithpExpression{

	public static final char ENCLOSING_TOKEN = '\'';
	@Override
	public String reconstitute(){
		var builder = new StringBuilder().append(ENCLOSING_TOKEN);
		for(char c : value.toCharArray()){
			builder.append(
				switch(c){
					case '\t' -> "\\t";
					case '\n' -> "\\n";
					case '\\' -> "\\\\";
					default -> Character.toString(c);
				}
			);
		}
		return builder.append(ENCLOSING_TOKEN).toString();

	}

}
