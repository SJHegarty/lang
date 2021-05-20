package majel.lang.descent.lithp;

import majel.lang.descent.lithp.handlers.*;
import majel.lang.descent.lithp.handlers.Optional;
import majel.util.LambdaUtils;
import majel.util.functional.CharPredicate;
import majel.lang.automata.fsa.FSA;
import majel.lang.automata.fsa.StringProcessor;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
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
	/*
		TODO:
			Build a (greedy) tokeniser and use it to parse the lithp expression format
				name :~ *[a...z]?*('-'*[a...z])
				expr :~ *.
			It would be nice to implement named and poly extractions:
				line :~ @(name)' :~ '@(expr)
				lithp :~ @(line)*('\n'@(line))
			Here it would be nice if within lithp, the line content was auto extracted to a value called "lines"

		TODO:
			A simpler mechanism is to allow for the tokeniser to read a token of a given type:
				Lithp:
					name :~ *[a...z]?*('-'*[a...z])
					separator :~ ' :~ '
					expr :~ *.
					new-line :~ '\n'
				Java:
					List<Expression> expressions = new ArrayList<>();
					for(;;){
						var name = readNamed(tokens, "name")
						readNamed(tokens, "separator")
						var expr = readNamed(tokens, "expression")
						expressions.add(new Expression(name, expr));
						if(stream.empty()){
							break;
						}
						readNamed("new-line");
						//Can support empty lithps and blank last lines with a loop check on stream.empty()
						//I don't really see why you'd need or want to.
					}
					//The readNamed will keep searching as long as the given state is terminable on the
					//token stream.
					//If a non-terminating state is reached, the last terminating state is returned to
					//Add mark + reset capability to the token stream, marking on all terminating states
					//For terminable states, read content should be appended to a char queue,
					//For terminating states the char queue content should be flushed to the return buffer.
					//If no terminating states are reached a NoMatchFound should be thrown



	 */
	public static void main(String...args){
		String lithpSrc = """
			tetht-optional :~ ?*('abacus''...')'sleep'
			tetht-literal :~ 'batman'
			tetht-negation :~ !'batman'
			tetht-kleene :~ *[a...z]
			tetht-wild :~ ...
			tetht-concatenation :~ *.'a'*.
			tetht-or :~ +('sleep', 'batman')
			tetht-and-not :~ -(*+([a...z], [A...Z]), 'batman'
			tetht-and :~ &(*[a...s], *[e...z])
			tetht-bound-repetition :~ #(3...5, [a...g])
			tetht-fixed-repetition :~ #(3, [a...z])
			tetht-unbound-repetition :~ #(4+, *.)
			""";

		System.err.println(lithpSrc);
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
		var bench = LambdaUtils.benchmark(() -> new Lithp(expressions));
		var lithp = bench.result();
		System.err.println(String.format("built parser in way too long (%sms)", bench.time()));
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

	static FSA parse(Expression e){
		return parse(e.label, new TokenStream(e.expression));
	}

	static class ParseException extends RuntimeException{
		ParseException(){}

		ParseException(String message){
			super(message);
		}
	}

	public static class IllegalExpression extends ParseException{
		public IllegalExpression(TokenStream tokens){
			super(new String(tokens.tokens));
		}
	}

	public static class IllegalToken extends ParseException{

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

	public static FSA parseWhile(String label, TokenStream tokens, BooleanSupplier terminator){
		var elements = new ArrayList<FSA>();
		while(terminator.getAsBoolean()){
			elements.add(parseSingle(tokens));
		}
		return FSA.concatenate(label, elements.toArray(FSA[]::new));
	}

	static FSA parse(String label, TokenStream tokens){
		return parseWhile(label, tokens, () -> !tokens.empty());
	}

	private static final Handler[] handlers;
	static{
		handlers = new Handler[256];
		Consumer<Handler> registrar = h -> {
			char headToken = h.headToken();
			if(handlers[headToken] != null){
				throw new UnsupportedOperationException(
					String.format(
						"%s already defined for head-token '%s'",
						Handler.class.getSimpleName(),
						headToken
					)
				);
			}
			handlers[headToken] = h;
		};
		registrar.accept(new And());
		registrar.accept(new Literal());
		registrar.accept(new Kleene());
		registrar.accept(new Negation());
		registrar.accept(new Parenthesis());
		registrar.accept(new Or());
		registrar.accept(new AndNot());
		registrar.accept(new Optional());
		registrar.accept(new WildCard());
		registrar.accept(new Range());
		registrar.accept(new Repetition());
	}

	public static FSA parseSingle(TokenStream tokens){
		var handler = handlers[tokens.peek()];
		if(handler == null){
			throw new IllegalToken(tokens);
		}
		return handler.parse(tokens);
	}

	public static FSA[] parseList(TokenStream tokens){
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

}
