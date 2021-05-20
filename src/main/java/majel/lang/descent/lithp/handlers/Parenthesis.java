package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.lithp.Handler;
import majel.lang.descent.lithp.RecursiveDescentContext;
import majel.lang.descent.lithp.RecursiveDescentParser;
import majel.lang.descent.lithp.TokenStream;

public class Parenthesis extends Handler<FSA>{

	public Parenthesis(RecursiveDescentParser<FSA> parser){
		super(parser);
	}

	@Override
	public char headToken(){
		return '(';
	}

	@Override
	public FSA parse(RecursiveDescentContext<FSA> context){
		var tokens = context.tokens();
		checkHead(tokens);
		var rv = parser.parseWhile(context, () -> tokens.peek() != ')');
		tokens.poll();
		return rv;
	}
}
