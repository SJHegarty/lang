package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.Expression;
import majel.lang.descent.Handler;
import majel.lang.descent.RecursiveDescentBuildContext;
import majel.lang.descent.RecursiveDescentParser;
import majel.lang.descent.lithp.Lithp;
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
				switch(tokens.peek()){
					case ENCLOSING_TOKEN -> {
						str = builder.toString();
						break outer;
					}
					default -> {
						builder.append(Lithp.parseLiteral(tokens));
					}
				}
			}
		}
		tokens.poll();
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
