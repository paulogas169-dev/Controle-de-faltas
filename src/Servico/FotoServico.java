package Servico;

import Escola.Aluno;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serviço responsável por localizar, processar, redimensionar e cachear
 * fotos de alunos e ícones da aplicação com alto desempenho e qualidade.
 */
public class FotoServico {

    private static final Map<String, ImageIcon> CACHE_FOTOS = new ConcurrentHashMap<>();
    private static final Map<String, ImageIcon> CACHE_ICONES = new ConcurrentHashMap<>();

    private static final String[] EXTENSOES = {
            ".jpeg", ".jpg", ".png", ".webp", ".bmp",
            ".JPEG", ".JPG", ".PNG"
    };

    private static final String[] DIRETORIOS_IMAGENS = {
            "imagens",
            "Imagens",
            "src/Imagens",
            "Controle de faltas/src/Imagens",
            "build/imagens",
            "dist/imagens",
            "fotos",
            "Fotos",
            "."
    };

    /**
     * Localiza e retorna a foto do aluno ajustada para a dimensão solicitada.
     */
    public static ImageIcon obterFotoAluno(Aluno aluno, String uid, int largura, int altura) {
        if (aluno == null && (uid == null || uid.isBlank())) {
            return null;
        }

        String chaveCache = (aluno != null ? aluno.getId() + "_" + aluno.getNome() : "uid_" + uid)
                + "_" + largura + "x" + altura;

        ImageIcon emCache = CACHE_FOTOS.get(chaveCache);
        if (emCache != null) {
            return emCache;
        }

        BufferedImage imagemOriginal = buscarImagemOriginal(aluno, uid);
        if (imagemOriginal != null) {
            ImageIcon ajustada = criarFotoAjustada(imagemOriginal, largura, altura);
            CACHE_FOTOS.put(chaveCache, ajustada);
            return ajustada;
        }

        return null;
    }

    /**
     * Busca a imagem original nos recursos do JAR e em pastas em disco.
     */
    private static BufferedImage buscarImagemOriginal(Aluno aluno, String uid) {
        List<String> possiveisNomes = gerarPossiveisNomes(aluno, uid);

        // 1. Tentar por nomes diretos em recursos e disco
        for (String base : possiveisNomes) {
            for (String ext : EXTENSOES) {
                String arquivo = base + ext;

                // Recursos (dentro do JAR)
                BufferedImage img = lerDeRecurso(arquivo);
                if (img != null) {
                    return img;
                }

                // Arquivo no disco
                img = lerDeDisco(arquivo);
                if (img != null) {
                    return img;
                }
            }
        }

        // 2. Varredura dinâmica em diretórios de imagem para correspondência parcial
        BufferedImage imgDinamica = buscarPorVarredura(aluno, uid);
        if (imgDinamica != null) {
            return imgDinamica;
        }

        return null;
    }

    /**
     * Gera lista de termos de busca ordenados por prioridade.
     */
    private static List<String> gerarPossiveisNomes(Aluno aluno, String uid) {
        LinkedHashSet<String> nomes = new LinkedHashSet<>();

        if (uid != null && !uid.isBlank()) {
            String u = uid.trim();
            nomes.add(u);
            nomes.add(u.replace(" ", ""));
            nomes.add(u.replace(":", ""));
            nomes.add(u.toUpperCase(Locale.ROOT));
            nomes.add(u.toLowerCase(Locale.ROOT));
        }

        if (aluno != null) {
            nomes.add(String.valueOf(aluno.getId()));
            nomes.add("aluno_" + aluno.getId());
            nomes.add("aluno" + aluno.getId());
        }

        return new ArrayList<>(nomes);
    }

