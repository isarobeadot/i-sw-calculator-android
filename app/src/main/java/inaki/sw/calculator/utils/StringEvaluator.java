package inaki.sw.calculator.utils;

import androidx.annotation.NonNull;

import org.mariuszgromada.math.mxparser.Expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import inaki.sw.calculator.R;

public class StringEvaluator {
    public static double evaluate(android.content.Context context, @NonNull String expression, String decimalSeparator) {
        expression = expression.replace(decimalSeparator, ".")
                .replace(context.getString(R.string.add), "+")
                .replace(context.getString(R.string.subtract), "-")
                .replace(context.getString(R.string.multiply), "*")
                .replace(context.getString(R.string.divide), "/")
                .replace(context.getString(R.string.percent), "%")
                .replace(context.getString(R.string.factorial), "!")
                .replace(context.getString(R.string.parenthesisL), "(")
                .replace(context.getString(R.string.parenthesisR), ")")
                .replace(context.getString(R.string.log), "lg");
        expression = autoCloseParentheses(expression);
        Expression e = new Expression(expression);
        if (!e.checkSyntax()) {
            throw new SyntaxException();
        }
        return e.calculate();
    }

    private static String autoCloseParentheses(String expression) {
        int countL = countSubstring(expression, "\\(");
        int countR = countSubstring(expression, "\\)");
        StringBuilder expressionBuilder = new StringBuilder(expression);
        for (int i = 0; i < countL - countR; i++) {
            expressionBuilder.append(")");
        }
        expression = expressionBuilder.toString();
        return expression;
    }

    private static int countSubstring(String string, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(string);
        int count = 0;
        while (m.find()) {
            count += 1;
        }
        return count;
    }
}
