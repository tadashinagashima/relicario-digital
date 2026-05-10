package com.mycompany.relicariodigital.controller;

import com.mycompany.relicariodigital.dao.RelatoDAO;
import com.mycompany.relicariodigital.model.Relato;
import com.mycompany.relicariodigital.service.GeminiService;
import java.sql.SQLException;

public class EntrevistaController {

    private final GeminiService geminiService;
    private final RelatoDAO relatoDAO;

    public EntrevistaController() {
        this.geminiService = new GeminiService();
        this.relatoDAO = new RelatoDAO();
    }

    public String gerarCronica(int idosoId, String textoBrutoDigitado) throws Exception {
        validarIdoso(idosoId);
        validarTexto(textoBrutoDigitado, "Digite o texto bruto da entrevista.");
        return geminiService.processarHistoria(textoBrutoDigitado.trim());
    }

    public Relato salvarRelato(int idosoId, String textoBrutoDigitado, String cronicaRevisada) throws SQLException {
        validarIdoso(idosoId);
        validarTexto(textoBrutoDigitado, "Digite o texto bruto da entrevista.");
        validarTexto(cronicaRevisada, "Revise ou informe a cronica antes de salvar.");

        Relato novoRelato = new Relato();
        novoRelato.setIdosoId(idosoId);
        novoRelato.setTextoBruto(textoBrutoDigitado.trim());
        novoRelato.setCronicaGerada(cronicaRevisada.trim());

        return relatoDAO.salvar(novoRelato);
    }

    private void validarIdoso(int idosoId) {
        if (idosoId <= 0) {
            throw new IllegalArgumentException("Selecione um idoso.");
        }
    }

    private void validarTexto(String texto, String mensagem) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(mensagem);
        }
    }
}
