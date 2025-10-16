package com.mycompany.trabalho.threads.dsd.exclusao;

import java.util.concurrent.Semaphore;

public class ExclusaoSemaforo implements IExclusaoMutua {
    
    private final Semaphore semaforo;
    
    public ExclusaoSemaforo() {
        this.semaforo = new Semaphore(1, true);
    }

    @Override
    public boolean tentarAdquirir() {
        return semaforo.tryAcquire();
    }
    
    @Override
    public void liberar() {
        semaforo.release();
    }
    
    @Override
    public String getNome() {
        return "Semáforo";
    }
}