package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.mycompany.trabalho.threads.dsd.model.TipoCelula;
import com.mycompany.trabalho.threads.dsd.model.Celula;
import com.mycompany.trabalho.threads.dsd.model.CelulaMonitor;
import com.mycompany.trabalho.threads.dsd.model.CelulaSemaforo;
import com.mycompany.trabalho.threads.dsd.model.Direcao;
import java.io.InputStreamReader;

public class LeitorMalhaViaria {
	
	private Celula[][] matrizMalhaViaria;
	private final boolean usarSemaforo;
	
	public LeitorMalhaViaria(boolean usarSemaforo) {
		this.usarSemaforo = usarSemaforo;
	}

	public Celula[][] criarMatrizMalhaViaria(String caminhoArquivo) throws FileNotFoundException, IOException {
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(caminhoArquivo)))) {
			int linhas = Integer.parseInt(bufferedReader.readLine().trim());
			int colunas = Integer.parseInt(bufferedReader.readLine().trim());
			
			matrizMalhaViaria = new Celula[linhas][colunas];
			
			for (int i = 0; i < linhas; i++) {
				String[] valoresLinha = bufferedReader.readLine().trim().split("\\s+");
				for (int j = 0; j < colunas; j++) {
					int valorColuna = Integer.parseInt(valoresLinha[j]);
					
					Direcao direcao = new Direcao(valorColuna);
					boolean isEntrada = identificarEntradas(direcao.getSentidoDirecao(), i, j);
					boolean isCruzamento = identificarCruzamentos(direcao.getSentidoDirecao());
					
					if (usarSemaforo) {
						matrizMalhaViaria[i][j] = new CelulaSemaforo(i,  j, direcao, isEntrada, isCruzamento);
					} else {
						matrizMalhaViaria[i][j] = new CelulaMonitor(i, j, direcao, isEntrada, isCruzamento);
					}
				}
			}
		}
		return matrizMalhaViaria;
	}
	
	private boolean identificarEntradas(int direcao, int linha, int coluna) {
		int linhas = matrizMalhaViaria.length;
		int colunas = matrizMalhaViaria[0].length;
		
		boolean entradaBaixo = direcao == TipoCelula.ESTRADA_BAIXO && linha == linhas - 1;
		boolean entradaCima = direcao == TipoCelula.ESTRADA_CIMA && linha == 0;
		boolean entradaDireita = direcao == TipoCelula.ESTRADA_DIREITA && coluna == colunas - 1;
		boolean entradaEsquerda = direcao == TipoCelula.ESTRADA_ESQUERDA && coluna == 0;
		
		return entradaBaixo || entradaCima || entradaDireita || entradaEsquerda;
	}
	
	private boolean identificarCruzamentos(int direcao) {
		return direcao > 4;
	}
}