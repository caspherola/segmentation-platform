package com.cred.segmentation.platform.application.util;

import com.cred.segmentation.platform.application.model.RuleDefinitionRequest;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExpressionToSqlConverter {
    private static final Pattern LOGICAL_OPERATOR_PATTERN = Pattern.compile("\\s+(and|or)\\s+", Pattern.CASE_INSENSITIVE);

    public static String convertToSqlCaseStatement(String expression, List<RuleDefinitionRequest.Parameter> parameters, String segmentId) {
        if (expression == null || expression.trim().isEmpty()) {
            return "NULL";
        }

        // Split by OR to handle multiple conditions
        String[] orConditions = expression.split("\\s+or\\s+", -1);

        StringBuilder caseStatement = new StringBuilder("CASE");

        for (int i = 0; i < orConditions.length; i++) {
            String condition = orConditions[i].trim();
            String sqlCondition = convertConditionToSql(condition);
            String segmentValue = segmentId + "_" + (i + 1);

            caseStatement.append(" WHEN ").append(sqlCondition)
                    .append(" THEN '").append(segmentValue).append("'");
        }

        caseStatement.append(" ELSE NULL END");

        return caseStatement.toString();
    }

    private static String convertConditionToSql(String condition) {
        // Handle AND conditions within the condition
        String[] andConditions = condition.split("\\s+and\\s+", -1);

        if (andConditions.length > 1) {
            return Arrays.stream(andConditions)
                    .map(String::trim)
                    .map(ExpressionToSqlConverter::convertSingleConditionToSql)
                    .collect(Collectors.joining(" AND "));
        } else {
            return convertSingleConditionToSql(condition);
        }
    }

    private static String convertSingleConditionToSql(String condition) {
        // Convert operators
        condition = condition.replace("==", "=");
        return condition.trim();
    }
}
