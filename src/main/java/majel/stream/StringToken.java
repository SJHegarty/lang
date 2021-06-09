package majel.stream;

import java.util.Set;

public record StringToken(String value, Set<String> labels) implements Token{
}
