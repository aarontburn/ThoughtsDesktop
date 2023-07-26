package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.handlers.Logger;

public class test {


    public static void main(String[] args) {


        try {
            Double d = null;
            d.byteValue();



        } catch (Exception e) {
            Logger.logException(e);

        }


    }


}
