package majel.lang.util;

import majel.stream.Token;

public record IndexedToken<T>(T token, int index) implements Token{

}
