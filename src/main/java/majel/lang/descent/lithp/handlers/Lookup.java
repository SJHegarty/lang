package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.StringProcessor;
import majel.lang.descent.CharHandler;
import majel.lang.descent.lithp.Lithp1;
import majel.lang.descent.lithp.Lithp2;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.LookupExpression;
import majel.lang.err.IllegalExpression;
import majel.lang.util.TokenStream$Char;
import majel.lang.util.TokenStream;
import majel.stream.Token$Char;

import static majel.lang.descent.lithp.expressions.LookupExpression.HEAD_TOKEN;
import static majel.lang.descent.lithp.expressions.LookupExpression.TAIL_TOKEN;

public class Lookup implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return HEAD_TOKEN;
	}

	private transient StringProcessor processor;

	@Override
	public LithpExpression parse(TokenStream<Token$Char> tokens, TokenStream<LithpExpression> parsed){
		checkHead(tokens);
		var simple = TokenStream$Char.of(tokens);
		/*if(simple.peek() == '.'){
			final String body = ".;";
			tokens.read(body);
			return new Expression<>(){
				@Override
				public String reconstitute(){
					return headToken() + body;
				}

			};
		}*/

		if(processor == null){
			var exprLC = "(*[a...z]?*('-'*[a...z]))";
			var exprUC = "*[A...Z]";
			var expr = "+(" + exprLC + ", " + exprUC + ")";
			var parser = new Lithp1().andThen(new Lithp2());
			processor = new StringProcessor(
				parser.parse(TokenStream$Char.from(expr).wrap()).poll()
			);
		}
		var name = processor.process(simple).value();
		if(name.length() == 0){
			throw new IllegalExpression(tokens);
		}
		simple.read(TAIL_TOKEN);
		return new LookupExpression(name);
	}
}
