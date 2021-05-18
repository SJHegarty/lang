package majel.lang.descent.lithp;

import majel.lang.automata.fsa.FSA;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Lithp{
	public static void main(String...args){
		var expressions = new TreeSet<>(
			List.of(
				new Expression("test", "'a'")
			)
		);
	}

	record Expression(String label, String expression) implements Comparable<Expression>{
		@Override
		public int compareTo(Expression o) {
			return label.compareTo(o.label);
		}
	}

	final FSA parser;

	public Lithp(SortedSet<Expression> expressions){
		parser = FSA.or(
			expressions.stream()
				.map(Lithp::parse)
				.toArray(FSA[]::new)
		)
		.dfa();
	}

	class TokenStream{
		final char[] tokens;
		int index;

		TokenStream(String expression){
			this.tokens = expression.toCharArray();
		}

		char peek(){
			if(index >= tokens.length){
				throw new IllegalEndOfStream();
			}
			return tokens[index];
		}

		char poll(){
			char rv = peek();
			index++;
			return rv;
		}
	}

	static FSA parse(Expression e){
		return null;
	}

	static class ParseException extends RuntimeException{
		ParseException(){}

		ParseException(String message){
			super(message);
		}
	}

	static class IllegalToken extends ParseException{
		public IllegalToken(char c){
			super("" + c);
		}
	}

	static class IllegalEndOfStream extends ParseException{
		public IllegalEndOfStream(){
			super();
		}
	}

	static FSA parse(TokenStream tokens){
		return switch(tokens.peek()){
			case '\'' -> parseLiteral(tokens);
			default -> throw new IllegalToken(tokens.peek());
		};
	}

	static FSA parseLiteral(TokenStream tokens){
		{
			char token = tokens.poll();
			if(token != '\''){
				throw new IllegalToken(token);
			}
		}
		var builder = new StringBuilder();
		outer:for(;;){
			char token = tokens.poll();
			switch(token){
				case '\'' -> {
					break outer;
				}
				case '\\' -> {
					token = tokens.poll();
					builder.append(
						switch(token){
							case 't' -> '\t';
							case 'n' -> '\n';
							case '\\' -> '\\';
							default -> throw new IllegalToken(token);
						}
					);
				}
				default -> {
					builder.append(token);
				}
			}
		}
		return FSA.literal(null, builder.toString());
	}

}
