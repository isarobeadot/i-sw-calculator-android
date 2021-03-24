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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private boolean equalPressed = false;
    private double ans = 0D;
    private Button b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, bDot;
    private Button bAAns, bAClear, bAQuit;
    private ImageButton bBackSpace;
    private Button bEqual;
    private Button bOAdd, bODivide, bOMultiply, bOPlusMinus, bOPow, bOSubtract;
    private final Locale locale = Locale.getDefault();
    private final String decimal_separator = String.format(locale, "%f", 0.0).replace("0", "");
    private final DecimalFormat decimalFormat = new DecimalFormat("#0.####################");
    private final DecimalFormat scientificFormat = new DecimalFormat("0.0#################E0");
    private TextView top;
    private TextView op;
    private TextView main;
    private SharedPreferences preferences;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        addListeners();
        _loadPreferences();
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
            CharSequence text = top.getText();
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
        b0 = findViewById(R.id.b0);
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);
        b5 = findViewById(R.id.b5);
        b6 = findViewById(R.id.b6);
        b7 = findViewById(R.id.b7);
        b8 = findViewById(R.id.b8);
        b9 = findViewById(R.id.b9);
        bAAns = findViewById(R.id.bAAns);
        bAClear = findViewById(R.id.bAClear);
        bAQuit = findViewById(R.id.bAQuit);
        bBackSpace = findViewById(R.id.bBackSpace);
        bDot = findViewById(R.id.bDot);
        bEqual = findViewById(R.id.bEqual);
        bOAdd = findViewById(R.id.bOAdd);
        bOSubtract = findViewById(R.id.bOSubtract);
        bOMultiply = findViewById(R.id.bOMultiply);
        bOPlusMinus = findViewById(R.id.bOPlusMinus);
        bODivide = findViewById(R.id.bODivide);
        bOPow = findViewById(R.id.bOPow);
        top = findViewById(R.id.top);
        op = findViewById(R.id.op);
        main = findViewById(R.id.main);
        // Localize decimal separator
        bDot.setText(decimal_separator);
        // Answer button should be disabled at beginning
        bAAns.setEnabled(false);
        // Power
        bOPow.setText(Html.fromHtml("x<sup><small>y</small></sup>"));
        // Vibrator
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private void addListeners() {
        b0.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b0));
        });
        b1.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b1));
        });
        b2.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b2));
        });
        b3.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b3));
        });
        b4.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b4));
        });
        b5.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b5));
        });
        b6.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b6));
        });
        b7.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b7));
        });
        b8.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b8));
        });
        b9.setOnClickListener(v -> {
            _hapticFeedback();
            _number(getString(R.string.b9));
        });
        bAAns.setOnClickListener(v -> {
            _hapticFeedback();
            _ans();
        });
        bAClear.setOnClickListener(v -> {
            _hapticFeedback();
            _clear();
        });
        bAQuit.setOnClickListener(v -> {
            _hapticFeedback();
            _quit();
        });
        bDot.setOnClickListener(v -> {
            _hapticFeedback();
            _dot();
        });
        bEqual.setOnClickListener(v -> {
            _hapticFeedback();
            _equal();
        });
        bOAdd.setOnClickListener(v -> {
            _hapticFeedback();
            _operand(getString(R.string.add));
        });
        bBackSpace.setOnClickListener(v -> {
            _hapticFeedback();
            _backSpace();
        });
        bBackSpace.setOnLongClickListener(v -> _backSpaceLong());
        bODivide.setOnClickListener(v -> {
            _hapticFeedback();
            _operand(getString(R.string.divide));
        });
        bOMultiply.setOnClickListener(v -> {
            _hapticFeedback();
            _operand(getString(R.string.multiply));
        });
        bOPlusMinus.setOnClickListener(v -> {
            _hapticFeedback();
            _plusMinus();
        });
        bOPow.setOnClickListener(v -> {
            _hapticFeedback();
            _operand(getString(R.string.pow));
        });
        bOSubtract.setOnClickListener(v -> {
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
        String _main = main.getText().toString();
        int limit = _main.contains(getString(R.string.subtract)) ? 2 : 1;
        if (_main.length() > limit) {
            main.setText(_main.substring(0, _main.length() - 1));
        } else if (_main.length() == limit) {
            String _top = top.getText().toString();
            String _op = op.getText().toString();
            if (!_top.equals("") && !_op.equals("")) {
                top.setText("");
                op.setText("");
                main.setText(_top);
            } else {
                main.setText(getString(R.string.b0));
                equalPressed = !top.getText().toString().isEmpty();
            }
        }
        _main = main.getText().toString();
        if (_main.substring(_main.length() - 1).equals(decimal_separator)) {
            main.setText(_main.substring(0, _main.length() - 1));
        }
        _updatePreferences();
    }

    private boolean _backSpaceLong() {
        String _main = main.getText().toString();
        if (_main.equals(getString(R.string.b0))) {
            return false;
        }
        main.setText(getString(R.string.b0));
        _updatePreferences();
        return true;
    }

    private void _clear() {
        top.setText("");
        op.setText("");
        main.setText(getString(R.string.b0));
        equalPressed = false;
        _updatePreferences();
    }

    private void _dot() {
        String s;
        if (!main.getText().toString().contains(decimal_separator)) {
            s = main.getText() + decimal_separator;
            main.setText(s);
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
            switch (op.getText().toString()) {
                case add:
                    d1 = ans;
                    d2 = Objects.requireNonNull(nf.parse(main.getText().toString())).doubleValue();
                    ans = d1 + d2;
                    break;
                case subtract:
                    d1 = ans;
                    d2 = Objects.requireNonNull(nf.parse(main.getText().toString())).doubleValue();
                    ans = d1 - d2;
                    break;
                case "x":
                    d1 = ans;
                    d2 = Objects.requireNonNull(nf.parse(main.getText().toString())).doubleValue();
                    ans = d1 * d2;
                    break;
                case "/":
                    d1 = ans;
                    d2 = Objects.requireNonNull(nf.parse(main.getText().toString())).doubleValue();
                    ans = d1 / d2;
                    break;
                case "^":
                    d1 = ans;
                    d2 = Objects.requireNonNull(nf.parse(main.getText().toString())).doubleValue();
                    ans = Math.pow(d1, d2);
                    break;
                default:
                    d2 = Objects.requireNonNull(nf.parse(main.getText().toString())).doubleValue();
                    ans = d2;
                    break;
            }
            _clear();
            _parseAnswerToTop();
            // Enable answer button
            bAAns.setEnabled(true);
        } catch (ParseException pe) {
            _clear();
            _parseAnswerToTop();
        }
        equalPressed = true;
        _updatePreferences();
    }

    private void _parseAnswerToTop() {
        if (Double.toString(ans).contains("E")) {
            top.setText(scientificFormat.format(ans));
        } else {
            top.setText(decimalFormat.format(ans));
        }
    }

    private void _loadPreferences() {
        preferences = getSharedPreferences("values", Context.MODE_PRIVATE);
        top.setText(preferences.getString("top", ""));
        op.setText(preferences.getString("op", ""));
        main.setText(preferences.getString("main", getString(R.string.b0)));
        equalPressed = preferences.getBoolean("equalPressed", false);
        ans = Double.longBitsToDouble(preferences.getLong("ans", Double.doubleToLongBits(0D)));
        if (ans != 0D) {
            bAAns.setEnabled(true);
        }
    }

    private void _number(String s) {
        if (main.getText().equals(getString(R.string.b0))) {
            main.setText("");
        }
        if (main.getText().equals(getString(R.string.subtract) + getString(R.string.b0))) {
            main.setText(getString(R.string.subtract));
        }
        s = main.getText() + s;
        main.setText(s);
        equalPressed = false;
        _updatePreferences();
    }

    private void _operand(String s) {
        if (!equalPressed) {
            _equal();
        }
        op.setText(s);
        equalPressed = false;
        _updatePreferences();
    }

    private void _plusMinus() {
        String _main = main.getText().toString();
        if (_main.charAt(0) == getString(R.string.subtract).charAt(0)) {
            main.setText(_main.replace(getString(R.string.subtract), ""));
        } else {
            _main = getString(R.string.subtract) + _main;
            main.setText(_main);
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
        editor.putString("top", top.getText().toString());
        editor.putString("op", op.getText().toString());
        editor.putString("main", main.getText().toString());
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
