package models.comparison;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComparisonRule {
    String ruleName;
    String sourceClass;
    String targetClass;
    Map<String, String> fieldMappings;
}