/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.relicariodigital.Controller;

import com.mycompany.relicariodigital.DAO.IdosoDAO;
import com.mycompany.relicariodigital.DAO.RelatoDAO;
import com.mycompany.relicariodigital.Model.Idoso;
import com.mycompany.relicariodigital.Model.Relato;
import java.util.List;

/**
 *
 * @author gyudi
 */
public class AcervoController {
    
    private IdosoDAO idosoDAO;
    private RelatoDAO relatoDAO;
    
    public AcervoController() {
        this.idosoDAO = new IdosoDAO();
        this.relatoDAO = new RelatoDAO();
    }
    
    public List<Idoso> carregarListaDeParticipantes() {
        return idosoDAO.listarTodos();
    }
    
    public List<Relato> buscarHistoriasDoIdoso(int idosoEscolhido) {
        
    }
    
    // Extra - public void exportarCronica(Relato relatoEscolhido);
}
