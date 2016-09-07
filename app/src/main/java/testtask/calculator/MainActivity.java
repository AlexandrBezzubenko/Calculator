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
    private TextView screen2; // displays second number
    private TextView screen3; // displays result
//    private Button btnClear;
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
        if(allowEnter()) {
            CharSequence num = ((Button)findViewById(view.getId())).getText();
            if(num.charAt(0) == '.') {
                if(buffer.contains(".")) {
                    return;
                }
                if(buffer.length() == 0 || (buffer.length() == 1 && buffer.charAt(0) == '-')) {
                    buffer += "0";
                }
            }
            buffer += num;
            updateEnteryScreen(formatEntery(buffer));
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
        } else if(screen2.getText().length() != 0) {
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
        if(buffer.length() > 0) {
            buffer = buffer.substring(0, buffer.length() - 1);
            updateEnteryScreen(buffer);
        } else if(!operation.equals("")) {
            operation = "";
            updateEnteryScreen(buffer);
        }
    }

    private void updateEnteryScreen(String display) {
        screen2.setText(operation + (display)); //format
    }

    private void updateFirstScreen(TextView screen, BigDecimal number) {
        String str = String.valueOf(number);
        screen.setText(format(str));
    }

    private String format (String number) {
        String sign = "";
        String intPart = "";
        String fracPart = "";
        String pow = "";
        StringBuilder formated;
        if(number.charAt(0) == '-') {
            sign = "-";
            number = number.substring(1);
        }
        if(number.contains(".")) {
            intPart = number.substring(0, number.indexOf('.'));
            fracPart = formatFrac(number.substring(number.indexOf('.')));
        } else {
            intPart = number;
        }
        if((intPart.length() + fracPart.length()) > 12) {
            if(intPart.length() > 12) {
                pow = "×10^" + (intPart.length()-6);
                intPart = intPart.substring(0,6);
                fracPart = "";
            } else {
                fracPart = formatFrac(fracPart.substring(0, 12 - intPart.length()));
            }
        }
        formated = formatInt(intPart);
        formated.insert(0,sign).append(pow).append(fracPart);
        return formated.toString();
    }

    private String formatEntery(String number) {
        String sign = "";
        String intPart = "";
        String fracPart = "";
        StringBuilder formated;
        if(number.charAt(0) == '-') {
            sign = "-";
            number = number.substring(1);
        }
        if(number.contains(".")) {
            intPart = number.substring(0, number.indexOf('.'));
            fracPart = number.substring(number.indexOf('.'));
        } else {
            intPart = number;
        }
        formated = formatInt(intPart);
        formated.insert(0,sign).append(fracPart);
        return formated.toString();
    }


    private boolean allowEnter() {
        if(buffer.isEmpty() && screen1.getText().length() != 0) {
            return (!operation.isEmpty());
        } else {
            return (buffer.length() < 12);
        }
    }

    private StringBuilder formatInt(String intPart) {
        StringBuilder formated = new StringBuilder(intPart);
        if (intPart.length() > 3) {
            formated.insert(formated.length() - 3, ' ');
        }
        if (intPart.length() > 6) {
            formated.insert(formated.length() - 7, ' ');
        }
        if (intPart.length() > 9) {
            formated.insert(formated.length() - 11, ' ');
        }
        return formated;
    }

    private String formatFrac(String fracPart) {
        while(fracPart.endsWith("0")) {
            fracPart = fracPart.substring(0,fracPart.length() - 1);
        }
        if(fracPart.endsWith(".")) {
            fracPart = "";
        }
        return fracPart;
    }
}