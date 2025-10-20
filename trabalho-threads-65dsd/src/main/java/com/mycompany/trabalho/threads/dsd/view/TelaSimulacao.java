package com.mycompany.trabalho.threads.dsd.view;

import com.mycompany.trabalho.threads.dsd.controller.Simulacao; 
import com.mycompany.trabalho.threads.dsd.model.Rua;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class TelaSimulacao extends javax.swing.JFrame {

    private MalhaPanel malhaPanel;
    private Rua rua;
    private Simulacao simulacao;
    private final int quantidadeMaximaVeiculos;
    private final int intervaloInsercao;
    
    public TelaSimulacao(Rua rua, int quantidadeMaximaVeiculos, int intervaloInsercao) {
        this.rua = rua;
        this.quantidadeMaximaVeiculos = quantidadeMaximaVeiculos;
        this.intervaloInsercao = intervaloInsercao;
        initComponents();
        
        malhaPanel = new MalhaPanel(rua.getMatrizMalhaViaria());
        malhaPanel.setPreferredSize(new Dimension(600, 600));
        
        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(malhaPanel, BorderLayout.CENTER);
        jPanel1.revalidate();
        jPanel1.repaint();
        
        this.simulacao = new Simulacao(rua, this, quantidadeMaximaVeiculos, intervaloInsercao);
        this.simulacao.start();
    }
    
    public void atualizarMalha() {
        if (malhaPanel != null) {
            malhaPanel.atualizarMalha(rua.getMatrizMalhaViaria());
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnPararInsercao = new javax.swing.JButton();
        btnEncerrarSimulacao = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setPreferredSize(new java.awt.Dimension(150, 150));
        jPanel1.setLayout(new java.awt.BorderLayout());
        getContentPane().add(jPanel1);

        jPanel2.setPreferredSize(new java.awt.Dimension(150, 0));

        btnPararInsercao.setText("Parar Inserção");
        btnPararInsercao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPararInsercaoActionPerformed(evt);
            }
        });

        btnEncerrarSimulacao.setText("Encerrar Simulação");
        btnEncerrarSimulacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEncerrarSimulacaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPararInsercao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEncerrarSimulacao, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(btnPararInsercao)
                .addGap(18, 18, 18)
                .addComponent(btnEncerrarSimulacao)
                .addContainerGap(573, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPararInsercaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPararInsercaoActionPerformed
        if (simulacao != null) {
            simulacao.pararInsercao();
        }
    }//GEN-LAST:event_btnPararInsercaoActionPerformed

    private void btnEncerrarSimulacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEncerrarSimulacaoActionPerformed
        if (simulacao != null) {
            simulacao.encerrarSimulacao();
        }
    }//GEN-LAST:event_btnEncerrarSimulacaoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEncerrarSimulacao;
    private javax.swing.JButton btnPararInsercao;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
