package com.mycompany.relicariodigital.controller;

import com.mycompany.relicariodigital.dao.IdosoDAO;
import com.mycompany.relicariodigital.model.Idoso;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class GestaoPerfilController {

    private final IdosoDAO idosoDAO;

    public GestaoPerfilController() {
        this.idosoDAO = new IdosoDAO();
    }

    public Idoso salvarNovoPerfil(String nome, LocalDate dataNascimento, String biografia) throws SQLException {
        validarPerfil(nome, dataNascimento);
        Idoso novoIdoso = new Idoso(0, nome.trim(), dataNascimento, limparTexto(biografia));
        return idosoDAO.cadastrar(novoIdoso);
    }

    public void atualizarPerfil(int id, String nome, LocalDate dataNascimento, String biografia) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Selecione um perfil para editar.");
        }
        validarPerfil(nome, dataNascimento);
        idosoDAO.atualizar(new Idoso(id, nome.trim(), dataNascimento, limparTexto(biografia)));
    }

    public List<Idoso> carregarListaPerfil() throws SQLException {
        return idosoDAO.listarTodos();
    }

    public List<Idoso> buscarPerfis(String termo) throws SQLException {
        if (termo == null || termo.trim().isEmpty()) {
            return carregarListaPerfil();
        }
        return idosoDAO.buscarPorNome(termo.trim());
    }

    public void excluirPerfil(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Selecione um perfil para excluir.");
        }
        idosoDAO.deletar(id);
    }

    private void validarPerfil(String nome, LocalDate dataNascimento) {
        if (nome == null || nome.trim().length() < 3) {
            throw new IllegalArgumentException("O nome deve ter pelo menos 3 letras.");
        }
        if (dataNascimento == null) {
            throw new IllegalArgumentException("Informe a data de nascimento.");
        }
        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de nascimento nao pode ser futura.");
        }
    }

    private String limparTexto(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.trim();
    }
}
