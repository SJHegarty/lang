package majel.stream;

public interface SimpleStreamParser<D extends Token>{
	interface UnsimpleStreamParser<D extends Token>{
		SimpleStream parse(MajelStream<D> stream);
		SimpleStreamParser<D> reverse();
	}

	MajelStream<D> parse(SimpleStream stream);
	UnsimpleStreamParser<D> reverse();

	default StreamParser<Token$Char, D> wrap(){
		return new StreamParser<>(){
			@Override
			public MajelStream<D> parse(MajelStream<Token$Char> source){
				final SimpleStream simple;
				if(source instanceof Wrappers.WrappedSimpleStream wrapped){
					simple = wrapped.wrapped();
				}
				else{
					simple = new Wrappers.WrappedMajelStream(source);
				}
				;
				return SimpleStreamParser.this.parse(simple);
			}

			@Override
			public StreamParser<D, Token$Char> reverse(){
				var unsimple = SimpleStreamParser.this.reverse();
				var reverseReverse = this;
				return new StreamParser<>(){
					@Override
					public MajelStream<Token$Char> parse(MajelStream<D> source){
						return new Wrappers.WrappedSimpleStream(unsimple.parse(source));
					}

					@Override
					public StreamParser<Token$Char, D> reverse(){
						return reverseReverse;
					}
				};
			}
		};
	}
}
