package uk.ac.nulondon;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

public class AppTest {
    @Test
    void helloTest() {
        Assertions.assertThat(2 + 2).isEqualTo(4);
    }

    @Test
    void testYWord() {
        Assertions.assertThat(App.startsWithY("juan")).isEqualTo(false);
        Assertions.assertThat(App.startsWithY("froyo")).isEqualTo(false);
        Assertions.assertThat(App.startsWithY("yo")).isEqualTo(true);

    }
    @Test
    void testBingoWord() {
        Assertions.assertThat(App.bingoWord("test")).isEqualTo("T 4");
        Assertions.assertThat(App.bingoWord("Yosh")).isEqualTo("Y 4");
        Assertions.assertThat(App.bingoWord("sos")).isEqualTo("S 3");

    }

}

//isEqualTo(false));