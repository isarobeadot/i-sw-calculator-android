package inaki.sw.calculator.utils;

import androidx.annotation.NonNull;

import org.mariuszgromada.math.mxparser.Expression;

import inaki.sw.calculator.R;

public class StringEvaluator {
    public static double evaluate(android.content.Context context, @NonNull String expression, String decimalSeparator) {
        expression = expression.replace(decimalSeparator, ".")
                .replace(context.getString(R.string.add), "+")
                .replace(context.getString(R.string.subtract), "-")
                .replace(context.getString(R.string.multiply), "*")
                .replace(context.getString(R.string.divide), "/")
                .replace(context.getString(R.string.pow), "^")
                .replace(context.getString(R.string.percent), "%")
                .replace(context.getString(R.string.factorial), "!");
        Expression e = new Expression(expression);
        if (!e.checkSyntax()) {
            throw new SyntaxException();
        }
        return e.calculate();
    }
}
