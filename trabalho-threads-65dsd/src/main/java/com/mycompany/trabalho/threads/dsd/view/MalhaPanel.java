package com.mycompany.trabalho.threads.dsd.view;

import com.mycompany.trabalho.threads.dsd.model.Carro;
import com.mycompany.trabalho.threads.dsd.model.Celula;
import com.mycompany.trabalho.threads.dsd.model.TipoCelula;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

public class MalhaPanel extends javax.swing.JPanel {

    private Celula[][] malhaViaria;
    private final int TAMANHO_CELULA = 30;
    private final Map<Integer, ImageIcon> iconesCelulas = new HashMap<>();

    public MalhaPanel(Celula[][] malhaViaria) {
        this.malhaViaria = malhaViaria;
        int largura = malhaViaria[0].length * TAMANHO_CELULA;
        int altura = malhaViaria.length * TAMANHO_CELULA;
        setPreferredSize(new Dimension(largura, altura));
        setBackground(Color.WHITE);

        carregarIconesCelulas();
    }

    public void atualizarMalha(Celula[][] malhaViaria) {
        this.malhaViaria = malhaViaria;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (malhaViaria == null) {
            return;
        }

        for (int i = 0; i < malhaViaria.length; i++) {
            for (int j = 0; j < malhaViaria[i].length; j++) {
                Celula celula = malhaViaria[i][j];
                int tipoCelula = celula.getDirecao().getSentidoDirecao();
     
//                g.setColor(getCorCelula(tipoCelula, celula));
//                g.fillRect(j * TAMANHO_CELULA, i * TAMANHO_CELULA, TAMANHO_CELULA, TAMANHO_CELULA);
//
//                // Borda preta
//                g.setColor(Color.BLACK);
//                g.drawRect(j * TAMANHO_CELULA, i * TAMANHO_CELULA, TAMANHO_CELULA, TAMANHO_CELULA);

                // Desenhar ícone se houver
                ImageIcon icone = iconesCelulas.get(tipoCelula);
                if (icone != null) {
                    g.drawImage(
                            icone.getImage(),
                            j * TAMANHO_CELULA + 5,
                            i * TAMANHO_CELULA + 5,
                            TAMANHO_CELULA - 10,
                            TAMANHO_CELULA - 10,
                            this
                    );
                }

                // Desenhar carro
                Carro carro = celula.getCarro();
//                if (carro != null) {
                    int x = j * TAMANHO_CELULA + TAMANHO_CELULA / 4;
                    int y = i * TAMANHO_CELULA + TAMANHO_CELULA / 4;
                    int diametro = TAMANHO_CELULA / 2;

                    g.setColor(Color.RED);
                    g.fillOval(x, y, diametro, diametro);

                    g.setColor(Color.WHITE);
//                    g.drawString(String.valueOf(carro.getCarroId()), x + diametro / 4, y + (3 * diametro) / 4);
                //}
            }
        }
    }

    private Color getCorCelula(int tipoCelula, Celula celula) {
        if (celula.isEntrada()) {
            return Color.BLACK;
        }
        if (celula.isCruzamento()) {
            return Color.GRAY;
        }
        return switch (tipoCelula) {
            case TipoCelula.VAZIO ->
                Color.GREEN.darker();
            case TipoCelula.ESTRADA_BAIXO, TipoCelula.ESTRADA_CIMA, TipoCelula.ESTRADA_DIREITA, TipoCelula.ESTRADA_ESQUERDA ->
                Color.BLACK;
            default ->
                Color.GRAY;
        };
    }
    
    private void carregarIconesCelulas() {
        try {
            iconesCelulas.put(TipoCelula.ESTRADA_CIMA, new ImageIcon(getClass().getResource("/assets/icones/setaEstradaCima.png")));
            iconesCelulas.put(TipoCelula.ESTRADA_BAIXO, new ImageIcon(getClass().getResource("/assets/icones/setaEstradaBaixo.png")));
            iconesCelulas.put(TipoCelula.ESTRADA_DIREITA, new ImageIcon(getClass().getResource("/assets/icones/setaEstradaDireita.png")));
            iconesCelulas.put(TipoCelula.ESTRADA_ESQUERDA, new ImageIcon(getClass().getResource("/assets/icones/setaEstradaEsquerda.png")));
        } catch (Exception e) {
            System.err.println("Erro ao carregar ícones: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 775, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 656, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
