package com.mycompany.trabalho.threads.dsd.model;

import com.mycompany.trabalho.threads.dsd.constantes.TipoCelula;
import com.mycompany.trabalho.threads.dsd.exclusao.ExclusaoMonitor;
import com.mycompany.trabalho.threads.dsd.exclusao.ExclusaoSemaforo;
import com.mycompany.trabalho.threads.dsd.exclusao.IExclusaoMutua;
import javax.swing.ImageIcon;
import util.IconeCelula;

/**
 * Representa uma célula da malha viária.
 * Cada célula pode conter um carro e possui um mecanismo de exclusão mútua.
 */
public class Celula {

    private final int linha;
    private final int coluna;
    private final int tipo;
    private Carro carro;
    
    private final int id;
    
    private final IExclusaoMutua exclusaoSemaforo;
    private final IExclusaoMutua exclusaoMonitor;

    public Celula(int linha, int coluna, int tipo, int totalColunas) { 
        this.linha = linha;
        this.coluna = coluna;
        this.tipo = tipo;
        this.carro = null;
        this.id = linha * totalColunas + coluna; 
        
        this.exclusaoSemaforo = new ExclusaoSemaforo();
        this.exclusaoMonitor = new ExclusaoMonitor();
    }
    
    public int getId() {
        return id;
    }
    
    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    public int getTipo() {
        return tipo;
    }

    public synchronized boolean temCarro() {
        return carro != null;
    }

    public synchronized Carro getCarro() {
        return carro;
    }

    public ImageIcon getIcon() {
        int idCarro = temCarro() ? carro.getCarroId() : -1;
        return IconeCelula.getInstance().getIconeCelula(tipo, idCarro);
    }
    
    public synchronized boolean podeEntrar() {
        return carro == null && tipo != TipoCelula.VAZIO;
    }

    public synchronized void entrarCarro(Carro novoCarro) {
        if (carro != null) {
            throw new IllegalStateException(
                "Célula [" + linha + "," + coluna + "] já possui um carro!"
            );
        }
        this.carro = novoCarro;
    }

    public synchronized void sairCarro() {
        this.carro = null;
    }

    // Exclusão Mútua 
    
    /**
     * Tenta adquirir o recurso usando a estratégia escolhida.
     */
    public boolean tentarAdquirir(boolean usarSemaforo) {
        return usarSemaforo 
            ? exclusaoSemaforo.tentarAdquirir() 
            : exclusaoMonitor.tentarAdquirir();
    }
    
    /**
     * Libera o recurso usando a estratégia escolhida.
     */
    public void liberar(boolean usarSemaforo) {
        if (usarSemaforo) {
            exclusaoSemaforo.liberar();
        } else {
            exclusaoMonitor.liberar();
        }
    }
    
    public boolean isCruzamento() {
        return tipo >= TipoCelula.CRUZAMENTO_CIMA 
            && tipo <= TipoCelula.CRUZAMENTO_BAIXO_ESQUERDA;
    }

    public boolean isEntrada(int linhas, int colunas) {
        if (tipo == TipoCelula.VAZIO) {
            return false;
        }

        boolean naBorda = (linha == 0 || linha == linhas - 1 
                        || coluna == 0 || coluna == colunas - 1);

        if (!naBorda) {
            return false;
        }

        // Verifica se a direção da estrada aponta para dentro da malha
        return (linha == 0 && tipo == TipoCelula.ESTRADA_BAIXO)
            || (linha == linhas - 1 && tipo == TipoCelula.ESTRADA_CIMA)
            || (coluna == 0 && tipo == TipoCelula.ESTRADA_DIREITA)
            || (coluna == colunas - 1 && tipo == TipoCelula.ESTRADA_ESQUERDA);
    }

    public boolean isSaida(int linhas, int colunas) {
        if (tipo == TipoCelula.VAZIO) {
            return false;
        }

        boolean naBorda = (linha == 0 || linha == linhas - 1 
                        || coluna == 0 || coluna == colunas - 1);

        if (!naBorda) {
            return false;
        }

        // Verifica se a direção da estrada aponta para fora da malha
        return (linha == 0 && tipo == TipoCelula.ESTRADA_CIMA)
            || (linha == linhas - 1 && tipo == TipoCelula.ESTRADA_BAIXO)
            || (coluna == 0 && tipo == TipoCelula.ESTRADA_ESQUERDA)
            || (coluna == colunas - 1 && tipo == TipoCelula.ESTRADA_DIREITA);
    }

    @Override
    public String toString() {
        return "Celula[" + linha + "," + coluna + "]";
    }
}