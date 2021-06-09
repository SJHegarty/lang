package majel.lang.util;

import majel.lang.err.IllegalToken;
import majel.stream.Token$Char;
import majel.stream.Token;

import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface TokenStream$Obj<T extends Token> extends TokenStream, Iterable<T>{

	static <T extends Token> TokenStream$Obj<T> emptyStream(){
		return new TokenStream$Obj<T>(){
			@Override
			public T peek(){
				throw new UnsupportedOperationException();
			}

			@Override
			public T poll(){
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean touched(){
				return false;
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

	default T peek(){
		var mark = mark();
		var rv = poll();
		mark.reset();
		return rv;
	}

	T poll();

	default void indexedForEach(Consumer<IndexedToken<T>> op){
		int index = 0;
		while(!empty()){
			op.accept(new IndexedToken<>(poll(), index));
			index = index + 1;
		}
	}

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

	default TokenStream$Obj<T> concat(Supplier<TokenStream$Obj<T>> continuation){
		final var wrapped = this;
		return new TokenStream$Obj<T>(){
			boolean touched;
			TokenStream$Obj<T> built;

			TokenStream$Obj<T> source(){
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
				touched = true;
				return source().poll();
			}

			@Override
			public boolean touched(){
				return touched;
			}

			@Override
			public boolean empty(){
				return source().empty();
			}

			@Override
			public Mark mark(){
				var m0 = source().mark();
				var m1 = touched;
				return () -> {
					m0.reset();
					touched = m1;
				};
			}
		};
	}

	static TokenStream$Obj<Token$Char> from(String s){
		return TokenStream$Char.from(s).wrap();
	}

	static TokenStream$Obj<Token$Char> from(InputStream stream){
		return TokenStream$Char.of(stream).wrap();
	}

	static <T extends Token> TokenStream$Obj<T> from(Supplier<T> supplier){
		return new TokenStream$Obj<T>(){
			T next = supplier.get();
			@Override
			public T peek(){
				return next;
			}

			@Override
			public T poll(){
				T rv = next;
				touched = true;
				next = supplier.get();
				return rv;
			}

			boolean touched;
			@Override
			public boolean touched(){
				return touched;
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
	static <T extends Token> TokenStream$Obj<T> of(T... tokens){
		return new TokenStream$Obj<T>(){
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
			public boolean touched(){
				return index != 0;
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
				return !TokenStream$Obj.this.empty();
			}

			@Override
			public T next(){
				return TokenStream$Obj.this.poll();
			}
		};
	}

	default TokenStream$Obj<IndexedToken<T>> indexed(){
		return new TokenStream$Obj<>(){
			int index;

			@Override
			public IndexedToken<T> peek(){
				return new IndexedToken<>(TokenStream$Obj.this.peek(), index);
			}

			@Override
			public IndexedToken<T> poll(){
				return new IndexedToken<>(TokenStream$Obj.this.poll(), index++);
			}

			@Override
			public boolean touched(){
				return TokenStream$Obj.this.touched();
			}

			@Override
			public boolean empty(){
				return TokenStream$Obj.this.empty();
			}

			@Override
			public Mark mark(){
				final int m0 = index;
				final var m1 = TokenStream$Obj.this.mark();
				return () -> {
					index = m0;
					m1.reset();
				};
			}
		};
	}
	default <D extends Token> TokenStream$Obj<D> map(Function<T, D> mapper){
		var wrapped = this;
		return new TokenStream$Obj<D>(){
			@Override
			public D peek(){
				return mapper.apply(wrapped.peek());
			}

			@Override
			public D poll(){
				return mapper.apply(wrapped.poll());
			}

			@Override
			public boolean touched(){
				return wrapped.touched();
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

	default <D extends Token> TokenStream$Obj<D> unwrap(Function<T, TokenStream$Obj<D>> unwrapper){
		return new TokenStream$Obj<D>(){
			boolean touched;
			TokenStream$Obj<D> current = emptyStream();

			TokenStream$Obj<D> current(){
				if(current.empty()){
					if(!TokenStream$Obj.this.empty()){
						current = unwrapper.apply(TokenStream$Obj.this.poll());
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
				touched = true;
				return current().poll();
			}

			@Override
			public boolean touched(){
				return touched;
			}

			@Override
			public boolean empty(){
				return current().empty();
			}

			@Override
			public Mark mark(){
				final var touched = this.touched;
				final var stream = current();
				final var streamMark = stream.mark();
				final var wrappedMark = TokenStream$Obj.this.mark();
				return () -> {
					current = stream;
					streamMark.reset();
					wrappedMark.reset();
					this.touched = touched;
				};
			}
		};
	}

	static <T extends Token> TokenStream$Obj<T> from(List<T> elements){
		return new TokenStream$Obj<T>(){
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
			public boolean touched(){
				return index != 0;
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

	default TokenStream$Obj<T> incorporate(TokenStream$Obj<IndexedToken<T>> stream){
		return new TokenStream$Obj<T>(){
			int index;

			@Override
			public T peek(){
				var mark = mark();
				var rv = poll();
				mark.reset();
				return rv;
			}

			@Override
			public T poll(){
				final T rv;
				block:{
					if(!stream.empty()){
						var peek = stream.peek();
						if(peek.index() < index){
							throw new IllegalStateException();
						}
						if(peek.index() == index){
							stream.poll();
							rv = peek.token();
							break block;
						}
					}
					rv = TokenStream$Obj.this.poll();
				}
				index += 1;
				return rv;
			}

			@Override
			public boolean touched(){
				return index != 0;
			}

			@Override
			public boolean empty(){
				return TokenStream$Obj.this.empty() && stream.empty();
			}

			@Override
			public Mark mark(){
				var m0 = TokenStream$Obj.this.mark();
				var m1 = stream.mark();
				return () -> {
					m0.reset();
					m1.reset();
				};
			}
		};
	}

	default TokenStream$Obj<T> retain(Predicate<T> predicate){
		return retain(predicate, t -> {});
	}

	default TokenStream$Obj<T> retain(Predicate<T> predicate, Consumer<IndexedToken<T>> sink){
		return new TokenStream$Obj<T>(){
			int index;
			int sinkIndex;

			private void findNext(){
				if(TokenStream$Obj.this.empty()){
					return;
				}
				for(;;){
					var mark = TokenStream$Obj.this.mark();
					var token = TokenStream$Obj.this.poll();
					if(predicate.test(token)){
						mark.reset();
						index += 1;
						break;
					}
					if(index > sinkIndex){
						sink.accept(new IndexedToken<>(token, index));
						sinkIndex = index;
					}
					index += 1;
				}
			}
			@Override
			public T peek(){
				findNext();
				return TokenStream$Obj.this.peek();
			}

			@Override
			public T poll(){
				findNext();
				return TokenStream$Obj.this.poll();
			}

			@Override
			public boolean touched(){
				return index != 0;
			}

			@Override
			public boolean empty(){
				findNext();
				return TokenStream$Obj.this.empty();
			}

			@Override
			public Mark mark(){
				var i = index;
				var m = TokenStream$Obj.this.mark();

				return () -> {
					index = i;
					m.reset();
				};
			}
		};
	}

	default <D extends Token> TokenStream$Obj<D> polymap(Function<TokenStream$Obj<T>, D> mapper){
		return new TokenStream$Obj<D>(){
			@Override
			public D poll(){
				return mapper.apply(TokenStream$Obj.this);
			}

			@Override
			public boolean touched(){
				return TokenStream$Obj.this.touched();
			}

			@Override
			public boolean empty(){
				return TokenStream$Obj.this.empty();
			}

			@Override
			public Mark mark(){
				return TokenStream$Obj.this.mark();
			}
		};
	}

	default T only(Predicate<T> predicate){
		var results = retain(predicate).collect(ArrayList::new);
		if(results.size() != 0){
			throw new IllegalStateException(results + ".size() != 1");
		}
		return results.get(0);
	}

	default TokenStream$Obj<T> until(Predicate<T> terminator){
		return new TokenStream$Obj<T>(){
			@Override
			public T poll(){
				return TokenStream$Obj.this.poll();
			}

			@Override
			public boolean touched(){
				return TokenStream$Obj.this.touched();
			}

			@Override
			public boolean empty(){
				return TokenStream$Obj.this.empty() || terminator.test(TokenStream$Obj.this.peek());
			}

			@Override
			public Mark mark(){
				return TokenStream$Obj.this.mark();
			}
		};
	}
}