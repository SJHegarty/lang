package majel.stream;

public interface SimpleToken extends Token{

	@Override
	default boolean eq(ObjectHack hack){
		return (hack instanceof SimpleToken t) && t.character() == character();
	}

	char character();
}
