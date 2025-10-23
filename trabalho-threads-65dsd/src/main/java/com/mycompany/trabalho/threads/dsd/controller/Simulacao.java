package com.mycompany.trabalho.threads.dsd.controller;

import com.mycompany.trabalho.threads.dsd.model.Carro;
import com.mycompany.trabalho.threads.dsd.model.Celula;
import com.mycompany.trabalho.threads.dsd.model.Rua;
import com.mycompany.trabalho.threads.dsd.view.TelaSimulacao;
import java.util.List;
import java.util.Random;

public class Simulacao extends Thread {

    private final Rua rua;
    private final TelaSimulacao telaSimulacao;
    private final int quantidadeMaximaVeiculos;
    private final int intervaloInsercao;
    private volatile boolean inserindo;
    private volatile boolean simulacaoAtiva;
    private final Random random;
    private int quantidadeCarrosAnterior = 0;

    public Simulacao(Rua rua, TelaSimulacao telaSimulacao, int quantidadeMaximaVeiculos, int intervaloInsercao) {
        this.rua = rua;
        this.telaSimulacao = telaSimulacao;
        this.quantidadeMaximaVeiculos = quantidadeMaximaVeiculos;
        this.intervaloInsercao = intervaloInsercao;
        this.inserindo = true;
        this.simulacaoAtiva = true;
        this.random = new Random();
    }

    @Override
    public void run() {
        while (simulacaoAtiva) {
            try {
                telaSimulacao.repaint();
                
                int quantidadeAtual = rua.getCarros().size();
                
                if (inserindo && quantidadeAtual < quantidadeMaximaVeiculos) {
                    inserirNovoCarro();                    
                    Thread.sleep(intervaloInsercao);
                } else {
                    Thread.sleep(100);
                }  
                
                if (!inserindo && quantidadeAtual == 0) {
                    simulacaoAtiva = false;
                }         
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    private void inserirNovoCarro() {
        List<Celula> entradas = obterPontosDeEntrada();
        
        if (entradas.isEmpty()) {
            return;
        }
        
        Celula entradaSelecionada = entradas.get(random.nextInt(entradas.size()));
        
        if (entradaSelecionada.getCarro() == null) {
            try {
                entradaSelecionada.bloquear();
                if (entradaSelecionada.getCarro() == null) {
                    int velocidade = 500 + random.nextInt(500);
                    Carro novoCarro = new Carro(rua, velocidade);
                    
                    entradaSelecionada.setCarro(novoCarro);
                    novoCarro.definirCelulaAtual(entradaSelecionada);
                    novoCarro.definirDirecao(entradaSelecionada.getDirecao());
                    
                    rua.adicionarCarro(novoCarro);
                    novoCarro.start();
                    
                    telaSimulacao.getMalhaPanel().atualizar();
                }
            } catch (InterruptedException e) {
                return;
            } finally {
                entradaSelecionada.liberar();
            }
        }
    }
    
    private List<Celula> obterPontosDeEntrada() {
        List<Celula> entradas = new java.util.ArrayList<>();
        Celula[][] malha = rua.getMatrizMalhaViaria();
        
        for (int i = 0; i < malha.length; i++) {
            for (int j = 0; j < malha[0].length; j++) {
                if (malha[i][j].isEntrada()) {
                    entradas.add(malha[i][j]);
                }
            }
        }
        
        return entradas;
    }
    
    public void pararInsercao() {
        this.inserindo = false;
    }
    
    public void continuarInsercao() {
        this.inserindo = true;
    }
    
    public boolean isInserindo() {
        return inserindo;
    }
    
    public void encerrarSimulacao() {
        this.simulacaoAtiva = false;
        this.inserindo = false;
        
        for (Carro carro : rua.getCarros()) {
            if (carro.isAlive()) {
                carro.interrupt();
            }
        }
    }
    
    public int getQuantidadeCarrosAtual() {
        return rua.getCarros().size();
    }
    
    public boolean isSimulacaoAtiva() {
        return simulacaoAtiva;
    }
}