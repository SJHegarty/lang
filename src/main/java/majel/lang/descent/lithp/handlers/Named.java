package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.StringProcessor;
import majel.lang.descent.CharHandler;
import majel.lang.descent.lithp.Lithp1;
import majel.lang.descent.lithp.Lithp2;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.NamedExpression;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

public class Named implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return NamedExpression.HEAD_TOKEN;
	}

	private transient StringProcessor processor;

	@Override
	public LithpExpression parse(TokenStream<SimpleToken> tokens, TokenStream<LithpExpression> parsed){
		if(processor == null){
			var word = "(*[a...z]?[A...Z]?*[a...z])";
			var expr = new StringBuilder()
				.append("(")
				.append(word)
				.append("?*('-'").append(word).append(")")
				.append(")");

			var parser = new Lithp1().andThen(new Lithp2());
			processor = new StringProcessor(
				parser.parse(SimpleTokenStream.from(expr.toString()).wrap()).poll()
			);
		}
		checkHead(tokens);
		var simple = SimpleTokenStream.of(tokens);
		simple.read(LithpExpression.OPENING_PARENTHESIS);
		var name = processor.process(simple).value();
		simple.read(LithpExpression.DELIMITER);
		var base = parsed.poll();
		simple.read(LithpExpression.CLOSING_PARENTHESIS);

		return new NamedExpression(name, base);
	}
}
