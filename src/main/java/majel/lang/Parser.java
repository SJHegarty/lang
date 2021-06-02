package majel.lang;

import majel.lang.util.IndexedToken;
import majel.lang.util.TokenStream;
import majel.stream.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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

	static <S extends Token> Parser<S, S> empty(){
		return tokens -> tokens;
	}

	default Parser<S, T> retain(Predicate<T> filter){
		return retain(filter, dropped -> {});
	}

	default Parser<S, T> exclude(Predicate<T> filter){
		return retain(filter.negate());
	}

	default Parser<S, T> exclude(Predicate<T> filter, Consumer<IndexedToken<T>> sink){
		return retain(filter.negate(), sink);
	}

	default <D extends Token> Parser<S, D> map(Function<T, D> mapper){
		return tokens -> Parser.this.parse(tokens).map(mapper);
	}

	default Parser<S, T> retain(Predicate<T> filter, Consumer<IndexedToken<T>> sink){
		return tokens -> Parser.this
			.parse(tokens)
			.retain(filter, sink);
	}
}