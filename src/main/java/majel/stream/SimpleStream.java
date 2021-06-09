package majel.stream;

public interface SimpleStream{

	default MajelStream<Token$Char> wrap(){
		return new Wrappers.WrappedSimpleStream(this);
	}

	char next();
	boolean empty();
	boolean finite();
}
