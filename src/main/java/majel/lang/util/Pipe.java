package majel.lang.util;

import majel.stream.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Pipe<Context, S extends Token, T extends Token>{

	static <Context, S extends Token> Pipe<Context, S, S> nop(){
		return (context, tokens) -> tokens;
	}

	TokenStream_Obj<T> parse(Context context, TokenStream_Obj<S> tokens);

	default T parseSingle(Context context, TokenStream_Obj<S> tokens){
		var stream = parse(context, tokens);
		var result = stream.poll();
		if(!stream.empty()){
			throw new IllegalStateException();
		}
		return result;
	}

	default List<T> parse(Context context, List<S> elements){
		var stream = TokenStream_Obj.from(elements);
		var rv = parse(context, stream).collect(ArrayList::new);
		if(!stream.empty()){
			throw new IllegalArgumentException();
		}
		return rv;
	}

	default T parse(Context context, S s){
		return parse(context, List.of(s)).get(0);
	}

	default <D extends Token> Pipe<Context, S, D> andThen(Pipe<Context, T, D> next){
		final var wrapped = this;
		return (context, tokens) -> next.parse(context, wrapped.parse(context, tokens));
	}


	default Pipe<Context, S, T> retain(Predicate<T> filter){
		return retain(filter, dropped -> {});
	}

	default Pipe<Context, S, T> exclude(Predicate<T> filter){
		return retain(filter.negate());
	}


	default Pipe<Context, S, T> exclude(
		int lookahead,
		Predicate<TokenStream_Obj<T>> filter
	){
		return exclude(lookahead, filter, t -> {});
	}

	default Pipe<Context, S, T> exclude(
		int lookahead,
		Predicate<TokenStream_Obj<T>> filter,
		Consumer<IndexedToken<T>> sink
	){
		return new Pipe<Context, S, T>(){
			@Override
			public TokenStream_Obj<T> parse(Context context, TokenStream_Obj<S> tokens){
				return Pipe.this.parse(context, tokens).exclude(lookahead, filter, sink);
			}
		};
	}
	default Pipe<Context, S, T> exclude(Predicate<T> filter, Consumer<IndexedToken<T>> sink){
		return retain(filter.negate(), sink);
	}

	default <D extends Token> Pipe<Context, S, D> map(Function<T, D> mapper){
		return (context, tokens) -> Pipe.this.parse(context, tokens).map(mapper);
	}

	default <D extends Token> Pipe<Context, S, D> polymap(Function<TokenStream_Obj<T>, D> mapper){
		return (context, tokens) -> Pipe.this.parse(context, tokens).polymap(mapper);
	}

	default Pipe<Context, S, T> retain(Predicate<T> filter, Consumer<IndexedToken<T>> sink){
		return (context, tokens) -> Pipe.this
			.parse(context, tokens)
			.retain(filter, sink);
	}

	default Pipe<Context, S, T> buffered(){
		return new Pipe<>(){
			@Override
			public TokenStream_Obj<T> parse(Context context, TokenStream_Obj<S> tokens){
				return Pipe.this.parse(context, tokens.buffered());
			}

			@Override
			public Pipe<Context, S, T> buffered(){
				return this;
			}

			@Override
			public <D extends Token> Pipe<Context, S, D> andThen(Pipe<Context, T, D> subsequent){
				return Pipe.super.andThen(subsequent.buffered());
			}
		};
	}
}