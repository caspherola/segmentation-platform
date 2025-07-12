package com.cred.segmentation.platform.application.validator;

import com.cred.segmentation.platform.application.model.RuleDefinitionRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExpressionValidator {
    private static final String VARIABLE_PATTERN = "[a-zA-Z_][a-zA-Z0-9_]*";
    private static final String NUMBER_PATTERN = "\\d+(\\.\\d+)?";
    private static final String STRING_PATTERN = "'[^']*'";
    private static final String BOOLEAN_PATTERN = "true|false";
    private static final String OPERATOR_PATTERN = ">=|<=|==|!=|>|<";

    private static final Pattern SIMPLE_CONDITION_PATTERN = Pattern.compile(
            "(" + VARIABLE_PATTERN + ")\\s*(" + OPERATOR_PATTERN + ")\\s*(" +
                    NUMBER_PATTERN + "|" + STRING_PATTERN + "|" + BOOLEAN_PATTERN + "|" + VARIABLE_PATTERN + ")"
    );

    private static final Pattern LOGICAL_OPERATOR_PATTERN = Pattern.compile("\\s+(and|or)\\s+", Pattern.CASE_INSENSITIVE);

    public static class ValidationResult {
        private boolean valid;
        private String errorMessage;
        private List<String> variables;

        public ValidationResult(boolean valid, String errorMessage, List<String> variables) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.variables = variables;
        }

        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
        public List<String> getVariables() { return variables; }
    }

    public static ValidationResult validateExpression(String expression, List<RuleDefinitionRequest.Parameter> parameters) {
        if (expression == null || expression.trim().isEmpty()) {
            return new ValidationResult(false, "Expression cannot be null or empty", new ArrayList<>());
        }

        // Extract variables from expression
        Set<String> extractedVariables = extractVariables(expression);

        // Validate that all variables are defined in parameters
        Set<String> parameterNames = parameters.stream()
                .map(RuleDefinitionRequest.Parameter::getName)
                .collect(Collectors.toSet());

        Set<String> undefinedVariables = extractedVariables.stream()
                .filter(var -> !parameterNames.contains(var))
                .collect(Collectors.toSet());

        if (!undefinedVariables.isEmpty()) {
            return new ValidationResult(false,
                    "Undefined variables: " + undefinedVariables,
                    new ArrayList<>(extractedVariables));
        }

        // Validate expression syntax
        if (!isValidSyntax(expression)) {
            return new ValidationResult(false,
                    "Invalid expression syntax",
                    new ArrayList<>(extractedVariables));
        }

        return new ValidationResult(true, null, new ArrayList<>(extractedVariables));
    }

    private static Set<String> extractVariables(String expression) {
        Set<String> variables = new HashSet<>();

        // First, temporarily remove string literals to avoid matching variables inside them
        String exprWithoutStrings = expression.replaceAll(STRING_PATTERN, "''");

        Matcher matcher = Pattern.compile(VARIABLE_PATTERN).matcher(exprWithoutStrings);

        while (matcher.find()) {
            String match = matcher.group();
            // Filter out boolean literals and logical operators
            if (!match.matches("true|false|and|or")) {
                variables.add(match);
            }
        }

        return variables;
    }

    private static boolean isValidSyntax(String expression) {
        // Remove logical operators and split into conditions
        String[] conditions = LOGICAL_OPERATOR_PATTERN.split(expression);

        for (String condition : conditions) {
            if (!SIMPLE_CONDITION_PATTERN.matcher(condition.trim()).matches()) {
                return false;
            }
        }

        return true;
    }
}