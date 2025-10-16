package com.mycompany.trabalho.threads.dsd.controller;

import com.mycompany.trabalho.threads.dsd.model.Carro;
import com.mycompany.trabalho.threads.dsd.model.Celula;
import com.mycompany.trabalho.threads.dsd.model.Malha;
import com.mycompany.trabalho.threads.dsd.view.PainelMalha;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Controlador principal da simulação de tráfego. Gerencia a criação, execução e remoção de carros na malha.
 */
public class SimuladorController extends Thread {

    private static final int INTERVALO_ATUALIZACAO = 50; // ms
    private static final int TENTATIVAS_INSERCAO = 3;
    private static final double VARIACAO_VELOCIDADE = 0.3; // ±30%

    private static SimuladorController instance;

    private Malha malha;
    private PainelMalha painelMalha;
    private List<Carro> carros;
    private Random random;

    private int quantidadeMaxima;
    private static final int VELOCIDADE_BASE = 500;
    private int intervaloInsercao;
    private boolean usarSemaforo;
    private volatile boolean inserindo;
    private volatile boolean rodando;

    private int totalCriados;
    private int totalFinalizados;

    private long ultimaInsercao; 

    private SimuladorController() {
        //escolha que facilita iteração segura enquanto outras threads adicionam/removem carros
        carros = new CopyOnWriteArrayList<>();
        random = new Random();
        totalCriados = 0;
        totalFinalizados = 0;
        ultimaInsercao = 0;
    }

    /**
     * Obtém a instância única do controlador 
     */
    public static synchronized SimuladorController getInstance() {
        if (instance == null || !instance.isAlive()) {
            instance = new SimuladorController();
        }
        return instance;
    }

    /**
     * Inicializa o controlador com os parâmetros da simulação.
     */
    public void inicializar(Malha malha, PainelMalha painel, int qtdCarros,
            int intervaloInsercao, boolean usarSemaforo) {
        // Encerra simulação anterior se estiver rodando
        if (this.rodando) {
            encerrarSimulacao();
            aguardarEncerramento();
        }

        this.malha = malha;
        this.painelMalha = painel;
        this.quantidadeMaxima = qtdCarros;
        this.intervaloInsercao = intervaloInsercao;
        this.usarSemaforo = usarSemaforo;
        this.inserindo = true;
        this.rodando = true;
        this.ultimaInsercao = 0; 

        System.out.println("=== Simulação Inicializada ===");
        System.out.println("Quantidade máxima: " + qtdCarros + " carros");
        System.out.println("Velocidade base: " + VELOCIDADE_BASE + "ms");
        System.out.println("Intervalo de inserção: " + intervaloInsercao + "ms");
        System.out.println("Exclusão mútua: " + (usarSemaforo ? "Semáforo" : "Monitor"));
        System.out.println("==============================");
    }

