package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.Expression;
import majel.lang.descent.CharHandler;
import majel.lang.descent.RecursiveDescentBuildContext;
import majel.lang.descent.RecursiveDescentParser;
import majel.lang.util.TokenStream;

public class Kleene implements CharHandler<FSA>{

	@Override
	public char headToken(){
		return '*';
	}

	@Override
	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
		checkHead(tokens);
		var base = parser.parse(tokens);
		return new Expression<>(){
			@Override
			public String reconstitute(){
				return headToken() + base.reconstitute();
			}

			@Override
			public FSA build(RecursiveDescentBuildContext<FSA> context){
				return base.build(context).kleene();
			}
		};
	}
}
