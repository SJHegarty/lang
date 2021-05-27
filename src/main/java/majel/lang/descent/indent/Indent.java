//package majel.lang.descent.indent;
//
//import majel.lang.automata.fsa.StringProcessor;
//import majel.lang.descent.*;
//import majel.lang.descent.lithp.Lithp;
//import majel.lang.util.TokenStream;
//import majel.util.ObjectUtils;
//
//public class Indent extends RecursiveDescentParser<Indent.Foo>{
//	static class Foo{
//
//	}
//
//	public static void main(String...args){
//		String sample = "\t\t\t";
//
//		System.err.println(sample);
//		var indent = new Indent();
//		indent.parse(sample);
//	}
//
//
//	Indent(){
//		super(
//			new ExpressionSelector<>(){
//				{
//					final var context = new Lithp().buildContext();
//
//					this.registerHandler(
//						new ExpressionHandler<>(){
//							final StringProcessor processor = new StringProcessor(context.build("?*'\\t'"));
//							@Override
//							public StringProcessor headProcessor(){
//								return processor;
//							}
//
//							@Override
//							public Expression<Foo> parse(RecursiveDescentParser<Foo> parser, TokenStream tokens){
//								final int indent = headProcessor().process(tokens).value().length();
//								final var next = parser.parse(tokens);
//								return new Expression<Foo>(){
//									@Override
//									public String reconstitute(){
//										return new String(ObjectUtils.repeating('\t', indent)) + next.reconstitute();
//									}
//
//									/*@Override
//									public Foo build(RecursiveDescentBuildContext<Foo> context){
//										throw new UnsupportedOperationException();
//									}*/
//								};
//							}
//						}
//					);
//
//					this.registerHandler(
//						new ExpressionHandler<Foo>(){
//							final StringProcessor processor = new StringProcessor(context.build("('\\t'*.)"));
//							final StringProcessor contentProcessor = new StringProcessor(context.build("(?*!'\\n''\\n')"));
//							@Override
//							public StringProcessor headProcessor(){
//								return processor;
//							}
//
//							@Override
//							public Expression<Foo> parse(RecursiveDescentParser<Foo> parser, TokenStream tokens){
//								String extract = contentProcessor.process(tokens).value();
//								return new Expression<Foo>(){
//									@Override
//									public String reconstitute(){
//										return extract;
//									}
//
//									/*@Override
//									public Foo build(RecursiveDescentBuildContext<Foo> context){
//										throw new UnsupportedOperationException();
//									}*/
//								};
//							}
//						}
//					);
//					this.validate();
//				}
//			}
//		);
//	}
//}