    @Override
    public void run() {
        System.out.println("SimuladorController iniciado");

        while (rodando) {
            try {
                // Atualiza visualização
                atualizarVisualizacao();

                // Remove carros finalizados
                limparCarrosFinalizados();

                //Verifica intervalo antes de inserir
                if (inserindo && carros.size() < quantidadeMaxima) {
                    long agora = System.currentTimeMillis();

                    // Só insere se passou o intervalo definido
                    if (agora - ultimaInsercao >= intervaloInsercao) {
                        tentarInserirCarro();
                        ultimaInsercao = agora;
                    }
                }

                Thread.sleep(INTERVALO_ATUALIZACAO);

            } catch (InterruptedException e) {
                System.out.println("SimuladorController interrompido");
                break;
            } catch (Exception e) {
                System.err.println("Erro no SimuladorController: " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("SimuladorController finalizado");
    }

    /**
     * Atualiza a visualização da malha.
     */
    private void atualizarVisualizacao() {
        if (painelMalha != null) {
            painelMalha.atualizar();
        }
    }

    /**
     * Tenta inserir um novo carro em uma entrada disponível.
     */
    private void tentarInserirCarro() {
        List<Celula> entradas = malha.getEntradas();

        if (entradas.isEmpty()) {
            System.err.println("AVISO: Nenhuma entrada disponível na malha!");
            return;
        }

        // Tenta várias vezes em entradas aleatórias
        for (int tentativa = 0; tentativa < TENTATIVAS_INSERCAO; tentativa++) {
            Celula entrada = entradas.get(random.nextInt(entradas.size()));

            if (tentarInserirNaEntrada(entrada)) {
                return; // Sucesso na inserção
            }
        }
    }

    /**
     * Tenta inserir um carro em uma entrada específica.
     */
    private boolean tentarInserirNaEntrada(Celula entrada) {
        // Verifica se a entrada está livre
        if (!entrada.podeEntrar()) {
            return false;
        }

        // Tenta adquirir o recurso
        if (!entrada.tentarAdquirir(usarSemaforo)) {
            return false;
        }

        try {
            // verifica novamente se pode entrar
            if (entrada.podeEntrar()) {
                int velocidade = calcularVelocidadeAleatoria();
                Carro carro = new Carro(entrada, malha, velocidade, usarSemaforo);

                entrada.entrarCarro(carro);
                carros.add(carro);
                carro.start();
                totalCriados++;

                System.out.println("Carro " + carro.getCarroId() + " inserido em "
                        + entrada + " com velocidade " + velocidade + "ms");

                atualizarVisualizacao();
                return true;
            } else {
                // Célula foi ocupada entre as verificações
                entrada.liberar(usarSemaforo);
                return false;
            }
        } catch (Exception e) {
            // Em caso de erro, libera o recurso
            entrada.liberar(usarSemaforo);
            System.err.println("Erro ao inserir carro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Calcula uma velocidade aleatória baseada na velocidade base ±30%.
     */
    private int calcularVelocidadeAleatoria() {
        int variacao = (int) (VELOCIDADE_BASE * VARIACAO_VELOCIDADE);  //constante
        int min = VELOCIDADE_BASE - variacao;
        int max = VELOCIDADE_BASE + variacao;
        return min + random.nextInt(max - min + 1);
    }

    /**
     * Remove carros que já finalizaram sua execução.
     */
    private void limparCarrosFinalizados() {
        List<Carro> finalizados = new ArrayList<>();

        for (Carro carro : carros) {
            if (!carro.isAlive()) {
                finalizados.add(carro);
                totalFinalizados++;
            }
        }

        if (!finalizados.isEmpty()) {
            carros.removeAll(finalizados);
            atualizarVisualizacao();
        }
    }

    /**
     * Aguarda o encerramento da thread com timeout.
     */
    private void aguardarEncerramento() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Para a inserção de novos carros.
     */
    public void pararInsercao() {
        inserindo = false;
        System.out.println("Inserção de carros pausada");
    }

    /**
     * Retoma a inserção de novos carros.
     */
    public void continuarInsercao() {
        inserindo = true;
        ultimaInsercao = System.currentTimeMillis();
        System.out.println("Inserção de carros retomada");
    }

    /**
     * Encerra completamente a simulação.
     */
    public void encerrarSimulacao() {
        System.out.println("Encerrando simulação...");

        rodando = false;
        inserindo = false;

        // Para todos os carros
        for (Carro carro : carros) {
            try {
                carro.parar();
            } catch (Exception e) {
                System.err.println("Erro ao parar carro " + carro.getCarroId() + ": " + e.getMessage());
            }
        }

        // Aguarda um momento para os carros finalizarem
        aguardarEncerramento();

        carros.clear();
        interrupt();

        System.out.println("=== Simulação Encerrada ===");
        System.out.println("Total criados: " + totalCriados);
        System.out.println("Total finalizados: " + totalFinalizados);
        System.out.println("==========================");
    }

    /**
     * Reseta o controlador para uma nova simulação.
     */
    public void reset() {
        System.out.println("Resetando SimuladorController");

        if (rodando) {
            encerrarSimulacao();
        }

        carros.clear();
        totalCriados = 0;
        totalFinalizados = 0;
        rodando = false;
        inserindo = false;
        ultimaInsercao = 0;
        malha = null;
        painelMalha = null;

        instance = null;
    }

    public int getCarrosAtivos() {
        return carros.size();
    }

    public int getTotalCriados() {
        return totalCriados;
    }

    public int getTotalFinalizados() {
        return totalFinalizados;
    }

    public boolean isInserindo() {
        return inserindo;
    }

    public boolean isRodando() {
        return rodando;
    }
}
