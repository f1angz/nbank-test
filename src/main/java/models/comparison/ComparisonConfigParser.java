package models.comparison;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ComparisonConfigParser {

    public static Map<String, ComparisonRule> parseConfig(String configPath) {
        try {
            Properties properties = new Properties();
            properties.load(Files.newInputStream(Paths.get(configPath)));

            Map<String, ComparisonRule> rules = new HashMap<>();

            for (String ruleName : properties.stringPropertyNames()) {
                String ruleDefinition = properties.getProperty(ruleName);
                ComparisonRule rule = parseRule(ruleName, ruleDefinition);
                rules.put(ruleName, rule);
            }

            return rules;
        } catch (Exception e) {
            throw new ComparisonException("Failed to parse config: " + configPath, e);
        }
    }

    private static ComparisonRule parseRule(String ruleName, String ruleDefinition) {
        try {
            String[] parts = ruleDefinition.split(":", 2);

            if (parts.length != 2) {
                throw new ComparisonException("Invalid rule format: " + ruleDefinition);
            }

            String[] classParts = parts[0].split("->");
            if (classParts.length != 2) {
                throw new ComparisonException("Invalid class mapping format: " + parts[0]);
            }

            String sourceClass = classParts[0].trim();
            String targetClass = classParts[1].trim();
            String[] fieldMappings = parts[1].split(",");

            Map<String, String> mappings = new HashMap<>();
            for (String mapping : fieldMappings) {
                String[] fieldPair = mapping.split("=");
                if (fieldPair.length != 2) {
                    throw new ComparisonException("Invalid field mapping: " + mapping);
                }
                mappings.put(fieldPair[0].trim(), fieldPair[1].trim());
            }

            return new ComparisonRule(ruleName, sourceClass, targetClass, mappings);
        } catch (Exception e) {
            throw new ComparisonException("Failed to parse rule: " + ruleName, e);
        }
    }
}