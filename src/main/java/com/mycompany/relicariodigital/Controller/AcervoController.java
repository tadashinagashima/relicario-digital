package com.mycompany.relicariodigital.controller;

import com.mycompany.relicariodigital.dao.IdosoDAO;
import com.mycompany.relicariodigital.dao.RelatoDAO;
import com.mycompany.relicariodigital.model.Idoso;
import com.mycompany.relicariodigital.model.Relato;
import java.sql.SQLException;
import java.util.List;

public class AcervoController {

    private final IdosoDAO idosoDAO;
    private final RelatoDAO relatoDAO;

    public AcervoController() {
        this.idosoDAO = new IdosoDAO();
        this.relatoDAO = new RelatoDAO();
    }

    public List<Idoso> carregarListaDeParticipantes() throws SQLException {
        return idosoDAO.listarTodos();
    }

    public List<Idoso> buscarParticipantes(String termo) throws SQLException {
        if (termo == null || termo.trim().isEmpty()) {
            return carregarListaDeParticipantes();
        }
        return idosoDAO.buscarPorNome(termo.trim());
    }

    public List<Relato> buscarHistoriasDoIdoso(int idosoEscolhido) throws SQLException {
        if (idosoEscolhido <= 0) {
            throw new IllegalArgumentException("Selecione um idoso.");
        }
        return relatoDAO.buscarPorIdoso(idosoEscolhido);
    }
}
