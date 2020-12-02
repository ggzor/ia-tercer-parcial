package implementacion;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.*;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class UtileriasTest {
  public static Stream<Arguments> generarPruebasBits() {
    return Stream.of(
      Arguments.of(
        5,
        "000 11 000",
        "000 11 100"
      ),
      Arguments.of(
        3,
        "00010 11011 1000 1111",
        "00000 11011 1000 1111"
      ),
      Arguments.of(
        3,
        "00010 11011 1000 1111",
        "00000 11011 1000 1111"
      ),
      Arguments.of(
        0,
        "0 1 1000 1011",
        "1 1 1000 1011"
      ),
      Arguments.of(
        3,
        "1 1 1 1",
        "1 1 1 0"
      ),
      Arguments.of(
        17,
        "1011 10101 1011 11101",
        "1011 10101 1011 11100"
      )
    );
  }

  @ParameterizedTest
  @MethodSource("generarPruebasBits")
  public void invertirBitFuncionaCorrectamente(int indice, String setsIniciales, String setEsperados) {
    BitSet iniciales[] = extraerBitSets(setsIniciales);
    BitSet esperados[] = extraerBitSets(setEsperados);

    int longitudes[] = Arrays.stream(setsIniciales.split(" "))
                             .mapToInt(String::length)
                             .toArray();

    Utilerias.invertirBit(iniciales, longitudes, indice);

    assertArrayEquals(esperados, iniciales);
  }

  private BitSet[] extraerBitSets(String sets) {
    return Arrays.stream(sets.split(" "))
                 .map(s -> BitSet.valueOf(new long[] { Long.valueOf(s, 2) }))
                 .toArray(BitSet[]::new);
  }
}
