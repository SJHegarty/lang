package majel.lang.descent.lithp;

import majel.lang.descent.Handler;
import majel.lang.descent.LA1Selector;
import majel.lang.descent.Reconstitutable;
import majel.lang.descent.RecursiveDescentParser;
import majel.lang.descent.lithp.handlers.*;
import majel.lang.err.IllegalToken;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;
import majel.util.functional.TokenStreamBuilder;

import java.util.ArrayList;

public class Lithp1 extends RecursiveDescentParser<SimpleToken, LithpExpression>{
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
			<(name, *.)
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
	public static void main(String... args){
		String lithpSrc = """
			<(lower-case, [a...z])
			<(ident, (<(word, *@LC;)?*('-'@W;)))
			""";

		var source = lithpSrc.replaceAll("\n", "").replaceAll("\t", "");
		var stream = SimpleTokenStream.from(source).wrap();
		var mark = stream.mark();
		var builder = new TokenStreamBuilder();

		/*new Lithp1().parse(stream).forEach(
			e -> builder.feed(e.regress())
		);*/
		builder.feed(new Lithp1().parse(stream).unwrap(Reconstitutable::regress));

		mark.reset();
		System.err.println("0: " + source);
		System.err.println("1: " + SimpleTokenStream.of(stream).remaining());
		System.err.println("2: " + builder.remaining());
	}

	public Lithp1(){
		super(
			new LA1Selector<>(){
				{
					registerHandler(And::new);
					registerHandler(Literal::new);
					registerHandler(Kleene::new);
					registerHandler(Negation::new);
					registerHandler(Parenthesis::new);
					registerHandler(Or::new);
					registerHandler(AndNot::new);
					registerHandler(Optional::new);
					registerHandler(WildCard::new);
					registerHandler(Range::new);
					registerHandler(Repetition::new);
					registerHandler(Named::new);
					registerHandler(Lookup::new);
				}
			}
		);
	}

	public static char parseLiteral(SimpleTokenStream tokens){
		var mark = tokens.mark();
		char token = tokens.poll();
		return switch(token){
			case '\'' -> {
				mark.reset();
				throw new IllegalToken(tokens.wrap());
			}
			case '\\' -> {
				yield switch(tokens.poll()){
					case 't' -> '\t';
					case 'n' -> '\n';
					case '\\' -> '\\';
					default -> throw new IllegalToken(tokens.wrap());
				};
			}
			default -> token;
		};
	}

}
