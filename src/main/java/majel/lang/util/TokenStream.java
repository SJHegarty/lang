package majel.lang.util;

import majel.lang.automata.fsa.FSA;
import majel.lang.err.IllegalToken;
import majel.stream.SimpleToken;
import majel.stream.Token;
import majel.util.functional.CharPredicate;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface TokenStream<T extends Token> extends Iterable<T>{

	static <T extends Token> TokenStream<T> emptyStream(){
		return new TokenStream<T>(){
			@Override
			public T peek(){
				throw new UnsupportedOperationException();
			}

			@Override
			public T poll(){
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean empty(){
				return true;
			}

			@Override
			public Mark mark(){
				return () -> {};
			}
		};
	}
	T peek();
	T poll();
	boolean empty();
	Mark mark();

	default T read(Predicate<T> predicate){
		var token = poll();
		if(!predicate.test(token)){
			throw new IllegalToken(this);
		}
		return token;
	}

	default List<T> readWhile(Predicate<T> predicate){
		var results = new ArrayList<T>();
		while(!empty()){
			var mark = mark();
			var next = poll();
			if(predicate.test(next)){
				results.add(next);
			}
			else{
				mark.reset();
				break;
			}
		}
		return Collections.unmodifiableList(results);
	}

	default void read(T expected){
		read(t -> t.equals(expected));
	}

	default TokenStream<T> concat(Supplier<TokenStream<T>> continuation){
		final var wrapped = this;
		return new TokenStream<T>(){
			TokenStream<T> built;

			TokenStream<T> source(){
				if(built == null){
					if(wrapped.empty()){
						built = continuation.get();
						if(built == null){
							throw new IllegalStateException();
						}
					}
					else{
						return wrapped;
					}
				}
				return built;
			}

			@Override
			public T peek(){
				return source().peek();
			}

			@Override
			public T poll(){
				return source().poll();
			}

			@Override
			public boolean empty(){
				return source().empty();
			}

			@Override
			public Mark mark(){
				return source().mark();
			}
		};
	}

	static <T extends Token> TokenStream<T> from(Supplier<T> supplier){
		return new TokenStream<T>(){
			T next = supplier.get();
			@Override
			public T peek(){
				return next;
			}

			@Override
			public T poll(){
				T rv = next;
				next = supplier.get();
				return rv;
			}

			@Override
			public boolean empty(){
				return next == null;
			}

			@Override
			public Mark mark(){
				throw new UnsupportedOperationException();
			}
		};
	}
	static <T extends Token> TokenStream<T> of(T... tokens){
		return new TokenStream<T>(){
			int index;
			@Override
			public T peek(){
				return tokens[index];
			}

			@Override
			public T poll(){
				return tokens[index++];
			}

			@Override
			public boolean empty(){
				return index >= tokens.length;
			}

			@Override
			public Mark mark(){
				final int mark = index;
				return () -> index = mark;
			}
		};
	}

	@Override
	default Iterator<T> iterator(){
		return new Iterator<>(){
			@Override
			public boolean hasNext(){
				return !TokenStream.this.empty();
			}

			@Override
			public T next(){
				return TokenStream.this.poll();
			}
		};
	}

	default <D extends Token> TokenStream<D> map(Function<T, D> mapper){
		var wrapped = this;
		return new TokenStream<D>(){
			@Override
			public D peek(){
				return mapper.apply(wrapped.peek());
			}

			@Override
			public D poll(){
				return mapper.apply(wrapped.poll());
			}

			@Override
			public boolean empty(){
				return wrapped.empty();
			}

			@Override
			public Mark mark(){
				return wrapped.mark();
			}
		};
	}

	default <C extends Collection<T>> C collect(Supplier<C> builder){
		var rv = builder.get();
		for(var t: this){
			rv.add(t);
		}
		return rv;
	}

	default <D extends Token> TokenStream<D> unwrap(Function<T, TokenStream<D>> unwrapper){
		return new TokenStream<D>(){
			TokenStream<D> current = emptyStream();

			TokenStream<D> current(){
				if(current.empty()){
					if(!TokenStream.this.empty()){
						current = unwrapper.apply(TokenStream.this.poll());
					}
				}
				return current;
			}

			@Override
			public D peek(){
				return current().peek();
			}

			@Override
			public D poll(){
				return current().poll();
			}

			@Override
			public boolean empty(){
				return current().empty();
			}

			@Override
			public Mark mark(){
				final var stream = current();
				final var streamMark = stream.mark();
				final var wrappedMark = TokenStream.this.mark();
				return () -> {
					current = stream;
					streamMark.reset();
					wrappedMark.reset();
				};
			}
		};
	}

	static <T extends Token> TokenStream<T> from(List<T> elements){
		return new TokenStream<T>(){
			int index;
			@Override
			public T peek(){
				return elements.get(index);
			}

			@Override
			public T poll(){
				return elements.get(index++);
			}

			@Override
			public boolean empty(){
				return index >= elements.size();
			}

			@Override
			public Mark mark(){
				final int mark = index;
				return () -> index = mark;
			}
		};
	}
}
