package majel.util;

import majel.stream.StringToken;

import java.util.function.Function;
import java.util.function.Predicate;

public interface Opt<T>{

	static class Gen{
		public static <T> Opt<T> of(T t){
			return () -> t;
		}

		public static <T> Opt<T> empty(){
			return of(null);
		}

	}

	T unwrap();

	default boolean empty(){
		return unwrap() == null;
	}

	default T value(){
		var t = unwrap();
		if(t == null){
			throw new UnsupportedOperationException();
		}
		return t;
	}

	default Opt<T> exclude(Predicate<T> executor){
		var value = unwrap();
		if(value == null || executor.test(value)){
			return Gen.empty();
		}
		return this;
	}

	default Opt<T> retain(Predicate<T> saviour){
		return exclude(saviour.negate());
	}

	default <N> Opt<N> map(Function<T, N> mapper){
		var value = unwrap();
		if(value == null){
			return Gen.empty();
		}
		return Gen.of(mapper.apply(value));
	}

	default <N> Opt<N> cast(Class<N> clazz){
		var value = unwrap();
		if(value == null){
			return Gen.empty();
		}
		return Gen.of(clazz.cast(value));
	}
}
