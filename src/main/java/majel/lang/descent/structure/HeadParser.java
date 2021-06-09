//package majel.lang.descent.structure;
//
//import majel.lang.automata.fsa.StringProcessor;
//import majel.lang.descent.*;
//import majel.lang.descent.lithp.Lithp1;
//import majel.lang.descent.lithp.Lithp2;
//import majel.lang.util.TokenStream$Char;
//import majel.lang.util.TokenStream;
//import majel.stream.Token$Char;
//import majel.stream.Token;
//
//import java.util.function.Function;
//
//public class HeadParser extends RecursiveDescentParser<Token$Char, HeadParser.BlahToken>{
//	public HeadParser(){
//		super(
//			new LA1Selector<>(){
//				{
//					var lithp = new Lithp1().andThen(new Lithp2());
//					Function<String, StringProcessor> lithps = expression -> new StringProcessor(
//						lithp.parseSingle(TokenStream.from(expression))
//					);
//
//					this.registerHandler(() -> new CharHandler<>(){
//
//						@Override
//						public char headToken(){
//							return '<';
//						}
//
//						StringProcessor identifiers = lithps.apply("(<(seg, *[a...z])?*('-'@seg;))");
//						StringProcessor types = lithps.apply("*([A...Z]?*[a...z])");
//						@Override
//						public BlahToken parse(TokenStream<Token$Char> tokens, TokenStream<BlahToken> parsed){
//							checkHead(tokens);
//							var simple = TokenStream$Char.of(tokens);
//							switch(simple.poll()){
//								case '@' -> {
//									var blah = identifiers.process(simple);
//									var filtered = simple.exclude(' ');
//									switch(filtered.poll()){
//										case '~' -> {
//
//										}
//										default -> throw new UnsupportedOperationException();
//									}
//									throw new UnsupportedOperationException();
//								}
//								default -> throw new IllegalStateException();
//							}
//						};
//					});
//				}
//			}
//		);
//	}
//
//	static interface BlahToken extends Token{
//
//	}
//}
