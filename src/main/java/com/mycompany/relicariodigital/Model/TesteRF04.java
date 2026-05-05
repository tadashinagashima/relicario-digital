package com.mycompany.relicariodigital;

import com.mycompany.relicariodigital.DAO.RelatoDAO;
import com.mycompany.relicariodigital.Model.Relato;

public class TesteRF04 {
    public static void main(String[] args) {
        System.out.println("Iniciando teste de persistência...");

        //Finge que é um relato real
        //e supondo que já existe um idoso com ID 1 cadastrado
        Relato relatoFalso = new Relato(
            1, 
            "História Exemplo...", 
            "História Exemplo..."
        );

        //chamamos o DAO para salvar
        RelatoDAO dao = new RelatoDAO();
        dao.salvarRelato(relatoFalso);
        
        System.out.println("Teste finalizado.");
    }
}
