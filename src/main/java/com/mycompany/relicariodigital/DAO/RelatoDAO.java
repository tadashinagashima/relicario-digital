package com.mycompany.relicariodigital.DAO;

import com.mycompany.relicariodigital.Model.Relato;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatoDAO {

    private static final String ARQUIVO = "relatos.csv";

    public void salvarRelato(Relato relato) {
        List<Relato> relatos = listarTodos();
        relato.setId(proximoId(relatos));

        if (relato.getNumeroNoPerfil() <= 0) {
            relato.setNumeroNoPerfil(proximoNumeroDoPerfil(relatos, relato.getIdosoId()));
        }

        if (relato.getDataRegistro() == null) {
            relato.setDataRegistro(new Date());
        }

        relatos.add(relato);
        salvarTodos(relatos);
    }

    public List<Relato> listarTodos() {
        List<Relato> relatos = new ArrayList<>();
        Path arquivo = ConexaoBD.getArquivo(ARQUIVO);

        if (!Files.exists(arquivo)) {
            return relatos;
        }

        try {
            List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);

            for (String linha : linhas) {
                if (!linha.trim().isEmpty()) {
                    relatos.add(converterLinhaParaRelato(linha));
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao listar relatos: " + e.getMessage());
        }

        preencherNumerosAntigos(relatos);
        return relatos;
    }

    public List<Relato> buscarPorIdoso(int idosoId) {
        List<Relato> listaRelatos = new ArrayList<>();

        for (Relato relato : listarTodos()) {
            if (relato.getIdosoId() == idosoId) {
                listaRelatos.add(relato);
            }
        }

        return listaRelatos;
    }

    public List<Relato> buscarPorTexto(String termo) {
        List<Relato> resultado = new ArrayList<>();
        String filtro = termo == null ? "" : termo.toLowerCase();

        for (Relato relato : listarTodos()) {
            String cronica = relato.getCronicaGerada() == null ? "" : relato.getCronicaGerada().toLowerCase();
            String bruto = relato.getTextoBruto() == null ? "" : relato.getTextoBruto().toLowerCase();

            if (cronica.contains(filtro) || bruto.contains(filtro)) {
                resultado.add(relato);
            }
        }

        return resultado;
    }

    public void atualizarCronica(Relato relatoAtualizado) {
        List<Relato> relatos = listarTodos();

        for (int i = 0; i < relatos.size(); i++) {
            if (relatos.get(i).getId() == relatoAtualizado.getId()) {
                relatos.set(i, relatoAtualizado);
                break;
            }
        }

        salvarTodos(relatos);
    }

    public void deletarRelato(int id) {
        List<Relato> relatos = listarTodos();
        relatos.removeIf(relato -> relato.getId() == id);
        salvarTodos(relatos);
    }

    public void deletarPorIdoso(int idosoId) {
        List<Relato> relatos = listarTodos();
        relatos.removeIf(relato -> relato.getIdosoId() == idosoId);
        salvarTodos(relatos);
    }

    private void salvarTodos(List<Relato> relatos) {
        List<String> linhas = new ArrayList<>();

        for (Relato relato : relatos) {
            linhas.add(converterRelatoParaLinha(relato));
        }

        try {
            Files.write(ConexaoBD.getArquivo(ARQUIVO), linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Erro ao salvar relatos: " + e.getMessage());
        }
    }

    private int proximoId(List<Relato> relatos) {
        int maiorId = 0;

        for (Relato relato : relatos) {
            if (relato.getId() > maiorId) {
                maiorId = relato.getId();
            }
        }

        return maiorId + 1;
    }

    private int proximoNumeroDoPerfil(List<Relato> relatos, int idosoId) {
        int maiorNumero = 0;

        for (Relato relato : relatos) {
            if (relato.getIdosoId() == idosoId && relato.getNumeroNoPerfil() > maiorNumero) {
                maiorNumero = relato.getNumeroNoPerfil();
            }
        }

        return maiorNumero + 1;
    }

    private void preencherNumerosAntigos(List<Relato> relatos) {
        Map<Integer, Integer> maiorNumeroPorIdoso = new HashMap<>();

        for (Relato relato : relatos) {
            if (relato.getNumeroNoPerfil() > 0) {
                maiorNumeroPorIdoso.put(relato.getIdosoId(), relato.getNumeroNoPerfil());
            }
        }

        for (Relato relato : relatos) {
            if (relato.getNumeroNoPerfil() <= 0) {
                Integer maiorAtual = maiorNumeroPorIdoso.get(relato.getIdosoId());

                if (maiorAtual == null) {
                    maiorAtual = 0;
                }

                relato.setNumeroNoPerfil(maiorAtual + 1);
                maiorNumeroPorIdoso.put(relato.getIdosoId(), relato.getNumeroNoPerfil());
            }
        }
    }

    private String converterRelatoParaLinha(Relato relato) {
        long data = relato.getDataRegistro() == null ? System.currentTimeMillis() : relato.getDataRegistro().getTime();

        return relato.getId()
                + ";" + relato.getIdosoId()
                + ";" + relato.getNumeroNoPerfil()
                + ";" + data
                + ";" + limpar(relato.getTextoBruto())
                + ";" + limpar(relato.getCronicaGerada());
    }

    private Relato converterLinhaParaRelato(String linha) {
        String[] partes = linha.split(";", -1);
        Relato relato = new Relato();

        relato.setId(Integer.parseInt(partes[0]));
        relato.setIdosoId(Integer.parseInt(partes[1]));

        if (partes.length >= 6) {
            relato.setNumeroNoPerfil(Integer.parseInt(partes[2]));
            relato.setDataRegistro(new Date(Long.parseLong(partes[3])));
            relato.setTextoBruto(restaurar(partes[4]));
            relato.setCronicaGerada(restaurar(partes[5]));
        } else {
            relato.setNumeroNoPerfil(0);
            relato.setDataRegistro(new Date(Long.parseLong(partes[2])));
            relato.setTextoBruto(restaurar(partes[3]));
            relato.setCronicaGerada(restaurar(partes[4]));
        }

        return relato;
    }

    private String limpar(String texto) {
        if (texto == null) {
            return "";
        }

        return texto.replace("\\", "\\\\").replace("\n", "\\n").replace(";", "\\p");
    }

    private String restaurar(String texto) {
        return texto.replace("\\p", ";").replace("\\n", "\n").replace("\\\\", "\\");
    }
}
