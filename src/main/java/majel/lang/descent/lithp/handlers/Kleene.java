package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.lithp.Handler;
import majel.lang.descent.lithp.RecursiveDescentContext;
import majel.lang.descent.lithp.RecursiveDescentParser;
import majel.lang.descent.lithp.TokenStream;

public class Kleene extends Handler<FSA>{

	public Kleene(RecursiveDescentParser<FSA> parser){
		super(parser);
	}

	@Override
	public char headToken(){
		return '*';
	}

	@Override
	public FSA parse(RecursiveDescentContext<FSA> context){
		var tokens = context.tokens();
		checkHead(tokens);
		return parser.parse(context).kleene();
	}
}
