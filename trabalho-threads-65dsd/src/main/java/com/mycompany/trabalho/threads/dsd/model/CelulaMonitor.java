package com.mycompany.trabalho.threads.dsd.model;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CelulaMonitor extends Celula {

    private final Lock monitor = new ReentrantLock();

    public CelulaMonitor(int linha, int coluna, Direcao direcao, boolean isEntrada, boolean isCruzamento) {
        super(linha, coluna, direcao, isEntrada, isCruzamento);
    }

    @Override
    public void liberar() {
        monitor.unlock();
    }

    @Override
    public void bloquear() throws InterruptedException {
        monitor.lock();
    }

    @Override
    public boolean tentarBloquear() throws InterruptedException {
        Random random = new Random();
        return monitor.tryLock(random.nextInt(500), TimeUnit.MILLISECONDS);
    }
}