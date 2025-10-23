package com.mycompany.trabalho.threads.dsd.model;

import static com.mycompany.trabalho.threads.dsd.model.TipoCelula.*;

public class Direcao {
    private final int sentidoDirecao;
	
    public Direcao (int sentidoDirecao) {
        this.sentidoDirecao = sentidoDirecao;
    }

    public int getSentidoDirecao() {
        return sentidoDirecao;
    }
	
    @Override
    public String toString() {
        return switch (sentidoDirecao) {
            case ESTRADA_BAIXO -> "Baixo"; 
            case ESTRADA_CIMA -> "Cima"; 
            case ESTRADA_DIREITA -> "Direita"; 
            case ESTRADA_ESQUERDA -> "Esquerda"; 
            default -> "Cruzamento";
        };
    }
}