package Servico;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;
import java.util.Random;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Serviço de envio de tokens e e-mails via Gmail SMTP (porta 587 STARTTLS)
 * utilizando apenas a biblioteca padrão do Java (sem dependência de jars externos).
 */
public class EmailServico {

    private static final Random RANDOM = new Random();
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    public static class ResultadoEnvio {
        public final boolean sucesso;
        public final String mensagem;
        public final String detalhe;
        public final boolean configurado;

        public ResultadoEnvio(boolean sucesso, String mensagem, String detalhe, boolean configurado) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
            this.detalhe = detalhe;
            this.configurado = configurado;
        }
    }

    public static String gerarToken() {
        int numero = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(numero);
    }

    /**
     * Localiza o arquivo email.properties na raiz do projeto ou diretórios conhecidos.
     */
    private static File encontrarArquivoConfig() {
        File[] candidatos = new File[]{
                new File("email.properties"),
                new File("Controle de faltas/email.properties"),
                new File("out/production/ProjetoDiogo/email.properties")
        };
        for (File f : candidatos) {
            if (f.exists()) {
                return f;
            }
        }
        return candidatos[0];
    }

    public static Properties carregarConfiguracoes() {
        Properties props = new Properties();
        File arquivo = encontrarArquivoConfig();
        if (arquivo.exists()) {
            try (FileInputStream fis = new FileInputStream(arquivo);
                 InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
                props.load(isr);
            } catch (Exception e) {
                System.err.println("Erro ao ler email.properties: " + e.getMessage());
            }
        } else {
            // Tenta carregar do classpath
            try (java.io.InputStream is = EmailServico.class.getResourceAsStream("/email.properties")) {
                if (is != null) {
                    try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                        props.load(isr);
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return props;
    }

    public static void salvarConfiguracoes(String usuario, String senha, String remetenteNome) {
        Properties props = carregarConfiguracoes();
        if (usuario != null) props.setProperty("smtp.usuario", usuario.trim());
        if (senha != null) props.setProperty("smtp.senha", senha.trim());
        if (remetenteNome != null) props.setProperty("smtp.remetente_nome", remetenteNome.trim());
        props.setProperty("smtp.host", SMTP_HOST);
        props.setProperty("smtp.port", String.valueOf(SMTP_PORT));

        File arquivo = encontrarArquivoConfig();
        try (FileOutputStream fos = new FileOutputStream(arquivo);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            props.store(osw, "Configurações de E-mail Gmail SMTP");
        } catch (Exception e) {
            System.err.println("Erro ao salvar email.properties: " + e.getMessage());
        }
    }

    public static boolean isConfigurado() {
        Properties props = carregarConfiguracoes();
        String usuario = props.getProperty("smtp.usuario", "").trim();
        String senha = props.getProperty("smtp.senha", "").trim();
        return !usuario.isEmpty() && !senha.isEmpty();
    }

    public static String getUsuarioRemetente() {
        Properties props = carregarConfiguracoes();
        return props.getProperty("smtp.usuario", "").trim();
    }

    /**
     * Envia o token de verificação diretamente para a caixa de e-mail do Gmail do usuário.
     * Conecta no servidor smtp.gmail.com:587 com STARTTLS e envia mensagem formatada em HTML.
     */
    public static ResultadoEnvio enviarTokenGmail(String destinatario, String nomeDestinatario, String token) {
        if (destinatario == null || !destinatario.contains("@") || !destinatario.contains(".")) {
            return new ResultadoEnvio(
                    false,
                    "Endereço de e-mail inválido.",
                    "O e-mail deve conter '@' e um domínio válido (ex: seuemail@gmail.com).",
                    false
            );
        }

        Properties props = carregarConfiguracoes();
        String usuarioSmtp = props.getProperty("smtp.usuario", "").trim();
        String senhaSmtp = props.getProperty("smtp.senha", "").trim();
        String remetenteNome = props.getProperty("smtp.remetente_nome", "Sistema de Gestão Escolar").trim();

        if (usuarioSmtp.isEmpty() || senhaSmtp.isEmpty()) {
            return new ResultadoEnvio(
                    false,
                    "Envio SMTP não configurado no arquivo email.properties.",
                    "Preencha 'smtp.usuario' e 'smtp.senha' no arquivo email.properties com sua Senha de App do Google.",
                    false
            );
        }

        // Remove espaços de senhas de app do Google (ex: 'abcd efgh ijkl mnop' -> 'abcdefghijklmnop')
        String senhaLimpa = senhaSmtp.replace(" ", "");

        Socket socket = null;
        SSLSocket sslSocket = null;
        try {
            System.out.println("📧 Conectando a " + SMTP_HOST + ":" + SMTP_PORT + " para envio a: " + destinatario);
            socket = new Socket(SMTP_HOST, SMTP_PORT);
            socket.setSoTimeout(15000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            // 1. Ler saudação 220
            String linha = reader.readLine();
            if (linha == null || !linha.startsWith("220")) {
                throw new Exception("Resposta inesperada do servidor: " + linha);
            }

            // 2. Enviar EHLO
            writer.write("EHLO localhost\r\n");
            writer.flush();
            lerRespostaSmtp(reader, 250);

            // 3. STARTTLS
            writer.write("STARTTLS\r\n");
            writer.flush();
            linha = reader.readLine();
            if (linha == null || !linha.startsWith("220")) {
                throw new Exception("Servidor recusou STARTTLS: " + linha);
            }

            // 4. Iniciar SSL Handshake
            SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
            sslSocket = (SSLSocket) ssf.createSocket(socket, SMTP_HOST, SMTP_PORT, true);
            sslSocket.setSoTimeout(15000);
            sslSocket.startHandshake();

            reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream(), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream(), StandardCharsets.UTF_8));

            // 5. Novo EHLO sobre TLS
            writer.write("EHLO localhost\r\n");
            writer.flush();
            lerRespostaSmtp(reader, 250);

            // 6. Autenticação AUTH LOGIN
            writer.write("AUTH LOGIN\r\n");
            writer.flush();
            linha = reader.readLine();
            if (linha == null || !linha.startsWith("334")) {
                throw new Exception("Falha ao iniciar AUTH LOGIN: " + linha);
            }

            // Enviar Usuário (Base64)
            String usuarioB64 = Base64.getEncoder().encodeToString(usuarioSmtp.getBytes(StandardCharsets.UTF_8));
            writer.write(usuarioB64 + "\r\n");
            writer.flush();
            linha = reader.readLine();
            if (linha == null || !linha.startsWith("334")) {
                throw new Exception("Usuário recusado pelo Gmail: " + linha);
            }

            // Enviar Senha de App (Base64)
            String senhaB64 = Base64.getEncoder().encodeToString(senhaLimpa.getBytes(StandardCharsets.UTF_8));
            writer.write(senhaB64 + "\r\n");
            writer.flush();
            linha = reader.readLine();
            if (linha == null || !linha.startsWith("235")) {
                throw new Exception("Autenticação recusada pelo Gmail (" + linha + "). Verifique se você gerou uma 'Senha de App' de 16 dígitos em myaccount.google.com/apppasswords.");
            }

            // 7. MAIL FROM
            writer.write("MAIL FROM:<" + usuarioSmtp + ">\r\n");
            writer.flush();
            lerRespostaSmtp(reader, 250);

            // 8. RCPT TO
            writer.write("RCPT TO:<" + destinatario + ">\r\n");
            writer.flush();
            lerRespostaSmtp(reader, 250);

            // 9. DATA
            writer.write("DATA\r\n");
            writer.flush();
            linha = reader.readLine();
            if (linha == null || !linha.startsWith("354")) {
                throw new Exception("Erro ao iniciar dados do e-mail: " + linha);
            }

            // 10. Construir Mensagem MIME com HTML
            String assunto = "Código de Verificação: " + token + " - Sistema Escolar";
            String assuntoB64 = "=?UTF-8?B?" + Base64.getEncoder().encodeToString(assunto.getBytes(StandardCharsets.UTF_8)) + "?=";
            String remetenteB64 = "=?UTF-8?B?" + Base64.getEncoder().encodeToString(remetenteNome.getBytes(StandardCharsets.UTF_8)) + "?= <" + usuarioSmtp + ">";

            String corpoHtml = gerarCorpoHtml(nomeDestinatario, token);
            String corpoB64 = Base64.getMimeEncoder().encodeToString(corpoHtml.getBytes(StandardCharsets.UTF_8));

            writer.write("From: " + remetenteB64 + "\r\n");
            writer.write("To: <" + destinatario + ">\r\n");
            writer.write("Subject: " + assuntoB64 + "\r\n");
            writer.write("MIME-Version: 1.0\r\n");
            writer.write("Content-Type: text/html; charset=UTF-8\r\n");
            writer.write("Content-Transfer-Encoding: base64\r\n");
            writer.write("\r\n");
            writer.write(corpoB64 + "\r\n");
            writer.write(".\r\n");
            writer.flush();

            linha = reader.readLine();
            if (linha == null || !linha.startsWith("250")) {
                throw new Exception("Erro ao confirmar entrega: " + linha);
            }

            // 11. QUIT
            writer.write("QUIT\r\n");
            writer.flush();

            System.out.println("✅ E-mail com token entregue com sucesso no Gmail de: " + destinatario);
            return new ResultadoEnvio(
                    true,
                    "E-mail com o token enviado com sucesso para " + destinatario + "!",
                    null,
                    true
            );

        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar e-mail via Gmail SMTP: " + e.getMessage());
            return new ResultadoEnvio(
                    false,
                    "Não foi possível entregar o e-mail via Gmail SMTP: " + e.getMessage(),
                    e.getMessage(),
                    true
            );
        } finally {
            try {
                if (sslSocket != null) sslSocket.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (Exception ignored) {
            }
        }
    }

    private static void lerRespostaSmtp(BufferedReader reader, int codigoEsperado) throws Exception {
        String linha;
        while ((linha = reader.readLine()) != null) {
            System.out.println("S: " + linha);
            if (linha.length() >= 3) {
                int codigo = Integer.parseInt(linha.substring(0, 3));
                if (linha.charAt(3) == ' ') {
                    if (codigo != codigoEsperado) {
                        throw new Exception("Resposta inesperada do SMTP: " + linha);
                    }
                    return;
                }
            }
        }
        throw new Exception("Conexão fechada prematuramente pelo servidor SMTP.");
    }

    private static String gerarCorpoHtml(String nome, String token) {
        String nomeExibicao = (nome != null && !nome.trim().isEmpty()) ? nome.trim() : "Usuário";
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head><meta charset=\"UTF-8\"></head>\n" +
                "<body style=\"margin:0;padding:24px;background-color:#f1f5f9;font-family:'Segoe UI',Helvetica,Arial,sans-serif;color:#1e293b;\">\n" +
                "  <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:520px;background-color:#ffffff;border-radius:14px;overflow:hidden;box-shadow:0 8px 24px rgba(0,0,0,0.06);margin:20px auto;border:1px solid #e2e8f0;\">\n" +
                "    <tr>\n" +
                "      <td style=\"background: linear-gradient(135deg, #2563eb, #1d4ed8);padding:30px 24px;text-align:center;color:#ffffff;\">\n" +
                "        <h1 style=\"margin:0;font-size:22px;font-weight:700;letter-spacing:0.5px;\">🎓 Sistema de Gestão Escolar</h1>\n" +
                "        <p style=\"margin:8px 0 0 0;font-size:13px;opacity:0.92;\">Confirmação de Cadastro e Segurança</p>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td style=\"padding:32px 30px;\">\n" +
                "        <h2 style=\"margin:0 0 14px 0;font-size:18px;color:#0f172a;\">Olá, " + nomeExibicao + "!</h2>\n" +
                "        <p style=\"margin:0 0 20px 0;font-size:14px;line-height:1.6;color:#475569;\">\n" +
                "          Recebemos sua solicitação de criação de conta no <strong>Sistema Escolar</strong>.<br>\n" +
                "          Para validar seu e-mail e ativar seu acesso com segurança, utilize o código de verificação abaixo:\n" +
                "        </p>\n" +
                "        <div style=\"text-align:center;margin:28px 0;padding:20px;background-color:#eff6ff;border:2px dashed #3b82f6;border-radius:12px;\">\n" +
                "          <div style=\"font-size:11px;font-weight:700;color:#2563eb;letter-spacing:1.5px;text-transform:uppercase;margin-bottom:8px;\">Seu Código de Acesso</div>\n" +
                "          <span style=\"font-size:36px;font-weight:800;letter-spacing:10px;color:#1e40af;font-family:'Courier New',Consolas,monospace;\">" + token + "</span>\n" +
                "        </div>\n" +
                "        <p style=\"margin:0 0 8px 0;font-size:12px;color:#64748b;line-height:1.5;\">\n" +
                "          ⏳ <strong>Atenção:</strong> Este código expira em instantes e deve ser inserido na tela de cadastro do aplicativo.\n" +
                "        </p>\n" +
                "        <p style=\"margin:0;font-size:12px;color:#94a3b8;line-height:1.5;\">\n" +
                "          Se você não solicitou a criação desta conta, por favor ignore esta mensagem com segurança.\n" +
                "        </p>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td style=\"background-color:#f8fafc;padding:18px;text-align:center;font-size:11px;color:#94a3b8;border-top:1px solid #f1f5f9;\">\n" +
                "        Sistema de Controle de Faltas e Organização Escolar • Gestão Segura\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </table>\n" +
                "</body>\n" +
                "</html>";
    }
}
