package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.context.NullContext;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.RepetitionExpression;
import majel.lang.err.IllegalToken;
import majel.lang.util.TokenStream$Char;
import majel.lang.util.TokenStream$Obj;
import majel.stream.Token$Char;
import majel.util.functional.CharPredicate;

import java.util.function.IntSupplier;

import static majel.lang.descent.lithp.expressions.RepetitionExpression.CONTINUATION;
import static majel.lang.descent.lithp.expressions.RepetitionExpression.UNBOUND;

public class Repetition implements CharHandler<LithpExpression>{

	private static final CharPredicate DIGITS = CharPredicate.inclusiveRange('0', '9');

	@Override
	public char headToken(){
		return RepetitionExpression.HEAD_TOKEN;
	}

	@Override
	public LithpExpression parse(NullContext ignored, TokenStream$Obj<Token$Char> tokens, TokenStream$Obj<LithpExpression> parsed){
		checkHead(tokens);
		var simple = TokenStream$Char.of(tokens);
		simple.read(LithpExpression.OPENING_PARENTHESIS);
		IntSupplier intReader = () -> {
			var builder = new StringBuilder();
			while(DIGITS.test(simple.peek())){
				builder.append(simple.poll());
			}
			return Integer.parseInt(builder.toString());
		};
		final int lower = intReader.getAsInt();

		final int upper = switch(simple.peek()){
			case '.' -> {
				simple.read(CONTINUATION);
				yield intReader.getAsInt();
			}
			case UNBOUND -> {
				tokens.poll();
				yield Integer.MAX_VALUE;
			}
			case ',' -> lower;
			default -> throw new IllegalToken(tokens);
		};

		simple.read(LithpExpression.DELIMITER);
		var base = parsed.poll();
		simple.read(LithpExpression.CLOSING_PARENTHESIS);

		return new RepetitionExpression(lower, upper, base);
	}
}
