package majel.lang.util;

import majel.stream.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Pipe<S extends Token, T extends Token>{

	static <S extends Token> Pipe<S, S> nop(){
		return tokens -> tokens;
	}

	TokenStream<T> parse(TokenStream<S> tokens);

	default T parseSingle(TokenStream<S> tokens){
		var stream = parse(tokens);
		var result = stream.poll();
		if(!stream.empty()){
			throw new IllegalStateException();
		}
		return result;
	}

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

	default <D extends Token> Pipe<S, D> andThen(Pipe<T, D> next){
		final var wrapped = this;
		return tokens -> next.parse(wrapped.parse(tokens));
	}


	default Pipe<S, T> retain(Predicate<T> filter){
		return retain(filter, dropped -> {});
	}

	default Pipe<S, T> exclude(Predicate<T> filter){
		return retain(filter.negate());
	}

	default Pipe<S, T> exclude(Predicate<T> filter, Consumer<IndexedToken<T>> sink){
		return retain(filter.negate(), sink);
	}

	default <D extends Token> Pipe<S, D> map(Function<T, D> mapper){
		return tokens -> Pipe.this.parse(tokens).map(mapper);
	}

	default <D extends Token> Pipe<S, D> polymap(Function<TokenStream<T>, D> mapper){
		return tokens -> Pipe.this.parse(tokens).polymap(mapper);
	}

	default Pipe<S, T> retain(Predicate<T> filter, Consumer<IndexedToken<T>> sink){
		return tokens -> Pipe.this
			.parse(tokens)
			.retain(filter, sink);
	}
}