package com.mycompany.trabalho.threads.dsd.view;

import com.mycompany.trabalho.threads.dsd.model.Malha;
import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Tela inicial de configuração da simulação.
 */
public class TelaInicial extends JFrame {

    private JLabel lblTitulo;
    private JLabel lblQtdCarros;
    private JSpinner spnQtdCarros;
    private JLabel lblIntervaloInsercao;
    private JSpinner spnIntervaloInsercao;
    private JLabel lblMalha;
    private JComboBox<String> cmbMalha;
    private JLabel lblExclusao;
    private JComboBox<String> cmbExclusao;
    private JButton btnIniciar;

    public TelaInicial() {
        initComponents();
        configurar();
    }

    private void initComponents() {
        setTitle("Simulador de Tráfego - Configuração");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setResizable(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        lblTitulo = new JLabel("Simulador de Tráfego em Malha Viária");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 20, 10);
        add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 5, 10);

        // Quantidade de Carros
        lblQtdCarros = new JLabel("Quantidade Máxima de Carros:");
        lblQtdCarros.setToolTipText("Quantidade máxima de carros circulando simultaneamente");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lblQtdCarros, gbc);

        spnQtdCarros = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(spnQtdCarros, gbc);

        // Intervalo de Inserção
        lblIntervaloInsercao = new JLabel("Intervalo de Inserção (ms):");
        lblIntervaloInsercao.setToolTipText("Tempo entre inserções de novos carros");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lblIntervaloInsercao, gbc);

        spnIntervaloInsercao = new JSpinner(new SpinnerNumberModel(1000, 100, 5000, 100));
        spnIntervaloInsercao.setToolTipText("Quanto tempo esperar antes de inserir próximo carro");
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(spnIntervaloInsercao, gbc);

        // Tipo de Malha
        lblMalha = new JLabel("Selecionar Malha:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(lblMalha, gbc);

        cmbMalha = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(cmbMalha, gbc);

        // Tipo de Exclusão Mútua
        lblExclusao = new JLabel("Tipo de Exclusão:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(lblExclusao, gbc);

        cmbExclusao = new JComboBox<>(new String[]{"Semáforo", "Monitor"});
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(cmbExclusao, gbc);

        // Botão Iniciar
        btnIniciar = new JButton("Iniciar Simulação");
        btnIniciar.setFont(new Font("Arial", Font.BOLD, 14));
        btnIniciar.setPreferredSize(new Dimension(250, 40));
        btnIniciar.addActionListener(e -> iniciarSimulacao());
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 20, 10);
        add(btnIniciar, gbc);

        pack();
        setLocationRelativeTo(null);
    }

    private void configurar() {
        carregarMalhas();
    }

    private void carregarMalhas() {
        File pasta = new File("malhas");

        if (!pasta.exists() || !pasta.isDirectory()) {
            cmbMalha.addItem("Nenhuma malha encontrada");
            btnIniciar.setEnabled(false);
            JOptionPane.showMessageDialog(this,
                    "Pasta 'malhas' não encontrada!\nCrie a pasta e adicione arquivos .txt",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File[] arquivos = pasta.listFiles((dir, name) -> name.endsWith(".txt"));

        if (arquivos == null || arquivos.length == 0) {
            cmbMalha.addItem("Nenhuma malha encontrada");
            btnIniciar.setEnabled(false);
            return;
        }

        for (File arquivo : arquivos) {
            cmbMalha.addItem(arquivo.getName());
        }
    }

    private void iniciarSimulacao() {
        try {
            int qtdCarros = (int) spnQtdCarros.getValue();
            int intervaloInsercao = (int) spnIntervaloInsercao.getValue();
            String nomeMalha = (String) cmbMalha.getSelectedItem();
            boolean usarSemaforo = "Semáforo".equals(cmbExclusao.getSelectedItem());

            if (nomeMalha == null || nomeMalha.equals("Nenhuma malha encontrada")) {
                JOptionPane.showMessageDialog(this,
                        "Selecione uma malha válida!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String caminhoMalha = "malhas/" + nomeMalha;
            Malha malha = new Malha(caminhoMalha);

            // Velocidade base fixa
            // Os carros terão velocidades diferentes automaticamente (±30%)
            TelaSimulacao telaSimulacao = new TelaSimulacao(
                    malha, qtdCarros, intervaloInsercao, usarSemaforo
            );
            telaSimulacao.setVisible(true);

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar malha: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new TelaInicial().setVisible(true);
        });
    }
}