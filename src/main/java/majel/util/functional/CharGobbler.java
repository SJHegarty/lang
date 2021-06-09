package majel.util.functional;

import majel.lang.util.TokenStream_Char;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;

public interface CharGobbler{
	CharGobbler feed(char c);

	default CharGobbler feed(String s){
		feed(s.toCharArray());
		return this;
	}

	default CharGobbler feed(char[] chars){
		feed(TokenStream_Char.of(chars));
		return this;
	}

	default CharGobbler feed(TokenStream_Obj<Token$Char> tokens){
		feed(TokenStream_Char.of(tokens));
		return this;
	}

	default CharGobbler feed(TokenStream_Char tokens){
		tokens.drain(this::feed);
		return this;
	}

}
