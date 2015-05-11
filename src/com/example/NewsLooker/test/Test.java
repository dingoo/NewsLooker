package com.example.NewsLooker.test;

public class Test{

    public static void main(String[] args) {

        String s="AAAAAAAA|108";
        String[] a=s.split("\\|");
        System.out.println(s.split("\\|")[1]);
        System.out.println(a[1]);
        System.out.println(a[0]);

    }
}
