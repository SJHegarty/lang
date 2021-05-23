package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.automata.fsa.StringProcessor;
import majel.lang.descent.Handler;
import majel.lang.err.IllegalExpression;
import majel.lang.descent.RecursiveDescentTokenStream;

public class Lookup implements Handler<FSA>{

	@Override
	public char headToken(){
		return '@';
	}

	private transient StringProcessor processor;

	@Override
	public FSA parse(RecursiveDescentTokenStream<FSA> tokens){
		if(processor == null){
			var exprLC = "(*[a...z]?*('-'*[a...z]))";
			var exprUC = "*[A...Z]";
			var expr = "+(" + exprLC + ", " + exprUC + ")";
			processor = new StringProcessor(
				tokens.parser().build(expr)
			);
		}
		checkHead(tokens);
		var name = processor.process(tokens).value();
		if(name.length() == 0){
			throw new IllegalExpression(tokens);
		}
		tokens.read(';');
		return tokens.context().named(name);
	}
}
