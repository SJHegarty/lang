package majel.lang;

import majel.stream.Token;

public interface ReversibleParser<S extends Token, T extends Token> extends Parser<S, T>{
	ReversibleParser<T, S> reverse();
}
