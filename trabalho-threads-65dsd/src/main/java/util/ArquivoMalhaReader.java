package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Leitor de arquivos de malha viária.
 */
public class ArquivoMalhaReader {

    /**
     * Lê arquivo de malha e retorna matriz de inteiros.
     */
    
    public static int[][] lerMalha(String caminhoArquivo) {
        List<int[]> linhas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            int contador = 0;
            int qtdLinhas = 0;
            int qtdColunas = 0;

            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) {
                    continue;
                }

                if (contador == 0) {
                    // Primeira linha: quantidade de linhas
                    qtdLinhas = Integer.parseInt(linha);
                } else if (contador == 1) {
                    // Segunda linha: quantidade de colunas
                    qtdColunas = Integer.parseInt(linha);
                } else {
                    // Linhas seguintes: dados da matriz
                    String[] partes = linha.split("\\s+");
                    int[] valores = new int[qtdColunas];
                    for (int i = 0; i < qtdColunas && i < partes.length; i++) {
                        valores[i] = Integer.parseInt(partes[i]);
                    }
                    linhas.add(valores);
                }
                contador++;
            }

            return linhas.toArray(new int[0][]);

        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de malha: " + e.getMessage());
            return null;
        } catch (NumberFormatException e) {
            System.err.println("Erro no formato do arquivo: " + e.getMessage());
            return null;
        }
    }
}
