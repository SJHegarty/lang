package majel.lang.util;

import majel.lang.err.IllegalEndOfStream;
import majel.lang.err.IllegalToken;
import majel.util.functional.CharPredicate;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TokenStream{
	private final char[] tokens;
	private int index;

	public TokenStream(String expression){
		this.tokens = expression.toCharArray();
	}

	public char peek(){
		if(empty()){
			throw new IllegalEndOfStream();
		}
		return tokens[index];
	}

	public char poll(){
		char rv = peek();
		index++;
		return rv;
	}

	public boolean empty(){
		return index >= tokens.length;
	}

	public void read(CharPredicate predicate){
		var token = poll();
		if(!predicate.test(token)){
			throw new IllegalToken(this);
		}
	}

	public void read(char expected){
		read(c -> c == expected);
	}

	public void read(String expected){
		for(char c : expected.toCharArray()){
			read(c);
		}
	}

	public class Mark{
		final int mark;

		public Mark(){
			this.mark = index;
		}

		public void reset(){
			index = mark;
		}
	}

	public Mark mark(){
		return new Mark();
	}

	public String expression(){
		return new String(tokens);
	}

	public String remaining(){
		return expression().substring(index);
	}
	public int index(){
		return index;
	}

	public String[] split(char separator){
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
