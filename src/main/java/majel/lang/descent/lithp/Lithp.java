package majel.lang.descent.lithp;

import majel.lang.automata.fsa.FSA;
import majel.lang.automata.fsa.StringProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class Lithp{
	public static void main(String...args){
		var expressions = new TreeSet<>(
			List.of(
				new Expression("test", "*('abacus''...')"),
				new Expression("dunna dunna dunna dunna", "'batman'"),
				new Expression("gone hungry", "!'batman'")
			)
		);
		var lithp = new Lithp(expressions);
		var samples = new String[]{
			"'a'",
			"",
			"abacus...",
			"abacus...abacus...",
			"batman",
			"'bZ'"
		};
		for(var s: samples){
			var result = lithp.parser.process(s);
			System.err.println(result.terminating() + " " + result.labels() + " " + s);
		}
	}

	record Expression(String label, String expression) implements Comparable<Expression>{
		@Override
		public int compareTo(Expression o) {
			return label.compareTo(o.label);
		}
	}

	final StringProcessor parser;

	public Lithp(SortedSet<Expression> expressions){
		parser = new StringProcessor(
			FSA.or(
				expressions.stream()
					.map(Lithp::parse)
					.toArray(FSA[]::new)
			)
		);
	}

	static class TokenStream{
		final char[] tokens;
		int index;

		TokenStream(String expression){
			this.tokens = expression.toCharArray();
		}

		char peek(){
			if(empty()){
				throw new IllegalEndOfStream();
			}
			return tokens[index];
		}

		char poll(){
			char rv = peek();
			index++;
			return rv;
		}

		public boolean empty(){
			return index >= tokens.length;
		}
	}

	static FSA parse(Expression e){
		return parse(e.label, new TokenStream(e.expression));
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

	static FSA parseWhile(String label, TokenStream tokens, BooleanSupplier terminator){
		var elements = new ArrayList<FSA>();
		while(terminator.getAsBoolean()){
			elements.add(parseSingle(tokens));
		}
		return FSA.concatenate(label, elements.toArray(FSA[]::new));
	}

	static FSA parse(String label, TokenStream tokens){
		return parseWhile(label, tokens, () -> !tokens.empty());
	}

	static FSA parseSingle(TokenStream tokens){
		return switch(tokens.peek()){
			case '\'' -> parseLiteral(tokens);
			case '*' -> parseKleene(tokens);
			case '!' -> parseNegation(tokens);
			case '(' -> parseParenthesis(tokens);
			default -> throw new IllegalToken(tokens.peek());
		};
	}
	static void expect(char token, char expected){
		if(token != expected){
			throw new IllegalToken(token);
		}
	}

	static FSA parseKleene(TokenStream tokens){
		expect(tokens.poll(), '*');
		return parseSingle(tokens).kleene();
	}

	static FSA parseNegation(TokenStream tokens){
		expect(tokens.poll(), '!');
		return parseSingle(tokens).negate();
	}

	static FSA parseParenthesis(TokenStream tokens){
		expect(tokens.poll(), '(');
		var rv = parseWhile(null, tokens, () -> tokens.peek() != ')');
		tokens.poll();
		return rv;
	}

	static FSA parseLiteral(TokenStream tokens){
		expect(tokens.poll(), '\'');
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
