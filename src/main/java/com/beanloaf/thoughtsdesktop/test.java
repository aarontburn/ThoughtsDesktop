package com.beanloaf.thoughtsdesktop;



public class test {


    public static void main(String[] args) {


        System.out.println(sum(10));


    }


    private static int sum(int boundary) {
        int sum = 0;
        for (int i = 1; i <= boundary; i++) {
            sum += i;
        }
        return sum;
    }


}
