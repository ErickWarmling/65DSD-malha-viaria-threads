package com.mycompany.trabalho.threads.dsd.constantes;

/**
 * Define os tipos de células da malha viária.
 */
public class TipoCelula {

    // Célula vazia (sem estrada)
    public static final int VAZIO = 0;
    
    // Estradas direcionais (movimento único)
    public static final int ESTRADA_CIMA = 1;
    public static final int ESTRADA_DIREITA = 2;
    public static final int ESTRADA_BAIXO = 3;
    public static final int ESTRADA_ESQUERDA = 4;
    
    // Cruzamentos com uma saída possível
    public static final int CRUZAMENTO_CIMA = 5;
    public static final int CRUZAMENTO_DIREITA = 6;
    public static final int CRUZAMENTO_BAIXO = 7;
    public static final int CRUZAMENTO_ESQUERDA = 8;
    
    // Cruzamentos com duas saídas possíveis
    public static final int CRUZAMENTO_CIMA_DIREITA = 9;
    public static final int CRUZAMENTO_CIMA_ESQUERDA = 10;
    public static final int CRUZAMENTO_DIREITA_BAIXO = 11;
    public static final int CRUZAMENTO_BAIXO_ESQUERDA = 12;
}