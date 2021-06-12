package majel.lang.util;

import majel.lang.err.IllegalToken;
import majel.stream.Token;
import majel.stream.Token$Char;
import majel.util.MathUtils;

import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface TokenStream_Obj<T> extends TokenStream, Iterable<T>{

	static <T> TokenStream_Obj<T> emptyStream(){
		return new TokenStream_Obj<T>(){
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

	static <T> TokenStream_Obj<T> lazy(Supplier<TokenStream_Obj<T>> source){
		return new TokenStream_Obj<T>(){
			TokenStream_Obj<T> wrapped;
			@Override
			public T poll(){
				return wrapped.poll();
			}

			@Override
			public boolean touched(){
				return wrapped != null && wrapped.touched();
			}

			TokenStream_Obj<T> wrapped(){
				if(wrapped == null){
					wrapped = source.get();
				}
				return wrapped;
			}

			@Override
			public boolean empty(){
				return wrapped().empty();
			}

			@Override
			public Mark mark(){
				return wrapped().mark();
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


	default TokenStream_Obj<T> andThen(Supplier<TokenStream_Obj<T>> continuation){
		return andThen(lazy(continuation));
	}

	default TokenStream_Obj<T> andThen(TokenStream_Obj<T> continuation){
		return concat(from(this, continuation));
	}

	static TokenStream_Obj<Token$Char> from(String s){
		return TokenStream_Char.from(s).wrap();
	}

	static TokenStream_Obj<Token$Char> from(InputStream stream){
		return TokenStream_Char.of(stream).wrap();
	}

	static <T extends Token> TokenStream_Obj<T> from(Supplier<T> supplier){
		return new TokenStream_Obj<T>(){
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
	static <T> TokenStream_Obj<T> from(T... tokens){
		return new TokenStream_Obj<T>(){
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
				return !TokenStream_Obj.this.empty();
			}

			@Override
			public T next(){
				return TokenStream_Obj.this.poll();
			}
		};
	}

	default TokenStream_Obj<IndexedToken<T>> indexed(){
		return new TokenStream_Obj<>(){
			int index;

			@Override
			public IndexedToken<T> peek(){
				return new IndexedToken<>(TokenStream_Obj.this.peek(), index);
			}

			@Override
			public IndexedToken<T> poll(){
				return new IndexedToken<>(TokenStream_Obj.this.poll(), index++);
			}

			@Override
			public boolean touched(){
				return TokenStream_Obj.this.touched();
			}

			@Override
			public boolean empty(){
				return TokenStream_Obj.this.empty();
			}

			@Override
			public Mark mark(){
				final int m0 = index;
				final var m1 = TokenStream_Obj.this.mark();
				return () -> {
					index = m0;
					m1.reset();
				};
			}
		};
	}
	default <D> TokenStream_Obj<D> map(Function<T, D> mapper){
		var wrapped = this;
		return new TokenStream_Obj<>(){
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

	static <T> TokenStream_Obj<T> concat(TokenStream_Obj<TokenStream_Obj<T>> elements){
		var unbuffered = new TokenStream_Obj<T>(){
			boolean touched;
			TokenStream_Obj<T> current = emptyStream();

			TokenStream_Obj<T> current(){
				while(!elements.empty() && current.empty()){
					current = elements.poll();
				}
				return current;
			}

			@Override
			public T peek(){
				return current().peek();
			}

			@Override
			public T poll(){
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
				throw new UnsupportedOperationException();
			}
		};
		return unbuffered.buffered();
	}

	static <T> TokenStream_Obj<T> from(Iterable<T> iterable){
		var iterator = iterable.iterator();
		return new TokenStream_Obj<T>(){
			boolean touched = false;
			@Override
			public T poll(){
				touched = true;
				return iterator.next();
			}

			@Override
			public boolean touched(){
				return touched;
			}

			@Override
			public boolean empty(){
				return !iterator.hasNext();
			}

			@Override
			public Mark mark(){
				throw new UnsupportedOperationException();
			}
		};
	}

	static <T> TokenStream_Obj<T> from(List<T> elements){
		return new TokenStream_Obj<T>(){
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

	default TokenStream_Obj<T> incorporate(TokenStream_Obj<IndexedToken<T>> stream){
		return new TokenStream_Obj<T>(){
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
					rv = TokenStream_Obj.this.poll();
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
				return TokenStream_Obj.this.empty() && stream.empty();
			}

			@Override
			public Mark mark(){
				var m0 = TokenStream_Obj.this.mark();
				var m1 = stream.mark();
				return () -> {
					m0.reset();
					m1.reset();
				};
			}
		};
	}

	default TokenStream_Obj<T> limit(int maxSize){
		return new TokenStream_Obj<T>(){
			int size;
			@Override
			public T poll(){
				return TokenStream_Obj.this.poll();
			}

			@Override
			public boolean touched(){
				return TokenStream_Obj.this.touched();
			}

			@Override
			public boolean empty(){
				return size >= maxSize || TokenStream_Obj.this.empty();
			}

			@Override
			public Mark mark(){
				return TokenStream_Obj.this.mark();
			}
		};
	}
	default TokenStream_Obj<T> retain(Predicate<T> predicate){
		return retain(predicate, t -> {});
	}


	default TokenStream_Obj<T> exclude(
		int lookahead,
		Predicate<TokenStream_Obj<T>> predicate,
		Consumer<IndexedToken<T>> sink
	){
		return retain(lookahead, predicate.negate(), sink);
	}

	default TokenStream_Obj<T> retain(
		int lookahead,
		Predicate<TokenStream_Obj<T>> predicate,
		Consumer<IndexedToken<T>> sink
	){
		return new TokenStream_Obj<T>(){
			int index;
			int sinkIndex;

			private void findNext(){
				for(;;){
					if(TokenStream_Obj.this.empty()){
						return;
					}
					var mark = TokenStream_Obj.this.mark();
					var substream = TokenStream_Obj.this.limit(lookahead);
					boolean include = predicate.test(substream);
					mark.reset();
					if(include){
						index += 1;
						break;
					}
					var token = TokenStream_Obj.this.poll();
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
				return TokenStream_Obj.this.peek();
			}

			@Override
			public T poll(){
				findNext();
				return TokenStream_Obj.this.poll();
			}

			@Override
			public boolean touched(){
				return index != 0;
			}

			@Override
			public boolean empty(){
				findNext();
				return TokenStream_Obj.this.empty();
			}

			@Override
			public Mark mark(){
				var i = index;
				var m = TokenStream_Obj.this.mark();

				return () -> {
					index = i;
					m.reset();
				};
			}
		};
	}

	default TokenStream_Obj<T> retain(Predicate<T> predicate, Consumer<IndexedToken<T>> sink){
		return new TokenStream_Obj<T>(){
			int index;
			int sinkIndex;

			private void findNext(){
				for(;;){
					if(TokenStream_Obj.this.empty()){
						return;
					}
					var mark = TokenStream_Obj.this.mark();
					var token = TokenStream_Obj.this.poll();
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
				return TokenStream_Obj.this.peek();
			}

			@Override
			public T poll(){
				findNext();
				return TokenStream_Obj.this.poll();
			}

			@Override
			public boolean touched(){
				return index != 0;
			}

			@Override
			public boolean empty(){
				findNext();
				return TokenStream_Obj.this.empty();
			}

			@Override
			public Mark mark(){
				var i = index;
				var m = TokenStream_Obj.this.mark();

				return () -> {
					index = i;
					m.reset();
				};
			}
		};
	}

	default <D extends Token> TokenStream_Obj<D> polymap(Function<TokenStream_Obj<T>, D> mapper){
		return new TokenStream_Obj<D>(){
			@Override
			public D poll(){
				return mapper.apply(TokenStream_Obj.this);
			}

			@Override
			public boolean touched(){
				return TokenStream_Obj.this.touched();
			}

			@Override
			public boolean empty(){
				return TokenStream_Obj.this.empty();
			}

			@Override
			public Mark mark(){
				return TokenStream_Obj.this.mark();
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

	default TokenStream_Obj<T> until(Predicate<T> terminator){
		return new TokenStream_Obj<T>(){
			@Override
			public T poll(){
				return TokenStream_Obj.this.poll();
			}

			@Override
			public boolean touched(){
				return TokenStream_Obj.this.touched();
			}

			@Override
			public boolean empty(){
				return TokenStream_Obj.this.empty() || terminator.test(TokenStream_Obj.this.peek());
			}

			@Override
			public Mark mark(){
				return TokenStream_Obj.this.mark();
			}
		};
	}

	default TokenStream_Obj<T> buffered(){
		return buffered(256);
	}
	default TokenStream_Obj<T> buffered(int maxBuffer){
		class Buffer{
			final T[] elements = (T[])new Object[MathUtils.nextPowerOfTwo(maxBuffer)];
			int offset;
			int size;

			private void add(T t){
				elements[offset] = t;
				offset = offsetIndex(1);
				if(size != elements.length){
					size += 1;
				}
			}

			public int capacity(){
				return elements.length;
			}

			public T get(int index){
				return elements[offsetIndex(index)];
			}

			private int offsetIndex(int index){
				return (offset + index) & (elements.length - 1);
			}
		}
		final var buffer = new Buffer();
		return new TokenStream_Obj<T>(){
			int srcIndex;
			int index;

			@Override
			public T poll(){
				final T rv;
				if(index == srcIndex){
					rv = TokenStream_Obj.this.poll();
					buffer.add(rv);
					srcIndex += 1;
				}
				else{
					int delta = srcIndex - index;
					int bufferIndex = buffer.capacity() - delta;
					rv = buffer.get(bufferIndex);
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
				return index == srcIndex && TokenStream_Obj.this.empty();
			}

			@Override
			public Mark mark(){
				int mark = index;
				return () -> {
					if(mark < srcIndex - buffer.capacity()){
						throw new IllegalStateException();
					}
					index = mark;
				};
			}
		};
	}

	default <D> TokenStream_Obj<D> flatMap(Function<T, TokenStream_Obj<D>> mapper){
		return concat(map(mapper));
	}

}
