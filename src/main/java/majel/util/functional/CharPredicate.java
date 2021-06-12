package majel.util.functional;

import majel.lang.util.TokenStream_Char;

@FunctionalInterface
public
interface CharPredicate {
	static CharPredicate ALL = c -> true;
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

	default TokenStream_Char toStream(){
		return TokenStream_Char
			.exclusiveRange((char)0, (char)0x100)
			.retain(this);
	}

	default void forEach(CharConsumer op){
		toStream().drain(op);
	}
}