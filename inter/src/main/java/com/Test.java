package com;

/**
 * Created by aponyk on 08.06.2016.
 */
public class Test {
    public static void main(String[] args) {
        System.out.println("");
        int ште = 1;
        System.out.println(p("a"));
        System.out.println(p("abcdcba"));
        System.out.println(p("abcdcbaa"));
        System.out.println(p("1aabcdcbaa"));
        System.out.println(p(""));
    }

    public static boolean p(String s){
        if (s.length() <= 1){
            return true;
        } else if (s.charAt(0) == s.charAt(s.length()-1)){
            return p (s.substring(1, s.length()-1));
        }

        return false;
    }
}
