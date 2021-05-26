package majel.lang.util;

import majel.lang.err.IllegalToken;
import majel.util.functional.CharPredicate;

import java.util.ArrayList;
import java.util.function.Consumer;

public interface TokenStream{
	static TokenStream from(String value){
		return TokenStream.of(value.toCharArray());
	}

	static TokenStream of(char...tokens){

		return new TokenStream(){
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

	char peek();
	char poll();
	boolean empty();

	interface Mark{
		void reset();
	}

	Mark mark();

	default char read(CharPredicate predicate){
		var token = poll();
		if(!predicate.test(token)){
			throw new IllegalToken(this);
		}
		return token;
	}

	default String readWhile(CharPredicate predicate){
		var builder = new StringBuilder();
		while(!empty() && predicate.test(peek())){
			builder.append(poll());
		}
		return builder.toString();
	}

	default void read(char expected){
		read(c -> c == expected);
	}

	default void read(String expected){
		for(char c : expected.toCharArray()){
			read(c);
		}
	}

	default String remaining(){
		var mark = mark();
		var builder = new StringBuilder();
		while(!empty()){
			builder.append(poll());
		}
		mark.reset();
		return builder.toString();
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
