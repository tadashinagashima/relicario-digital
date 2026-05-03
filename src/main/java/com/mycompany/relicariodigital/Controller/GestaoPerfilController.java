/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.relicariodigital.Controller;

import com.mycompany.relicariodigital.DAO.IdosoDAO;
import com.mycompany.relicariodigital.Model.Idoso;
import java.util.Date;
import java.util.List;

/**
 *
 * @author gyudi
 */
public class GestaoPerfilController {
    private IdosoDAO idosoDAO;
    
    public GestaoPerfilController() {
        this.idosoDAO = new IdosoDAO(); 
    }
    
    public void salvarNovoPerfil(String nome, Date dataNasc, String bio) {
        
    }
    
    public List<Idoso> carregarListaPerfil() {
        return idosoDAO.listarTodos();
    }
    
    public void excluirPerfil(int id) {
        
    }
}
