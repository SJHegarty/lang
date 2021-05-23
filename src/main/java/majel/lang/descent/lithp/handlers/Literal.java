package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.Handler;
import majel.lang.err.IllegalToken;
import majel.lang.descent.RecursiveDescentTokenStream;

public class Literal implements Handler<FSA>{

	@Override
	public char headToken(){
		return '\'';
	}

	@Override
	public FSA parse(RecursiveDescentTokenStream<FSA> tokens){
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
							default -> throw new IllegalToken(tokens);
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
