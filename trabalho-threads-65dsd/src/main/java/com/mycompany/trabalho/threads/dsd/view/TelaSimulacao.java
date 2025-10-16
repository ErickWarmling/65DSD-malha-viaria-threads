package com.mycompany.trabalho.threads.dsd.view;

import com.mycompany.trabalho.threads.dsd.controller.SimuladorController;
import com.mycompany.trabalho.threads.dsd.model.Malha;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Tela de simulação em execução.
 */
public class TelaSimulacao extends JFrame {
    
    private Malha malha;
    private PainelMalha painelMalha;
    private SimuladorController controller;
    
    private JPanel pnlSuperior;
    private JLabel lblStatus;
    private JLabel lblTipo;
    private JLabel lblCarros;
    
    private JScrollPane scrollMalha;
    
    private JPanel pnlControles;
    private JButton btnInsercao;
    private JButton btnEncerrar;
    
    private Timer atualizadorLabels;
    
    public TelaSimulacao(Malha malha, int qtdCarros, int intervaloInsercao, 
                         boolean usarSemaforo) {
        this.malha = malha;
        
        initComponents(usarSemaforo);
        inicializarSimulacao(qtdCarros, intervaloInsercao, usarSemaforo);
    }
    
    private void initComponents(boolean usarSemaforo) {
        setTitle("Simulador de Tráfego - Em Execução");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Painel Superior - Informações
        pnlSuperior = new JPanel(new GridLayout(1, 3, 10, 5));
        pnlSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lblStatus = new JLabel("Status: Rodando", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatus.setForeground(new Color(0, 128, 0));
        
        lblTipo = new JLabel("Exclusão: " + (usarSemaforo ? "Semáforo" : "Monitor"), 
                            SwingConstants.CENTER);
        lblTipo.setFont(new Font("Arial", Font.BOLD, 12));
        
        lblCarros = new JLabel("Carros: 0 | Criados: 0 | Finalizados: 0", 
                              SwingConstants.CENTER);
        lblCarros.setFont(new Font("Arial", Font.PLAIN, 12));
        
        pnlSuperior.add(lblStatus);
        pnlSuperior.add(lblTipo);
        pnlSuperior.add(lblCarros);
        
        add(pnlSuperior, BorderLayout.NORTH);
        
        // Painel Central - Malha
        painelMalha = new PainelMalha(malha);
        scrollMalha = new JScrollPane(painelMalha);
        scrollMalha.setPreferredSize(new Dimension(800, 600));
        add(scrollMalha, BorderLayout.CENTER);
        
        // Painel Inferior - Controles
        pnlControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        btnInsercao = new JButton("🛑 Parar Inserção");
        btnInsercao.setFont(new Font("Arial", Font.BOLD, 14));
        btnInsercao.setPreferredSize(new Dimension(200, 40));
        btnInsercao.addActionListener(e -> toggleInsercao());
        
        btnEncerrar = new JButton("⛔ Encerrar Simulação");
        btnEncerrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnEncerrar.setForeground(Color.RED);
        btnEncerrar.setPreferredSize(new Dimension(200, 40));
        btnEncerrar.addActionListener(e -> encerrarSimulacao());
        
        pnlControles.add(btnInsercao);
        pnlControles.add(btnEncerrar);
        
        add(pnlControles, BorderLayout.SOUTH);
        
        // Timer para atualizar labels
        atualizadorLabels = new Timer(500, e -> atualizarLabels());
        atualizadorLabels.start();
        
        // Confirmação ao fechar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarFechamento();
            }
        });
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void inicializarSimulacao(int qtdCarros, int intervaloInsercao, 
                                      boolean usarSemaforo) {
        try {
            // Obtém instância (pode ser nova ou existente)
            controller = SimuladorController.getInstance();
            
            // Se já estiver rodando, para primeiro
            if (controller.isAlive()) {
                System.out.println("Controller anterior ainda ativo, aguardando...");
                controller.encerrarSimulacao();
                controller.join(1000);
            }
            
            // Cria nova instância garantida
            controller = SimuladorController.getInstance();
            
            // Inicializa sem parâmetro de velocidade (velocidade base é fixa)
            controller.inicializar(malha, painelMalha, qtdCarros, 
                                  intervaloInsercao, usarSemaforo);
            controller.start();
            
            System.out.println("Simulação iniciada com sucesso");
            
        } catch (Exception e) {
            System.err.println("Erro ao inicializar simulação: " + e.getMessage());
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(this,
                "Erro ao iniciar simulação: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void toggleInsercao() {
        if (controller != null) {
            if (controller.isInserindo()) {
                controller.pararInsercao();
                btnInsercao.setText("▶️ Continuar Inserção");
                lblStatus.setText("Status: Sem Inserção");
                lblStatus.setForeground(Color.ORANGE);
            } else {
                controller.continuarInsercao();
                btnInsercao.setText("🛑 Parar Inserção");
                lblStatus.setText("Status: Rodando");
                lblStatus.setForeground(new Color(0, 128, 0));
            }
        }
    }
    
    private void encerrarSimulacao() {
        int opcao = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente encerrar a simulação?",
            "Confirmar Encerramento",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (opcao == JOptionPane.YES_OPTION) {
            finalizarEVoltar();
        }
    }
    
    private void confirmarFechamento() {
        int opcao = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente sair? A simulação será encerrada.",
            "Confirmar Saída",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (opcao == JOptionPane.YES_OPTION) {
            finalizarEVoltar();
        }
    }
    
    private void finalizarEVoltar() {
        System.out.println("Finalizando tela de simulação...");
        
        // Para o timer de atualização
        if (atualizadorLabels != null && atualizadorLabels.isRunning()) {
            atualizadorLabels.stop();
        }
        
        // Encerra o controller
        if (controller != null) {
            try {
                controller.encerrarSimulacao();
                controller.join(500);
                controller.reset();
                
            } catch (InterruptedException e) {
                System.err.println("Erro ao aguardar finalização: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Erro ao encerrar controller: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Aguarda um pouco para garantir limpeza
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Fecha esta janela
        dispose();
        
        // Abre nova tela inicial
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Abrindo nova tela inicial...");
                TelaInicial telaInicial = new TelaInicial();
                telaInicial.setVisible(true);
            } catch (Exception e) {
                System.err.println("Erro ao abrir tela inicial: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private void atualizarLabels() {
        try {
            if (controller != null) {
                int ativos = controller.getCarrosAtivos();
                int criados = controller.getTotalCriados();
                int finalizados = controller.getTotalFinalizados();
                
                lblCarros.setText(String.format(
                    "Carros: %d | Criados: %d | Finalizados: %d",
                    ativos, criados, finalizados
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}