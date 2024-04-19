package com.newcoder;

import com.newcoder.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "这里可以♥赌♥博，嫖♥娼，吸♥毒，开♥票";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }

}
