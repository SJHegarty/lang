package majel.lang.automata.fsa;

@FunctionalInterface
interface CharPredicate {
	static CharPredicate inclusiveRange(char c0, char cN){
		return c -> c >= c0 && c <= cN;
	}

	static CharPredicate exclusiveRange(char c0, char cN){
		return c -> c >= c0 && c < cN;
	}

	boolean test(char c);
}