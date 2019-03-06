package com.teachassist.teachassist;


import java.text.DecimalFormat;

public class tests {
    public void test(){
        DecimalFormat round = new DecimalFormat(".#");
        System.out.println(round.format(78.2312 * 100).replaceAll(",", "."));
    }

}
