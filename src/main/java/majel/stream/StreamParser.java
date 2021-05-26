package majel.stream;

import java.util.ArrayList;

public interface StreamParser<S extends Token, D extends Token>{
	MajelStream<D> parse(MajelStream<S> source);

	StreamParser<D, S> reverse();

	default boolean validate(S...elements){
		return errorIndex(elements) == -1;
	}

	default int errorIndex(S...elements){
		final var reconstituted = reverse().parse(
			parse(MajelStream.of(elements))
		)
			.collect(ArrayList::new);

		final int eSize = elements.length;
		final int rSize = reconstituted.size();

		final int limit = (eSize < rSize) ? eSize : rSize;
		for(int i = 0; i < limit; i++){
			if(!elements[i].eq(reconstituted.get(i))){
				return i;
			}
		}
		if(eSize != rSize){
			return limit;
		}
		return -1;
	}
}
