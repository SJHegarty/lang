package majel.lang.util;

public interface TokenStream extends AutoCloseable{

	boolean touched();
	boolean empty();
	Mark mark();

	default CloseOption option(){
		return CloseOption.ALWAYS;
	}
	@Override
	default void close(){
		boom:{
			switch(option()){
				case ALWAYS: break boom;
				case WHEN_EMPTY:{
					if(empty()){
						break boom;
					}
				}
			}
			throw new IllegalStateException();
		}
	}
}
