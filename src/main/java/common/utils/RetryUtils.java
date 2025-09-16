package common.utils;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class RetryUtils {
    public static <T> T retry(Supplier<T> action, Predicate<T> condition, int maxAttempts, long delay) {
        T result;
        int attemps = 0;
        while (attemps < maxAttempts) {
            attemps++;
            result = action.get();
            if (condition.test(result)) {
                return result;
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Retry faile after " + maxAttempts + " attempts");
    }
}
