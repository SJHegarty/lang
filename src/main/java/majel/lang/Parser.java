package majel.lang;

import majel.lang.util.TokenStream;
import majel.stream.Token;

import java.util.ArrayList;
import java.util.List;

public interface Parser<S extends Token, T extends Token>{
	TokenStream<T> parse(TokenStream<S> tokens);

	default List<T> parse(List<S> elements){
		var stream = TokenStream.from(elements);
		var rv = parse(stream).collect(ArrayList::new);
		if(!stream.empty()){
			throw new IllegalArgumentException();
		}
		return rv;
	}

	default T parse(S s){
		return parse(List.of(s)).get(0);
	}

	default <D extends Token> Parser<S, D> andThen(Parser<T, D> next){
		final var wrapped = this;
		return tokens -> next.parse(wrapped.parse(tokens));
	}
}