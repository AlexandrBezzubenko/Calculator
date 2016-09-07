package testtask.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Calculator {

    protected static BigDecimal firstNumber;
    protected static BigDecimal secondNumber;
    protected static BigDecimal result;

    protected static BigDecimal sum() {
        result = firstNumber.add(secondNumber);
        return result;
    }

    protected static BigDecimal sub() {
        result = firstNumber.subtract(secondNumber);
        return result;
    }

    protected static BigDecimal mul() {
        result = firstNumber.multiply(secondNumber);
        return result;
    }

    protected static BigDecimal div() throws ArithmeticException {
        result = firstNumber.divide(secondNumber, 9, RoundingMode.HALF_UP);
        return result;
    }
}