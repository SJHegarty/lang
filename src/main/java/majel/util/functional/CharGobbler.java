package majel.util.functional;

import majel.lang.util.TokenStream$Char;
import majel.lang.util.TokenStream$Obj;
import majel.stream.Token$Char;

public interface CharGobbler{
	CharGobbler feed(char c);

	default CharGobbler feed(String s){
		feed(s.toCharArray());
		return this;
	}

	default CharGobbler feed(char[] chars){
		feed(TokenStream$Char.of(chars));
		return this;
	}

	default CharGobbler feed(TokenStream$Obj<Token$Char> tokens){
		feed(TokenStream$Char.of(tokens));
		return this;
	}

	default CharGobbler feed(TokenStream$Char tokens){
		tokens.drain(this::feed);
		return this;
	}

}
