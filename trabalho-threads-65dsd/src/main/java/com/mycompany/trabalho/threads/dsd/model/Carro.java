package com.mycompany.trabalho.threads.dsd.model;

import java.util.Random;

public class Carro extends Thread {

    private static int contador = 0;
    private final int id;
    private final Rua rua;
    private final int velocidade;
    private Direcao direcao;
    private Celula celulaAtual;
    private Celula proximaPosicao;
    private boolean ativo;

    public Carro(Rua rua, int velocidade) {
        this.rua = rua;
        this.velocidade = velocidade;
        this.id = contador++;
    }

    @Override
    public void run() {
        ativo = true;
        
        if(celulaAtual != null) {
            this.direcao = celulaAtual.getDirecao();
        }
        
        while (ativo) {
            mover();
        }
        System.out.println(getName() + " Parou");
        rua.removerCarro(this);
    }

    public void mover() {
        try {
            irParaCelula(proximaPosicao);
            Celula proximaPosicao = calcularProximaPosicao();
            if (proximaPosicao != null) {
                if (proximaPosicao.isCruzamento()) {
                    opcoesCruzamento();
                }
                setProximaPosicao(calcularProximaPosicao());
            } else {
                pararCarro();
                Thread.sleep(velocidade);
            }
        } catch (InterruptedException e) {
            System.out.println("Erro ao mover o carro: " + e.getMessage());
        }
    }

    public int getCarroId() {
        return id;
    }

    public void setDirecao(Direcao direcao) {
        this.direcao = direcao;
    }
    
    public Celula getCelulaAtual() {
        return celulaAtual;
    }

    public void setCelulaAtual(Celula celulaAtual) {
        this.celulaAtual = celulaAtual;
    }

    public void setProximaPosicao(Celula proximaPosicao) {
        this.proximaPosicao = proximaPosicao;
    }

    private void pararCarro() {
        this.ativo = false;
    }

