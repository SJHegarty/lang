package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.lithp.Handler;
import majel.lang.descent.lithp.RecursiveDescentTokenStream;

public class Kleene implements Handler<FSA>{

	@Override
	public char headToken(){
		return '*';
	}

	@Override
	public FSA parse(RecursiveDescentTokenStream<FSA> tokens){
		checkHead(tokens);
		return tokens.parse().kleene();
	}
}
