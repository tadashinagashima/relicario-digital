/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.relicariodigital.Controller;

import com.mycompany.relicariodigital.DAO.IdosoDAO;
import com.mycompany.relicariodigital.DAO.RelatoDAO;
import com.mycompany.relicariodigital.Model.Idoso;
import java.util.Date;
import java.util.List;

/**
 *
 * @author gyudi
 */
public class GestaoPerfilController {
    private IdosoDAO idosoDAO;
    private RelatoDAO relatoDAO;
    
    public GestaoPerfilController() {
        this.idosoDAO = new IdosoDAO();
        this.relatoDAO = new RelatoDAO();
    }
    
    public boolean salvarNovoPerfil(String nome, Date dataNasc, String bio) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }

        Idoso idoso = new Idoso();
        idoso.setNome(nome.trim());
        idoso.setDataNascimento(dataNasc);
        idoso.setBiografiaBreve(bio == null ? "" : bio.trim());
        
        idosoDAO.cadastrar(idoso);
        return true;
    }

    public boolean atualizarPerfil(int id, String nome, Date dataNasc, String bio) {
        if (id <= 0 || nome == null || nome.trim().isEmpty()) {
            return false;
        }

        Idoso idoso = new Idoso();
        idoso.setId(id);
        idoso.setNome(nome.trim());
        idoso.setDataNascimento(dataNasc);
        idoso.setBiografiaBreve(bio == null ? "" : bio.trim());

        idosoDAO.atualizar(idoso);
        return true;
    }
    
    public List<Idoso> carregarListaPerfil() {
        return idosoDAO.listarTodos();
    }

    public List<Idoso> buscarPerfilPorNome(String termo) {
        return idosoDAO.buscarPorNome(termo);
    }
    
    public void excluirPerfil(int id) {
        relatoDAO.deletarPorIdoso(id);
        idosoDAO.deletar(id);
    }
}
