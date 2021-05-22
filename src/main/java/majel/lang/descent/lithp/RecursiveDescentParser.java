package majel.lang.descent.lithp;

import java.util.*;
import java.util.function.BooleanSupplier;
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

	public RecursiveDescentContext<T> buildContext(String...expressions){
		var context = new RecursiveDescentContext<>(
			new TreeMap<>(),
			this
		);

		Stream.of(expressions)
			.map(context::createStream)
			.forEach(this::parse);

		return context;
	}

	public List<T> parseWhile(RecursiveDescentTokenStream<T> tokens, BooleanSupplier terminator){
		var elements = new ArrayList<T>();
		while(terminator.getAsBoolean()){
			elements.add(parse(tokens));
		}
		return Collections.unmodifiableList(elements);
	}

	public T parse(RecursiveDescentTokenStream<T> tokens){
		var handler = handlers[tokens.peek()];
		if(handler == null){
			throw new IllegalToken(tokens);
		}
		return handler.parse(tokens);
	}

	public List<T> parseList(RecursiveDescentTokenStream<T> tokens){
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
