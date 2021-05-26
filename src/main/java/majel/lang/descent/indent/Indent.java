package majel.lang.descent.indent;

import majel.lang.automata.fsa.StringProcessor;
import majel.lang.descent.*;
import majel.lang.descent.lithp.Lithp;
import majel.lang.err.IllegalExpression;
import majel.lang.util.TokenStream;
import majel.util.ObjectUtils;
import majel.util.functional.CharPredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class Indent extends RecursiveDescentParser<Indent.Foo>{
	static class Foo{

	}

	public static void main(String...args){
		String sample = "\t\t\t";

		System.err.println(sample);
		var indent = new Indent();
		indent.parse(sample);
	}


	Indent(){
		super(
			new ExpressionSelector<>(){
				{
					final var lithp = new Lithp();
					final var context = lithp.buildContext();

					this.registerHandler(
						new ExpressionHandler<>(){
							final StringProcessor processor = new StringProcessor(context.build("?*'\\t'"));
							@Override
							public StringProcessor headProcessor(){
								return processor;
							}

							@Override
							public Expression<Foo> parse(RecursiveDescentParser<Foo> parser, TokenStream tokens){
								final int indent = headProcessor().process(tokens).value().length();
								return new Expression<Foo>(){
									@Override
									public String reconstitute(){
										return new String(ObjectUtils.repeating('\t', indent));
									}

									@Override
									public Foo build(RecursiveDescentBuildContext<Foo> context){
										throw new UnsupportedOperationException();
									}
								};
							}
						}
					);


					this.validate();
				}
			}
		);
	}
}
