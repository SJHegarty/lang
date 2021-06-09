package majel.stream;

public class Wrappers{
	public static class WrappedSimpleStream implements MajelStream<Token$Char>{
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
		public Token$Char next(){
			return new Token$Char(wrapped().next());
		}

		public SimpleStream wrapped(){
			return wrapped;
		}
	}

	public static class WrappedMajelStream implements SimpleStream{
		private final MajelStream<Token$Char> wrapped;

		public WrappedMajelStream(MajelStream<Token$Char> wrapped){
			this.wrapped = wrapped;
		}
		@Override
		public char next(){
			return wrapped.next().value();
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
