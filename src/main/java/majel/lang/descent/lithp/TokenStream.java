package majel.lang.descent.lithp;

import majel.util.functional.CharPredicate;

public class TokenStream{
	private final char[] tokens;
	private int index;
	private int mark;

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

	public void mark(){
		mark = index;
	}

	public void reset(){
		index = mark;
	}

	public String expression(){
		return new String(tokens);
	}

	public int index(){
		return index;
	}

}
