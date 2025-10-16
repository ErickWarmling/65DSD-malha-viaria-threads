package com.mycompany.trabalho.threads.dsd.model;

import java.util.ArrayList;
import java.util.List;
import util.ArquivoMalhaReader;

/**
 * Representa a malha viária completa.
 */
public class Malha {

    private Celula[][] matriz;
    private int linhas;
    private int colunas;
    private List<Celula> entradas;
    private List<Celula> saidas;

    /**
     * Carrega malha de arquivo e identifica entradas/saídas.
     */
    public Malha(String caminhoArquivo) {
        carregarMalha(caminhoArquivo);
        identificarEntradasSaidas();
    }

    /**
     * Lê arquivo e cria matriz de células.
     */
    private void carregarMalha(String caminhoArquivo) {
        int[][] dados = ArquivoMalhaReader.lerMalha(caminhoArquivo);

        if (dados == null || dados.length == 0) {
            throw new IllegalArgumentException("Arquivo de malha inválido");
        }

        this.linhas = dados.length;
        this.colunas = dados[0].length;
        this.matriz = new Celula[linhas][colunas];

        // Cria células passando totalColunas para cálculo de ID único
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                int tipo = dados[i][j];
                matriz[i][j] = new Celula(i, j, tipo, colunas);
            }
        }
    }

    /**
     * Percorre malha identificando células de entrada e saída.
     */
    private void identificarEntradasSaidas() {
        entradas = new ArrayList<>();
        saidas = new ArrayList<>();

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                Celula celula = matriz[i][j];
                if (celula.isEntrada(linhas, colunas)) {
                    entradas.add(celula);
                }
                if (celula.isSaida(linhas, colunas)) {
                    saidas.add(celula);
                }
            }
        }

        System.out.println("Malha carregada: " + linhas + "x" + colunas);
        System.out.println("Entradas: " + entradas.size());
        System.out.println("Saídas: " + saidas.size());
    }

    public Celula getCelula(int linha, int coluna) {
        if (linha < 0 || linha >= linhas || coluna < 0 || coluna >= colunas) {
            return null;
        }
        return matriz[linha][coluna];
    }

    public Celula[][] getMatriz() {
        return matriz;
    }

    public int getLinhas() {
        return linhas;
    }

    public int getColunas() {
        return colunas;
    }

    public List<Celula> getEntradas() {
        return new ArrayList<>(entradas);
    }

    public List<Celula> getSaidas() {
        return new ArrayList<>(saidas);
    }
}

