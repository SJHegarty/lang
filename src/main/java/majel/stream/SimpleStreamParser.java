package majel.stream;

public interface SimpleStreamParser<D extends Token>{
	interface UnsimpleStreamParser<D extends Token>{
		SimpleStream parse(MajelStream<D> stream);
		SimpleStreamParser<D> reverse();
	}

	MajelStream<D> parse(SimpleStream stream);
	UnsimpleStreamParser<D> reverse();

	default StreamParser<SimpleToken, D> wrap(){
		return new StreamParser<>(){
			@Override
			public MajelStream<D> parse(MajelStream<SimpleToken> source){
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
			public StreamParser<D, SimpleToken> reverse(){
				var unsimple = SimpleStreamParser.this.reverse();
				var reverseReverse = this;
				return new StreamParser<>(){
					@Override
					public MajelStream<SimpleToken> parse(MajelStream<D> source){
						return new Wrappers.WrappedSimpleStream(unsimple.parse(source));
					}

					@Override
					public StreamParser<SimpleToken, D> reverse(){
						return reverseReverse;
					}
				};
			}
		};
	}
}
