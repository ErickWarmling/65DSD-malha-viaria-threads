package com.mycompany.trabalho.threads.dsd.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MalhaViaria {
    
    private int linhas;
    private int colunas;
    private Celula[][] matrizMalha;
    
    public MalhaViaria(String caminhoArquivo) throws IOException {
        carregarMalhaViaria(caminhoArquivo);
    }

    public int getLinhas() {
        return linhas;
    }

    public void setLinhas(int linhas) {
        this.linhas = linhas;
    }

    public int getColunas() {
        return colunas;
    }

    public void setColunas(int colunas) {
        this.colunas = colunas;
    }
  
    private void carregarMalhaViaria(String caminho) throws IOException{
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(caminho))) {
            this.linhas = Integer.parseInt(bufferedReader.readLine().trim());
            this.colunas = Integer.parseInt(bufferedReader.readLine().trim());
            matrizMalha = new Celula[linhas][colunas];
            
            for (int i = 0; i < linhas; i++) {
                String[] valoresLinha = bufferedReader.readLine().trim().split("\\s+");
                for (int j = 0; j < colunas; j++) {
                    int valorColuna = Integer.parseInt(valoresLinha[j]);
                    TipoCelula tipoCelula = TipoCelula.tipoPorCodigo(valorColuna);
                    matrizMalha[i][j] = new Celula(tipoCelula);
                }
            }
        } catch (IOException ex) {
            System.out.println("Erro ao ler o arquivo: " + ex.getMessage());
            ex.printStackTrace();
        }         
    }
    
    public void imprimirMalhaViaria() {
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                System.out.print(matrizMalha[i][j].getTipoCelula().getTipo() + " ");
            }
            System.out.println();
        }
    }
}