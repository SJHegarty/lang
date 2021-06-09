package majel.lang.automata.fsa;

import majel.lang.util.Mark;
import majel.lang.util.Pipe;
import majel.lang.util.TokenStream;
import majel.stream.Token;

import java.util.HashMap;

public class Dealiaser<T extends Token> implements Pipe<T, T>{
	@Override
	public TokenStream<T> parse(TokenStream<T> tokens){
		final HashMap<T, T> elements = new HashMap<>();
		return new TokenStream<T>(){
			@Override
			public T poll(){
				var result = tokens.poll();
				var rv = elements.get(result);
				if(rv == null){
					rv = result;
					elements.put(rv, rv);
				}
				return rv;
			}

			@Override
			public boolean empty(){
				return tokens.empty();
			}

			@Override
			public Mark mark(){
				return tokens.mark();
			}
		};
	}
}