    /**
     * Varre diretórios em busca de arquivos que contenham o primeiro nome ou UID do aluno.
     */
    private static BufferedImage buscarPorVarredura(Aluno aluno, String uid) {
        String primeiroNomeNorm = null;
        if (aluno != null && aluno.getNome() != null && !aluno.getNome().isBlank()) {
            String[] partes = removerAcentos(aluno.getNome().trim().toUpperCase(Locale.ROOT)).split("\\s+");
            if (partes.length > 0) {
                primeiroNomeNorm = partes[0];
            }
        }

        String uidSemEspacos = uid != null ? uid.replace(" ", "").toUpperCase(Locale.ROOT) : null;

        for (String dirPath : DIRETORIOS_IMAGENS) {
            File dir = new File(dirPath);
            if (!dir.isDirectory()) continue;

            File[] arquivos = dir.listFiles();
            if (arquivos == null) continue;

            for (File f : arquivos) {
                if (!f.isFile()) continue;
                String nomeArq = f.getName();
                String nomeSemExt = nomeArq.contains(".") ?
                        nomeArq.substring(0, nomeArq.lastIndexOf('.')) : nomeArq;
                String nomeSemExtNorm = removerAcentos(nomeSemExt.toUpperCase(Locale.ROOT));

                // Verifica match por primeiro nome (ex: Julio.jpeg com Julio Gabriel...)
                if (primeiroNomeNorm != null && (nomeSemExtNorm.equals(primeiroNomeNorm)
                        || nomeSemExtNorm.contains(primeiroNomeNorm)
                        || primeiroNomeNorm.contains(nomeSemExtNorm))) {
                    try {
                        BufferedImage bi = ImageIO.read(f);
                        if (bi != null) return bi;
                    } catch (Exception ignored) {}
                }

                // Match por UID
                if (uidSemEspacos != null && nomeSemExtNorm.replace(" ", "").contains(uidSemEspacos)) {
                    try {
                        BufferedImage bi = ImageIO.read(f);
                        if (bi != null) return bi;
                    } catch (Exception ignored) {}
                }
            }
        }
        return null;
    }

