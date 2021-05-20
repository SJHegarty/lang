package majel.lang.descent.lithp;

public abstract class Handler<T>{

	protected final RecursiveDescentParser<T> parser;

	protected Handler(RecursiveDescentParser<T> parser){
		this.parser = parser;
	}

	protected void checkHead(TokenStream tokens){
		tokens.read(headToken());
	}

	public abstract char headToken();

	public abstract T parse(RecursiveDescentContext<T> context);
}
