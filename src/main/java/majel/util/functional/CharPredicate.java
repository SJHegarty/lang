package majel.util.functional;

@FunctionalInterface
public
interface CharPredicate {
	static CharPredicate inclusiveRange(char c0, char cN){
		return c -> c >= c0 && c <= cN;
	}

	static CharPredicate exclusiveRange(char c0, char cN){
		return c -> c >= c0 && c < cN;
	}

	boolean test(char c);

	default CharPredicate negate(){
		return c -> !CharPredicate.this.test(c);
	}

	default CharPredicate and(CharPredicate other){
		return c -> test(c) && other.test(c);
	}

	default void forEach(CharConsumer op){
		for(char c = 0; c < 0x100; c++){
			if(test(c)){
				op.consume(c);
			}
		}
	}
}