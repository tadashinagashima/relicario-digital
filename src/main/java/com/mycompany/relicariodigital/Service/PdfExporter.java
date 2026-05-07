package com.mycompany.relicariodigital.Service;

import com.mycompany.relicariodigital.Model.Idoso;
import com.mycompany.relicariodigital.Model.Relato;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;

public class PdfExporter {

    public Path exportar(Relato relato, Idoso idoso) throws IOException {
        Files.createDirectories(Paths.get("exports"));
        int numeroVisivel = relato.getNumeroNoPerfil() > 0 ? relato.getNumeroNoPerfil() : relato.getId();
        String nomeArquivo = "cronica-perfil-" + relato.getIdosoId() + "-relato-" + numeroVisivel + ".pdf";
        Path destino = Paths.get("exports", nomeArquivo);
        String nomeIdoso = idoso == null ? "Perfil nao encontrado" : idoso.getNome();
        String titulo = "Relicario Digital - " + nomeIdoso + " - Relato " + numeroVisivel;
        String texto = titulo + "\n\n" + relato.getCronicaGerada();

        Files.write(destino, montarPdfSimples(texto).getBytes(StandardCharsets.ISO_8859_1));
        return destino;
    }

    private String montarPdfSimples(String texto) {
        String conteudo = quebrarLinhas(texto)
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("\n", ") Tj T* (");

        String stream = "BT /F1 12 Tf 50 780 Td 14 TL (" + conteudo + ") Tj ET";
        int tamanhoStream = stream.getBytes(StandardCharsets.ISO_8859_1).length;

        String obj1 = "1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj\n";
        String obj2 = "2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj\n";
        String obj3 = "3 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] "
                + "/Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >> endobj\n";
        String obj4 = "4 0 obj << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> endobj\n";
        String obj5 = "5 0 obj << /Length " + tamanhoStream + " >> stream\n" + stream + "\nendstream endobj\n";

        String cabecalho = "%PDF-1.4\n";
        int xref1 = cabecalho.length();
        int xref2 = xref1 + obj1.length();
        int xref3 = xref2 + obj2.length();
        int xref4 = xref3 + obj3.length();
        int xref5 = xref4 + obj4.length();
        int xref = xref5 + obj5.length();

        return cabecalho + obj1 + obj2 + obj3 + obj4 + obj5
                + "xref\n0 6\n"
                + "0000000000 65535 f \n"
                + formatarXref(xref1) + " 00000 n \n"
                + formatarXref(xref2) + " 00000 n \n"
                + formatarXref(xref3) + " 00000 n \n"
                + formatarXref(xref4) + " 00000 n \n"
                + formatarXref(xref5) + " 00000 n \n"
                + "trailer << /Size 6 /Root 1 0 R >>\n"
                + "startxref\n" + xref + "\n%%EOF";
    }

    private String quebrarLinhas(String texto) {
        String semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        StringBuilder resultado = new StringBuilder();

        for (String paragrafo : semAcentos.split("\n")) {
            while (paragrafo.length() > 80) {
                resultado.append(paragrafo, 0, 80).append("\n");
                paragrafo = paragrafo.substring(80);
            }

            resultado.append(paragrafo).append("\n");
        }

        return resultado.toString();
    }

    private String formatarXref(int numero) {
        return String.format("%010d", numero);
    }
}
