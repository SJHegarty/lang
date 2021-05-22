package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.lithp.*;

public class And implements Handler<FSA>{

	@Override
	public char headToken(){
		return '&';
	}

	@Override
	public FSA parse(RecursiveDescentTokenStream<FSA> tokens){
		checkHead(tokens);
		var parser = tokens.context().parser();

		return FSA.and(parser.parseList(tokens).toArray(FSA[]::new));
	}
}