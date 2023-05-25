package inaki.sw.calculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Objects;

import inaki.sw.calculator.utils.LayoutMode;
import inaki.sw.calculator.utils.StringEvaluator;
import inaki.sw.calculator.utils.SyntaxException;

public class MainActivity extends AppCompatActivity {

    private Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9, buttonDot;
    private Button buttonAns, buttonClear, buttonQuit;
    private ImageButton buttonBackspace;
    private Button buttonEqual;
    private Button buttonAdd, buttonDivide, buttonMultiply, buttonPlusMinus, buttonPow, buttonSubtract;
    private Button buttonPercent, buttonParenthesisL, buttonParenthesisR, buttonFactorial;
    private Button buttonSin, buttonCos, buttonTan;
    private Button buttonASin, buttonACos, buttonATan;
    private Button buttonLn, buttonLog;
    private Button buttonExp, buttonPi, buttonE;
    private ImageButton buttonMore, buttonLess;
    private TextView textViewTop, textViewOp;
    private EditText editTextMain;
    private SharedPreferences preferences;
    private double ans = 0D;
    private boolean equalPressed = false;
    private final Locale locale = Locale.getDefault();
    private final String decimal_separator = String.format(locale, "%f", 0.0).replace("0", "");
    private final DecimalFormat decimalFormat = new DecimalFormat("#0.####################");
    private final DecimalFormat scientificFormat = new DecimalFormat("0.0#################E0");
    private Vibrator vibrator;
    private LayoutMode layout = LayoutMode.BASIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
        addListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem mode = menu.findItem(R.id.action_mode);
        if (layout.equals(LayoutMode.EXTENDED)) {
            mode.setTitle(R.string.basic);
        } else {
            mode.setTitle(R.string.extended);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_copy_ans) {
            CharSequence text = textViewTop.getText();
            if (text == null || text.toString().equals("")) {
                return true;
            }

            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(text, text);
            clipboard.setPrimaryClip(clip);

            Toast toast = Toast.makeText(getApplicationContext(), R.string.ans_copied, Toast.LENGTH_SHORT);
            toast.show();
            return true;
        } else if (id == R.id.action_mode) {
            if (layout.equals(LayoutMode.BASIC)) {
                layout = LayoutMode.EXTENDED;
            } else {
                layout = LayoutMode.BASIC;
            }
            textViewTop.setText("");
            textViewOp.setText("");
            editTextMain.setText(R.string.b0);
            _updatePreferences();
            initialize();
            addListeners();
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
        try {
            layout = LayoutMode.valueOf(preferences.getString("layout", LayoutMode.BASIC.name()));
        } catch (java.lang.IllegalArgumentException e) {
            layout = LayoutMode.BASIC;
        }
        // Set layout
        if (layout.equals(LayoutMode.EXTENDED)) {
            setContentView(R.layout.activity_main_extended);
        } else {
            setContentView(R.layout.activity_main_basic);
        }
        // Buttons
        button0 = findViewById(R.id.b_0);
        button1 = findViewById(R.id.b_1);
        button2 = findViewById(R.id.b_2);
        button3 = findViewById(R.id.b_3);
        button4 = findViewById(R.id.b_4);
        button5 = findViewById(R.id.b_5);
        button6 = findViewById(R.id.b_6);
        button7 = findViewById(R.id.b_7);
        button8 = findViewById(R.id.b_8);
        button9 = findViewById(R.id.b_9);
        buttonAns = findViewById(R.id.ans);
        buttonClear = findViewById(R.id.clear);
        buttonQuit = findViewById(R.id.quit);
        buttonBackspace = findViewById(R.id.b_backspace);
        buttonDot = findViewById(R.id.b_dot);
        buttonEqual = findViewById(R.id.b_equal);
        buttonAdd = findViewById(R.id.b_add);
        buttonSubtract = findViewById(R.id.b_subtract);
        buttonMultiply = findViewById(R.id.b_multiply);
        buttonDivide = findViewById(R.id.b_divide);
        buttonPow = findViewById(R.id.b_pow);
        textViewTop = findViewById(R.id.tv_top);
        textViewTop.setText(preferences.getString("top", ""));
        textViewOp = findViewById(R.id.tv_op);
        textViewOp.setText(preferences.getString("op", ""));
        if (layout.equals(LayoutMode.EXTENDED)) textViewOp.setVisibility(View.GONE);
        else textViewOp.setVisibility(View.VISIBLE);
        editTextMain = findViewById(R.id.et_main);
        editTextMain.setText(preferences.getString("main", getString(R.string.b0)));
        editTextMain.setEnabled(false); // TODO: enable with extended layout
        // Specific buttons of each layout
        if (layout.equals(LayoutMode.EXTENDED)) {
            buttonPercent = findViewById(R.id.b_percent);
            buttonParenthesisL = findViewById(R.id.b_parenthesisL);
            buttonParenthesisR = findViewById(R.id.b_parenthesisR);
            buttonFactorial = findViewById(R.id.b_factorial);
            buttonSin = findViewById(R.id.b_sin);
            buttonCos = findViewById(R.id.b_cos);
            buttonTan = findViewById(R.id.b_tan);
            buttonASin = findViewById(R.id.b_asin);
            buttonACos = findViewById(R.id.b_acos);
            buttonATan = findViewById(R.id.b_atan);
            buttonLn = findViewById(R.id.b_ln);
            buttonLog = findViewById(R.id.b_log);
            buttonExp = findViewById(R.id.b_exp);
            buttonPi = findViewById(R.id.b_pi);
            buttonE = findViewById(R.id.b_e);
            buttonMore = findViewById(R.id.b_more);
            buttonLess = findViewById(R.id.b_less);
        } else {
            buttonPlusMinus = findViewById(R.id.b_plusMinus);
        }
        // Localize decimal separator
        buttonDot.setText(decimal_separator);
        // Answer button should be disabled at beginning
        buttonAns.setEnabled(ans != 0D);
        // Vibrator
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private void addListeners() {
        button0.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b0));
        });
        button1.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b1));
        });
        button2.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b2));
        });
        button3.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b3));
        });
        button4.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b4));
        });
        button5.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b5));
        });
        button6.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b6));
        });
        button7.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b7));
        });
        button8.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b8));
        });
        button9.setOnClickListener(v -> {
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
        buttonDot.setOnClickListener(v -> {
            _hapticFeedback();
            _dot();
        });
        buttonEqual.setOnClickListener(v -> {
            _hapticFeedback();
            _equal();
        });
        buttonAdd.setOnClickListener(v -> {
            _hapticFeedback();
            _operator(getString(R.string.add));
        });
        buttonBackspace.setOnClickListener(v -> {
            _hapticFeedback();
            _backSpace();
        });
        buttonBackspace.setOnLongClickListener(v -> _backSpaceLong());
        buttonDivide.setOnClickListener(v -> {
            _hapticFeedback();
            _operator(getString(R.string.divide));
        });
        buttonMultiply.setOnClickListener(v -> {
            _hapticFeedback();
            _operator(getString(R.string.multiply));
        });
        buttonPow.setOnClickListener(v -> {
            _hapticFeedback();
            _operator("^");
        });
        buttonSubtract.setOnClickListener(v -> {
            _hapticFeedback();
            _operator(getString(R.string.subtract));
        });
        // Specific buttons of each layout
        if (layout.equals(LayoutMode.EXTENDED)) {
            buttonPercent.setOnClickListener(v -> {
                _hapticFeedback();
                _operator(getString(R.string.percent));
            });
            buttonParenthesisL.setOnClickListener(v -> {
                _hapticFeedback();
                _operator(getString(R.string.parenthesisL));
            });
            buttonParenthesisR.setOnClickListener(v -> {
                _hapticFeedback();
                _operator(getString(R.string.parenthesisR));
            });
            buttonFactorial.setOnClickListener(v -> {
                _hapticFeedback();
                _operator(getString(R.string.factorial));
            });
            buttonSin.setOnClickListener(v -> {
                _hapticFeedback();
                _function(getString(R.string.sin));
            });
            buttonCos.setOnClickListener(v -> {
                _hapticFeedback();
                _function(getString(R.string.cos));
            });
            buttonTan.setOnClickListener(v -> {
                _hapticFeedback();
                _function(getString(R.string.tan));
            });
            buttonASin.setOnClickListener(v -> {
                _hapticFeedback();
                _function(getString(R.string.asin));
            });
            buttonACos.setOnClickListener(v -> {
                _hapticFeedback();
                _function(getString(R.string.acos));
            });
            buttonATan.setOnClickListener(v -> {
                _hapticFeedback();
                _function(getString(R.string.atan));
            });
            buttonLn.setOnClickListener(v -> {
                _hapticFeedback();
                _function(getString(R.string.ln));
            });
            buttonLog.setOnClickListener(v -> {
                _hapticFeedback();
                _function(getString(R.string.log));
            });
            buttonExp.setOnClickListener(v -> {
                _hapticFeedback();
                _exp();
            });
            buttonPi.setOnClickListener(v -> {
                _hapticFeedback();
                _operator(getString(R.string.pi));
            });
            buttonE.setOnClickListener(v -> {
                _hapticFeedback();
                _operator(getString(R.string.e));
            });
            buttonMore.setOnClickListener(v -> {
                _hapticFeedback();
                _more(true);
            });
            buttonLess.setOnClickListener(v -> {
                _hapticFeedback();
                _more(false);
            });
        } else {
            buttonPlusMinus.setOnClickListener(v -> {
                _hapticFeedback();
                _plusMinus();
            });
        }
    }

    private void _ans() {
        if (layout.equals(LayoutMode.EXTENDED)) {
            _parseAnswerToMain();
        } else {
            _parseAnswerToTop();
            equalPressed = true;
        }
        _updatePreferences();
    }

    private void _backSpace() {
        String _main = editTextMain.getText().toString();
        int limit = _main.startsWith(getString(R.string.subtract)) ? 2 : 1;
        if (_main.length() > limit) {
            if (layout.equals(LayoutMode.EXTENDED)) {
                if (_main.endsWith(getString(R.string.sin) + getString(R.string.parenthesisL))) {
                    editTextMain.setText(_main.substring(0, _main.length() - getString(R.string.sin).length() - 1));
                } else if (_main.endsWith(getString(R.string.cos) + getString(R.string.parenthesisL))) {
                    editTextMain.setText(_main.substring(0, _main.length() - getString(R.string.cos).length() - 1));
                } else if (_main.endsWith(getString(R.string.tan) + getString(R.string.parenthesisL))) {
                    editTextMain.setText(_main.substring(0, _main.length() - getString(R.string.tan).length() - 1));
                } else if (_main.endsWith(getString(R.string.asin) + getString(R.string.parenthesisL))) {
                    editTextMain.setText(_main.substring(0, _main.length() - getString(R.string.asin).length() - 1));
                } else if (_main.endsWith(getString(R.string.acos) + getString(R.string.parenthesisL))) {
                    editTextMain.setText(_main.substring(0, _main.length() - getString(R.string.acos).length() - 1));
                } else if (_main.endsWith(getString(R.string.atan) + getString(R.string.parenthesisL))) {
                    editTextMain.setText(_main.substring(0, _main.length() - getString(R.string.atan).length() - 1));
                } else if (_main.endsWith(getString(R.string.ln) + getString(R.string.parenthesisL))) {
                    editTextMain.setText(_main.substring(0, _main.length() - getString(R.string.ln).length() - 1));
                } else if (_main.endsWith(getString(R.string.log) + getString(R.string.parenthesisL))) {
                    editTextMain.setText(_main.substring(0, _main.length() - getString(R.string.log).length() - 1));
                } else {
                    editTextMain.setText(_main.substring(0, _main.length() - 1));
                }
                if (editTextMain.getText().toString().equals("")) {
                    editTextMain.setText(getString(R.string.b0));
                }
            } else {
                editTextMain.setText(_main.substring(0, _main.length() - 1));
            }
        } else if (_main.length() == limit) {
            String _top = textViewTop.getText().toString();
            String _op = textViewOp.getText().toString();
            if (!_top.equals("") && !_op.equals("")) {
                textViewTop.setText("");
                textViewOp.setText("");
                editTextMain.setText(_top);
            } else {
                editTextMain.setText(getString(R.string.b0));
                equalPressed = !textViewTop.getText().toString().isEmpty();
            }
        }
        _main = editTextMain.getText().toString();
        if (_main.endsWith(decimal_separator)) {
            editTextMain.setText(_main.substring(0, _main.length() - 1));
        }
        if (_main.endsWith(getString(R.string.exp))) {
            _backSpace();
        }
        _updatePreferences();
    }

    private boolean _backSpaceLong() {
        String _main = editTextMain.getText().toString();
        if (_main.equals(getString(R.string.b0))) {
            return false;
        }
        editTextMain.setText(getString(R.string.b0));
        _updatePreferences();
        return true;
    }

    private void _clear() {
        textViewTop.setText("");
        textViewOp.setText("");
        editTextMain.setText(getString(R.string.b0));
        equalPressed = false;
        _updatePreferences();
    }

    private void _dot() {
        String s;
        if (!editTextMain.getText().toString().contains(decimal_separator) || layout.equals(LayoutMode.EXTENDED)) {
            s = editTextMain.getText().toString() + decimal_separator;
            editTextMain.setText(s);
        }
        _updatePreferences();
    }

    private void _equal() {
        try {
            if (layout.equals(LayoutMode.EXTENDED)) {
                ans = StringEvaluator.evaluate(this, editTextMain.getText().toString(), decimal_separator);
            } else {
                double d1;
                double d2;
                NumberFormat nf = NumberFormat.getInstance(locale);
                if (textViewOp.getText().toString().equals(getString(R.string.add))) {
                    d1 = ans;
                    d2 = Objects.requireNonNull(nf.parse(editTextMain.getText().toString())).doubleValue();
                    ans = d1 + d2;
                } else if (textViewOp.getText().toString().equals(getString(R.string.subtract))) {
                    d1 = ans;
                    d2 = Objects.requireNonNull(nf.parse(editTextMain.getText().toString())).doubleValue();
                    ans = d1 - d2;
                } else if (textViewOp.getText().toString().equals(getString(R.string.multiply))) {
                    d1 = ans;
                    d2 = Objects.requireNonNull(nf.parse(editTextMain.getText().toString())).doubleValue();
                    ans = d1 * d2;
                } else if (textViewOp.getText().toString().equals(getString(R.string.divide))) {
                    d1 = ans;
                    d2 = Objects.requireNonNull(nf.parse(editTextMain.getText().toString())).doubleValue();
                    ans = d1 / d2;
                } else if (textViewOp.getText().toString().equals(getString(R.string.pow))) {
                    d1 = ans;
                    d2 = Objects.requireNonNull(nf.parse(editTextMain.getText().toString())).doubleValue();
                    ans = Math.pow(d1, d2);
                } else {
                    d2 = Objects.requireNonNull(nf.parse(editTextMain.getText().toString())).doubleValue();
                    ans = d2;
                }
            }
            _clear();
            _parseAnswerToTop();
            // Enable answer button
            buttonAns.setEnabled(true);
            equalPressed = true;
        } catch (ParseException e) {
            _clear();
            _parseAnswerToTop();
            equalPressed = true;
        } catch (SyntaxException e) {
            equalPressed = false;
            Toast t = Toast.makeText(this, getString(R.string.syntaxError), Toast.LENGTH_LONG);
            t.show();
        }
        _updatePreferences();
    }

    private void _parseAnswerToMain() {
        if (Double.toString(ans).contains("E")) {
            editTextMain.setText(scientificFormat.format(ans));
        } else {
            editTextMain.setText(decimalFormat.format(ans));
        }
    }

    private void _parseAnswerToTop() {
        if (Double.toString(ans).contains("E")) {
            textViewTop.setText(scientificFormat.format(ans));
        } else {
            textViewTop.setText(decimalFormat.format(ans));
        }
    }

    private void _number(String s) {
        if (editTextMain.getText().toString().equals(getString(R.string.b0))) {
            editTextMain.setText("");
        }
        if (editTextMain.getText().toString().equals(getString(R.string.subtract) + getString(R.string.b0))) {
            editTextMain.setText(getString(R.string.subtract));
        }
        s = editTextMain.getText().toString() + s;
        editTextMain.setText(s);
        equalPressed = false;
        _updatePreferences();
    }

    private void _operator(String s) {
        if (layout.equals(LayoutMode.EXTENDED)) {
            if (editTextMain.getText().toString().equals(getString(R.string.b0))
                    && !s.equals(getString(R.string.multiply))
                    && !s.equals(getString(R.string.divide))
                    && !s.equals(getString(R.string.pow))
                    && !s.equals(getString(R.string.percent))
                    && !s.equals(getString(R.string.factorial))) {
                editTextMain.setText("");
            }
            s = editTextMain.getText().toString() + s;
            editTextMain.setText(s);
        } else {
            if (!equalPressed) {
                _equal();
            }
            textViewOp.setText(s);
            equalPressed = false;
        }
        _updatePreferences();
    }

    private void _plusMinus() {
        String _main = editTextMain.getText().toString();
        if (_main.charAt(0) == getString(R.string.subtract).charAt(0)) {
            editTextMain.setText(_main.replace(getString(R.string.subtract), ""));
        } else {
            _main = getString(R.string.subtract) + _main;
            editTextMain.setText(_main);
        }
        _updatePreferences();
    }

    private void _function(String s) {
        if (layout.equals(LayoutMode.EXTENDED)) {
            if (editTextMain.getText().toString().equals(getString(R.string.b0))) {
                editTextMain.setText("");
            }
            if (editTextMain.getText().toString().equals(getString(R.string.subtract) + getString(R.string.b0))) {
                editTextMain.setText(getString(R.string.subtract));
            }
            s = editTextMain.getText().toString() + s + getString(R.string.parenthesisL);
            editTextMain.setText(s);
        }
        _updatePreferences();
    }

    private void _exp() {
        String _main = editTextMain.getText().toString();
        try {
            Integer.parseInt(_main.substring(_main.length() - 1));
            buttonMultiply.performClick();
            button1.performClick();
            button0.performClick();
            buttonPow.performClick();
        } catch (NumberFormatException e) {
           if (_main.endsWith(getString(R.string.e)) || _main.endsWith(getString(R.string.pi))) {
               buttonMultiply.performClick();
               button1.performClick();
               button0.performClick();
               buttonPow.performClick();
           }
        }
        _updatePreferences();
    }

    private void _more(boolean showMore) {
        int orientation = getResources().getConfiguration().orientation;

        buttonMore.setVisibility(showMore ? View.GONE : View.VISIBLE);
        buttonLess.setVisibility(showMore ? View.VISIBLE : View.GONE);

        buttonSin.setVisibility(showMore ? View.GONE : View.VISIBLE);
        buttonASin.setVisibility(showMore ? View.VISIBLE : View.GONE);

        buttonCos.setVisibility(showMore ? View.GONE : View.VISIBLE);
        buttonACos.setVisibility(showMore ? View.VISIBLE : View.GONE);

        buttonTan.setVisibility(showMore ? View.GONE : View.VISIBLE);
        buttonATan.setVisibility(showMore ? View.VISIBLE : View.GONE);

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            buttonExp.setVisibility(showMore ? View.GONE : View.VISIBLE);
            buttonPi.setVisibility(showMore ? View.VISIBLE : View.GONE);

            buttonPercent.setVisibility(showMore ? View.GONE : View.VISIBLE);
            buttonE.setVisibility(showMore ? View.VISIBLE : View.GONE);

            buttonFactorial.setVisibility(showMore ? View.GONE : View.VISIBLE);
            buttonLn.setVisibility(showMore ? View.VISIBLE : View.GONE);

            buttonPow.setVisibility(showMore ? View.GONE : View.VISIBLE);
            buttonLog.setVisibility(showMore ? View.VISIBLE : View.GONE);
        } else {
            buttonE.setVisibility(showMore ? View.GONE : View.VISIBLE);
            buttonLn.setVisibility(showMore ? View.VISIBLE : View.GONE);

            buttonPi.setVisibility(showMore ? View.GONE : View.VISIBLE);
            buttonLog.setVisibility(showMore ? View.VISIBLE : View.GONE);
        }
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
        editor.putString("top", textViewTop.getText().toString());
        editor.putString("op", textViewOp.getText().toString());
        editor.putString("main", editTextMain.getText().toString());
        editor.putBoolean("equalPressed", equalPressed);
        editor.putLong("ans", Double.doubleToRawLongBits(ans));
        editor.putString("layout", layout.name());
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
