package com.mycompany.trabalho.threads.dsd.model;

import java.util.Random;

public class Carro extends Thread {

	private final Rua rua;
	private final int velocidade;
	private Direcao direcao;
	private Celula celulaAtual;
	private Celula proximaPosicao;
	private boolean ativo;

	public Carro(Rua rua, int velocidade) {
		this.rua = rua;
		this.velocidade = velocidade;
	}

	@Override
	public void run() {
		ativo = true;
		while (ativo) {
			mover();
		}
		System.out.println("Carro saiu da rua");
		rua.removerCarro(this);
	}
	
	public void mover() {
		
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
	}

	private void direitaParaCima() {
		Celula c1 = celulaADireita();
		Celula c2 = rua.celulaParaDireita(c1);
		Celula c3 = rua.celulaParaCima(c2);
		Celula c4 = rua.celulaParaCima(c3);
	}

	private void direitaParaBaixo() {
		Celula c1 = celulaADireita();
		Celula c2 = rua.celulaParaBaixo(c1);
	}

	private void esquerdaParaEsquerda() {
		Celula c1 = celulaAEsquerda();
		Celula c2 = rua.celulaParaEsquerda(c1);
		Celula c3 = rua.celulaParaEsquerda(c2);
	}

	private void esquerdaParaCima() {
		Celula c1 = celulaAEsquerda();
		Celula c2 = rua.celulaParaCima(c1);
	}

	private void esquerdaParaBaixo() {
		Celula c1 = celulaAEsquerda();
		Celula c2 = rua.celulaParaDireita(c1);
		Celula c3 = rua.celulaParaBaixo(c2);
		Celula c4 = rua.celulaParaBaixo(c3);
	}

	private void cimaParaDireita() {
		Celula c1 = celulaACima();
		Celula c2 = rua.celulaParaDireita(c1);
	}

	private void cimaParaEsquerda() {
		Celula c1 = celulaACima();
		Celula c2 = rua.celulaParaCima(c1);
		Celula c3 = rua.celulaParaEsquerda(c2);
		Celula c4 = rua.celulaParaEsquerda(c3);
	}

	private void cimaParaCima() {
		Celula c1 = celulaACima();
		Celula c2 = rua.celulaParaCima(c1);
		Celula c3 = rua.celulaParaCima(c2);
	}

	private void baixoParaDireita() {
		Celula c1 = celulaABaixo();
		Celula c2 = rua.celulaParaBaixo(c1);
		Celula c3 = rua.celulaParaDireita(c2);
		Celula c4 = rua.celulaParaDireita(c3);
	}

	private void baixoParaEsquerda() {
		Celula c1 = celulaABaixo();
		Celula c2 = rua.celulaParaEsquerda(c1);
	}

	private void baixoParaBaixo() {
		Celula c1 = celulaABaixo();
		Celula c2 = rua.celulaParaBaixo(c1);
		Celula c3 = rua.celulaParaBaixo(c2);
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
}