    private Celula calcularProximaPosicao() {
        try {
            switch (direcao.getSentidoDirecao()) {
                case TipoCelula.ESTRADA_CIMA:
                    return celulaACima();
                case TipoCelula.ESTRADA_BAIXO:
                    return celulaABaixo();
                case TipoCelula.ESTRADA_DIREITA:
                    return celulaADireita();
                case TipoCelula.ESTRADA_ESQUERDA:
                    return celulaAEsquerda();
                default:
                    return null;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Carro " + getName() + " não conseguiu calcular próxima posição.");
            return null;
        }

    }

    private void irParaCelula(Celula celula) {
        try {
            Celula celulaAntiga = this.celulaAtual;
            celula.bloquear();
            setCelulaAtual(celula);
            celula.setCarro(this);

            if (celulaAntiga != null) {
                celulaAntiga.removerCarroDaCelula();
            }

            Thread.sleep(velocidade);
        } catch (InterruptedException e) {           
            System.out.println("Carro interrompido: " + getName());
        }
    }

    private void irParaCelulaNoCruzamento(Celula celula) {
        try {
            Celula celulaAntiga = this.celulaAtual;
            setCelulaAtual(celula);
            celula.setCarro(this);

            if (celulaAntiga != null) {
                celulaAntiga.removerCarroDaCelula();
            }

            Thread.sleep(velocidade);
        } catch (InterruptedException e) {
            System.out.println("Carro interrompido: " + getName());
        }
    }

    private Celula celulaABaixo() {
        return rua.celulaParaBaixo(celulaAtual);
    }

    private Celula celulaACima() {
        return rua.celulaParaCima(celulaAtual);
    }

    private Celula celulaADireita() {
        return rua.celulaParaDireita(celulaAtual);
    }

    private Celula celulaAEsquerda() {
        return rua.celulaParaEsquerda(celulaAtual);
    }

    private void direitaParaDireita() {
        Celula c1 = celulaADireita();
        Celula c2 = rua.celulaParaDireita(c1);
        Celula c3 = rua.celulaParaDireita(c2);

        if (c1.getDirecao().getSentidoDirecao() > 4 && c1.getDirecao().getSentidoDirecao() < 9) {
            opcoesCruzamento();
        } else {
            jantarDosFilosofos(c1, c2, c3);
        }
    }

    private void direitaParaCima() {
        Celula c1 = celulaADireita();
        Celula c2 = rua.celulaParaDireita(c1);
        Celula c3 = rua.celulaParaCima(c2);
        Celula c4 = rua.celulaParaCima(c3);

        if (c3.getDirecao().getSentidoDirecao() > 4 && c3.getDirecao().getSentidoDirecao() < 9) {
            opcoesCruzamento();
        } else {
            jantarDosFilosofos(c1, c2, c3, c4);
        }
    }

    private void direitaParaBaixo() {
        Celula c1 = celulaADireita();
        Celula c2 = rua.celulaParaBaixo(c1);

        if (c1.getDirecao().getSentidoDirecao() > 4 && c1.getDirecao().getSentidoDirecao() < 9) {
            opcoesCruzamento();
        } else {
            jantarDosFilosofos(c1, c2);
        }
    }

    private void esquerdaParaEsquerda() {
        Celula c1 = celulaAEsquerda();
        Celula c2 = rua.celulaParaEsquerda(c1);
        Celula c3 = rua.celulaParaEsquerda(c2);

        if (c2.getDirecao().getSentidoDirecao() > 4 && c2.getDirecao().getSentidoDirecao() < 9) {
            opcoesCruzamento();
        } else {
            jantarDosFilosofos(c1, c2, c3);
        }
    }

    private void esquerdaParaCima() {
        Celula c1 = celulaAEsquerda();
        Celula c2 = rua.celulaParaCima(c1);

        if (c1.getDirecao().getSentidoDirecao() > 4 && c1.getDirecao().getSentidoDirecao() < 9) {
            opcoesCruzamento();
        } else {
            jantarDosFilosofos(c1, c2);
        }
    }

    private void esquerdaParaBaixo() {
        Celula c1 = celulaAEsquerda();
        Celula c2 = rua.celulaParaDireita(c1);
        Celula c3 = rua.celulaParaBaixo(c2);
        Celula c4 = rua.celulaParaBaixo(c3);

        if (c3.getDirecao().getSentidoDirecao() > 4 && c3.getDirecao().getSentidoDirecao() < 9) {
            opcoesCruzamento();
        } else {
            jantarDosFilosofos(c1, c2, c3, c4);
        }
    }

    private void cimaParaDireita() {
        Celula c1 = celulaACima();
        Celula c2 = rua.celulaParaDireita(c1);

        if (c1.getDirecao().getSentidoDirecao() > 4 && c1.getDirecao().getSentidoDirecao() < 9) {
            opcoesCruzamento();
        } else {
            jantarDosFilosofos(c1, c2);
        }
    }

    private void cimaParaEsquerda() {
        Celula c1 = celulaACima();
        Celula c2 = rua.celulaParaCima(c1);
        Celula c3 = rua.celulaParaEsquerda(c2);
        Celula c4 = rua.celulaParaEsquerda(c3);

        if (c3.getDirecao().getSentidoDirecao() > 4 && c3.getDirecao().getSentidoDirecao() < 9) {
            opcoesCruzamento();
        } else {
            jantarDosFilosofos(c1, c2, c3, c4);
        }
    }

    private void cimaParaCima() {
        Celula c1 = celulaACima();
        Celula c2 = rua.celulaParaCima(c1);
        Celula c3 = rua.celulaParaCima(c2);

        if (c2.getDirecao().getSentidoDirecao() > 4 && c2.getDirecao().getSentidoDirecao() < 9) {
            opcoesCruzamento();
        } else {
            jantarDosFilosofos(c1, c2, c3);
        }
    }

    private void baixoParaDireita() {
        Celula c1 = celulaABaixo();
        Celula c2 = rua.celulaParaBaixo(c1);
        Celula c3 = rua.celulaParaDireita(c2);
        Celula c4 = rua.celulaParaDireita(c3);

        if (c3.getDirecao().getSentidoDirecao() > 4 && c3.getDirecao().getSentidoDirecao() < 9) {
            opcoesCruzamento();
        } else {
            jantarDosFilosofos(c1, c2, c3, c4);
        }
    }

    private void baixoParaEsquerda() {
        Celula c1 = celulaABaixo();
        Celula c2 = rua.celulaParaEsquerda(c1);

        if (c1.getDirecao().getSentidoDirecao() > 4 && c1.getDirecao().getSentidoDirecao() < 9) {
            opcoesCruzamento();
        } else {
            jantarDosFilosofos(c1, c2);
        }
    }

    private void baixoParaBaixo() {
        Celula c1 = celulaABaixo();
        Celula c2 = rua.celulaParaBaixo(c1);
        Celula c3 = rua.celulaParaBaixo(c2);

        if (c2.getDirecao().getSentidoDirecao() > 4 && c2.getDirecao().getSentidoDirecao() < 9) {
            opcoesCruzamento();
        } else {
            jantarDosFilosofos(c1, c2, c3);
        }
    }

    private void opcoesCruzamento() {
        Random random = new Random();
        int opcao = random.nextInt(3);

        switch (direcao.getSentidoDirecao()) {
            case TipoCelula.ESTRADA_CIMA:
                switch (opcao) {
                    case 0:
                        cimaParaDireita();
                        break;
                    case 1:
                        cimaParaCima();
                        break;
                    case 2:
                        cimaParaEsquerda();
                        break;
                }
                break;
            case TipoCelula.ESTRADA_BAIXO:
                switch (opcao) {
                    case 0:
                        baixoParaDireita();
                        break;
                    case 1:
                        baixoParaBaixo();
                        break;
                    case 2:
                        baixoParaEsquerda();
                        break;
                }
                break;
            case TipoCelula.ESTRADA_ESQUERDA:
                switch (opcao) {
                    case 0:
                        esquerdaParaEsquerda();
                        break;
                    case 1:
                        esquerdaParaCima();
                        break;
                    case 2:
                        esquerdaParaBaixo();
                        break;
                }
                break;
            case TipoCelula.ESTRADA_DIREITA:
                switch (opcao) {
                    case 0:
                        direitaParaDireita();
                        break;
                    case 1:
                        direitaParaCima();
                        break;
                    case 2:
                        direitaParaBaixo();
                        break;
                }
                break;
        }
    }

    private void jantarDosFilosofos(Celula... celulas) {
        Random random = new Random();
        boolean conseguiuAvancar = false;

        try {
            do {
                boolean[] bloqueios = new boolean[celulas.length];
                boolean todasBloqueadas = true;

                for (int i = 0; i < celulas.length; i++) {
                    Celula celula = celulas[i];
                    if (celula != null) {
                        bloqueios[i] = celula.tentarBloquear();
                        if (!bloqueios[i]) {
                            todasBloqueadas = false;
                            break;
                        }
                    } else {
                        todasBloqueadas = false;
                        break;
                    }
                }

                // Verificar para remover o instace of
                if (todasBloqueadas) {
                    if (this.celulaAtual instanceof CelulaSemaforo) {
                        this.celulaAtual.liberar();
                        for (Celula celula : celulas) {
                            irParaCelulaNoCruzamento(celula);
                        }
                    } else {
                        for (Celula celula : celulas) {
                            irParaCelula(celula);
                        }
                    }

                    this.direcao = celulas[celulas.length - 1].getDirecao();

                    for (Celula celula : celulas) {
                        celula.liberar();
                    }

                    conseguiuAvancar = true;

                } else {
                    for (int i = 0; i < celulas.length; i++) {
                        if (bloqueios[i] && celulas[i] != null) {
                            celulas[i].liberar();
                        }
                    }

                    Thread.sleep(velocidade + random.nextInt(500));
                }

            } while (!conseguiuAvancar && ativo);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Carro " + getName() + " interrompido durante jantar dos filósofos.");
        }
    }
}