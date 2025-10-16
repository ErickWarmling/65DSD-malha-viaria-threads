package com.mycompany.trabalho.threads.dsd.exclusao;

public interface IExclusaoMutua {
    
    boolean tentarAdquirir();
    
    void liberar();
    
    String getNome();
}