    private static BufferedImage lerDeRecurso(String caminho) {
        String[] prefixos = {"/imagens/", "/Imagens/", "imagens/", "Imagens/", "/"};
        for (String pre : prefixos) {
            String full = pre + caminho;
            try (InputStream is = FotoServico.class.getResourceAsStream(full)) {
                if (is != null) {
                    BufferedImage img = ImageIO.read(is);
                    if (img != null) return img;
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    private static BufferedImage lerDeDisco(String caminho) {
        for (String dir : DIRETORIOS_IMAGENS) {
            File f = new File(dir, caminho);
            if (f.isFile()) {
                try {
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) return img;
                } catch (Exception ignored) {}
            }
        }
        File fAbs = new File(caminho);
        if (fAbs.isFile()) {
            try {
                BufferedImage img = ImageIO.read(fAbs);
                if (img != null) return img;
            } catch (Exception ignored) {}
        }
        return null;
    }

    /**
     * Redimensiona mantendo a proporção e preenchendo o quadro com interpolação bicúbica.
     */
    public static ImageIcon criarFotoAjustada(BufferedImage original, int largura, int altura) {
        if (original == null) return null;

        double escala = Math.max((double) largura / original.getWidth(),
                (double) altura / original.getHeight());
        int larguraNova = Math.max(largura, (int) Math.round(original.getWidth() * escala));
        int alturaNova = Math.max(altura, (int) Math.round(original.getHeight() * escala));

        BufferedImage ampliada = new BufferedImage(larguraNova, alturaNova, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = ampliada.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(original, 0, 0, larguraNova, alturaNova, null);
        } finally {
            g.dispose();
        }

        int x = (larguraNova - largura) / 2;
        int y = (alturaNova - altura) / 2;

        BufferedImage finalImagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        Graphics2D gFinal = finalImagem.createGraphics();
        try {
            gFinal.drawImage(ampliada, 0, 0, largura, altura,
                    x, y, x + largura, y + altura, null);
        } finally {
            gFinal.dispose();
        }

        return new ImageIcon(finalImagem);
    }

    /**
     * Retorna o ícone oficial da aplicação redimensionado para as dimensões desejadas.
     */
    public static ImageIcon getIconeApp(int largura, int altura) {
        String chave = largura + "x" + altura;
        ImageIcon cache = CACHE_ICONES.get(chave);
        if (cache != null) {
            return cache;
        }

        BufferedImage imgOriginal = lerDeRecurso("icone_app.png");
        if (imgOriginal == null) {
            imgOriginal = lerDeDisco("icone_app.png");
        }

        if (imgOriginal != null) {
            BufferedImage resized = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = resized.createGraphics();
            try {
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.drawImage(imgOriginal, 0, 0, largura, altura, null);
            } finally {
                g.dispose();
            }
            ImageIcon icon = new ImageIcon(resized);
            CACHE_ICONES.put(chave, icon);
            return icon;
        }

        return null;
    }

    /**
     * Cria um placeholder estilizado exibido enquanto aguarda a aproximação de um cartão.
     */
    public static ImageIcon criarPlaceholderAguardando(int largura, int altura) {
        BufferedImage img = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Fundo suave
            g.setColor(new Color(248, 250, 252));
            g.fillRect(0, 0, largura, altura);

            // Borda interna arredondada pontilhada/suave
            g.setColor(new Color(226, 232, 240));
            g.drawRoundRect(10, 10, largura - 20, altura - 20, 16, 16);

            // Desenha o ícone do aplicativo ou silhueta no centro
            ImageIcon iconeCentro = getIconeApp(96, 96);
            int centroY = (altura / 2) - 35;
            if (iconeCentro != null) {
                g.drawImage(iconeCentro.getImage(), (largura - 96) / 2, centroY - 48, null);
            }

            // Texto de orientação
            g.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g.setColor(new Color(100, 116, 139));
            FontMetrics fm = g.getFontMetrics();
            String txt1 = "Aguardando cartão RFID";
            g.drawString(txt1, (largura - fm.stringWidth(txt1)) / 2, centroY + 75);

            g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g.setColor(new Color(148, 163, 184));
            fm = g.getFontMetrics();
            String txt2 = "Aproxime o cartão do leitor";
            g.drawString(txt2, (largura - fm.stringWidth(txt2)) / 2, centroY + 95);

        } finally {
            g.dispose();
        }
        return new ImageIcon(img);
    }

    /**
     * Cria um avatar com as iniciais do aluno caso não haja arquivo de foto em disco.
     */
    public static ImageIcon criarAvatarPlaceholder(String nomeAluno, int largura, int altura) {
        BufferedImage img = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Gradiente de fundo elegante
            Color c1 = new Color(30, 64, 175);
            Color c2 = new Color(59, 130, 246);
            GradientPaint gp = new GradientPaint(0, 0, c1, largura, altura, c2);
            g.setPaint(gp);
            g.fillRect(0, 0, largura, altura);

            // Círculo central para as iniciais
            int diam = Math.min(largura, altura) / 2;
            int cx = (largura - diam) / 2;
            int cy = (altura - diam) / 2 - 20;

            g.setColor(new Color(255, 255, 255, 40));
            g.fillOval(cx, cy, diam, diam);
            g.setColor(new Color(255, 255, 255, 80));
            g.setStroke(new BasicStroke(3));
            g.drawOval(cx, cy, diam, diam);

            // Iniciais
            String iniciais = extrairIniciais(nomeAluno);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Segoe UI", Font.BOLD, diam / 2));
            FontMetrics fm = g.getFontMetrics();
            int tx = cx + (diam - fm.stringWidth(iniciais)) / 2;
            int ty = cy + ((diam - fm.getHeight()) / 2) + fm.getAscent();
            g.drawString(iniciais, tx, ty);

            // Legenda inferior
            g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            g.setColor(new Color(224, 231, 255));
            fm = g.getFontMetrics();
            String leg = "Foto não cadastrada";
            g.drawString(leg, (largura - fm.stringWidth(leg)) / 2, cy + diam + 40);

        } finally {
            g.dispose();
        }
        return new ImageIcon(img);
    }

    public static void limparCache() {
        CACHE_FOTOS.clear();
        CACHE_ICONES.clear();
    }

    /**
     * Verifica de forma rápida e leve se existe um arquivo de foto para o aluno ou UID.
     */
    public static boolean temFoto(Aluno aluno, String uid) {
        if (aluno == null) return false;

        // 1. Checa fotos/aluno_<id>.jpg ou png
        File f1 = new File("fotos/aluno_" + aluno.getId() + ".jpg");
        if (f1.exists()) return true;
        File f1Png = new File("fotos/aluno_" + aluno.getId() + ".png");
        if (f1Png.exists()) return true;

        // 2. Checa fotos/<UID>.jpg ou png
        if (uid != null && !uid.isBlank()) {
            String u = uid.trim().replace(" ", "").replace(":", "").toUpperCase(Locale.ROOT);
            File f2 = new File("fotos/" + u + ".jpg");
            if (f2.exists()) return true;
            File f2Png = new File("fotos/" + u + ".png");
            if (f2Png.exists()) return true;
        }

        // 3. Checa por primeiro nome em fotos ou imagens
        if (aluno.getNome() != null && !aluno.getNome().isBlank()) {
            String primeiro = removerAcentos(aluno.getNome().trim().split("\\s+")[0]);
            String[] nomes = { primeiro, primeiro.toLowerCase(Locale.ROOT), primeiro.toUpperCase(Locale.ROOT) };
            for (String n : nomes) {
                if (new File("fotos/" + n + ".jpg").exists() ||
                    new File("fotos/" + n + ".jpeg").exists() ||
                    new File("fotos/" + n + ".png").exists() ||
                    new File("Imagens/" + n + ".jpeg").exists() ||
                    new File("Imagens/" + n + ".jpg").exists() ||
                    new File("src/Imagens/" + n + ".jpeg").exists() ||
                    new File("Controle de faltas/src/Imagens/" + n + ".jpeg").exists()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Salva a foto para o aluno no diretório "fotos" do sistema.
     */
    public static boolean salvarFotoAluno(File arquivoOrigem, Aluno aluno, String uid) {
        if (arquivoOrigem == null || !arquivoOrigem.exists() || aluno == null) {
            return false;
        }

        try {
            BufferedImage original = ImageIO.read(arquivoOrigem);
            if (original == null) {
                return false;
            }

            // Garante que o diretório fotos exista
            File pastaFotos = new File("fotos");
            if (!pastaFotos.exists()) {
                pastaFotos.mkdirs();
            }

            // Salva como aluno_<id>.jpg
            File destinoId = new File(pastaFotos, "aluno_" + aluno.getId() + ".jpg");
            ImageIO.write(original, "jpg", destinoId);

            // Se o aluno tiver nome, salva também com o primeiro nome (ex: Paulo.jpeg ou Paulo.jpg)
            if (aluno.getNome() != null && !aluno.getNome().isBlank()) {
                String primeiro = removerAcentos(aluno.getNome().trim().split("\\s+")[0]);
                File destinoNome = new File(pastaFotos, primeiro + ".jpg");
                ImageIO.write(original, "jpg", destinoNome);
            }

            // Se tiver UID, salva também como <UID>.jpg
            if (uid != null && !uid.isBlank()) {
                String u = uid.trim().replace(" ", "").replace(":", "").toUpperCase(Locale.ROOT);
                File destinoUid = new File(pastaFotos, u + ".jpg");
                ImageIO.write(original, "jpg", destinoUid);
            }

            // Se existir pasta src/Imagens no projeto (desenvolvimento), replica para conveniência
            File pastaSrc = new File("Controle de faltas/src/Imagens");
            if (pastaSrc.exists() && pastaSrc.isDirectory()) {
                File destinoSrcId = new File(pastaSrc, "aluno_" + aluno.getId() + ".jpg");
                ImageIO.write(original, "jpg", destinoSrcId);
            }

            limparCache();
            return true;
        } catch (Exception e) {
            System.out.println("Erro ao salvar foto do aluno: " + e.getMessage());
            return false;
        }
    }

    /**
     * Remove os arquivos de fotos associados ao aluno.
     */
    public static boolean removerFotoAluno(Aluno aluno, String uid) {
        if (aluno == null) return false;
        boolean removido = false;
        List<String> possiveisNomes = gerarPossiveisNomes(aluno, uid);

        for (String dirPath : DIRETORIOS_IMAGENS) {
            File dir = new File(dirPath);
            if (!dir.exists() || !dir.isDirectory()) continue;

            for (String base : possiveisNomes) {
                for (String ext : EXTENSOES) {
                    File f = new File(dir, base + ext);
                    if (f.exists() && f.isFile()) {
                        // Não deleta ícones da aplicação
                        if (!f.getName().toLowerCase(Locale.ROOT).contains("icone")) {
                            if (f.delete()) {
                                removido = true;
                            }
                        }
                    }
                }
            }
        }

        limparCache();
        return removido;
    }

    private static String extrairIniciais(String nome) {
        if (nome == null || nome.isBlank()) return "AL";
        String[] partes = nome.trim().split("\\s+");
        if (partes.length == 1) {
            return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase(Locale.ROOT);
        }
        return ("" + partes[0].charAt(0) + partes[partes.length - 1].charAt(0)).toUpperCase(Locale.ROOT);
    }

    private static String removerAcentos(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    private static String primeiraMaiuscula(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1).toLowerCase(Locale.ROOT);
    }
}