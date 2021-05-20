package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.lithp.Handler;
import majel.lang.descent.lithp.Lithp;
import majel.lang.descent.lithp.TokenStream;

public class AndNot implements Handler{
	@Override
	public char headToken(){
		return '-';
	}

	@Override
	public FSA parse(TokenStream tokens){
		checkHead(tokens);
		var list = Lithp.parseList(tokens);
		if(list.length != 2){
			throw new Lithp.IllegalExpression(tokens);
		}
		return FSA.and(list[0], list[1].negate());
	}
}
