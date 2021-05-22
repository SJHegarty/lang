package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.automata.fsa.StringProcessor;
import majel.lang.descent.lithp.Handler;
import majel.lang.descent.lithp.IllegalExpression;
import majel.lang.descent.lithp.RecursiveDescentTokenStream;

public class Named implements Handler<FSA>{

	@Override
	public char headToken(){
		return '<';
	}

	private transient StringProcessor processor;

	@Override
	public FSA parse(RecursiveDescentTokenStream<FSA> tokens){
		if(processor == null){
			processor = new StringProcessor(
				tokens.parser().build("(*[a...z]?*('-'*[a...z]))")
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
		var rv = base.named(name);
		tokens.context().register(name, rv);
		return rv;
	}
}
