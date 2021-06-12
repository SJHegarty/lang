package majel.stream;

public record Token$Char(char value) implements Token{

	public boolean is(char c){
		return c == value;
	}
}
