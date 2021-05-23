package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.Handler;
import majel.lang.descent.RecursiveDescentTokenStream;

public class Parenthesis implements Handler<FSA>{

	@Override
	public char headToken(){
		return '(';
	}

	@Override
	public FSA parse(RecursiveDescentTokenStream<FSA> tokens){
		checkHead(tokens);
		var parser = tokens.context().parser();
		var result = parser.parseWhile(tokens, () -> tokens.peek() != ')');
		tokens.poll();
		return FSA.concatenate(result.toArray(FSA[]::new));
	}
}
