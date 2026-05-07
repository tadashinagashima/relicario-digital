package com.mycompany.relicariodigital.DAO;

import com.mycompany.relicariodigital.Model.Idoso;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IdosoDAO {

    private static final String ARQUIVO = "idosos.csv";

    public void cadastrar(Idoso idoso) {
        List<Idoso> idosos = listarTodos();
        idoso.setId(proximoId(idosos));
        idosos.add(idoso);
        salvarTodos(idosos);
    }

    public List<Idoso> listarTodos() {
        List<Idoso> listaIdosos = new ArrayList<>();
        Path arquivo = ConexaoBD.getArquivo(ARQUIVO);

        if (!Files.exists(arquivo)) {
            return listaIdosos;
        }

        try {
            List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);

            for (String linha : linhas) {
                if (!linha.trim().isEmpty()) {
                    listaIdosos.add(converterLinhaParaIdoso(linha));
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao listar perfis: " + e.getMessage());
        }

        return listaIdosos;
    }

    public Idoso buscarPorId(int id) {
        for (Idoso idoso : listarTodos()) {
            if (idoso.getId() == id) {
                return idoso;
            }
        }

        return null;
    }

    public List<Idoso> buscarPorNome(String termo) {
        List<Idoso> resultado = new ArrayList<>();
        String filtro = termo == null ? "" : termo.toLowerCase();

        for (Idoso idoso : listarTodos()) {
            if (idoso.getNome().toLowerCase().contains(filtro)) {
                resultado.add(idoso);
            }
        }

        return resultado;
    }

    public void atualizar(Idoso idosoAtualizado) {
        List<Idoso> idosos = listarTodos();

        for (int i = 0; i < idosos.size(); i++) {
            if (idosos.get(i).getId() == idosoAtualizado.getId()) {
                idosos.set(i, idosoAtualizado);
                break;
            }
        }

        salvarTodos(idosos);
    }

    public void deletar(int id) {
        List<Idoso> idosos = listarTodos();
        idosos.removeIf(idoso -> idoso.getId() == id);
        salvarTodos(idosos);
    }

    private void salvarTodos(List<Idoso> idosos) {
        List<String> linhas = new ArrayList<>();

        for (Idoso idoso : idosos) {
            linhas.add(converterIdosoParaLinha(idoso));
        }

        try {
            Files.write(ConexaoBD.getArquivo(ARQUIVO), linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Erro ao salvar perfis: " + e.getMessage());
        }
    }

    private int proximoId(List<Idoso> idosos) {
        int maiorId = 0;

        for (Idoso idoso : idosos) {
            if (idoso.getId() > maiorId) {
                maiorId = idoso.getId();
            }
        }

        return maiorId + 1;
    }

    private String converterIdosoParaLinha(Idoso idoso) {
        long nascimento = idoso.getDataNascimento() == null ? 0 : idoso.getDataNascimento().getTime();

        return idoso.getId()
                + ";" + nascimento
                + ";" + limpar(idoso.getNome())
                + ";" + limpar(idoso.getBiografiaBreve());
    }

    private Idoso converterLinhaParaIdoso(String linha) {
        String[] partes = linha.split(";", -1);
        Idoso idoso = new Idoso();

        idoso.setId(Integer.parseInt(partes[0]));
        idoso.setDataNascimento("0".equals(partes[1]) ? null : new Date(Long.parseLong(partes[1])));
        idoso.setNome(restaurar(partes[2]));
        idoso.setBiografiaBreve(restaurar(partes[3]));

        return idoso;
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
