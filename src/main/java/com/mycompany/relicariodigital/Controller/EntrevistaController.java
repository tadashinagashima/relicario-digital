/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.relicariodigital.Controller;

import com.mycompany.relicariodigital.DAO.RelatoDAO;
import com.mycompany.relicariodigital.Model.Relato;
import com.mycompany.relicariodigital.Service.GeminiService;

/**
 *
 * @author gyudi
 */
public class EntrevistaController {
    
    private GeminiService geminiService;
    private RelatoDAO relatoDAO;
    
    public EntrevistaController() {
        this.geminiService = new GeminiService();
        this.relatoDAO = new RelatoDAO();
    }
    
    public Relato processarESalvarRelato(int idosoId, String textoBrutoDigitado) {
        if (textoBrutoDigitado == null || textoBrutoDigitado.trim().isEmpty()) {
            System.out.println("Aviso para a tela: O relato não pode estar vazio!");
            return null;
        }

        System.out.println("Aviso para a tela: Processando com a Inteligência Artificial... Aguarde.");

        String textoFormatadoPelaIA = geminiService.processarHistoria(textoBrutoDigitado);

        if (textoFormatadoPelaIA.contains("erro")) {
            System.out.println("Aviso para a tela: Falha na IA. Tente novamente.");
            return null;
        }

        Relato novoRelato = new Relato();
        novoRelato.setIdosoId(idosoId);
        novoRelato.setTextoBruto(textoBrutoDigitado);
        novoRelato.setCronicaGerada(textoFormatadoPelaIA);
        
        relatoDAO.salvarRelato(novoRelato);

        System.out.println("Aviso para a tela: Sucesso! Crônica salva no acervo biográfico.");

        return novoRelato; 
    }
}
