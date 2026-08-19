package dev.scx.app.test;

import dev.scx.app.ScxApp;
import org.testng.annotations.Test;

public class ScxAppTest {

    public static void main(String[] args) throws Exception {
        test1();
    }

    @Test
    public static void test1() throws Exception {
        ScxApp.builder()
            .run();
    }

}
