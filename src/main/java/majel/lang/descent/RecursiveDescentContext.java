package majel.lang.descent;

import majel.lang.err.IllegalToken;

import java.util.Optional;
import java.util.SortedMap;

public record RecursiveDescentContext<T>(
	SortedMap<String, T> namedInstances,
	RecursiveDescentParser<T> parser
){

	public T named(String name){
		return Optional
			.ofNullable(namedInstances.get(name))
			.orElseThrow();
	}

	public void register(String name, T t){
		if(namedInstances.containsKey(name)){
			throw new UnsupportedOperationException(
				String.format(
					"name \"%s\" is already registered to %s",
					name,
					namedInstances.get(name)
				)
			);
		}
		namedInstances.put(name, t);
	}

	public RecursiveDescentTokenStream<T> createStream(String expression){
		return new RecursiveDescentTokenStream<>(this, expression);
	}

	public T parse(String expression){
		var stream = createStream(expression);
		var rv = stream.parse();
		if(!stream.empty()){
			throw new IllegalToken(stream);
		}
		return rv;
	}
	public RecursiveDescentParser<T> parser(){
		return parser;
	}
}
