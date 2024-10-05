package uk.ac.nulondon;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringFuncsTest {
    @Test
    void startsWithYTest(){
        StringFuncs myStringFunc = new StringFuncs();
        Assertions.assertThat(myStringFunc.startsWithY("Yes")).isEqualTo(true);
    }
}
