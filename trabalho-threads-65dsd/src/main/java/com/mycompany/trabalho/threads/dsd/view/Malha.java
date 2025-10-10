package com.mycompany.trabalho.threads.dsd.view;

import com.mycompany.trabalho.threads.dsd.model.Celula;
import com.mycompany.trabalho.threads.dsd.model.MalhaViaria;
import com.mycompany.trabalho.threads.dsd.model.TipoCelula;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

public class Malha extends javax.swing.JPanel {

    private final MalhaViaria malhaViaria;
    private final int tamanhoCelula = 30;
    private final Map<TipoCelula, ImageIcon> iconesCelulas = new HashMap<>();
    
    public Malha(MalhaViaria malhaViaria) {
        this.malhaViaria = malhaViaria;
        int largura = malhaViaria.getColunas() * tamanhoCelula;
        int altura = malhaViaria.getColunas() * tamanhoCelula;
        setPreferredSize(new Dimension(largura, altura));
        setBackground(Color.WHITE);
        
        carregarIconesCelulas();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        for (int i = 0; i < malhaViaria.getLinhas(); i++) {
            for (int j = 0; j < malhaViaria.getColunas(); j++) {
                Celula celula = malhaViaria.getCelula(i, j);
                TipoCelula tipoCelula = celula.getTipoCelula();
                
                // Estilização Células Matriz 
                g.setColor(getCorCelula(tipoCelula));
                g.fillRect(j * tamanhoCelula, i * tamanhoCelula, tamanhoCelula, tamanhoCelula);
                
                // Borda das Células
                g.setColor(Color.BLACK);
                g.drawRect(j * tamanhoCelula, i * tamanhoCelula, tamanhoCelula, tamanhoCelula);
                
                ImageIcon icone = iconesCelulas.get(tipoCelula);
                if (icone != null) {
                    g.drawImage(
                        icone.getImage(),
                        j * tamanhoCelula + 5,
                        i * tamanhoCelula + 5,
                        tamanhoCelula - 10,
                        tamanhoCelula - 10,
                        this
                    );
                }
            }
        }
    }
    
    private Color getCorCelula(TipoCelula tipo) {
        return switch (tipo) {
            case VAZIO -> Color.GREEN.darker();
            case CRUZAMENTO_BAIXO, CRUZAMENTO_BAIXO_ESQUERDA, CRUZAMENTO_DIREITA_BAIXO, CRUZAMENTO_CIMA, CRUZAMENTO_CIMA_DIREITA, CRUZAMENTO_CIMA_ESQUERDA, CRUZAMENTO_DIREITA, CRUZAMENTO_ESQUERDA -> Color.GRAY;
            case ESTRADA_BAIXO, ESTRADA_CIMA, ESTRADA_DIREITA, ESTRADA_ESQUERDA -> Color.BLACK;
        };
    }
    
    private void carregarIconesCelulas() {
        try {
            iconesCelulas.put(TipoCelula.ESTRADA_CIMA, new ImageIcon(getClass().getResource("/assets/icones/setaEstradaCima.png")));
            iconesCelulas.put(TipoCelula.ESTRADA_BAIXO, new ImageIcon(getClass().getResource("/assets/icones/setaEstradaBaixo.png")));
            iconesCelulas.put(TipoCelula.ESTRADA_DIREITA, new ImageIcon(getClass().getResource("/assets/icones/setaEstradaDireita.png")));
            iconesCelulas.put(TipoCelula.ESTRADA_ESQUERDA, new ImageIcon(getClass().getResource("/assets/icones/setaEstradaEsquerda.png")));
        } catch (Exception e) {
            System.out.println("Não é possível carregar o arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
