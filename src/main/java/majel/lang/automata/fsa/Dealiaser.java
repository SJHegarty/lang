package majel.lang.automata.fsa;

import majel.lang.descent.context.NullContext;
import majel.lang.util.Mark;
import majel.lang.util.Pipe;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token;

import java.util.HashMap;

public class Dealiaser<T extends Token> implements Pipe<NullContext, T, T>{
	@Override
	public TokenStream_Obj<T> parse(NullContext ignored, TokenStream_Obj<T> tokens){
		final HashMap<T, T> elements = new HashMap<>();
		return new TokenStream_Obj<T>(){
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
			public boolean touched(){
				return tokens.touched();
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
