package net.zpavelocity.java.lambda.syntax;

import net.zpavelocity.java.lambda.interfaces.LambdaNoneReturnMultiParameter;
import net.zpavelocity.java.lambda.interfaces.LambdaNoneReturnNoneParameter;
import net.zpavelocity.java.lambda.interfaces.LambdaNoneReturnSingleParameter;
import net.zpavelocity.java.lambda.interfaces.LambdaSingleReturnNoneParameter;

public class Syntax1 {
    public static void main(String[] args) {
        LambdaNoneReturnNoneParameter lambdaNoneReturnNoneParameter = () -> {
            System.out.println("Hello lambda");
        };
        lambdaNoneReturnNoneParameter.test();

        LambdaNoneReturnSingleParameter lambdaNoneReturnSingleParameter = (int a) -> {
            System.out.println(a);
        };
        lambdaNoneReturnSingleParameter.test(10);

        LambdaNoneReturnMultiParameter lambdaNoneReturnMultiParameter = (int a, int b) -> {
            System.out.println(a + b);
        };
        lambdaNoneReturnMultiParameter.test(10, 20);

        LambdaSingleReturnNoneParameter lambdaSingleReturnNoneParameter = () -> {
            System.out.println("Hello lambda");
            return 0;
        };
        System.out.println(lambdaSingleReturnNoneParameter.test());


    }
}
