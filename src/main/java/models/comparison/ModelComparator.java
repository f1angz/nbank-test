package models.comparison;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

public class ModelComparator {

    private final Map<String, ComparisonRule> rules;
    private boolean ignoreMissingFields = false;

    public ModelComparator() {
        this.rules = ComparisonConfigParser.parseConfig("src/main/resources/model-comparison.properties");
    }

    public void setIgnoreMissingFields(boolean ignoreMissingFields) {
        this.ignoreMissingFields = ignoreMissingFields;
    }

    public boolean isIgnoreMissingFields() {
        return ignoreMissingFields;
    }

    public void assertObjectsEqual(String ruleName, Object source, Object target) {
        ComparisonRule rule = rules.get(ruleName);

        if (rule == null) {
            throw new ComparisonException("No comparison rule found with name: " + ruleName);
        }

        validateClassMatch(source, rule.getSourceClass(), "Source");
        validateClassMatch(target, rule.getTargetClass(), "Target");

        for (Map.Entry<String, String> mapping : rule.getFieldMappings().entrySet()) {
            compareField(mapping.getKey(), mapping.getValue(), source, target);
        }
    }

    public void assertObjectsEqual(Object source, Object target) {
        String sourceClassName = source.getClass().getSimpleName();
        String targetClassName = target.getClass().getSimpleName();
        String autoRuleName = sourceClassName + "->" + targetClassName;

        ComparisonRule rule = rules.get(autoRuleName);
        if (rule == null) {
            throw new ComparisonException("No rule found for: " + autoRuleName +
                    ". Available rules: " + rules.keySet());
        }

        assertObjectsEqual(autoRuleName, source, target);
    }

    public boolean areObjectsEqual(String ruleName, Object source, Object target) {
        try {
            assertObjectsEqual(ruleName, source, target);
            return true;
        } catch (ComparisonException e) {
            return false;
        }
    }

    public List<String> findDifferences(String ruleName, Object source, Object target) {
        List<String> differences = new ArrayList<>();
        ComparisonRule rule = rules.get(ruleName);

        if (rule == null) {
            differences.add("Rule not found: " + ruleName);
            return differences;
        }

        for (Map.Entry<String, String> mapping : rule.getFieldMappings().entrySet()) {
            try {
                Object sourceValue = getNestedFieldValue(source, mapping.getKey());
                Object targetValue = getNestedFieldValue(target, mapping.getValue());

                if (!areValuesEqual(sourceValue, targetValue)) {
                    differences.add(String.format("%s (%s) != %s (%s)",
                            mapping.getKey(), formatValue(sourceValue),
                            mapping.getValue(), formatValue(targetValue)));
                }
            } catch (Exception e) {
                if (!ignoreMissingFields) {
                    differences.add("Error comparing " + mapping.getKey() + ": " + e.getMessage());
                }
            }
        }

        return differences;
    }

    private void validateClassMatch(Object obj, String expectedClassName, String type) {
        String actualClassName = obj.getClass().getSimpleName();
        if (!actualClassName.equals(expectedClassName)) {
            throw new ComparisonException(type + " class mismatch. Expected: " +
                    expectedClassName + ", but got: " + actualClassName);
        }
    }

    private void compareField(String sourcePath, String targetPath, Object source, Object target) {
        try {
            Object sourceValue = getNestedFieldValue(source, sourcePath);
            Object targetValue = getNestedFieldValue(target, targetPath);

            if (ignoreMissingFields && (sourceValue == null || targetValue == null)) {
                return;
            }

            if (!areValuesEqual(sourceValue, targetValue)) {
                throw new ComparisonException(String.format(
                        "Field mismatch:\nSource %s: %s\nTarget %s: %s",
                        sourcePath, formatValue(sourceValue),
                        targetPath, formatValue(targetValue)
                ));
            }

        } catch (Exception e) {
            if (!ignoreMissingFields) {
                throw new ComparisonException("Field comparison failed: " + sourcePath + " -> " + targetPath, e);
            }
        }
    }

    private Object getNestedFieldValue(Object obj, String fieldPath) {
        if (obj == null) {
            if (ignoreMissingFields) return null;
            throw new ComparisonException("Cannot get field from null object: " + fieldPath);
        }

        String[] fields = fieldPath.split("\\.");
        Object current = obj;

        for (String field : fields) {
            if (current == null) {
                if (ignoreMissingFields) return null;
                throw new ComparisonException("Intermediate field is null when accessing: " + fieldPath);
            }

            if (field.contains("[")) {
                current = getArrayOrCollectionElement(current, field);
            } else {
                current = getFieldValue(current, field);
            }

            if (current == null && ignoreMissingFields) {
                return null;
            }
        }

        return current;
    }

