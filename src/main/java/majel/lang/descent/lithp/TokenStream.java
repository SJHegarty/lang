package majel.lang.descent.lithp;

import majel.util.functional.CharPredicate;

import java.util.List;

public class TokenStream<T>{
	private final RecursiveDescentContext<T> context;
	private final char[] tokens;
	private int index;
	private int mark;

	public TokenStream(
		RecursiveDescentContext<T> context,
		String expression
	){
		this.context = context;
		this.tokens = expression.toCharArray();
	}

	public char peek(){
		if(empty()){
			throw new RecursiveDescentParser.IllegalEndOfStream();
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
			throw new RecursiveDescentParser.IllegalToken(this);
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

	public RecursiveDescentContext<T> context(){
		return context;
	}

	public RecursiveDescentParser<T> parser(){
		return context.parser();
	}

	public T parse(){
		return parser().parse(this);
	}

	public List<T> parseList(){
		return parser().parseList(this);
	}
}
