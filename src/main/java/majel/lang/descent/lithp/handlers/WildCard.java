package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.lithp.Handler;
import majel.lang.descent.lithp.RecursiveDescentParser;
import majel.lang.descent.lithp.TokenStream;

public class WildCard extends Handler{
	public WildCard(RecursiveDescentParser parser){
		super(parser);
	}

	@Override
	public char headToken(){
		return '.';
	}

	@Override
	public FSA parse(TokenStream tokens){
		checkHead(tokens);
		return new FSA(c -> true, null);
	}
}
