package testtask.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static testtask.calculator.Calculator.*;

public class MainActivity extends Activity {

    private TextView screen1; // displays first number
    private TextView screen2; // displays second number
    private TextView screen3; // displays result
    private String display = "";
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
                display = "";
                operation = "";
                screen1.setText("");
                screen2.setText("");
                screen3.setText("");

                firstNumber = 0;
                secondNumber = 0;
                result = 0;
                return true;
            }
        });
    }

    public void updateEnteryScreen(String display) {
        String intPart = "";
        String fracPart = "";
        if (display.contains(".")) {
            intPart = display.substring(0, display.indexOf('.'));
            fracPart = display.substring(display.indexOf('.'));
        } else {
            intPart = display;
        }
        StringBuilder sb = new StringBuilder(intPart);
        if (intPart.length() > 3) {
            sb.insert(sb.length() - 3, ' ');
        }
        if (intPart.length() > 6) {
            sb.insert(sb.length() - 7, ' ');
        }
        if (intPart.length() > 9) {
            sb.insert(sb.length() - 11, ' ');
        }
        screen2.setText(sb.append(fracPart).insert(0, operation).toString());
    }

    public void updateFirstScreen(TextView screen, double number) {
        screen.setText(String.valueOf(number));
    }


    public void onNumberClick(View view) {
        if (allowEnter()) {
            CharSequence num = ((Button) findViewById(view.getId())).getText();
            if (num.charAt(0) == '.') {
                if (display.contains(".")) {
                    return;
                }
                if (display.length() == 0 || (display.length() == 1 && display.charAt(0) == '-')) {
                    display += "0";
                }
            }
            display += num;
            updateEnteryScreen(display);
        }
    }

    public void onOperationClick(View view) {
        if (display.equals("")) {
            if (screen1.getText().length() == 0) {
                operation = ((Button) findViewById(view.getId())).getText().toString();
                if (!operation.equals("-")) {
                    operation = "";
                    return;
                }
                display = "-";
                operation = "";
                updateEnteryScreen(display);
                return;
            }
            if (screen3.getText().length() != 0) {
                firstNumber = result;
                updateFirstScreen(screen1, firstNumber);
                screen3.setText("");
            }
            operation = ((Button) findViewById(view.getId())).getText().toString();
            updateEnteryScreen(display);
        } else {
            String oldOperation = operation;
            operation = ((Button) findViewById(view.getId())).getText().toString();
            if (screen1.getText().length() == 0) {
                firstNumber = Double.valueOf(display);
                updateFirstScreen(screen1, firstNumber);
                display = "";
                updateEnteryScreen(display);
            } else {
                firstNumber = Double.valueOf(screen1.getText().toString());
                secondNumber = Double.valueOf(display.toString());
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
                        firstNumber = div();
                        break;
                }
                updateFirstScreen(screen1, firstNumber);
                display = "";
                updateEnteryScreen(display);
            }
        }
    }

    public void onEqualClick(View view) {
        if (screen1.getText().length() == 0) {
            return;
        } else if(screen2.getText().length() != 0) {
            if (!display.isEmpty()) {
                secondNumber = Double.valueOf(display);
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
                case "\u00D7":
                    mul();
                    break;
                case "\u00F7":
                    div();
                    break;
            }
            display = "";
            updateFirstScreen(screen3, result);
        }
    }

    public void onClearClick(View view) {
        if (display.length() > 0) {
            display = display.substring(0, display.length() - 1);
            updateEnteryScreen(display);
        } else if (!operation.equals("")) {
            operation = "";
            updateEnteryScreen(display);
        }
    }

    private boolean allowEnter() {
        if(display.isEmpty() && screen1.getText().length() != 0) {
            if(operation == "") {
                return false;
            }
            else {
                return true;
            }
        }
        if(display.length() < 12) {
            return true;
        }else {
//            char [] ch;
//            ch.
//            CharSequence cs[] = {"+", "-", "*", "/", "."};
//            String tmp = buffer;
//            for (int i = 0; i < 5; i++) {
//                if(tmp.contains(cs[i])) {
//                    tmp.
//                }
//            }
        }
//         else if(display.length() < 13) {
//            for (char ch: ops) {
//               if (display.charAt(0) == ch) {
//        }
        return false;
//                   return true;
//               }
//            }
    }
}
