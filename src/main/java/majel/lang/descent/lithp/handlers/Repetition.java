package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.lithp.Handler;
import majel.lang.descent.lithp.RecursiveDescentContext;
import majel.lang.descent.lithp.RecursiveDescentParser;
import majel.lang.descent.lithp.TokenStream;
import majel.util.functional.CharPredicate;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class Repetition extends Handler<FSA>{
	public Repetition(RecursiveDescentParser<FSA> parser){
		super(parser);
	}

	@Override
	public char headToken(){
		return '#';
	}

	@Override
	public FSA parse(RecursiveDescentContext<FSA> context){
		var tokens = context.tokens();
		checkHead(tokens);
		tokens.read('(');
		CharPredicate digits = CharPredicate.inclusiveRange('0', '9');
		IntSupplier intReader = () -> {
			var builder = new StringBuilder();
			while(digits.test(tokens.peek())){
				builder.append(tokens.poll());
			}
			return Integer.parseInt(builder.toString());
		};
		int lower = intReader.getAsInt();
		Supplier<FSA> baseExtractor = () -> {
			tokens.read(", ");
			var rv = parser.parse(context);
			tokens.read(')');
			return rv;
		};
		switch(tokens.peek()){
			case '.' -> {
				tokens.read("...");
				int upper = intReader.getAsInt();
				return baseExtractor.get()
					.repeating(lower, upper);
			}
			case '+' -> {
				tokens.poll();
				return baseExtractor.get()
					.repeating(lower);
			}
			case ',' -> {
				return baseExtractor.get()
					.repeating(lower, lower);
			}
			default -> throw new RecursiveDescentParser.IllegalToken(tokens);
		}
	}
}
