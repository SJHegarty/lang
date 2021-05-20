package majel.lang.descent.lithp;

import java.util.HashMap;
import java.util.Map;

public record RecursiveDescentContext<T>(Map<String, T> namedInstances, TokenStream tokens){

	public TokenStream tokens(){
		return tokens;
	}

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
}
