package majel.lang.descent;

import majel.lang.automata.fsa.FSA;
import majel.lang.automata.fsa.Node;
import majel.lang.descent.lithp.Lithp;
import majel.lang.util.TokenStream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExpressionSelector<T> implements HandlerSelector<T>{

	private boolean validated = true;
	private final List<ExpressionHandler<T>> handlers = new ArrayList<>();

	protected void registerHandler(ExpressionHandler<T> handler){
		validated = false;
		handlers.add(handler);
	}

	protected void validate(){
		if(validated){
			return;
		}
		final FSA tail = new Lithp().build("?*.");
		/*
		TODO:
			build a lookup table mapping generated named to parsers.
			Lookup is performed by getting the name associate with the terminating state of the found machine.
		*/
		final FSA[] extended = handlers.stream()
			.map(
				h -> FSA
					.concatenate(h.headProcessor().automaton(), tail)
					.named(Long.toString(System.currentTimeMillis()))
			)
			.toArray(FSA[]::new);

		final var or = FSA.or(extended).dfa();
		final var collisions = or.nodes().stream()
			.filter(Node::terminating)
			.filter(n -> n.labels().size() > 1)
			.collect(Collectors.toList());

		if(!collisions.isEmpty()){
			throw new IllegalStateException(collisions.toString());
		}
		validated = true;
	}

	@Override
	public Handler<T> handlerFor(TokenStream tokens){
		if(!validated){
			throw new IllegalStateException();
		}
		return null;
	}
}
