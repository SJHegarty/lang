package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.*;
import majel.lang.err.IllegalToken;
import majel.lang.util.TokenStream;

public class Literal implements Handler<FSA>{

	private static final char ENCLOSING_TOKEN = '\'';

	@Override
	public char headToken(){
		return ENCLOSING_TOKEN;
	}

	@Override
	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
		checkHead(tokens);
		final String str;
		outer:{
			var builder = new StringBuilder();
			for(;;){
				char token = tokens.poll();
				switch(token){
					case ENCLOSING_TOKEN -> {
						str = builder.toString();
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
		}
		return new Expression<>(){
			@Override
			public String reconstitute(){
				var builder = new StringBuilder().append(ENCLOSING_TOKEN);
				for(char c : str.toCharArray()){
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

			@Override
			public FSA build(RecursiveDescentBuildContext<FSA> context){
				return FSA.literal(str);
			}
		};
	}
}
