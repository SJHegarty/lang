package majel.lang.descent.lithp;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		var context = new RecursiveDescentContext<>(
			new TreeMap<>(),
			this
		);
		return parse(context.createStream(expression));
	}

	public SortedMap<String, T> build(String...expressions){
		var context = new RecursiveDescentContext<>(
			new TreeMap<>(),
			this
		);

		Stream.of(expressions)
			.map(context::createStream)
			.map(this::parse)
			.collect(Collectors.toList());

		return context.namedInstances();
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

	public List<T> parseWhile(TokenStream<T> tokens, BooleanSupplier terminator){
		var elements = new ArrayList<T>();
		while(terminator.getAsBoolean()){
			elements.add(parse(tokens));
		}
		return Collections.unmodifiableList(elements);
	}

	public T parse(TokenStream<T> tokens){
		var handler = handlers[tokens.peek()];
		if(handler == null){
			throw new IllegalToken(tokens);
		}
		return handler.parse(tokens);
	}

	public List<T> parseList(TokenStream<T> tokens){
		tokens.read('(');
		var list = new ArrayList<T>();
		for(;;){
			list.add(parse(tokens));
			if(tokens.peek() == ')'){
				break;
			}
			tokens.read(", ");
		}
		tokens.poll();
		return list;
	}

}
