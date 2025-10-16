package util;

import com.mycompany.trabalho.threads.dsd.constantes.TipoCelula;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 * Gerenciador de ícones para células e carros.
 */
public class IconeCelula {

    private static IconeCelula instance;
    private static final String PATH_IMAGENS = "/imagens/";
    private static final int TAMANHO_CELULA = 32;

    // Cores das células
    private static final Color COR_VAZIO = new Color(34, 139, 34); // Verde
    private static final Color COR_ESTRADA = Color.BLACK;     
    private static final Color COR_CRUZAMENTO = new Color(128, 128, 128); // Cinza

    private final Map<Integer, ImageIcon> iconesCelulasVazias;
    private final Map<String, ImageIcon> iconesCelulasCarros; 
    
    // Imagens base
    private final Map<DirecaoSeta, ImageIcon> setas;
    
    // Cores para os carros
    private static final Color[] CORES_CARROS = {
        new Color(255, 69, 0),    // Vermelho-laranja
        new Color(30, 144, 255),  // Azul
        new Color(50, 205, 50),   // Verde-limão
        new Color(255, 215, 0),   // Ouro
        new Color(255, 20, 147),  // Rosa
        new Color(138, 43, 226),  // Violeta
        new Color(0, 255, 255),   // Ciano
        new Color(255, 165, 0),   // Laranja
        new Color(255, 105, 180), // Rosa-claro
        new Color(64, 224, 208)   // Turquesa
    };

    private enum DirecaoSeta {
        CIMA, BAIXO, DIREITA, ESQUERDA
    }

    private IconeCelula() {
        iconesCelulasVazias = new HashMap<>();
        iconesCelulasCarros = new HashMap<>();
        setas = new EnumMap<>(DirecaoSeta.class);
        
        carregarImagensBase();
        gerarIconesCelulasVazias();
    }

    public static synchronized IconeCelula getInstance() {
        if (instance == null) {
            instance = new IconeCelula();
        }
        return instance;
    }

