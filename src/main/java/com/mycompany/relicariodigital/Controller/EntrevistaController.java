package com.mycompany.relicariodigital.Controller;

import com.mycompany.relicariodigital.DAO.RelatoDAO;
import com.mycompany.relicariodigital.Model.Relato;
import com.mycompany.relicariodigital.Service.GeminiService;

public class EntrevistaController {

    private GeminiService geminiService;
    private RelatoDAO relatoDAO;

    public EntrevistaController() {
        this.geminiService = new GeminiService();
        this.relatoDAO = new RelatoDAO();
    }

    public Relato processarRelato(int idosoId, String textoBrutoDigitado) {
        if (textoBrutoDigitado == null || textoBrutoDigitado.trim().isEmpty()) {
            System.out.println("Aviso para a tela: O relato nao pode estar vazio!");
            return null;
        }

        System.out.println("Aviso para a tela: Processando com a Inteligencia Artificial... Aguarde.");

        String textoFormatadoPelaIA = geminiService.processarHistoria(textoBrutoDigitado);

        if (textoFormatadoPelaIA == null || textoFormatadoPelaIA.toLowerCase().contains("erro")) {
            System.out.println("Aviso para a tela: Falha na IA. Tente novamente.");
            return null;
        }

        Relato novoRelato = new Relato();
        novoRelato.setIdosoId(idosoId);
        novoRelato.setTextoBruto(textoBrutoDigitado);
        novoRelato.setCronicaGerada(textoFormatadoPelaIA);

        return novoRelato;
    }

    public void salvarRelato(Relato relato) {
        relatoDAO.salvarRelato(relato);
        System.out.println("Aviso para a tela: Sucesso! Cronica salva no acervo biografico.");
    }

    public Relato processarESalvarRelato(int idosoId, String textoBrutoDigitado) {
        Relato novoRelato = processarRelato(idosoId, textoBrutoDigitado);

        if (novoRelato != null) {
            salvarRelato(novoRelato);
        }

        return novoRelato;
    }
}
