package majel.util;

import java.util.function.Supplier;

public class LambdaUtils{
	public record Benchmark<T>(T result, long time){

	}

	public static <T> Benchmark<T> benchmark(Supplier<T> supplier){
		long time = System.currentTimeMillis();
		return new Benchmark<T>(supplier.get(), System.currentTimeMillis() - time);
	}
}
