package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.lithp.*;

public class AndNot implements Handler<FSA>{

	@Override
	public char headToken(){
		return '-';
	}

	@Override
	public FSA parse(RecursiveDescentTokenStream<FSA> tokens){
		checkHead(tokens);
		var list = tokens.parseList();
		if(list.size() != 2){
			throw new IllegalExpression(tokens);
		}
		return FSA.and(list.get(0), list.get(1).negate());
	}
}
