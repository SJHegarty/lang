package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.lithp.Handler;
import majel.lang.descent.lithp.Lithp;
import majel.lang.descent.lithp.TokenStream;

public class Optional implements Handler{
	@Override
	public char headToken(){
		return '?';
	}

	@Override
	public FSA parse(TokenStream tokens){
		checkHead(tokens);
		return Lithp.parseSingle(tokens).optional();
	}
}
