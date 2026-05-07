package com.mycompany.relicariodigital.DAO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConexaoBD {

    private static final Path PASTA_DADOS = Paths.get("data");

    public static Path getPastaDados() {
        try {
            Files.createDirectories(PASTA_DADOS);
        } catch (IOException e) {
            System.err.println("Erro ao criar pasta de dados: " + e.getMessage());
        }

        return PASTA_DADOS;
    }

    public static Path getArquivo(String nomeArquivo) {
        return getPastaDados().resolve(nomeArquivo);
    }
}
