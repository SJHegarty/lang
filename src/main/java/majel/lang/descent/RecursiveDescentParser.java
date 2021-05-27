package majel.lang.descent;

import majel.lang.Parser;
import majel.lang.err.IllegalToken;
import majel.lang.err.ParseException;
import majel.lang.util.Mark;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.Token;
import majel.util.functional.CharPredicate;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static majel.lang.automata.fsa.FSA.TABLE_SIZE;

/*
	TODO:
 		Split the context into ExpressionContext and ParseContext.
 		The context type is somewhat conflated
 			There're expression contexts, e.g.: <(some-name, *.) should parse to:
 				record NamedExpression(String name, Expression wrapped);
 			However the name here still has no meaning - it's premature to actually lookup the definition.
 			Likewise @some-name; should not lookup some-name until later in the process.
 			The context containing a Map<name:String, Expression> should not be accessible until semantic processing is occurring.
 */
public class RecursiveDescentParser<S extends Token, T extends Token> implements Parser<S, T>{

	private final HandlerSelector<S, T> selector;
	public RecursiveDescentParser(HandlerSelector<S, T> selector){
		//handlers = new Handler[256];
		this.selector = selector;
	}

	/*public T build(String expression){
		return buildContext().build(expression);
	}*/

	/*public RecursiveDescentBuildContext<T> buildContext(String...expressions){
		var rv = new RecursiveDescentBuildContext<>(this, new LinkedHashMap<>());
		Stream.of(expressions).forEach(rv::build);
		return rv;
	}*/

	/*public List<Expression<T>> parseUntil(SimpleTokenStream tokens, char c){
		return parseWhile(tokens, t -> t != c);
	}*/

	/*public List<Expression<T>> parseWhile(SimpleTokenStream tokens, CharPredicate predicate){
		return parseWhile(tokens, () -> predicate.test(tokens.peek()));
	}*/

	/*public List<Expression<T>> parseWhile(SimpleTokenStream tokens, BooleanSupplier terminator){
		var elements = new ArrayList<Expression<T>>();
		while(terminator.getAsBoolean()){
			elements.add(parse(tokens));
		}
		return Collections.unmodifiableList(elements);
	}*/

	/*public Expression<T> parse(String expression){
		var rv = parse(TokenStream.from(expression));
		System.err.println(rv.reconstitute());
		if(!rv.reconstitute().equals(expression)){
			rv.reconstitute();
			throw new IllegalStateException();
		}
		return rv;
	}*/

	/*public List<Expression<T>> parseList(
		TokenStream tokens,
		char openingParenthesis,
		char closingParenthesis,
		String delimiter
	){
		tokens.read(openingParenthesis);
		var list = new ArrayList<Expression<T>>();
		for(;;){
			list.add(parse(tokens));
			if(tokens.peek() == closingParenthesis){
				break;
			}
			tokens.read(delimiter);
		}
		tokens.poll();
		return list;
	}

	public String reconstituteList(
		List<Expression<T>> elements,
		char openingParenthesis,
		char closingParenthesis,
		String delimiter
	){
		var builder = new StringBuilder().append(openingParenthesis);
		if(elements.size() != 0){
			builder.append(elements.get(0).reconstitute());
			for(int i = 1; i < elements.size(); i++){
				builder
					.append(delimiter)
					.append(elements.get(i).reconstitute());
			}
		}
		return builder
			.append(closingParenthesis)
			.toString();
	}*/

	@Override
	public TokenStream<T> parse(TokenStream<S> tokens){
		return new TokenStream<T>(){
			@Override
			public T peek(){
				var mark = tokens.mark();
				var rv = poll();
				mark.reset();
				return rv;
			}

			@Override
			public T poll(){
				var mark = tokens.mark();
				var handler = selector.markedHandlerFor(tokens);
				if(handler == null){
					throw new IllegalToken(tokens);
				}
				//try{
					return handler.parse(tokens, this);
				//}
				/*catch(ParseException e){
					throw e;
				}
				catch(RuntimeException e){
					mark.reset();
					throw new IllegalToken(tokens);
				}*/
			}

			@Override
			public boolean empty(){
				return tokens.empty();
			}

			@Override
			public Mark mark(){
				return tokens.mark();
			}
		};
	}
}
