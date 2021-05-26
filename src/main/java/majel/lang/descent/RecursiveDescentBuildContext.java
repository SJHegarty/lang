package majel.lang.descent;

import java.util.Optional;
import java.util.SortedMap;
import java.util.function.IntFunction;

public record RecursiveDescentBuildContext<T>(
	RecursiveDescentParser<T> parser,
	SortedMap<String, T> namedInstances
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

	public T build(String expression){
		return build(parser.parse(expression));
	}

	public T build(Expression<T> expression){
		return expression.build(this);
	}

	public T[] machines(IntFunction<T[]> generator){
		return namedInstances.values().toArray(generator);
	}
}
