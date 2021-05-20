package majel.lang.descent.lithp;

import majel.lang.automata.fsa.FSA;

public interface Handler{

	default void checkHead(TokenStream tokens){
		tokens.read(headToken());
	}

	char headToken();

	FSA parse(TokenStream tokens);
}
