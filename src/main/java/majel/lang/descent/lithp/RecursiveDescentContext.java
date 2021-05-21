package majel.lang.descent.lithp;

import java.util.SortedMap;

public record RecursiveDescentContext<T>(
	SortedMap<String, T> namedInstances,
	RecursiveDescentParser<T> parser
){

	public T named(String name){
		return namedInstances.get(name);
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

	public TokenStream<T> createStream(String expression){
		return new TokenStream<>(this, expression);
	}

	public RecursiveDescentParser<T> parser(){
		return parser;
	}
}
