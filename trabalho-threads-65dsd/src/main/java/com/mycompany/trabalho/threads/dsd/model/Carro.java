package com.mycompany.trabalho.threads.dsd.model;

import com.mycompany.trabalho.threads.dsd.constantes.TipoCelula;
import com.mycompany.trabalho.threads.dsd.model.Celula;
import com.mycompany.trabalho.threads.dsd.model.Malha;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Representa um carro que se move pela malha viária.
 * Implementa uma Thread independente que busca um caminho até a saída.
 * Utiliza exclusão mútua para evitar colisões.
 */
public class Carro extends Thread {

    private static int contadorId = 0;
    private static final int TEMPO_RETRY = 50; // Tempo de espera entre tentativas

    private final int id;
    private Celula posicao;
    private final Malha malha;
    private final int velocidade;
    private final boolean usarSemaforo;
    private final Random random;
    private volatile boolean rodando; //Controlar loop
    
    // Controle de direção para evitar voltar
    private Celula ultimaPosicao;

    public Carro(Celula posicaoInicial, Malha malha, int velocidade, boolean usarSemaforo) {
        this.id = ++contadorId;
        this.posicao = posicaoInicial;
        this.malha = malha;
        this.velocidade = velocidade;
        this.usarSemaforo = usarSemaforo;
        this.random = new Random();
        this.rodando = true;
        this.ultimaPosicao = null;
        setName("Carro-" + id);
    }

    @Override
    public void run() {
        try {
            System.out.println("Carro " + id + " iniciou em " + posicao + 
                             " com velocidade " + velocidade + "ms");

            while (rodando && posicao != null) {
                // Verifica se chegou na saída
                if (posicao.isSaida(malha.getLinhas(), malha.getColunas())) {
                    System.out.println("Carro " + id + " chegou na saída");
                    finalizarCarro();
                    break;
                }

                // Tenta mover
                mover();
                
                // Aguarda baseado na velocidade
                Thread.sleep(velocidade);
            }
        } catch (InterruptedException e) {
            System.out.println("Carro " + id + " foi interrompido");
        } finally {
            finalizarCarro();
        }
    }

    /**
     * Move carro para próxima célula.
     */
    private void mover() throws InterruptedException {
        Celula proxima = calcularProximaCelula();

        if (proxima == null) {
            System.out.println("Carro " + id + " sem caminho válido - finalizando");
            rodando = false;
            return;
        }

        int idAtual = posicao.getId();
        int idProxima = proxima.getId();

        if (idAtual < idProxima) {
            tentarMoverParaProxima(proxima);
        } else {
            tentarMoverComReordenacao(proxima);
        }
    }

    private void tentarMoverParaProxima(Celula proxima) throws InterruptedException {
        if (!proxima.tentarAdquirir(usarSemaforo)) {
            Thread.sleep(TEMPO_RETRY);
            return;
        }

        // verifica se próxima ainda está livre
        if (proxima.podeEntrar()) {
            realizarMovimento(proxima);
        } else {
            proxima.liberar(usarSemaforo);
            Thread.sleep(TEMPO_RETRY);
        }
    }

    private void tentarMoverComReordenacao(Celula proxima) throws InterruptedException {
        // Libera temporariamente célula atual
        posicao.liberar(usarSemaforo);

        boolean sucesso = false;
        while (!sucesso && rodando) {
            // Tenta adquirir próxima (menor ID)
            if (proxima.tentarAdquirir(usarSemaforo)) {
                // Tenta re-adquirir atual (maior ID)
                if (posicao.tentarAdquirir(usarSemaforo)) {
                    // Agora tem ambos na ordem correta
                    if (proxima.podeEntrar()) {
                        realizarMovimento(proxima);
                        sucesso = true;
                    } else {
                        // Próxima foi ocupada, libera tudo e retry
                        posicao.liberar(usarSemaforo);
                        proxima.liberar(usarSemaforo);
                        Thread.sleep(TEMPO_RETRY);
                    }
                } else {
                    // Falhou em re-adquirir atual, libera próxima e retry
                    proxima.liberar(usarSemaforo);
                    Thread.sleep(TEMPO_RETRY);
                }
            } else {
                // Falhou em adquirir próxima, aguarda
                Thread.sleep(TEMPO_RETRY);
            }
        }
    }

    /**
     * Realiza o movimento do carro de uma célula para outra.
     */
    private void realizarMovimento(Celula destino) {
        // Remove carro da posição atual
        posicao.sairCarro();
        posicao.liberar(usarSemaforo);

        // Guarda posição anterior para evitar voltar
        ultimaPosicao = posicao;

        // Entra na nova posição
        destino.entrarCarro(this);
        
        System.out.println("Carro " + id + " moveu de " + posicao + " para " + destino);
        
        posicao = destino;
    }

