package majel.lang.descent.lithp.expressions;

import majel.lang.descent.lithp.LithpExpression;
import majel.lang.util.TokenStream_Char;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;

public record EscapeExpression(char token) implements LithpExpression{
	public EscapeExpression{
		switch(token){
			case 'n', 'r', 't', 's', '\'', '\\', '\"' -> {}
			default -> throw new IllegalArgumentException(
				"Unknown escape sequence \\" + token
			);
		}
	}

	@Override
	public TokenStream_Obj<Token$Char> decompose(){
		return TokenStream_Char.of('\\', token).wrap();
	}

	public char represented(){
		return switch(token){
			case 'n' -> '\n';
			case 'r' -> '\r';
			case 't' -> '\t';
			case 's' -> ' ';
			case '\'' -> '\'';
			case '\\' -> '\\';
			case '\"' -> '\"';
			default -> throw new IllegalStateException();
		};
	}
}
