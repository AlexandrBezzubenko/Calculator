package testtask.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

import static testtask.calculator.Calculator.*;

public class MainActivity extends Activity {

    private TextView screen1; // displays first number
    private TextView screen2; // displays second  (entery screen)
    private TextView screen3; // displays result
    private String buffer = "";
    private String operation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screen1 = (TextView) findViewById(R.id.text_field1);
        screen2 = (TextView) findViewById(R.id.text_field2);
        screen3 = (TextView) findViewById(R.id.result_field);

        Button btnClear = (Button)findViewById(R.id.btn_clear);
        btnClear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                buffer = "";
                operation = "";
                screen1.setText("");
                screen2.setText("");
                screen3.setText("");

                firstNumber = BigDecimal.ZERO;
                secondNumber = BigDecimal.ZERO;
                result = BigDecimal.ZERO;
                return true;
            }
        });
    }

    public void onNumberClick(View view) {
        if (allowEnter()) {
            CharSequence num = ((Button)findViewById(view.getId())).getText();
            if (num.charAt(0) == '.') {
                if (buffer.contains(".")) {
                    return;
                }
                if (buffer.length() == 0 || (buffer.length() == 1 && buffer.charAt(0) == '-')) {
                    buffer += "0";
                }
            }
            buffer += num;
            updateEnteryScreen(format(buffer, true));
        }
    }

    public void onOperationClick(View view) {
        if(buffer.equals("")) {
            if(screen1.getText().length() == 0) {
                operation = ((Button)findViewById(view.getId())).getText().toString();
                if (!operation.equals("-")) {
                    operation = "";
                    return;
                }
                buffer = "-";
                operation = "";
                updateEnteryScreen(buffer);
                return;
            }
            if(screen2.getText().length() == 1) {
                if(((Button)findViewById(view.getId())).getText().toString().equals("-")) {
                    buffer = "-";
                    updateEnteryScreen(buffer);
                    return;
                }
            }
            if(screen3.getText().length() != 0) {
                firstNumber = result;
                updateFirstScreen(screen1, firstNumber);
                screen3.setText("");
            }
            operation = ((Button) findViewById(view.getId())).getText().toString();
            updateEnteryScreen(buffer);
        } else if(buffer.length() == 1 && buffer.charAt(0) == '-') {
            return;
        } else {
            String oldOperation = operation;
            operation = ((Button) findViewById(view.getId())).getText().toString();
            if (screen1.getText().length() == 0) {
                firstNumber = new BigDecimal(buffer);
                updateFirstScreen(screen1, firstNumber);
                buffer = "";
                updateEnteryScreen(buffer);
            } else {
                secondNumber = new BigDecimal(buffer);
                switch (oldOperation) {
                    case "+":
                        firstNumber = sum();
                        break;
                    case "-":
                        firstNumber = sub();
                        break;
                    case "\u00D7": // multiplication symbol
                        firstNumber = mul();
                        break;
                    case "\u00F7": // division symbol
                        try {
                            firstNumber = div();
                        } catch(ArithmeticException e) {
                            Toast.makeText(MainActivity.this, "Division ZERO", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                }
                updateFirstScreen(screen1, firstNumber);
                buffer = "";
                updateEnteryScreen(buffer);
            }
        }
    }

    public void onEqualClick(View view) {
        if (screen1.getText().length() == 0) {
            return;
        }
        if(screen2.getText().length() != 0) {
            if(!buffer.isEmpty()) {
                if(buffer.length() == 1 && buffer.charAt(0) == '-') {
                    return;
                } else {
                    secondNumber = new BigDecimal(buffer);
                }
            }
            if(screen3.getText().length() != 0) {
                firstNumber = result;
                updateFirstScreen(screen1, firstNumber);
            }
            switch (operation) {
                case "+":
                    sum();
                    break;
                case "-":
                    sub();
                    break;
                case "\u00D7": // multiplication symbol
                    mul();
                    break;
                case "\u00F7": // division symbol
                    try {
                        div();
                    } catch(ArithmeticException e) {
                        Toast.makeText(MainActivity.this, "Division ZERO", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
            }
            buffer = "";
            updateFirstScreen(screen3, result);
        }
    }

    public void onClearClick(View view) {
        if (buffer.length() > 0) {
            buffer = buffer.substring(0, buffer.length() - 1);
            updateEnteryScreen(buffer);
        } else if (!operation.equals("")) {
            operation = "";
            updateEnteryScreen(buffer);
        }
    }

    private void updateEnteryScreen(String display) {
        screen2.setText(operation + display);
    }

    private void updateFirstScreen(TextView screen, BigDecimal number) {
        String str = String.valueOf(number);
        screen.setText(format(str, false));
    }

    /**
     * Returns true if digital input is allowed.
     * In case the buffer is empty but the first screen is not
     * - allowed to enter digits after entering the operator.
     * In other cases, the input is enabled, if the buffer length is not more than 12 characters.
     */
    private boolean allowEnter() {
        if (buffer.isEmpty() && screen1.getText().length() != 0) {
            return (!operation.isEmpty());
        } else {
            return (buffer.length() < 12);
        }
    }

    /**
     * Returns {@code String} object representing formated view of number.
     * In formated view every three digits of integer part are separated by space
     * and fraction part ends with non-zero digit.
     * In case integer and fraction parts contain more than 12 symbols,
     * the fraction part trims to match to 12 symbols of total length.
     * In case the integer part contains more than 12 symbols,
     * the number appears in format XXX XXX x 10^Y.
     *
     * @param number {@code String} representation of formating number
     *
     * @param forEnteryScreen true if number formating for entery screen
     *                        (fraction part not formated)
     */
    private String format (String number, boolean forEnteryScreen) {
        String sign = "";
        String intPart = "";
        String fracPart = "";
        String pow = "";
        StringBuilder formated;

        if (number.charAt(0) == '-') {
            sign = "-";
            number = number.substring(1);
        }
        if (number.contains(".")) {
            intPart = number.substring(0, number.indexOf('.'));
            if (!forEnteryScreen) {
                fracPart = formatFrac(number.substring(number.indexOf('.')));
            } else {
                fracPart = number.substring(number.indexOf('.'));
            }
        } else {
            intPart = number;
        }
        if ((intPart.length() + fracPart.length()) > 12) {
            if (intPart.length() > 12) {
                pow = "×10^" + (intPart.length() - 6);
                intPart = intPart.substring(0, 6);
                fracPart = "";
            } else {
                fracPart = formatFrac(fracPart.substring(0, 12 - intPart.length()));
            }
        }
        formated = formatInt(intPart);
        formated.insert(0,sign).append(pow).append(fracPart);
        return formated.toString();
    }

    /**
     * Returns {@code StringBuilder} object representing formated view of integer part of number.
     * Method separates every three digits by space starting from the end.
     *
     * @param number {@code String} representation of formating number
     */
    private StringBuilder formatInt(String number) {
        StringBuilder formated = new StringBuilder(number);
        if (number.length() > 3) {
            formated.insert(formated.length() - 3, ' ');
        }
        if (number.length() > 6) {
            formated.insert(formated.length() - 7, ' ');
        }
        if (number.length() > 9) {
            formated.insert(formated.length() - 11, ' ');
        }
        return formated;
    }

    /**
     *  Returns {@code String} object representing formated view of fraction part of number.
     *  Method delete zeros at the end of fraction part.
     *
     * @param fracPart {@code String} representation of fraction part of formating number
     */
    private String formatFrac(String fracPart) {
        while(fracPart.endsWith("0")) {
            fracPart = fracPart.substring(0,fracPart.length() - 1);
        }
        if (fracPart.endsWith(".")) {
            fracPart = "";
        }
        return fracPart;
    }

}