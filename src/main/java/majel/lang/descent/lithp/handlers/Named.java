package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.StringProcessor;
import majel.lang.descent.CharHandler;
import majel.lang.descent.context.NullContext;
import majel.lang.descent.lithp.Lithp1;
import majel.lang.descent.lithp.Lithp2;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.NamedExpression;
import majel.lang.util.TokenStream_Char;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;

public class Named implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return NamedExpression.HEAD_TOKEN;
	}

	private transient StringProcessor processor;

	@Override
	public LithpExpression parse(NullContext ignored, TokenStream_Obj<Token$Char> tokens, TokenStream_Obj<LithpExpression> parsed){
		if(processor == null){
			var word = "(*[a...z]?[A...Z]?*[a...z])";
			var expr = new StringBuilder()
				.append("(")
				.append(word)
				.append("?*('-'").append(word).append(")")
				.append(")");

			var parser = new Lithp1().andThen(new Lithp2());
			processor = new StringProcessor(
				parser.parse(ignored, TokenStream_Char.from(expr.toString()).wrap()).poll()
			);
		}
		checkHead(tokens);
		var simple = TokenStream_Char.of(tokens);
		simple.read(LithpExpression.OPENING_PARENTHESIS);
		var name = processor.process(simple).value();
		simple.read(LithpExpression.DELIMITER);
		var base = parsed.poll();
		simple.read(LithpExpression.CLOSING_PARENTHESIS);

		return new NamedExpression(name, base);
	}
}
