package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.Handler;
import majel.lang.descent.RecursiveDescentTokenStream;

public class Or implements Handler<FSA>{

	@Override
	public char headToken(){
		return '+';
	}

	@Override
	public FSA parse(RecursiveDescentTokenStream<FSA> tokens){
		checkHead(tokens);
		return FSA.or(tokens.parseList().toArray(FSA[]::new));
	}
}
