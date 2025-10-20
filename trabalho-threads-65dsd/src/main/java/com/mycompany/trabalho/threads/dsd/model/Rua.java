package com.mycompany.trabalho.threads.dsd.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import utils.LeitorMalhaViaria;

public class Rua {

    private Celula[][] matrizMalhaViaria;
    private List<Celula> pontosDeEntrada = new CopyOnWriteArrayList<>();
    private List<Carro> carros = new ArrayList<>();

    public Rua(String arquivoMalha, boolean usarSemaforo) throws FileNotFoundException, IOException {
        String caminhoArquivo = "/assets/arquivos/" + arquivoMalha + ".txt";
        LeitorMalhaViaria leitorMalhaViaria = new LeitorMalhaViaria(usarSemaforo);
        this.matrizMalhaViaria = leitorMalhaViaria.criarMatrizMalhaViaria(caminhoArquivo);

        for (int i = 0; i < matrizMalhaViaria.length; i++) {
            for (int j = 0; j < matrizMalhaViaria[0].length; j++) {
                Celula celula = matrizMalhaViaria[i][j];
                if (celula.isEntrada()) {
                    pontosDeEntrada.add(celula);
                }
            }
        }
    }

    public Celula[][] getMatrizMalhaViaria() {
        return matrizMalhaViaria;
    }

    public void adicionarCarro(Carro carro) {
        this.carros.add(carro);
    }

    public void removerCarro(Carro carro) {
        carro.setProximaPosicao(null);
        if (carro.getCelulaAtual() != null) {
            carro.getCelulaAtual().removerCarroDaCelula();
            carro.setCelulaAtual(null);
        }
        this.carros.remove(carro);
    }

    public List<Carro> getCarros() {
        return carros;
    }

    public Celula celulaParaBaixo(Celula celula) {
        try {
            int novaLinha = celula.getLinha() + 1;
            int novacoluna = celula.getColuna();
            return matrizMalhaViaria[novaLinha][novacoluna];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public Celula celulaParaCima(Celula celula) {
        try {
            int novaLinha = celula.getLinha() - 1;
            int novacoluna = celula.getColuna();
            return matrizMalhaViaria[novaLinha][novacoluna];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public Celula celulaParaDireita(Celula celula) {
        try {
            int novaLinha = celula.getLinha();
            int novacoluna = celula.getColuna() + 1;
            return matrizMalhaViaria[novaLinha][novacoluna];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public Celula celulaParaEsquerda(Celula celula) {
        try {
            int novaLinha = celula.getLinha();
            int novacoluna = celula.getColuna() - 1;
            return matrizMalhaViaria[novaLinha][novacoluna];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}