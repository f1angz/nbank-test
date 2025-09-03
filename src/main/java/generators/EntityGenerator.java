package generators;

import com.mifmif.common.regex.Generex;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Random;

public class EntityGenerator {
    private static final Random RANDOM = new Random();

    @SuppressWarnings("unchecked")
    public static <T> T generate(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                // Проверяем наличие аннотации
                GeneratingRule rule = field.getAnnotation(GeneratingRule.class);
                if (rule != null) {
                    Generex generex = new Generex(rule.regex());
                    field.set(instance, generex.random());
                    continue;
                }

                // Если аннотации нет → стандартная генерация по типу
                Class<?> type = field.getType();
                if (type.equals(String.class)) {
                    field.set(instance, "str_" + RANDOM.nextInt(1000));
                } else if (type.equals(int.class) || type.equals(Integer.class)) {
                    field.set(instance, RANDOM.nextInt(1000));
                } else if (type.equals(long.class) || type.equals(Long.class)) {
                    field.set(instance, RANDOM.nextLong(10000));
                } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                    field.set(instance, RANDOM.nextBoolean());
                } else if (type.equals(double.class) || type.equals(Double.class)) {
                    field.set(instance, RANDOM.nextDouble());
                } else if (type.equals(LocalDate.class)) {
                    field.set(instance, LocalDate.now().minusDays(RANDOM.nextInt(365)));
                }
                // можно расширять: BigDecimal, enums и т.п.
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации сущности для " + clazz.getSimpleName(), e);
        }
    }
}