    /**
     * Calcula a próxima célula baseada no tipo da célula atual.
     * Evita voltar para a última posição quando possível.
     */
    private Celula calcularProximaCelula() {
        List<Celula> opcoes = obterOpcoesMovimento();

        if (opcoes.isEmpty()) {
            return null;
        }

        // Remove a opção de voltar se houver outras alternativas
        if (ultimaPosicao != null && opcoes.size() > 1) {
            opcoes.removeIf(c -> c.equals(ultimaPosicao));
        }

        // Se removeu todas as opções, recalcula sem filtro
        if (opcoes.isEmpty()) {
            opcoes = obterOpcoesMovimento();
        }

        // Escolhe aleatoriamente entre as opções válidas
        return opcoes.isEmpty() ? null : opcoes.get(random.nextInt(opcoes.size()));
    }

    /**
     * Obtém todas as células válidas baseadas no tipo da célula atual.
     */
    private List<Celula> obterOpcoesMovimento() {
        int linha = posicao.getLinha();
        int coluna = posicao.getColuna();
        int tipo = posicao.getTipo();
        
        List<Celula> opcoes = new ArrayList<>();

        switch (tipo) {
            case TipoCelula.ESTRADA_CIMA:
                adicionarCelulaSeValida(opcoes, linha - 1, coluna);
                break;
                
            case TipoCelula.ESTRADA_BAIXO:
                adicionarCelulaSeValida(opcoes, linha + 1, coluna);
                break;
                
            case TipoCelula.ESTRADA_DIREITA:
                adicionarCelulaSeValida(opcoes, linha, coluna + 1);
                break;
                
            case TipoCelula.ESTRADA_ESQUERDA:
                adicionarCelulaSeValida(opcoes, linha, coluna - 1);
                break;
                
            case TipoCelula.CRUZAMENTO_CIMA:
                adicionarCelulaSeValida(opcoes, linha - 1, coluna);
                break;
                
            case TipoCelula.CRUZAMENTO_BAIXO:
                adicionarCelulaSeValida(opcoes, linha + 1, coluna);
                break;
                
            case TipoCelula.CRUZAMENTO_DIREITA:
                adicionarCelulaSeValida(opcoes, linha, coluna + 1);
                break;
                
            case TipoCelula.CRUZAMENTO_ESQUERDA:
                adicionarCelulaSeValida(opcoes, linha, coluna - 1);
                break;
                
            case TipoCelula.CRUZAMENTO_CIMA_DIREITA:
                adicionarCelulaSeValida(opcoes, linha - 1, coluna);
                adicionarCelulaSeValida(opcoes, linha, coluna + 1);
                break;
                
            case TipoCelula.CRUZAMENTO_CIMA_ESQUERDA:
                adicionarCelulaSeValida(opcoes, linha - 1, coluna);
                adicionarCelulaSeValida(opcoes, linha, coluna - 1);
                break;
                
            case TipoCelula.CRUZAMENTO_DIREITA_BAIXO:
                adicionarCelulaSeValida(opcoes, linha, coluna + 1);
                adicionarCelulaSeValida(opcoes, linha + 1, coluna);
                break;
                
            case TipoCelula.CRUZAMENTO_BAIXO_ESQUERDA:
                adicionarCelulaSeValida(opcoes, linha + 1, coluna);
                adicionarCelulaSeValida(opcoes, linha, coluna - 1);
                break;
                
            default:
                System.err.println("Carro " + id + " em tipo de célula desconhecido: " + tipo);
        }

        return opcoes;
    }

    /**
     * Adiciona uma célula à lista se ela for válida (existe e não é vazia).
     */
    private void adicionarCelulaSeValida(List<Celula> lista, int linha, int coluna) {
        Celula celula = malha.getCelula(linha, coluna);
        if (celula != null && celula.getTipo() != TipoCelula.VAZIO) {
            lista.add(celula);
        }
    }

    /**
     * Finaliza o carro, liberando todos os recursos.
     */
    private void finalizarCarro() {
        if (posicao != null) {
            posicao.sairCarro();
            posicao.liberar(usarSemaforo);
            System.out.println("Carro " + id + " saiu da malha");
            posicao = null;
        }
        rodando = false;
    }

    /**
     * Para a execução do carro.
     */
    public void parar() {
        rodando = false;
        interrupt();
    }
    
    public Celula getPosicao() {
        return posicao;
    }

    public int getCarroId() {
        return id;
    }
    
    public int getVelocidade() {
        return velocidade;
    }
    
    public boolean isUsandoSemaforo() {
        return usarSemaforo;
    }
}