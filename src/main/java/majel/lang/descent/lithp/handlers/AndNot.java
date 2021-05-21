package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.lithp.Handler;
import majel.lang.descent.lithp.RecursiveDescentContext;
import majel.lang.descent.lithp.RecursiveDescentParser;
import majel.lang.descent.lithp.TokenStream;

public class AndNot implements Handler<FSA>{

	@Override
	public char headToken(){
		return '-';
	}

	@Override
	public FSA parse(TokenStream<FSA> tokens){
		checkHead(tokens);
		var list = tokens.parseList();
		if(list.size() != 2){
			throw new RecursiveDescentParser.IllegalExpression(tokens);
		}
		return FSA.and(list.get(0), list.get(1).negate());
	}
}
