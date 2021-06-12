package majel.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Opt<T>{

	static class Gen{
		public static <T> Opt<T> when(boolean flag, Supplier<T> supplier){
			return flag ? empty() : of(supplier.get());
		}
		public static <T> Opt<T> of(T t){
			return () -> t;
		}

		public static <T> Opt<T> empty(){
			return of(null);
		}

		public static <T> Opt<T> tryGet(Supplier<T> supplier){
			try{
				return of(supplier.get());
			}
			catch(Exception e){
				return empty();
			}
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

	default <N> Opt<N> tryMap(Function<T, N> mapper){
		var value = unwrap();
		if(value == null){
			return Gen.empty();
		}
		return Gen.tryGet(() -> mapper.apply(value));
	}

	default void ifPresent(Consumer<T> op){
		var value = unwrap();
		if(value != null){
			op.accept(value);
		}
	}

	default T orGet(Supplier<T> source){
		var value = unwrap();
		if(value == null){
			var supplied = source.get();
			if(supplied == null){
				throw new IllegalArgumentException();
			}
			return supplied;
		}
		return value;
	}

	default T or(T t){
		if(t == null){
			throw new IllegalArgumentException();
		}
		var value = unwrap();
		return (value == null) ? t : value;
	}

	default <N> Opt<N> tryCast(Class<N> clazz){
		var value = unwrap();
		if(value == null){
			return Gen.empty();
		}
		return Gen.tryGet(() -> clazz.cast(value));
	}
	default <N> Opt<N> cast(Class<N> clazz){
		var value = unwrap();
		if(value == null){
			return Gen.empty();
		}
		return Gen.of(clazz.cast(value));
	}
}
