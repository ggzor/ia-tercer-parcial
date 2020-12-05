
import java.io.*;
import java.nio.file.*;
import java.util.*;

import implementacion.*;

public class Explorador {
  public static void main(String[] args) throws Exception {
    Path checkpoint = Paths.get(args.length > 0 ? args[0] : "");
    int atributos = 9;
    int clases = 2;
    // Inicializar configuración de parámetros de neuronas
    Individuo.inicializar(atributos, clases);

    if (Files.exists(checkpoint)) {
      try (ObjectInputStream is = new ObjectInputStream(Files.newInputStream(checkpoint))) {
        Object valores[] = (Object[]) is.readObject();
        List<Individuo> poblacion = (List<Individuo>) valores[0];

        int contador = 1;
        for (Individuo individuo : poblacion) {
          System.out.printf("%d. %2.3f\n", contador, individuo.fitness);
          individuo.imprimir();
          System.out.println();
          
          contador++;
        }
      }
    } else {
      System.out.println("El archivo proporcionado no existe o no se proporcionó archivo.");
    }
  }
}
