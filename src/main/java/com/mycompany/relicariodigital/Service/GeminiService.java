package com.mycompany.relicariodigital.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class GeminiService {

    private static final String MODELO = "gemini-2.5-flash";
    private static final String API_KEY = System.getenv("GEMINI_API_KEY");

    public String processarHistoria(String textoBrutoDigitado) {
        String prompt = "Transforme o relato abaixo em uma cronica curta, humana, respeitosa "
                + "e em portugues do Brasil. Preserve nomes, lugares e sentimentos importantes.\n\n"
                + textoBrutoDigitado;

        String requisicaoJson = montarJson(prompt);

        if (API_KEY == null || API_KEY.trim().isEmpty()) {
            return gerarCronicaLocal(textoBrutoDigitado);
        }

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                    + MODELO + ":generateContent";

            HttpURLConnection conexao = (HttpURLConnection) new URL(url).openConnection();
            conexao.setRequestMethod("POST");
            conexao.setRequestProperty("Content-Type", "application/json");
            conexao.setRequestProperty("x-goog-api-key", API_KEY);
            conexao.setDoOutput(true);

            try (OutputStream saida = conexao.getOutputStream()) {
                saida.write(requisicaoJson.getBytes(StandardCharsets.UTF_8));
            }

            if (conexao.getResponseCode() < 200 || conexao.getResponseCode() >= 300) {
                return gerarCronicaLocal(textoBrutoDigitado);
            }

            try (Scanner scanner = new Scanner(conexao.getInputStream(), "UTF-8")) {
                scanner.useDelimiter("\\A");
                String resposta = scanner.hasNext() ? scanner.next() : "";
                return extrairTextoDoJson(resposta);
            }
        } catch (IOException e) {
            return gerarCronicaLocal(textoBrutoDigitado);
        }
    }

    private String montarJson(String prompt) {
        return "{"
                + "\"contents\":["
                + "{"
                + "\"parts\":["
                + "{"
                + "\"text\":\"" + escaparJson(prompt) + "\""
                + "}"
                + "]"
                + "}"
                + "]"
                + "}";
    }

    private String extrairTextoDoJson(String jsonBruto) {
        String marcador = "\"text\":";
        int inicio = jsonBruto.indexOf(marcador);

        if (inicio == -1) {
            return "Nao foi possivel ler a resposta da IA.";
        }

        inicio = jsonBruto.indexOf("\"", inicio + marcador.length());
        int fim = inicio + 1;
        boolean escapado = false;

        while (fim < jsonBruto.length()) {
            char atual = jsonBruto.charAt(fim);

            if (atual == '"' && !escapado) {
                break;
            }

            escapado = atual == '\\' && !escapado;

            if (atual != '\\') {
                escapado = false;
            }

            fim++;
        }

        return desfazerEscapesJson(jsonBruto.substring(inicio + 1, fim));
    }

    private String gerarCronicaLocal(String textoBruto) {
        return "Uma lembranca para guardar\n\n"
                + "Entre tantas passagens da vida, esta memoria merece ser contada com calma. "
                + "O relato fala de momentos vividos, pessoas importantes e sentimentos que continuam presentes.\n\n"
                + textoBruto.trim()
                + "\n\n"
                + "Assim, a historia fica registrada como um pequeno relicario: simples, afetivo "
                + "e pronto para ser compartilhado com quem tambem faz parte dessa caminhada.";
    }

    private String escaparJson(String texto) {
        return texto.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    private String desfazerEscapesJson(String texto) {
        return texto.replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}
