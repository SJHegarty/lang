package majel.util.functional;


@FunctionalInterface
public interface ObjectIntFunction<S, D>{
	D apply(S s, int i);
}
