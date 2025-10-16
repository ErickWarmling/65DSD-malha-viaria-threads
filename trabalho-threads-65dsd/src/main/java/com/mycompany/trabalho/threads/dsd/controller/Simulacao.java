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
                if (inserindo && rua.getCarros().size() < quantidadeMaximaVeiculos) {
                    inserirNovoCarro();
                    Thread.sleep(intervaloInsercao);
                } else {
                    Thread.sleep(100);
                }
                
                limparCarrosInativos();
            } catch (InterruptedException e) {
                System.out.println("Simulação interrompida");
                break;
            }
        }
    }
    
    private void inserirNovoCarro() {
        List<Celula> entradas = obterPontosDeEntrada();
        
        if (entradas.isEmpty()) {
            System.out.println("Nenhum ponto de entrada disponível");
            return;
        }
        
        Celula entradaSelecionada = entradas.get(random.nextInt(entradas.size()));
        
        if (entradaSelecionada.getCarro() == null) {
            int velocidade = 500 + random.nextInt(500);
            Carro novoCarro = new Carro(rua, velocidade);
            
            novoCarro.setCelulaAtual(entradaSelecionada);
            novoCarro.setProximaPosicao(entradaSelecionada);
            novoCarro.setDirecao(entradaSelecionada.getDirecao());
            
            rua.adicionarCarro(novoCarro);
            novoCarro.start();
            
            System.out.println("Carro " + novoCarro.getCarroId() + " inserido na posição (" 
                    + entradaSelecionada.getLinha() + ", " + entradaSelecionada.getColuna() + ")");
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
    
    private void limparCarrosInativos() {
        List<Carro> carros = rua.getCarros();
        carros.removeIf(carro -> !carro.isAlive());
    }
    
    public void pararInsercao() {
        this.inserindo = false;
        System.out.println("Inserção de carros pausada");
    }
    
    public void continuarInsercao() {
        this.inserindo = true;
        System.out.println("Inserção de carros retomada");
    }
    
    public boolean isInserindo() {
        return inserindo;
    }
    
    public void encerrarSimulacao() {
        System.out.println("Encerrando simulação...");
        this.simulacaoAtiva = false;
        this.inserindo = false;
        
        for (Carro carro : rua.getCarros()) {
            if (carro.isAlive()) {
                carro.interrupt();
            }
        }
        
        System.out.println("Simulação encerrada");
    }
    
    public int getQuantidadeCarrosAtual() {
        return rua.getCarros().size();
    }
    
    public boolean isSimulacaoAtiva() {
        return simulacaoAtiva;
    }
}
