package com.mycompany.trabalho.threads.dsd.view;

import com.mycompany.trabalho.threads.dsd.model.Celula;
import com.mycompany.trabalho.threads.dsd.model.Malha;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Painel responsável por renderizar a malha viária e os carros.
 */
public class PainelMalha extends JPanel {

    private static final int TAMANHO_CELULA = 20;
    private static final Color COR_GRADE = new Color(200, 200, 200);
    
    private final Malha malha;
    
    // Métricas de desempenho
    private long ultimaAtualizacao = 0;
    private int contadorAtualizacoes = 0;
    private boolean mostrarFPS = false;

    public PainelMalha(Malha malha) {
        this.malha = malha;
        configurarPainel();
    }

    /**
     * Configura as propriedades do painel.
     */
    private void configurarPainel() {
        int largura = malha.getColunas() * TAMANHO_CELULA;
        int altura = malha.getLinhas() * TAMANHO_CELULA;
        
        Dimension tamanho = new Dimension(largura, altura);
        setSize(tamanho);
        setPreferredSize(tamanho);
        setDoubleBuffered(true); // Ativa double buffering para suavidade
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (malha == null) {
            return;
        }

        renderizarMalha(g);
        
        if (mostrarFPS) {
            atualizarMetricasFPS();
        }
    }

    /**
     * Renderiza toda a malha viária.
     */
    private void renderizarMalha(Graphics g) {
        Celula[][] matriz = malha.getMatriz();

        for (int i = 0; i < malha.getLinhas(); i++) {
            for (int j = 0; j < malha.getColunas(); j++) {
                renderizarCelula(g, matriz[i][j], i, j);
            }
        }
    }

    /**
     * Renderiza uma célula individual.
     */
    private void renderizarCelula(Graphics g, Celula celula, int linha, int coluna) {
        int x = coluna * TAMANHO_CELULA;
        int y = linha * TAMANHO_CELULA;

        // Desenha o ícone da célula
        ImageIcon icone = celula.getIcon();
        
        if (icone != null) {
            g.drawImage(icone.getImage(), x, y, 
                       TAMANHO_CELULA, TAMANHO_CELULA, this);
        } else {
            // Fallback: desenha fundo cinza se não houver ícone
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x, y, TAMANHO_CELULA, TAMANHO_CELULA);
        }

        desenharGrade(g, x, y);
    }

    /**
     * Desenha a grade da célula.
     */
    private void desenharGrade(Graphics g, int x, int y) {
        g.setColor(COR_GRADE);
        g.drawRect(x, y, TAMANHO_CELULA, TAMANHO_CELULA);
    }

    /**
     * Atualiza FPS
     */
    private void atualizarMetricasFPS() {
        long agora = System.currentTimeMillis();
        contadorAtualizacoes++;
        
        if (agora - ultimaAtualizacao >= 1000) {
            System.out.println("FPS da visualização: " + contadorAtualizacoes);
            contadorAtualizacoes = 0;
            ultimaAtualizacao = agora;
        }
    }

    /**
     * Atualiza a interface gráfica
     */
    public void atualizar() {
        if (SwingUtilities.isEventDispatchThread()) {
            repaint();
        } else {
            SwingUtilities.invokeLater(this::repaint);
        }
    }
    
    public void setMostrarFPS(boolean mostrar) {
        this.mostrarFPS = mostrar;
    }
    
    public Malha getMalha() {
        return malha;
    }
    
    public static int getTamanhoCelula() {
        return TAMANHO_CELULA;
    }
}