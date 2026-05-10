package com.mycompany.relicariodigital.dao;

import com.mycompany.relicariodigital.model.Relato;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RelatoDAO {

    public Relato salvar(Relato relato) throws SQLException {
        String sqlNumero = "SELECT COALESCE(MAX(numero), 0) + 1 AS proximo FROM relatos WHERE idoso_id = ?";
        String sqlInsert = "INSERT INTO relatos (idoso_id, numero, texto_bruto, cronica_gerada) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtNumero = conn.prepareStatement(sqlNumero);
                 PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                stmtNumero.setInt(1, relato.getIdosoId());

                try (ResultSet rs = stmtNumero.executeQuery()) {
                    if (rs.next()) {
                        relato.setNumero(rs.getInt("proximo"));
                    }
                }

                stmtInsert.setInt(1, relato.getIdosoId());
                stmtInsert.setInt(2, relato.getNumero());
                stmtInsert.setString(3, relato.getTextoBruto());
                stmtInsert.setString(4, relato.getCronicaGerada());
                stmtInsert.executeUpdate();

                try (ResultSet rs = stmtInsert.getGeneratedKeys()) {
                    if (rs.next()) {
                        relato.setId(rs.getInt(1));
                    }
                }

                conn.commit();
                return relato;
            } catch (SQLException erro) {
                conn.rollback();
                throw erro;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Relato> buscarPorIdoso(int idosoId) throws SQLException {
        String sql = "SELECT id, idoso_id, numero, texto_bruto, cronica_gerada, data_registro "
                + "FROM relatos WHERE idoso_id = ? ORDER BY numero";
        List<Relato> listaRelatos = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idosoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listaRelatos.add(mapearRelato(rs));
                }
            }
        }

        return listaRelatos;
    }

    public int contarPorIdoso(int idosoId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM relatos WHERE idoso_id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idosoId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    private Relato mapearRelato(ResultSet rs) throws SQLException {
        Relato relato = new Relato();
        relato.setId(rs.getInt("id"));
        relato.setIdosoId(rs.getInt("idoso_id"));
        relato.setNumero(rs.getInt("numero"));
        relato.setTextoBruto(rs.getString("texto_bruto"));
        relato.setCronicaGerada(rs.getString("cronica_gerada"));

        Timestamp dataRegistro = rs.getTimestamp("data_registro");
        if (dataRegistro != null) {
            relato.setDataRegistro(dataRegistro.toLocalDateTime());
        }

        return relato;
    }
}