    private void carregarImagensBase() {
        try {
            setas.put(DirecaoSeta.CIMA, carregarIcone("seta_cima.png"));
            setas.put(DirecaoSeta.BAIXO, carregarIcone("seta_baixo.png"));
            setas.put(DirecaoSeta.DIREITA, carregarIcone("seta_direita.png"));
            setas.put(DirecaoSeta.ESQUERDA, carregarIcone("seta_esquerda.png"));
            
            System.out.println("Setas carregadas com sucesso");
        } catch (Exception e) {
            System.err.println("Erro ao carregar setas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carrega um ícone
     */
    private ImageIcon carregarIcone(String nomeArquivo) {
        String caminho = PATH_IMAGENS + nomeArquivo;
        ImageIcon icone = new ImageIcon(getClass().getResource(caminho));
        
        if (icone.getIconWidth() <= 0) {
            throw new RuntimeException("Falha ao carregar: " + caminho);
        }
        
        return icone;
    }

    /**
     * Gera ícones de células vazias
     */
    private void gerarIconesCelulasVazias() {
        for (int tipo = TipoCelula.VAZIO; tipo <= TipoCelula.CRUZAMENTO_BAIXO_ESQUERDA; tipo++) {
            iconesCelulasVazias.put(tipo, criarIconeCelulaVazia(tipo));
        }
        System.out.println("Cache de células vazias gerado: " + iconesCelulasVazias.size() + " ícones");
    }

    /**
     * Cria um ícone para uma célula vazia.
     */
    private ImageIcon criarIconeCelulaVazia(int tipo) {
        BufferedImage imagem = new BufferedImage(
            TAMANHO_CELULA, 
            TAMANHO_CELULA, 
            BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2d = imagem.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Desenha cor de fundo
        desenharFundo(g2d, tipo);

        // 2. Desenha seta (apenas para estradas)
        if (isEstrada(tipo)) {
            desenharSeta(g2d, tipo);
        }

        g2d.dispose();
        return new ImageIcon(imagem);
    }
    
    /**
     * Cria um ícone para uma célula com carro.
     */
    private ImageIcon criarIconeCelulaComCarro(int tipo, int idCarro) {
        BufferedImage imagem = new BufferedImage(
            TAMANHO_CELULA, 
            TAMANHO_CELULA, 
            BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2d = imagem.createGraphics();
       
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 1. Desenha cor de fundo
        desenharFundo(g2d, tipo);

        // 2. Desenha bolinha com número
        desenharCarro(g2d, idCarro);

        g2d.dispose();
        return new ImageIcon(imagem);
    }
    
    /**
     * Desenha uma bolinha colorida com o ID do carro.
     */
    private void desenharCarro(Graphics2D g2d, int idCarro) {
        // Calcula cor baseada no ID
        Color corCarro = CORES_CARROS[idCarro % CORES_CARROS.length];
        
        // Tamanho da bolinha (80% do tamanho da célula)
        int diametro = (int) (TAMANHO_CELULA * 0.8);
        int x = (TAMANHO_CELULA - diametro) / 2;
        int y = (TAMANHO_CELULA - diametro) / 2;
        
        // Desenha sombra
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillOval(x + 2, y + 2, diametro, diametro);
        
        // Desenha bolinha
        g2d.setColor(corCarro);
        g2d.fillOval(x, y, diametro, diametro);
        
        // Desenha borda
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new java.awt.BasicStroke(2));
        g2d.drawOval(x, y, diametro, diametro);
        
        // Desenha número
        String numero = String.valueOf(idCarro);
        Font fonte = new Font("Arial", Font.BOLD, TAMANHO_CELULA / 2);
        g2d.setFont(fonte);
        
        FontMetrics fm = g2d.getFontMetrics();
        int larguraTexto = fm.stringWidth(numero);
        int alturaTexto = fm.getAscent();
        
        int xTexto = (TAMANHO_CELULA - larguraTexto) / 2;
        int yTexto = (TAMANHO_CELULA + alturaTexto) / 2 - 2;
        
        // Sombra do texto
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(numero, xTexto + 1, yTexto + 1);
        
        // Texto
        g2d.setColor(Color.WHITE);
        g2d.drawString(numero, xTexto, yTexto);
    }

    /**
     * Desenha o fundo colorido da célula.
     */
    private void desenharFundo(Graphics2D g2d, int tipo) {
        Color cor = obterCorFundo(tipo);
        g2d.setColor(cor);
        g2d.fillRect(0, 0, TAMANHO_CELULA, TAMANHO_CELULA);
    }

    /**
     * Retorna a cor de fundo baseada no tipo da célula.
     */
    private Color obterCorFundo(int tipo) {
        if (tipo == TipoCelula.VAZIO) {
            return COR_VAZIO;
        } else if (isEstrada(tipo)) {
            return COR_ESTRADA;
        } else if (isCruzamento(tipo)) {
            return COR_CRUZAMENTO;
        }
        return Color.WHITE;
    }

    /**
     * Desenha a seta apropriada para estradas.
     */
    private void desenharSeta(Graphics2D g2d, int tipo) {
        ImageIcon seta = obterSetaPorTipo(tipo);
        
        if (seta != null) {
            g2d.drawImage(seta.getImage(), 0, 0, 
                         TAMANHO_CELULA, TAMANHO_CELULA, null);
        }
    }

    /**
     * Retorna a seta apropriada baseada no tipo da estrada.
     */
    private ImageIcon obterSetaPorTipo(int tipo) {
        switch (tipo) {
            case TipoCelula.ESTRADA_CIMA:
                return setas.get(DirecaoSeta.CIMA);
                
            case TipoCelula.ESTRADA_BAIXO:
                return setas.get(DirecaoSeta.BAIXO);
                
            case TipoCelula.ESTRADA_DIREITA:
                return setas.get(DirecaoSeta.DIREITA);
                
            case TipoCelula.ESTRADA_ESQUERDA:
                return setas.get(DirecaoSeta.ESQUERDA);
                
            default:
                return null;
        }
    }

    /**
     * Verifica se o tipo é uma estrada.
     */
    private boolean isEstrada(int tipo) {
        return tipo >= TipoCelula.ESTRADA_CIMA 
            && tipo <= TipoCelula.ESTRADA_ESQUERDA;
    }

    /**
     * Verifica se o tipo é um cruzamento.
     */
    private boolean isCruzamento(int tipo) {
        return tipo >= TipoCelula.CRUZAMENTO_CIMA 
            && tipo <= TipoCelula.CRUZAMENTO_BAIXO_ESQUERDA;
    }

    /**
     * Retorna o ícone apropriado para uma célula.
     */
    public ImageIcon getIconeCelula(int tipo, int idCarro) {
        // Se não há carro, retorna célula vazia
        if (idCarro < 0) {
            return iconesCelulasVazias.getOrDefault(tipo, null);
        }
        
        // Se há carro, verifica cache ou cria novo
        String chave = tipo + "_" + idCarro;
        
        if (!iconesCelulasCarros.containsKey(chave)) {
            // Cria e armazena no cache
            iconesCelulasCarros.put(chave, criarIconeCelulaComCarro(tipo, idCarro));
        }
        
        return iconesCelulasCarros.get(chave);
    }
}