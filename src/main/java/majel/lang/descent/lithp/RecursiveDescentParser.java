package majel.lang.descent.lithp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class RecursiveDescentParser<T>{

	private final Handler<T>[] handlers;

	public RecursiveDescentParser(){
		handlers = new Handler[256];
	}

	public void registerHandler(Function<RecursiveDescentParser<T>, Handler<T>> builder){
		Handler<T> h = builder.apply(this);
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

	public T build(String...expressions){
		return or(
			Stream.of(expressions)
				.map(this::parse).toList()
			);
	}

	T parse(String expression){
		return parse(new TokenStream(expression));
	}

	static class ParseException extends RuntimeException{
		ParseException(){}

		ParseException(String message){
			super(message);
		}
	}

	public static class IllegalExpression extends ParseException{
		public IllegalExpression(TokenStream tokens){
			super(tokens.expression());
		}
	}

	public static class IllegalToken extends ParseException{

		public IllegalToken(TokenStream tokens){
			super(
				String.format(
					"Illegal token '%s' at index:%s of expression:%s",
					tokens.peek(),
					tokens.index(),
					tokens.expression()
				)
			);
		}
	}

	static class IllegalEndOfStream extends ParseException{
		public IllegalEndOfStream(){
			super();
		}
	}

	public T parseWhile(TokenStream tokens, BooleanSupplier terminator){
		var elements = new ArrayList<T>();
		while(terminator.getAsBoolean()){
			elements.add(parseSingle(tokens));
		}
		return concat(Collections.unmodifiableList(elements));
	}

	public abstract T concat(List<T> elements);
	public abstract T or(List<T> elements);

	T parse(TokenStream tokens){
		return parseWhile(tokens, () -> !tokens.empty());
	}

	public T parseSingle(TokenStream tokens){
		var handler = handlers[tokens.peek()];
		if(handler == null){
			throw new IllegalToken(tokens);
		}
		return handler.parse(tokens);
	}

	public List<T> parseList(TokenStream tokens){
		tokens.read('(');
		var list = new ArrayList<T>();
		for(;;){
			list.add(parseSingle(tokens));
			if(tokens.peek() == ')'){
				break;
			}
			tokens.read(", ");
		}
		tokens.poll();
		return list;
	}

}
