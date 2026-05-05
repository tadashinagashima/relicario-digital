/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.relicariodigital.DAO;

import com.mycompany.relicariodigital.Model.Idoso;
import com.mycompany.relicariodigital.Model.Relato;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gyudi
 */

// OBS: Não necessariamente precisamos já implementar o update e o delete de relato.
// É recomendável, mas em relato, o mais importante no momento é focar no create e no read.
public class RelatoDAO {
    
    // Create
    public void salvarRelato(Relato relato) {
        //obs: os nomes das colunas devem ser confirmados com o Daniel (DB)
        String sql = "INSERT INTO relatos (idoso_id, texto_bruto, cronica_gerada, data_registro) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            //preenchendo os parâmetros do SQL
            stmt.setInt(1, relato.getIdosoId());
            stmt.setString(2, relato.getTextoBruto());
            stmt.setString(3, relato.getCronicaGerada());
            //converte a data do java para o formato do banco 
            stmt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));

            stmt.executeUpdate();
            System.out.println("[RF04] Sucesso: Relato salvo no banco de dados.");

        } catch (SQLException e) {
            System.err.println("[RF04] Erro ao salvar relato: " + e.getMessage());
        }
    }
    
    // Read
    public List<Relato> buscarPorIdoso(int idosoId) {
        String sql = "SELECT * FROM relatos WHERE idoso_id = ?";
        List<Relato> listaRelatos = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idosoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Relato r = new Relato();
                r.setId(rs.getInt("id"));
                r.setIdosoId(rs.getInt("idoso_id"));
                r.setTextoBruto(rs.getString("texto_bruto"));
                r.setCronicaGerada(rs.getString("cronica_gerada"));
                r.setDataRegistro(rs.getTimestamp("data_registro"));
                listaRelatos.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar relatos: " + e.getMessage());
        }
        return listaRelatos;
    }
    
    // Update
    public void atualizarCronica(Idoso idoso) {
        String sql = "";
        
        try() {
            
        } catch() {
            
        }
    }
    
    // Delete
    public void deletarRelato(int id) {
        String sql = "";
        
        try() {
            
        } catch() {
            
        }
    }
}
