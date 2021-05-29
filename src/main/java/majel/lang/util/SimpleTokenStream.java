package majel.lang.util;

import majel.lang.err.IllegalToken;
import majel.stream.SimpleToken;
import majel.util.functional.CharConsumer;
import majel.util.functional.CharPredicate;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.function.Consumer;

public interface SimpleTokenStream{

	char peek();
	char poll();
	boolean empty();
	Mark mark();

	default void drain(CharConsumer consumer){
		while(!empty()){
			consumer.consume(poll());
		}
	}

	static SimpleTokenStream from(String value){
		return of(value.toCharArray());
	}

	static SimpleTokenStream of(char...tokens){

		return new SimpleTokenStream(){
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


	static SimpleTokenStream of(InputStream stream){

		return new SimpleTokenStream(){
			char next = 0xffff;
			@Override
			public char peek(){
				if(next == 0xffff){
					try{
						final int read = stream.read();
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

			@Override
			public char poll(){
				var rv = peek();
				next = 0xffff;
				return rv;
			}

			@Override
			public boolean empty(){
				try{
					return next == 0xffff && stream.available() == 0;
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

	static SimpleTokenStream of(TokenStream<SimpleToken> stream){
		if(stream instanceof WrappedTokenStream wrapped){
			return wrapped.wrapped;
		}
		return new WrappedSimpleTokenStream(stream);
	}

	class WrappedTokenStream implements TokenStream<SimpleToken>{
		final SimpleTokenStream wrapped;
		WrappedTokenStream(SimpleTokenStream wrapped){
			this.wrapped = wrapped;
		}

		@Override
		public SimpleToken peek(){
			return new SimpleToken(wrapped.peek());
		}

		@Override
		public SimpleToken poll(){
			return new SimpleToken(wrapped.poll());
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

	class WrappedSimpleTokenStream implements SimpleTokenStream{
		final TokenStream<SimpleToken> wrapped;

		public WrappedSimpleTokenStream(TokenStream<SimpleToken> wrapped){
			this.wrapped = wrapped;
		}

		@Override
		public char peek(){
			return wrapped.peek().character();
		}

		@Override
		public char poll(){
			return wrapped.poll().character();
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

	default TokenStream<SimpleToken> wrap(){
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
}
