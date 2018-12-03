package com.hoddmimes.fitview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Test
{

    private void test(Class pClass) {
       if (pClass == Foo.class) {
           System.out.println("Foo");
       }
        if (pClass == Integer.class) {
            System.out.println("Integer");
        }
    }

    public static void main( String[] pArgs ) {
        Test t = new Test();
        t.test( Foo.class );
        t.test( Integer.class );
    }

    public class Foo
    {

    }
}
