package com.mycompany.trabalho.threads.dsd.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import utils.LeitorMalhaViaria;

public class Rua {

    private Celula[][] matrizMalhaViaria;
    private CopyOnWriteArrayList<Celula> pontosDeEntrada = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Carro> carros = new CopyOnWriteArrayList<>();

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
        if (carro.obterCelulaAtual() != null) {
            carro.obterCelulaAtual().removerCarroDaCelula();
            carro.definirCelulaAtual(null);
        }
        this.carros.remove(carro);
    }

    public CopyOnWriteArrayList<Carro> getCarros() {
        return carros;
    }

    public Celula celulaParaBaixo(Celula celula) {
        int novaLinha = celula.getLinha() + 1;
        int novacoluna = celula.getColuna();
        if (novaLinha >= matrizMalhaViaria.length) return null;
        return matrizMalhaViaria[novaLinha][novacoluna];
    }

    public Celula celulaParaCima(Celula celula) {
        int novaLinha = celula.getLinha() - 1;
        int novacoluna = celula.getColuna();
        if (novaLinha < 0) return null;
        return matrizMalhaViaria[novaLinha][novacoluna];
    }

    public Celula celulaParaDireita(Celula celula) {
        int novaLinha = celula.getLinha();
        int novacoluna = celula.getColuna() + 1;
        if (novacoluna >= matrizMalhaViaria[0].length) return null;
        return matrizMalhaViaria[novaLinha][novacoluna];
    }

    public Celula celulaParaEsquerda(Celula celula) {
        int novaLinha = celula.getLinha();
        int novacoluna = celula.getColuna() - 1;
        if (novacoluna < 0) return null;
        return matrizMalhaViaria[novaLinha][novacoluna];
    }
}