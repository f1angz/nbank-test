package common.utils;

import common.helpers.StepLogger;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class RetryUtils {
    public static <T> T retry(String title, Supplier<T> action, Predicate<T> condition, int maxAttempts, long delay) {
        T result;
        int attemps = 0;
        while (attemps < maxAttempts) {
            attemps++;

            try {
                result = StepLogger.log("Попытка" + attemps + ": " + title, () -> action.get());
                if (condition.test(result)) {
                    return result;
                }
            } catch (Throwable e) {
                System.out.println("Exception " + e.getMessage());
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
