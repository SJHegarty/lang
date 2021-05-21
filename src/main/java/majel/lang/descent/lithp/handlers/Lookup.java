package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.automata.fsa.StringProcessor;
import majel.lang.descent.lithp.Handler;
import majel.lang.descent.lithp.RecursiveDescentContext;
import majel.lang.descent.lithp.RecursiveDescentParser;
import majel.lang.descent.lithp.TokenStream;

public class Lookup implements Handler<FSA>{

	@Override
	public char headToken(){
		return '@';
	}

	private transient StringProcessor processor;

	@Override
	public FSA parse(TokenStream<FSA> tokens){
		if(processor == null){
			processor = new StringProcessor(
				tokens.parser().build("(*[a...z]?*('-'*[a...z]))")
			);
		}
		checkHead(tokens);
		var name = processor.process(tokens).value();
		if(name.length() == 0){
			throw new RecursiveDescentParser.IllegalExpression(tokens);
		}
		tokens.read(';');
		return tokens.context().named(name);
	}
}
