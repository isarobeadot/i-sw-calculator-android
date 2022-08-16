package inaki.sw.calculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button buttonBasic0, buttonBasic1, buttonBasic2, buttonBasic3, buttonBasic4, buttonBasic5, buttonBasic6, buttonBasic7, buttonBasic8, buttonBasic9, buttonBasicDot;
    private Button buttonAns, buttonClear, buttonQuit;
    private Button buttonBasicBackspace, buttonBasicEqual;
    private Button buttonBasicAdd, buttonBasicDivide, buttonBasicMultiply, buttonBasicPlusMinus, buttonBasicPow, buttonBasicSubtract;
    private TextView textViewBasicTop;
    private TextView textViewBasicOp;
    private TextView textViewBasicMain;
    private SharedPreferences preferences;
    private double ans = 0D;
    private boolean equalPressed = false;
    private final Locale locale = Locale.getDefault();
    private final String decimal_separator = String.format(locale, "%f", 0.0).replace("0", "");
    private final DecimalFormat decimalFormat = new DecimalFormat("#0.####################");
    private final DecimalFormat scientificFormat = new DecimalFormat("0.0#################E0");
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        addListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_copy_ans) {
            CharSequence text = textViewBasicTop.getText();
            if (text == null || text.toString().equals("")) {
                return true;
            }

            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(text, text);
            clipboard.setPrimaryClip(clip);

            Toast toast = Toast.makeText(getApplicationContext(), R.string.ans_copied, Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        _quit();
    }

    private void initialize() {
        // Load preferences
        preferences = getSharedPreferences("values", Context.MODE_PRIVATE);
        equalPressed = preferences.getBoolean("equalPressed", false);
        ans = Double.longBitsToDouble(preferences.getLong("ans", Double.doubleToLongBits(0D)));
        // Buttons
        buttonBasic0 = findViewById(R.id.basic_0);
        buttonBasic1 = findViewById(R.id.basic_1);
        buttonBasic2 = findViewById(R.id.basic_2);
        buttonBasic3 = findViewById(R.id.basic_3);
        buttonBasic4 = findViewById(R.id.basic_4);
        buttonBasic5 = findViewById(R.id.basic_5);
        buttonBasic6 = findViewById(R.id.basic_6);
        buttonBasic7 = findViewById(R.id.basic_7);
        buttonBasic8 = findViewById(R.id.basic_8);
        buttonBasic9 = findViewById(R.id.basic_9);
        buttonAns = findViewById(R.id.ans);
        buttonClear = findViewById(R.id.clear);
        buttonQuit = findViewById(R.id.quit);
        buttonBasicBackspace = findViewById(R.id.basic_backspace);
        buttonBasicDot = findViewById(R.id.basic_dot);
        buttonBasicEqual = findViewById(R.id.basic_equal);
        buttonBasicAdd = findViewById(R.id.basic_add);
        buttonBasicSubtract = findViewById(R.id.basic_subtract);
        buttonBasicMultiply = findViewById(R.id.basic_multiply);
        buttonBasicPlusMinus = findViewById(R.id.basic_plusMinus);
        buttonBasicDivide = findViewById(R.id.basic_divide);
        buttonBasicPow = findViewById(R.id.basic_pow);
        textViewBasicTop = findViewById(R.id.basic_top);
        textViewBasicTop.setText(preferences.getString("top", ""));
        textViewBasicOp = findViewById(R.id.basic_op);
        textViewBasicOp.setText(preferences.getString("op", ""));
        textViewBasicMain = findViewById(R.id.basic_main);
        textViewBasicMain.setText(preferences.getString("main", getString(R.string.b0)));
        // Localize decimal separator
        buttonBasicDot.setText(decimal_separator);
        // Answer button should be disabled at beginning
        buttonAns.setEnabled(ans != 0D);
        // Power
        buttonBasicPow.setText(Html.fromHtml("x<sup><small>y</small></sup>"));
        // Backspace
        buttonBasicBackspace.setText(Html.fromHtml("<small>&#10094;</small>"));
        // Vibrator
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private void addListeners() {
        buttonBasic0.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b0));
        });
        buttonBasic1.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b1));
        });
        buttonBasic2.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b2));
        });
        buttonBasic3.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b3));
        });
        buttonBasic4.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b4));
        });
        buttonBasic5.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b5));
        });
        buttonBasic6.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b6));
        });
        buttonBasic7.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b7));
        });
        buttonBasic8.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b8));
        });
        buttonBasic9.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b9));
        });
        buttonAns.setOnClickListener(v -> {
            _hapticFeedback();
            _ans();
        });
        buttonClear.setOnClickListener(v -> {
            _hapticFeedback();
            _clear();
        });
        buttonQuit.setOnClickListener(v -> {
            _hapticFeedback();
            _quit();
        });
        buttonBasicDot.setOnClickListener(v -> {
            _hapticFeedback();
            _dot();
        });
        buttonBasicEqual.setOnClickListener(v -> {
            _hapticFeedback();
            _equal();
        });
        buttonBasicAdd.setOnClickListener(v -> {
            _hapticFeedback();
            _operand(getString(R.string.add));
        });
        buttonBasicBackspace.setOnClickListener(v -> {
            _hapticFeedback();
            _backSpace();
        });
        buttonBasicBackspace.setOnLongClickListener(v -> _backSpaceLong());
        buttonBasicDivide.setOnClickListener(v -> {
            _hapticFeedback();
            _operand(getString(R.string.divide));
        });
        buttonBasicMultiply.setOnClickListener(v -> {
            _hapticFeedback();
            _operand(getString(R.string.multiply));
        });
        buttonBasicPlusMinus.setOnClickListener(v -> {
            _hapticFeedback();
            _plusMinus();
        });
        buttonBasicPow.setOnClickListener(v -> {
            _hapticFeedback();
            _operand(getString(R.string.pow));
        });
        buttonBasicSubtract.setOnClickListener(v -> {
            _hapticFeedback();
            _operand(getString(R.string.subtract));
        });
    }

    private void _ans() {
        _parseAnswerToTop();
        equalPressed = true;
        _updatePreferences();
    }

    private void _backSpace() {
        String _main = textViewBasicMain.getText().toString();
        int limit = _main.contains(getString(R.string.subtract)) ? 2 : 1;
        if (_main.length() > limit) {
            textViewBasicMain.setText(_main.substring(0, _main.length() - 1));
        } else if (_main.length() == limit) {
            String _top = textViewBasicTop.getText().toString();
            String _op = textViewBasicOp.getText().toString();
            if (!_top.equals("") && !_op.equals("")) {
                textViewBasicTop.setText("");
                textViewBasicOp.setText("");
                textViewBasicMain.setText(_top);
            } else {
                textViewBasicMain.setText(getString(R.string.b0));
                equalPressed = !textViewBasicTop.getText().toString().isEmpty();
            }
        }
        _main = textViewBasicMain.getText().toString();
        if (_main.substring(_main.length() - 1).equals(decimal_separator)) {
            textViewBasicMain.setText(_main.substring(0, _main.length() - 1));
        }
        _updatePreferences();
    }

    private boolean _backSpaceLong() {
        String _main = textViewBasicMain.getText().toString();
        if (_main.equals(getString(R.string.b0))) {
            return false;
        }
        textViewBasicMain.setText(getString(R.string.b0));
        _updatePreferences();
        return true;
    }

    private void _clear() {
        textViewBasicTop.setText("");
        textViewBasicOp.setText("");
        textViewBasicMain.setText(getString(R.string.b0));
        equalPressed = false;
        _updatePreferences();
    }

    private void _dot() {
        String s;
        if (!textViewBasicMain.getText().toString().contains(decimal_separator)) {
            s = textViewBasicMain.getText() + decimal_separator;
            textViewBasicMain.setText(s);
        }
        _updatePreferences();
    }

    private void _equal() {
        final String add = "+";
        final String subtract = "−";
        try {
            double d1;
            double d2;
            NumberFormat nf = NumberFormat.getInstance(locale);
            switch (textViewBasicOp.getText().toString()) {
                case add:
                    d1 = ans;
                    d2 = Objects.requireNonNull(nf.parse(textViewBasicMain.getText().toString())).doubleValue();
                    ans = d1 + d2;
                    break;
                case subtract:
                    d1 = ans;
                    d2 = Objects.requireNonNull(nf.parse(textViewBasicMain.getText().toString())).doubleValue();
                    ans = d1 - d2;
                    break;
                case "x":
                    d1 = ans;
                    d2 = Objects.requireNonNull(nf.parse(textViewBasicMain.getText().toString())).doubleValue();
                    ans = d1 * d2;
                    break;
                case "/":
                    d1 = ans;
                    d2 = Objects.requireNonNull(nf.parse(textViewBasicMain.getText().toString())).doubleValue();
                    ans = d1 / d2;
                    break;
                case "^":
                    d1 = ans;
                    d2 = Objects.requireNonNull(nf.parse(textViewBasicMain.getText().toString())).doubleValue();
                    ans = Math.pow(d1, d2);
                    break;
                default:
                    d2 = Objects.requireNonNull(nf.parse(textViewBasicMain.getText().toString())).doubleValue();
                    ans = d2;
                    break;
            }
            _clear();
            _parseAnswerToTop();
            // Enable answer button
            buttonAns.setEnabled(true);
        } catch (ParseException pe) {
            _clear();
            _parseAnswerToTop();
        }
        equalPressed = true;
        _updatePreferences();
    }

    private void _parseAnswerToTop() {
        if (Double.toString(ans).contains("E")) {
            textViewBasicTop.setText(scientificFormat.format(ans));
        } else {
            textViewBasicTop.setText(decimalFormat.format(ans));
        }
    }

    private void _number(String s) {
        if (textViewBasicMain.getText().equals(getString(R.string.b0))) {
            textViewBasicMain.setText("");
        }
        if (textViewBasicMain.getText().equals(getString(R.string.subtract) + getString(R.string.b0))) {
            textViewBasicMain.setText(getString(R.string.subtract));
        }
        s = textViewBasicMain.getText() + s;
        textViewBasicMain.setText(s);
        equalPressed = false;
        _updatePreferences();
    }

    private void _operand(String s) {
        if (!equalPressed) {
            _equal();
        }
        textViewBasicOp.setText(s);
        equalPressed = false;
        _updatePreferences();
    }

    private void _plusMinus() {
        String _main = textViewBasicMain.getText().toString();
        if (_main.charAt(0) == getString(R.string.subtract).charAt(0)) {
            textViewBasicMain.setText(_main.replace(getString(R.string.subtract), ""));
        } else {
            _main = getString(R.string.subtract) + _main;
            textViewBasicMain.setText(_main);
        }
        _updatePreferences();
    }

    private void _quit() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("top", "");
        editor.putString("op", "");
        editor.putString("main", getString(R.string.b0));
        editor.putBoolean("equalPressed", false);
        editor.apply();
        finish();
    }

    private void _updatePreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("top", textViewBasicTop.getText().toString());
        editor.putString("op", textViewBasicOp.getText().toString());
        editor.putString("main", textViewBasicMain.getText().toString());
        editor.putBoolean("equalPressed", equalPressed);
        editor.putLong("ans", Double.doubleToRawLongBits(ans));
        editor.apply();
    }

    private void _hapticFeedback() {
        long milliseconds = 10;
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(milliseconds);
        }
    }
}
