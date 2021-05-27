package majel.stream;

public class Wrappers{
	public static class WrappedSimpleStream implements MajelStream<SimpleToken>{
		private final SimpleStream wrapped;

		public WrappedSimpleStream(SimpleStream wrapped){
			this.wrapped = wrapped;
		}

		@Override
		public boolean finite(){
			return wrapped.finite();
		}

		@Override
		public boolean empty(){
			return wrapped.empty();
		}

		@Override
		public SimpleToken next(){
			return new SimpleToken(wrapped().next());
		}

		public SimpleStream wrapped(){
			return wrapped;
		}
	}

	public static class WrappedMajelStream implements SimpleStream{
		private final MajelStream<SimpleToken> wrapped;

		public WrappedMajelStream(MajelStream<SimpleToken> wrapped){
			this.wrapped = wrapped;
		}
		@Override
		public char next(){
			return wrapped.next().character();
		}

		@Override
		public boolean empty(){
			return wrapped.empty();
		}

		@Override
		public boolean finite(){
			return wrapped.finite();
		}
	}
}
