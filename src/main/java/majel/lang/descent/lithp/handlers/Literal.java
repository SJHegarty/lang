package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.lithp.Handler;
import majel.lang.descent.lithp.Lithp;
import majel.lang.descent.lithp.TokenStream;

public class Literal implements Handler{
	@Override
	public char headToken(){
		return '\'';
	}

	@Override
	public FSA parse(TokenStream tokens){
		checkHead(tokens);
		var builder = new StringBuilder();
		outer:for(;;){
			char token = tokens.poll();
			switch(token){
				case '\'' -> {
					break outer;
				}
				case '\\' -> {
					builder.append(
						switch(tokens.peek()){
							case 't' -> '\t';
							case 'n' -> '\n';
							case '\\' -> '\\';
							default -> throw new Lithp.IllegalToken(tokens);
						}
					);
					tokens.poll();
				}
				default -> {
					builder.append(token);
				}
			}
		}
		return FSA.literal(null, builder.toString());
	}
}
