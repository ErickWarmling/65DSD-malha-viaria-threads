package com.mycompany.trabalho.threads.dsd.model;

public class Celula {
    
    private TipoCelula tipoCelula;
    
    public Celula(TipoCelula tipoCelula) {
        this.tipoCelula = tipoCelula;
    }

    public TipoCelula getTipoCelula() {
        return tipoCelula;
    }

    public void setTipoCelula(TipoCelula tipoCelula) {
        this.tipoCelula = tipoCelula;
    }
}