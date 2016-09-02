package testtask.calculator;

public class Calculator {

    protected static double firstNumber;
    protected static double secondNumber;
    protected static double result;

    protected static double sum() {
        result = firstNumber + secondNumber;
        return result;
    }

    protected static double sub() {
        result = firstNumber - secondNumber;
        return result;
    }

    protected static double mul() {
        result = firstNumber * secondNumber;
        return result;
    }

    protected static double div() {
        result = firstNumber / secondNumber;
        return result;
    }
}