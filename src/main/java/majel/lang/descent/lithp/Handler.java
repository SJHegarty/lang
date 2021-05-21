package majel.lang.descent.lithp;

public interface Handler<T>{

	default void checkHead(TokenStream<T> tokens){
		tokens.read(headToken());
	}

	char headToken();

	T parse(TokenStream<T> context);
}