    private Object getFieldValue(Object obj, String fieldName) {
        try {
            Class<?> clazz = obj.getClass();
            Field field = getFieldRecursively(clazz, fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException e) {
            if (ignoreMissingFields) return null;
            throw new ComparisonException("Field not found: " + fieldName + " in class: " + obj.getClass().getName(), e);
        } catch (Exception e) {
            throw new ComparisonException("Failed to get field value: " + fieldName, e);
        }
    }

    private Field getFieldRecursively(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                return getFieldRecursively(superClass, fieldName);
            }
            throw e;
        }
    }

    private Object getArrayOrCollectionElement(Object obj, String fieldWithIndex) {
        try {
            int bracketIndex = fieldWithIndex.indexOf('[');
            String fieldName = fieldWithIndex.substring(0, bracketIndex);
            String indexStr = fieldWithIndex.substring(bracketIndex + 1, fieldWithIndex.indexOf(']'));
            int index = Integer.parseInt(indexStr);

            Object arrayOrCollection = getFieldValue(obj, fieldName);

            if (arrayOrCollection == null) {
                if (ignoreMissingFields) return null;
                throw new ComparisonException("Array/collection is null: " + fieldName);
            }

            if (arrayOrCollection.getClass().isArray()) {
                int length = Array.getLength(arrayOrCollection);
                if (index < 0 || index >= length) {
                    if (ignoreMissingFields) return null;
                    throw new ComparisonException("Array index out of bounds: " + index + " for array length: " + length);
                }
                return Array.get(arrayOrCollection, index);
            }

            if (arrayOrCollection instanceof Collection) {
                Collection<?> collection = (Collection<?>) arrayOrCollection;
                if (index < 0 || index >= collection.size()) {
                    if (ignoreMissingFields) return null;
                    throw new ComparisonException("Collection index out of bounds: " + index + " for collection size: " + collection.size());
                }

                if (collection instanceof List) {
                    return ((List<?>) collection).get(index);
                } else {
                    return collection.toArray()[index];
                }
            }

            if (ignoreMissingFields) return null;
            throw new ComparisonException("Field is not an array or collection: " + fieldName);

        } catch (Exception e) {
            if (e instanceof ComparisonException) {
                throw e;
            }
            throw new ComparisonException("Failed to get array/collection element: " + fieldWithIndex, e);
        }
    }

    private boolean areValuesEqual(Object value1, Object value2) {
        if (value1 == null && value2 == null) return true;
        if (value1 == null || value2 == null) return false;

        if (value1.getClass().isArray() && value2.getClass().isArray()) {
            return areArraysEqual(value1, value2);
        }

        if (value1 instanceof Collection && value2 instanceof Collection) {
            return areCollectionsEqual((Collection<?>) value1, (Collection<?>) value2);
        }

        return value1.equals(value2);
    }

    private boolean areArraysEqual(Object array1, Object array2) {
        int length1 = Array.getLength(array1);
        int length2 = Array.getLength(array2);

        if (length1 != length2) return false;

        for (int i = 0; i < length1; i++) {
            Object element1 = Array.get(array1, i);
            Object element2 = Array.get(array2, i);

            if (!areValuesEqual(element1, element2)) {
                return false;
            }
        }

        return true;
    }

    private boolean areCollectionsEqual(Collection<?> coll1, Collection<?> coll2) {
        if (coll1.size() != coll2.size()) return false;

        Object[] array1 = coll1.toArray();
        Object[] array2 = coll2.toArray();

        for (int i = 0; i < array1.length; i++) {
            if (!areValuesEqual(array1[i], array2[i])) {
                return false;
            }
        }

        return true;
    }

    private String formatValue(Object value) {
        if (value == null) return "null";
        if (value.getClass().isArray()) return formatArray(value);
        if (value instanceof Collection) return formatCollection((Collection<?>) value);
        return String.valueOf(value);
    }

    private String formatArray(Object array) {
        int length = Array.getLength(array);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(formatValue(Array.get(array, i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private String formatCollection(Collection<?> collection) {
        StringBuilder sb = new StringBuilder("[");
        int i = 0;
        for (Object item : collection) {
            if (i++ > 0) sb.append(", ");
            sb.append(formatValue(item));
        }
        sb.append("]");
        return sb.toString();
    }

    public Map<String, ComparisonRule> getRules() {
        return new HashMap<>(rules);
    }

    public void addRule(String ruleName, ComparisonRule rule) {
        rules.put(ruleName, rule);
    }

    public void removeRule(String ruleName) {
        rules.remove(ruleName);
    }
}