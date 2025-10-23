package com.mycompany.trabalho.threads.dsd.model;

public abstract class Celula {

    private final int linha;
    private final int coluna;
    private final Direcao direcao;
    private Carro carro;
    private final boolean isEntrada;
    private final boolean isCruzamento;

    public Celula(int linha, int coluna, Direcao direcao, boolean isEntrada, boolean isCruzamento) {
        this.linha = linha;
        this.coluna = coluna;
        this.direcao = direcao;
        this.isEntrada = isEntrada;
        this.isCruzamento = isCruzamento;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    public Direcao getDirecao() {
        return direcao;
    }

    public Carro getCarro() {
        return carro;
    }

    public void setCarro(Carro carro) {
        this.carro = carro;
    }

    public void removerCarroDaCelula() {
        this.carro = null;
    }

    public boolean isEntrada() {
        return isEntrada;
    }

    public boolean isCruzamento() {
        return isCruzamento;
    }

    public abstract void liberar();

    public abstract void bloquear() throws InterruptedException;
    
    public abstract boolean tentarBloquear();
}