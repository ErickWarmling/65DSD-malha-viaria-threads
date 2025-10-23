package com.mycompany.trabalho.threads.dsd.model;

import java.util.concurrent.Semaphore;

public class CelulaSemaforo extends Celula {

    private final Semaphore semaforo = new Semaphore(1);

    public CelulaSemaforo(int linha, int coluna, Direcao direcao, boolean isEntrada, boolean isCruzamento) {
        super(linha, coluna, direcao, isEntrada, isCruzamento);
    }

    public Semaphore getSemaforo() {
        return semaforo;
    }

    @Override
    public void liberar() {
        semaforo.release();
    }

    @Override
    public void bloquear() throws InterruptedException {
        semaforo.acquire();
    }

    @Override
    public boolean tentarBloquear() {
        return semaforo.tryAcquire();
    }

}