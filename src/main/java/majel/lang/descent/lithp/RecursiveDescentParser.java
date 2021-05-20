package majel.lang.descent.lithp;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
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
		final Map<String, T> map = new HashMap<>();
		return or(
			Stream.of(expressions)
				.map(expr -> new RecursiveDescentContext<>(map, new TokenStream(expr)))
				.map(this::parse).toList()
			);
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

	public T parseWhile(RecursiveDescentContext<T> context, BooleanSupplier terminator){
		var elements = new ArrayList<T>();
		while(terminator.getAsBoolean()){
			elements.add(parseSingle(context));
		}
		return concat(Collections.unmodifiableList(elements));
	}

	public abstract T concat(List<T> elements);
	public abstract T or(List<T> elements);

	T parse(RecursiveDescentContext<T> context){
		var tokens = context.tokens();
		return parseWhile(context, () -> !tokens.empty());
	}

	public T parseSingle(RecursiveDescentContext<T> context){
		var tokens = context.tokens();
		var handler = handlers[tokens.peek()];
		if(handler == null){
			throw new IllegalToken(tokens);
		}
		return handler.parse(context);
	}

	public List<T> parseList(RecursiveDescentContext<T> context){
		var tokens = context.tokens();
		tokens.read('(');
		var list = new ArrayList<T>();
		for(;;){
			list.add(parseSingle(context));
			if(tokens.peek() == ')'){
				break;
			}
			tokens.read(", ");
		}
		tokens.poll();
		return list;
	}

}
