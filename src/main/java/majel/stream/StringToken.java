package majel.stream;

import majel.util.ObjectUtils;

import java.util.Set;

public record StringToken(String value, Set<String> labels) implements Token{
	public StringToken{
		value = ObjectUtils.escape(value);
	}

	public int length(){
		return value.length();
	}
}
