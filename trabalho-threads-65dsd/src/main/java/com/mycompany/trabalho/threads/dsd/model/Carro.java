package com.mycompany.trabalho.threads.dsd.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class Carro extends Thread {

    private static int contador = 0;
    private final int id;
    private final Rua rua;
    private final int velocidade;
    private Direcao direcao;
    private Celula celulaAtual;
    private boolean ativo;
    private final Random random;

    public Carro(Rua rua, int velocidade) {
        this.rua = rua;
        this.velocidade = velocidade;
        this.id = contador++;
        this.random = new Random();
    }

    @Override
    public void run() {
        ativo = true;

        if (celulaAtual != null) {
            this.direcao = celulaAtual.getDirecao();
        }

        while (ativo) {
            try {
                movimentar();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                pararCarro();
            }
        }
        rua.removerCarro(this);
    }

    public void movimentar() throws InterruptedException {
        Thread.sleep(velocidade);

        Celula proximaCelula = calcularProximaCelula();

        if (proximaCelula == null || proximaCelula.getDirecao().getSentidoDirecao() == TipoCelula.VAZIO) {
            pararCarro();
            return;
        }

        int proximoDirecao = proximaCelula.getDirecao().getSentidoDirecao();

        if (proximaCelula.isCruzamento()) {
            atravessarCruzamento(proximaCelula);
        } else if (ehEstradaReta(proximoDirecao) && proximoDirecao != direcao.getSentidoDirecao()) {
            movimentarComMudancaDirecao(proximaCelula);
        } else {
            movimentarSimples(proximaCelula);
        }
    }

    private boolean ehEstradaReta(int tipoCelula) {
        return tipoCelula == TipoCelula.ESTRADA_CIMA
                || tipoCelula == TipoCelula.ESTRADA_BAIXO
                || tipoCelula == TipoCelula.ESTRADA_DIREITA
                || tipoCelula == TipoCelula.ESTRADA_ESQUERDA;
    }

    private void movimentarComMudancaDirecao(Celula destino) throws InterruptedException {
        Celula origem = celulaAtual;

        List<Celula> toLock = new ArrayList<>();
        toLock.add(origem);
        toLock.add(destino);
        ordenarParaBloquear(toLock);

        bloquearTodos(toLock);

        try {
            if (origem.getCarro() == this && destino.getCarro() == null) {
                origem.removerCarroDaCelula();
                destino.setCarro(this);
                definirCelulaAtual(destino);

                this.direcao = destino.getDirecao();

                System.out.println("Carro " + id + " se moveu de (" + origem.getLinha() + "," + origem.getColuna() + ") para (" + destino.getLinha() + "," + destino.getColuna() + ")");
            }
        } finally {
            liberarTodos(toLock);
        }
    }

    private void movimentarSimples(Celula destino) throws InterruptedException {
        Celula origem = celulaAtual;

        List<Celula> toLock = new ArrayList<>();
        toLock.add(origem);
        toLock.add(destino);
        ordenarParaBloquear(toLock);

        bloquearTodos(toLock);

        try {
            if (origem.getCarro() == this && destino.getCarro() == null) {
                origem.removerCarroDaCelula();
                destino.setCarro(this);
                definirCelulaAtual(destino);

                System.out.println("Carro " + id + " se moveu de (" + origem.getLinha() + "," + origem.getColuna() + ") para (" + destino.getLinha() + "," + destino.getColuna() + ")");
            }
        } finally {
            liberarTodos(toLock);
        }
    }

    private void atravessarCruzamento(Celula primeiroCruzamento) throws InterruptedException {
        InformacoesCaminho infoCaminho = escolherCaminhoSaida(primeiroCruzamento);

        if (infoCaminho == null) {
            Thread.sleep(velocidade);
            return;
        }

        List<Celula> caminho = infoCaminho.caminho;
        Celula saida = infoCaminho.saida;
        Direcao novaDirecao = infoCaminho.direcao;

        jantarDosFilosofos(celulaAtual, caminho, saida, novaDirecao);
    }

    private static class InformacoesCaminho {

        List<Celula> caminho;
        Celula saida;
        Direcao direcao;
    }

    private InformacoesCaminho escolherCaminhoSaida(Celula primeiroCruzamento) {
        List<InformacoesCaminho> caminhosValidos = encontrarCaminhosSaida(primeiroCruzamento);

        if (caminhosValidos.isEmpty()) {
            return null;
        }

        return caminhosValidos.get(random.nextInt(caminhosValidos.size()));
    }

    private List<InformacoesCaminho> encontrarCaminhosSaida(Celula primeiroCruzamento) {
        List<InformacoesCaminho> caminhosValidos = new ArrayList<>();
        Queue<Object[]> fila = new LinkedList<>();
        Set<String> visitados = new HashSet<>();
        visitados.add(primeiroCruzamento.getLinha() + "," + primeiroCruzamento.getColuna());
        fila.add(new Object[]{primeiroCruzamento, new ArrayList<>()});

        while (!fila.isEmpty()) {
            Object[] item = fila.poll();
            Celula atual = (Celula) item[0];
            @SuppressWarnings("unchecked")
            List<Celula> caminho = (List<Celula>) item[1];
            List<Celula> caminhoAqui = new ArrayList<>(caminho);
            caminhoAqui.add(atual);

            List<Integer> permitidas = obterDirecoesPermitidas(atual.getDirecao().getSentidoDirecao());

            for (int dirInt : permitidas) {
                Celula proximaCel = calcularCelulaSaida(atual, new Direcao(dirInt));
                if (proximaCel == null) {
                    continue;
                }

                if (!proximaCel.isCruzamento()) {
                    if (proximaCel.getDirecao().getSentidoDirecao() == dirInt && dirInt != TipoCelula.VAZIO) {
                        InformacoesCaminho info = new InformacoesCaminho();
                        info.caminho = caminhoAqui;
                        info.saida = proximaCel;
                        info.direcao = new Direcao(dirInt);
                        caminhosValidos.add(info);
                    }
                } else {
                    String chave = proximaCel.getLinha() + "," + proximaCel.getColuna();
                    if (!visitados.contains(chave)) {
                        visitados.add(chave);
                        fila.add(new Object[]{proximaCel, caminhoAqui});
                    }
                }
            }
        }

        return caminhosValidos;
    }

    private List<Integer> obterDirecoesPermitidas(int tipoCruzamento) {
        List<Integer> permitidas = new ArrayList<>();

        switch (tipoCruzamento) {
            case TipoCelula.CRUZAMENTO_CIMA:
                permitidas.add(TipoCelula.ESTRADA_CIMA);
                break;
            case TipoCelula.CRUZAMENTO_DIREITA:
                permitidas.add(TipoCelula.ESTRADA_DIREITA);
                break;
            case TipoCelula.CRUZAMENTO_BAIXO:
                permitidas.add(TipoCelula.ESTRADA_BAIXO);
                break;
            case TipoCelula.CRUZAMENTO_ESQUERDA:
                permitidas.add(TipoCelula.ESTRADA_ESQUERDA);
                break;
            case TipoCelula.CRUZAMENTO_CIMA_E_DIREITA:
                permitidas.add(TipoCelula.ESTRADA_CIMA);
                permitidas.add(TipoCelula.ESTRADA_DIREITA);
                break;
            case TipoCelula.CRUZAMENTO_CIMA_E_ESQUERDA:
                permitidas.add(TipoCelula.ESTRADA_CIMA);
                permitidas.add(TipoCelula.ESTRADA_ESQUERDA);
                break;
            case TipoCelula.CRUZAMENTO_DIREITA_E_BAIXO:
                permitidas.add(TipoCelula.ESTRADA_DIREITA);
                permitidas.add(TipoCelula.ESTRADA_BAIXO);
                break;
            case TipoCelula.CRUZAMENTO_BAIXO_E_ESQUERDA:
                permitidas.add(TipoCelula.ESTRADA_BAIXO);
                permitidas.add(TipoCelula.ESTRADA_ESQUERDA);
                break;
        }

        return permitidas;
    }

    private Celula calcularCelulaSaida(Celula celula, Direcao novaDirecao) {
        int sentido = novaDirecao.getSentidoDirecao();

        switch (sentido) {
            case TipoCelula.ESTRADA_CIMA:
                return rua.celulaParaCima(celula);
            case TipoCelula.ESTRADA_BAIXO:
                return rua.celulaParaBaixo(celula);
            case TipoCelula.ESTRADA_DIREITA:
                return rua.celulaParaDireita(celula);
            case TipoCelula.ESTRADA_ESQUERDA:
                return rua.celulaParaEsquerda(celula);
            default:
                return null;
        }
    }

    private Celula calcularProximaCelula() {
        if (celulaAtual == null || direcao == null) {
            return null;
        }

        return calcularCelulaSaida(celulaAtual, direcao);
    }

    private void jantarDosFilosofos(Celula origem, List<Celula> caminho, Celula saida, Direcao novaDirecao)
            throws InterruptedException {

        boolean conseguiuReservar = false;
        int tentativas = 0;

        while (!conseguiuReservar && ativo) {
            tentativas++;

            List<Celula> celulas = new ArrayList<>();
            celulas.add(origem);
            celulas.addAll(caminho);
            celulas.add(saida);

            ordenarParaBloquear(celulas);

            boolean[] bloqueios = new boolean[celulas.size()];
            boolean todasReservadas = true;

            for (int i = 0; i < celulas.size(); i++) {
                bloqueios[i] = celulas.get(i).tentarBloquear();
                if (!bloqueios[i]) {
                    todasReservadas = false;
                    break;
                }
            }

            if (todasReservadas) {
                try {
                    if (origem.getCarro() == this) {
                        boolean livre = true;
                        for (Celula c : caminho) {
                            if (c.getCarro() != null) {
                                livre = false;
                                break;
                            }
                        }
                        if (saida.getCarro() != null) {
                            livre = false;
                        }

                        if (livre) {
                            origem.removerCarroDaCelula();
                            caminho.get(0).setCarro(this);
                            definirCelulaAtual(caminho.get(0));
                            System.out.println("Carro " + id + " se moveu de (" + origem.getLinha() + "," + origem.getColuna() + ") para (" + caminho.get(0).getLinha() + "," + caminho.get(0).getColuna() + ")");
                            Thread.sleep(velocidade);

                            for (int i = 0; i < caminho.size() - 1; i++) {
                                caminho.get(i).removerCarroDaCelula();
                                caminho.get(i + 1).setCarro(this);
                                definirCelulaAtual(caminho.get(i + 1));
                                System.out.println("Carro " + id + " se moveu de (" + caminho.get(i).getLinha() + "," + caminho.get(i).getColuna() + ") para (" + caminho.get(i + 1).getLinha() + "," + caminho.get(i + 1).getColuna() + ")");
                                Thread.sleep(velocidade);
                            }

                            caminho.get(caminho.size() - 1).removerCarroDaCelula();
                            saida.setCarro(this);
                            definirCelulaAtual(saida);
                            System.out.println("Carro " + id + " se moveu de (" + caminho.get(caminho.size() - 1).getLinha() + "," + caminho.get(caminho.size() - 1).getColuna() + ") para (" + saida.getLinha() + "," + saida.getColuna() + ")");

                            this.direcao = novaDirecao;

                            conseguiuReservar = true;
                        }
                    }
                } finally {
                    for (int i = celulas.size() - 1; i >= 0; i--) {
                        if (bloqueios[i]) {
                            celulas.get(i).liberar();
                        }
                    }
                }
            } else {
                for (int i = 0; i < celulas.size(); i++) {
                    if (bloqueios[i]) {
                        celulas.get(i).liberar();
                    }
                }

                int backoff = velocidade + random.nextInt(500);
                Thread.sleep(backoff);
            }
        }
    }

    private void ordenarParaBloquear(List<Celula> toLock) {
        int cols = rua.getMatrizMalhaViaria()[0].length;
        toLock.sort(Comparator.comparingInt(c -> c.getLinha() * cols + c.getColuna()));
    }

    private void bloquearTodos(List<Celula> toLock) throws InterruptedException {
        for (Celula c : toLock) {
            c.bloquear();
        }
    }

    private void liberarTodos(List<Celula> toLock) {
        for (int i = toLock.size() - 1; i >= 0; i--) {
            toLock.get(i).liberar();
        }
    }

    public int obterIdCarro() {
        return id;
    }

    public void definirDirecao(Direcao direcao) {
        this.direcao = direcao;
    }

    public Celula obterCelulaAtual() {
        return celulaAtual;
    }

    public void definirCelulaAtual(Celula celulaAtual) {
        this.celulaAtual = celulaAtual;
    }

    private void pararCarro() {
        this.ativo = false;
    }
}