package majel.lang.util;

import majel.lang.err.IllegalToken;
import majel.stream.Token$Char;
import majel.util.functional.CharConsumer;
import majel.util.functional.CharPredicate;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.function.Consumer;

public interface TokenStream$Char{

	default char peek(){
		var mark = mark();
		var rv = poll();
		mark.reset();
		return rv;
	}

	char poll();
	boolean touched();
	boolean empty();
	Mark mark();

	default void drain(CharConsumer consumer){
		while(!empty()){
			consumer.consume(poll());
		}
	}

	static TokenStream$Char from(String value){
		return of(value.toCharArray());
	}

	static TokenStream$Char of(byte...tokens){
		return new TokenStream$Char(){
			private int index;

			@Override
			public char peek(){
				return (char)tokens[index];
			}

			@Override
			public char poll(){
				return (char)tokens[index++];
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
	static TokenStream$Char of(char...tokens){

		return new TokenStream$Char(){
			private int index;

			@Override
			public char peek(){
				return tokens[index];
			}

			@Override
			public char poll(){
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


	static TokenStream$Char of(InputStream stream){
		var buffered = new BufferedInputStream(stream);
		return new TokenStream$Char(){
			char next = 0xffff;
			@Override
			public char peek(){
				if(next == 0xffff){
					try{
						final int read = buffered.read();
						if((read & 0xffffff00) != 0){
							throw new IllegalStateException();
						}
						next = (char)read;
					}
					catch(IOException e){
						throw new UncheckedIOException(e);
					}
				}
				return next;
			}

			boolean touched;
			@Override
			public char poll(){
				var rv = peek();
				next = 0xffff;
				touched = true;
				return rv;
			}

			@Override
			public boolean touched(){
				return touched;
			}

			@Override
			public boolean empty(){
				try{
					return next == 0xffff && buffered.available() == 0;
				}
				catch(IOException e){
					throw new UncheckedIOException(e);
				}
			}

			@Override
			public Mark mark(){
				throw new UnsupportedOperationException();
			}
		};
	}
	static TokenStream$Char of(TokenStream<Token$Char> stream){
		if(stream instanceof WrappedTokenStream wrapped){
			return wrapped.wrapped;
		}
		return new WrappedSimpleTokenStream(stream);
	}

	class WrappedTokenStream implements TokenStream<Token$Char>{
		final TokenStream$Char wrapped;
		WrappedTokenStream(TokenStream$Char wrapped){
			this.wrapped = wrapped;
		}

		@Override
		public Token$Char peek(){
			return new Token$Char(wrapped.peek());
		}

		@Override
		public Token$Char poll(){
			return new Token$Char(wrapped.poll());
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
	}

	class WrappedSimpleTokenStream implements TokenStream$Char{
		final TokenStream<Token$Char> wrapped;

		public WrappedSimpleTokenStream(TokenStream<Token$Char> wrapped){
			this.wrapped = wrapped;
		}

		@Override
		public char peek(){
			return wrapped.peek().value();
		}

		@Override
		public char poll(){
			return wrapped.poll().value();
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
	}

	default TokenStream<Token$Char> wrap(){
		return new WrappedTokenStream(this);
	}

	default String remaining(){
		var mark = mark();
		var rv = drain();
		mark.reset();
		return rv;
	}

	default String drain(){
		var builder = new StringBuilder();
		while(!empty()){
			builder.append(poll());
		}
		return builder.toString();
	}


	default char read(CharPredicate predicate){
		var token = poll();
		if(!predicate.test(token)){
			throw new IllegalToken(this.wrap());
		}
		return token;
	}

	default void read(char expected){
		read(t -> t == expected);
	}

	default void read(String expected){
		for(char c : expected.toCharArray()){
			read(c);
		}
	}

	default TokenStream$Char withHead(char c){
		return withHead(TokenStream$Char.of(c));
	}

	default TokenStream$Char withHead(TokenStream$Char head){
		return new TokenStream$Char(){

			boolean touched;
			@Override
			public char poll(){
				if(head.empty()){
					return TokenStream$Char.this.poll();
				}
				char target = head.poll();
				if(TokenStream$Char.this.peek() == target){
					return TokenStream$Char.this.poll();
				}
				touched = true;
				return target;
			}

			@Override
			public boolean touched(){
				return touched;
			}

			@Override
			public boolean empty(){
				return TokenStream$Char.this.empty() && head.empty();
			}

			@Override
			public Mark mark(){
				var m0 = TokenStream$Char.this.mark();
				var m1 = head.mark();
				var m2 = touched;
				return () -> {
					m0.reset();
					m1.reset();
					touched = m2;
				};
			}
		};
	}

	default String[] split(char separator){
		var results = new ArrayList<String>();
		Consumer<StringBuilder> addOp = builder -> {
			var built = builder.toString();
			if(built.length() != 0){
				results.add(built);
			}
		};
		outer:for(;;){
			var builder = new StringBuilder();
			for(;;){
				if(empty()){
					addOp.accept(builder);
					break outer;
				}
				char c = poll();
				if(c == separator){
					addOp.accept(builder);
					break;
				}
				builder.append(c);
			}
		}
		return results.toArray(String[]::new);
	}

	default TokenStream$Char exclude(char c){
		return retain(cn -> cn != c);
	}

	default TokenStream$Char exclude(CharPredicate predicate){
		return retain(predicate.negate());
	}
	default TokenStream$Char retain(CharPredicate predicate){
		return retain(predicate, t -> {});
	}

	default TokenStream$Char exclude(CharPredicate predicate, Consumer<IndexedChar> sink){
		return retain(predicate.negate(), sink);
	}

	default TokenStream$Char retain(CharPredicate predicate, Consumer<IndexedChar> sink){
		return new TokenStream$Char(){
			char next = 0xffff;
			int index;
			int sinkIndex;

			@Override
			public char peek(){
				while(next == 0xffff){
					if(TokenStream$Char.this.empty()){
						break;
					}
					var n = TokenStream$Char.this.poll();
					if(predicate.test(n)){
						next = n;
					}
					else if(index > sinkIndex){
						sink.accept(new IndexedChar(n, index));
						sinkIndex = index;
					}
					index++;
				}
				return next;
			}

			@Override
			public char poll(){
				var rv = peek();
				next = 0xffff;
				touched = true;
				return rv;
			}

			boolean touched;
			@Override
			public boolean touched(){
				return touched;
			}

			@Override
			public boolean empty(){
				return peek() == 0xffff;
			}

			@Override
			public Mark mark(){
				var i = index;
				var t = touched;
				var m = TokenStream$Char.this.mark();

				return () -> {
					index = i;
					m.reset();
					touched = t;
				};
			}
		};
	}

}
