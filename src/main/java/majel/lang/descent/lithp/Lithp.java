package majel.lang.descent.lithp;

import majel.util.functional.CharPredicate;
import majel.lang.automata.fsa.FSA;
import majel.lang.automata.fsa.StringProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class Lithp{
	/*
	TODO:
		Language Feature:
			The layers of code.
				 There are four layers: formatting, structural-separators, structure and meaning.
				 	To the largest extent possible formatting should be kept minimal, however reversal of formatting should still be supported
				 	structural separators are language features whose format is not strictly required to be correct in order to parse correctly running software
				 		in the list: [a, b, c] the separator ", " is the preferred form - this is a matter of preference
				 			the strings "," and " " also provide adequate separation
				 			as in "[a b c] and [a,b,c], ([abc] works when the list elements are all REQUIRED to be length one, but not when they simply CAN be.)
				 				in these examples, the verbose form is not used - however formatting is consistent (but may not be across a file or project).
				 				[a b,c] and [a,b c] are also parsable, but have inconsistent formatting,
				 				Ultimately, once parsed, a, b, and c can be placed in a list,
				 					the structural separators to be discarded (the defaults can be regenerated later),
				 						and all divergencies from the default are the content of the formatting layer
				 	structure is the extracted structure of the input format.
				 	meaning is the interpretation of that structure.

	 */
	public static void main(String...args){
		var expressions = new TreeSet<>(
			List.of(
				new Expression("test", "?*('abacus''...')'sleep'"),
				new Expression("dunna dunna dunna dunna", "'batman'"),
				new Expression("gone hungry", "!'batman'"),
				new Expression("...", "*[a...z]"),
				new Expression("blah", "..."),
				new Expression("more", "*.'a'*."),
				new Expression("childhood", "+('sleep', 'batman')"),
				new Expression("foo", "-(*+([a...z], [A...Z]), 'batman')"),
				new Expression("whaver", "&(*[a...s], *[e...z])"),
				new Expression("???", "#(3...5, [a...g])"),
				new Expression("qweqwr", "#(3, [a...z])"),
				new Expression("dfgdfh", "#(4+, *.)")
			)
		);
		Lithp.parseList(new TokenStream("('sleep', 'batman')"));
		var lithp = new Lithp(expressions);
		var samples = new String[]{
			"'a'",
			"sleep",
			"abacus...sleep",
			"abacus...abacus...sleep",
			"batman",
			"'bZ'",
			"bZap",
			"foo",
			"a",
			"ab",
			"abc",
			"abcd",
			"abcde",
			"abcdef",
			"abcdefg"
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

		public void read(CharPredicate predicate){
			var token = poll();
			if(!predicate.test(token)){
				throw new IllegalToken(token);
			}
		}
		public void read(char expected){
			read(c -> c == expected);
		}

		public void read(String expected){
			for(char c: expected.toCharArray()){
				read(c);
			}
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

	static class IllegalExpression extends ParseException{
		public IllegalExpression(String expression){
			super(expression);
		}
	}

	static class IllegalToken extends ParseException{
		public IllegalToken(char c){
			super(String.format("'%s'", c));
		}

		public IllegalToken(TokenStream tokens){
			super(
				String.format(
					"Illegal token '%s' at index:%s of expression:%s",
					tokens.peek(),
					tokens.index,
					new String(tokens.tokens)
				)
			);
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
			case '&' -> parseAnd(tokens);
			case '+' -> parseOr(tokens);
			case '-' -> parseAndNot(tokens);
			case '?' -> parseOptional(tokens);
			case '.' -> parseWild(tokens);
			case '[' ->	parseRange(tokens);
			case '#' -> parseRepetition(tokens);
			default -> throw new IllegalToken(tokens.peek());
		};
	}

	static FSA[] parseList(TokenStream tokens){
		tokens.read('(');
		var list = new ArrayList<FSA>();
		for(;;){
			list.add(parseSingle(tokens));
			if(tokens.peek() == ')'){
				break;
			}
			tokens.read(", ");
		}
		tokens.poll();
		return list.toArray(FSA[]::new);
	}

	static FSA parseAnd(TokenStream tokens){
		tokens.read('&');
		return FSA.and(parseList(tokens));
	}

	static FSA parseOr(TokenStream tokens){
		tokens.read('+');
		return FSA.or(parseList(tokens));
	}

	static FSA parseAndNot(TokenStream tokens){
		tokens.read('-');
		var list = parseList(tokens);
		if(list.length != 2){
			throw new IllegalExpression(new String(tokens.tokens));
		}
		return FSA.and(list[0], list[1].negate());
	}

	static FSA parseOptional(TokenStream tokens){
		tokens.read('?');
		return parseSingle(tokens).optional();
	}

	static FSA parseWild(TokenStream tokens){
		tokens.read('.');
		return new FSA(c -> true, null);
	}

	static FSA parseRange(TokenStream tokens){
		tokens.read('[');
		char c0 = tokens.poll();
		tokens.read("...");
		char c1 = tokens.poll();
		tokens.read(']');
		return new FSA(c -> c >= c0 && c <= c1, null);
	}

	static FSA parseKleene(TokenStream tokens){
		tokens.read('*');
		return parseSingle(tokens).kleene();
	}

	static FSA parseNegation(TokenStream tokens){
		tokens.read('!');
		return parseSingle(tokens).negate();
	}

	static FSA parseParenthesis(TokenStream tokens){
		tokens.read('(');
		var rv = parseWhile(null, tokens, () -> tokens.peek() != ')');
		tokens.poll();
		return rv;
	}

	static FSA parseRepetition(TokenStream tokens){
		tokens.read("#(");
		CharPredicate digits = CharPredicate.inclusiveRange('0', '9');
		IntSupplier intReader = () -> {
			var builder = new StringBuilder();
			while(digits.test(tokens.peek())){
				builder.append(tokens.poll());
			}
			return Integer.parseInt(builder.toString());
		};
		int lower = intReader.getAsInt();
		Supplier<FSA> baseExtractor = () -> {
			tokens.read(", ");
			var rv = parseSingle(tokens);
			tokens.read(')');
			return rv;
		};
		switch(tokens.peek()){
			case '.' -> {
				tokens.read("...");
				int upper = intReader.getAsInt();
				return baseExtractor.get()
					.repeating(lower, upper);
			}
			case '+' -> {
				tokens.poll();
				return baseExtractor.get()
					.repeating(lower);
			}
			case ',' -> {
				return baseExtractor.get()
					.repeating(lower, lower);
			}
			default -> throw new IllegalToken(tokens);
		}
	//	var example = "#(1...3, *.)";

	}

	static FSA parseLiteral(TokenStream tokens){
		tokens.read('\'');
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
