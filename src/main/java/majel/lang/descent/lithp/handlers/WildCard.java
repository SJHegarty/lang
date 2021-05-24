package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.*;
import majel.lang.util.TokenStream;

public class WildCard implements Handler<FSA>{

	@Override
	public char headToken(){
		return '.';
	}

	@Override
	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
		checkHead(tokens);
		return new Expression<>(){
			@Override
			public String reconstitute(){
				return Character.toString(headToken());
			}

			@Override
			public FSA build(RecursiveDescentBuildContext<FSA> context){
				return new FSA(c -> true);
			}
		};
	}
}
