package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.Expression;
import majel.lang.descent.Handler;
import majel.lang.descent.RecursiveDescentBuildContext;
import majel.lang.descent.RecursiveDescentParser;
import majel.lang.util.TokenStream;

public class Negation implements Handler<FSA>{

	private final char HEAD_TOKEN = '!';

	@Override
	public char headToken(){
		return HEAD_TOKEN;
	}

	@Override
	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
		checkHead(tokens);
		var base = parser.parse(tokens);
		return new Expression<>(){
			@Override
			public String reconstitute(){
				return HEAD_TOKEN + base.reconstitute();
			}

			@Override
			public FSA build(RecursiveDescentBuildContext<FSA> context){
				return base.build(context).negate();
			}
		};
	}
}
