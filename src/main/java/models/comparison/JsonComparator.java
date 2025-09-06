package models.comparison;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class JsonComparator {

    private static final String DEFAULT_MAPPING_FILE = "model-comparison.properties";
    private final Properties properties = new Properties();

    public JsonComparator() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(DEFAULT_MAPPING_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Файл " + DEFAULT_MAPPING_FILE + " не найден в resources");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки " + DEFAULT_MAPPING_FILE, e);
        }
    }

    /**
     * Ассерт сравнения объектов на основе правил в mapping.properties.
     * Бросает AssertionError при несовпадении.
     */
    public void assertMatches(Object request, Object response) {
        String reqClass = request.getClass().getSimpleName();
        String respClass = response.getClass().getSimpleName();

        String rule = properties.getProperty(reqClass);
        if (rule == null) {
            throw new AssertionError("Нет правил для запроса: " + reqClass);
        }

        String[] parts = rule.split(":");
        if (parts.length != 2) {
            throw new AssertionError("Некорректное правило для " + reqClass + ": " + rule);
        }

        String expectedRespClass = parts[0].trim();
        if (!respClass.equals(expectedRespClass)) {
            throw new AssertionError("Ожидался ответ класса " + expectedRespClass + ", но получен " + respClass);
        }

        String mapping = parts[1];
        Map<String, String> fieldMapping = Arrays.stream(mapping.split(","))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(arr -> arr[0].trim(), arr -> arr[1].trim()));

        List<String> errors = new ArrayList<>();

        for (Map.Entry<String, String> entry : fieldMapping.entrySet()) {
            Object requestValue = getFieldValue(request, entry.getKey());
            Object responseValue = getFieldValue(response, entry.getValue());

            if (!Objects.equals(requestValue, responseValue)) {
                errors.add(String.format(
                        "%s.%s = %s <> %s.%s = %s",
                        reqClass, entry.getKey(), requestValue,
                        respClass, entry.getValue(), responseValue
                ));
            }
        }

        if (!errors.isEmpty()) {
            throw new AssertionError("Ошибки сравнения:\n" + String.join("\n", errors));
        }
    }

    private Object getFieldValue(Object obj, String fieldName) {
        try {
            String[] parts = fieldName.split("\\.");
            Object currentObj = obj;

            for (String part : parts) {
                if (currentObj == null) {
                    return null; // чтобы корректно сравнивалось с null
                }
                Field field = currentObj.getClass().getDeclaredField(part);
                field.setAccessible(true);
                currentObj = field.get(currentObj);
            }
            return currentObj;
        } catch (Exception e) {
            throw new AssertionError("Ошибка доступа к полю " + fieldName + " в " + obj.getClass().getSimpleName(), e);
        }
    }

}
