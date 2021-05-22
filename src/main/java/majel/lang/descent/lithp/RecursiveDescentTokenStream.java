package majel.lang.descent.lithp;

import java.util.List;

public class RecursiveDescentTokenStream<T> extends TokenStream{
	private final RecursiveDescentContext<T> context;

	public RecursiveDescentTokenStream(
		RecursiveDescentContext<T> context,
		String expression
	){
		super(expression);
		this.context = context;
	}

	public RecursiveDescentContext<T> context(){
		return context;
	}

	public RecursiveDescentParser<T> parser(){
		return context.parser();
	}

	public T parse(){
		return parser().parse(this);
	}

	public List<T> parseList(){
		return parser().parseList(this);
	}

}
