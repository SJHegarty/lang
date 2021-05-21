package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.lithp.Handler;
import majel.lang.descent.lithp.RecursiveDescentContext;
import majel.lang.descent.lithp.RecursiveDescentParser;
import majel.lang.descent.lithp.TokenStream;

public class Optional implements Handler<FSA>{

	@Override
	public char headToken(){
		return '?';
	}

	@Override
	public FSA parse(TokenStream<FSA> tokens){
		checkHead(tokens);
		return tokens.parse().optional();
	}
}
