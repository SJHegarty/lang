package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.automata.fsa.StringProcessor;
import majel.lang.descent.Handler;
import majel.lang.err.IllegalExpression;
import majel.lang.descent.RecursiveDescentTokenStream;
import majel.lang.util.TokenStream;

public class Named implements Handler<FSA>{

	@Override
	public char headToken(){
		return '<';
	}

	private transient StringProcessor processor;

	@Override
	public FSA parse(RecursiveDescentTokenStream<FSA> tokens){
		if(processor == null){
			var word = "(*[a...z]?[A...Z]?*[a...z])";
			var expr = new StringBuilder()
				.append("(")
				.append(word)
				.append("?*('-'").append(word).append(")")
				.append(")");

			processor = new StringProcessor(
				tokens.parser().build(expr.toString())
			);
		}
		checkHead(tokens);
		tokens.read('(');
		var name = processor.process(tokens).value();
		if(name.length() == 0){
			throw new IllegalExpression(tokens);
		}
		tokens.read(", ");
		var base = tokens.parse();
		tokens.poll();
		var lower = name.toLowerCase();
		var rv = base.named(lower);

		var shortForm = new StringBuilder();
		for(String s: new TokenStream(name).split('-')){
			var builder = new StringBuilder();
			for(char c: s.toCharArray()){
				if(Character.isUpperCase(c)){
					builder.append(c);
				}
			}
			switch(builder.length()){
				case 0: builder.append(Character.toUpperCase(s.charAt(0)));
				case 1: break;
				default: throw new IllegalExpression(tokens);
			}
			shortForm.append(builder);
		}
		tokens.context().register(lower, rv);
		tokens.context().register(shortForm.toString(), rv);
		return rv;
	}
}
