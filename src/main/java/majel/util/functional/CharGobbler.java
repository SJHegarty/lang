package majel.util.functional;

import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

public interface CharGobbler{
	CharGobbler feed(char c);

	default CharGobbler feed(String s){
		feed(s.toCharArray());
		return this;
	}

	default CharGobbler feed(char[] chars){
		feed(SimpleTokenStream.of(chars));
		return this;
	}

	default CharGobbler feed(TokenStream<SimpleToken> tokens){
		feed(SimpleTokenStream.of(tokens));
		return this;
	}

	default CharGobbler feed(SimpleTokenStream tokens){
		tokens.drain(this::feed);
		return this;
	}

}
