package com.mycompany.relicariodigital.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GeminiService {

    private static final String CHAVE_TEXTO = "\"text\"";
    private static final String CHAVE_FINISH_REASON = "\"finishReason\"";

    public String processarHistoria(String textoBrutoDigitado) throws IOException {
        String apiKey = Configuracao.getObrigatoria("gemini.api.key");
        String modelo = Configuracao.get("gemini.model", "gemini-2.5-flash");
        String url = montarUrl(modelo, apiKey);
        String requisicaoJson = montarRequisicaoJson(textoBrutoDigitado);

        HttpURLConnection conexao = (HttpURLConnection) URI.create(url).toURL().openConnection();
        conexao.setRequestMethod("POST");
        conexao.setConnectTimeout(20000);
        conexao.setReadTimeout(60000);
        conexao.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conexao.setDoOutput(true);

        byte[] corpo = requisicaoJson.getBytes(StandardCharsets.UTF_8);
        try (OutputStream output = conexao.getOutputStream()) {
            output.write(corpo);
        }

        int status = conexao.getResponseCode();
        String resposta = lerResposta(status >= 200 && status < 300
                ? conexao.getInputStream()
                : conexao.getErrorStream());

        if (status < 200 || status >= 300) {
            throw new IOException("Erro na API Gemini (HTTP " + status + "): " + resposta);
        }

        return limparTextoGerado(extrairTextoDoJson(resposta));
    }

    public String montarRequisicaoJson(String textoBrutoDigitado) {
        int maxOutputTokens = lerMaxOutputTokens();
        String prompt = "Reescreva a entrevista abaixo como uma cronica completa, afetiva e respeitosa.\n"
                + "Preserve os fatos narrados, nao invente acontecimentos e mantenha linguagem simples.\n"
                + "Se a entrevista for longa, mantenha comeco, meio e fim em vez de cortar o texto.\n"
                + "Responda somente com a historia final, sem titulo, sem subtitulo, sem Markdown, sem listas,\n"
                + "sem aspas e sem frases introdutorias como 'Aqui esta' ou 'Claro'.\n"
                + "Entrevista:\n";

        String textoFinal = prompt + "\n" + textoBrutoDigitado;

        return "{\n"
                + "  \"contents\": [\n"
                + "    {\n"
                + "      \"parts\": [\n"
                + "        {\n"
                + "          \"text\": \"" + escaparJson(textoFinal) + "\"\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  ],\n"
                + "  \"generationConfig\": {\n"
                + "    \"temperature\": 0.7,\n"
                + "    \"maxOutputTokens\": " + maxOutputTokens + "\n"
                + "  }\n"
                + "}";
    }

    private String montarUrl(String modelo, String apiKey) throws IOException {
        String modeloCodificado = URLEncoder.encode(modelo, "UTF-8");
        String chaveCodificada = URLEncoder.encode(apiKey, "UTF-8");
        return "https://generativelanguage.googleapis.com/v1beta/models/"
                + modeloCodificado
                + ":generateContent?key="
                + chaveCodificada;
    }

    private String lerResposta(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }

        StringBuilder resposta = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                resposta.append(linha).append('\n');
            }
        }
        return resposta.toString();
    }

    private String extrairTextoDoJson(String jsonBruto) throws IOException {
        String finishReason = extrairFinishReason(jsonBruto);
        if ("MAX_TOKENS".equals(finishReason)) {
            throw new IOException("A cronica foi cortada porque atingiu o limite de tokens. "
                    + "Aumente 'gemini.max.output.tokens' no config.properties ou reduza o texto da entrevista.");
        }

        StringBuilder textoCompleto = new StringBuilder();
        int indice = 0;

        while ((indice = jsonBruto.indexOf(CHAVE_TEXTO, indice)) >= 0) {
            int doisPontos = jsonBruto.indexOf(':', indice + CHAVE_TEXTO.length());
            if (doisPontos < 0) {
                break;
            }

            int aspasIniciais = encontrarProximaAspas(jsonBruto, doisPontos + 1);
            if (aspasIniciais < 0) {
                break;
            }

            TextoJson textoJson = lerStringJson(jsonBruto, aspasIniciais);
            if (textoCompleto.length() > 0) {
                textoCompleto.append("\n");
            }
            textoCompleto.append(textoJson.getTexto().trim());
            indice = textoJson.getProximoIndice();
        }

        if (textoCompleto.length() > 0) {
            return textoCompleto.toString().trim();
        }

        throw new IOException("A resposta da API Gemini nao trouxe um texto de cronica.");
    }

    private String extrairFinishReason(String jsonBruto) {
        int indice = jsonBruto.indexOf(CHAVE_FINISH_REASON);
        if (indice < 0) {
            return "";
        }

        int doisPontos = jsonBruto.indexOf(':', indice + CHAVE_FINISH_REASON.length());
        if (doisPontos < 0) {
            return "";
        }

        int aspasIniciais = encontrarProximaAspas(jsonBruto, doisPontos + 1);
        if (aspasIniciais < 0) {
            return "";
        }

        try {
            return lerStringJson(jsonBruto, aspasIniciais).getTexto();
        } catch (IOException erro) {
            return "";
        }
    }

    private int lerMaxOutputTokens() {
        String valor = Configuracao.get("gemini.max.output.tokens", "4096");
        try {
            int tokens = Integer.parseInt(valor);
            if (tokens < 512) {
                return 512;
            }
            return tokens;
        } catch (NumberFormatException erro) {
            return 4096;
        }
    }

    private String limparTextoGerado(String texto) {
        String[] linhas = texto.split("\\r?\\n");
        StringBuilder resultado = new StringBuilder();
        boolean encontrouConteudo = false;

        for (String linha : linhas) {
            String linhaLimpa = linha.trim();
            if (!encontrouConteudo && deveIgnorarLinhaInicial(linhaLimpa)) {
                continue;
            }

            encontrouConteudo = true;
            if (resultado.length() > 0) {
                resultado.append('\n');
            }
            resultado.append(linha);
        }

        return resultado.toString().trim();
    }

    private boolean deveIgnorarLinhaInicial(String linha) {
        if (linha.isEmpty()) {
            return true;
        }

        String minuscula = linha.toLowerCase();
        return linha.startsWith("#")
                || minuscula.startsWith("claro")
                || minuscula.startsWith("aqui esta")
                || minuscula.startsWith("aqui está")
                || minuscula.startsWith("segue")
                || minuscula.startsWith("titulo:")
                || minuscula.startsWith("título:")
                || minuscula.startsWith("cronica:")
                || minuscula.startsWith("crônica:");
    }

    private String escaparJson(String texto) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 32) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }

    private int encontrarProximaAspas(String json, int inicio) {
        if (inicio < 0) {
            return -1;
        }

        for (int i = inicio; i < json.length(); i++) {
            if (json.charAt(i) == '"') {
                return i;
            }
        }
        return -1;
    }

    private TextoJson lerStringJson(String json, int indiceAspasIniciais) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = indiceAspasIniciais + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"') {
                return new TextoJson(sb.toString(), i + 1);
            }

            if (c != '\\') {
                sb.append(c);
                continue;
            }

            if (i + 1 >= json.length()) {
                throw new IOException("String JSON incompleta na resposta da API Gemini.");
            }

            char proximo = json.charAt(++i);
            switch (proximo) {
                case '"':
                    sb.append('"');
                    break;
                case '\\':
                    sb.append('\\');
                    break;
                case '/':
                    sb.append('/');
                    break;
                case 'b':
                    sb.append('\b');
                    break;
                case 'f':
                    sb.append('\f');
                    break;
                case 'n':
                    sb.append('\n');
                    break;
                case 'r':
                    sb.append('\r');
                    break;
                case 't':
                    sb.append('\t');
                    break;
                case 'u':
                    if (i + 4 < json.length()) {
                        String hex = json.substring(i + 1, i + 5);
                        try {
                            sb.append((char) Integer.parseInt(hex, 16));
                        } catch (NumberFormatException erro) {
                            throw new IOException("Sequencia Unicode invalida na resposta da API Gemini.", erro);
                        }
                        i += 4;
                    }
                    break;
                default:
                    sb.append(proximo);
                    break;
            }
        }

        throw new IOException("String JSON incompleta na resposta da API Gemini.");
    }

    private static class TextoJson {

        private final String texto;
        private final int proximoIndice;

        TextoJson(String texto, int proximoIndice) {
            this.texto = texto;
            this.proximoIndice = proximoIndice;
        }

        String getTexto() {
            return texto;
        }

        int getProximoIndice() {
            return proximoIndice;
        }
    }
}
