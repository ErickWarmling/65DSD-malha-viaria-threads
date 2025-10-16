package com.mycompany.trabalho.threads.dsd.exclusao;

import java.util.concurrent.locks.ReentrantLock;

public class ExclusaoMonitor implements IExclusaoMutua {
    
    private final ReentrantLock lock;
    
    public ExclusaoMonitor() {
        this.lock = new ReentrantLock(true);
    }
    
    @Override
    public boolean tentarAdquirir() {
        return lock.tryLock();
    }
    
    @Override
    public void liberar() {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
    
    @Override
    public String getNome() {
        return "Monitor";
    }
}