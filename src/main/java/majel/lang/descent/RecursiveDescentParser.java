package majel.lang.descent;

import majel.lang.err.IllegalToken;
import majel.lang.err.ParseException;
import majel.lang.util.TokenStream;
import majel.util.functional.CharPredicate;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
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
public class RecursiveDescentParser<T>{

	private final Handler<T>[] handlers;

	public RecursiveDescentParser(){
		handlers = new Handler[256];
	}

	public void registerHandler(Supplier<Handler<T>> builder){
		Handler<T> h = builder.get();
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
	}

	public T build(String expression){
		return parse(expression).build(buildContext());
	}

	public RecursiveDescentBuildContext<T> buildContext(String...expressions){
		var rv = new RecursiveDescentBuildContext<T>(new TreeMap<>());
		Stream.of(expressions)
			.map(
				s -> {
					var expr = parse(s);
					if(!expr.reconstitute().equals(s)){
						throw new IllegalStateException();
					}
					return expr;
				}
			)
			.forEach(expr -> expr.build(rv));

		return rv;
	}

	public List<Expression<T>> parseUntil(TokenStream tokens, char c){
		return parseWhile(tokens, t -> t != c);
	}

	public List<Expression<T>> parseWhile(TokenStream tokens, CharPredicate predicate){
		return parseWhile(tokens, () -> predicate.test(tokens.peek()));
	}

	public List<Expression<T>> parseWhile(TokenStream tokens, BooleanSupplier terminator){
		var elements = new ArrayList<Expression<T>>();
		while(terminator.getAsBoolean()){
			elements.add(parse(tokens));
		}
		return Collections.unmodifiableList(elements);
	}

	public Expression<T> parse(String expression){
		return parse(new TokenStream(expression));
	}

	public Expression<T> parse(TokenStream tokens){
		var mark = tokens.mark();
		var handler = handlers[tokens.peek()];
		if(handler == null){
			throw new IllegalToken(tokens);
		}
		try{
			return handler.parse(this, tokens);
		}
		catch(ParseException e){
			throw e;
		}
		catch(RuntimeException e){
			mark.reset();
			throw new IllegalToken(tokens);
		}
	}

	public List<Expression<T>> parseList(
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
	}

}
