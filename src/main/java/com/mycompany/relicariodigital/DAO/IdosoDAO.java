package com.mycompany.relicariodigital.dao;

import com.mycompany.relicariodigital.model.Idoso;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IdosoDAO {

    public Idoso cadastrar(Idoso idoso) throws SQLException {
        String sql = "INSERT INTO idosos (nome, data_nascimento, biografia_breve) VALUES (?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherParametros(stmt, idoso);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    idoso.setId(rs.getInt(1));
                }
            }
        }

        return idoso;
    }

    public List<Idoso> listarTodos() throws SQLException {
        String sql = "SELECT id, nome, data_nascimento, biografia_breve FROM idosos ORDER BY nome";
        List<Idoso> listaIdosos = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                listaIdosos.add(mapearIdoso(rs));
            }
        }

        return listaIdosos;
    }

    public List<Idoso> buscarPorNome(String termo) throws SQLException {
        String sql = "SELECT id, nome, data_nascimento, biografia_breve "
                + "FROM idosos WHERE nome ILIKE ? ORDER BY nome";
        List<Idoso> listaIdosos = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + termo + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listaIdosos.add(mapearIdoso(rs));
                }
            }
        }

        return listaIdosos;
    }

    public Idoso buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, data_nascimento, biografia_breve FROM idosos WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearIdoso(rs);
                }
            }
        }

        return null;
    }

    public void atualizar(Idoso idoso) throws SQLException {
        String sql = "UPDATE idosos SET nome = ?, data_nascimento = ?, biografia_breve = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherParametros(stmt, idoso);
            stmt.setInt(4, idoso.getId());
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM idosos WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private void preencherParametros(PreparedStatement stmt, Idoso idoso) throws SQLException {
        stmt.setString(1, idoso.getNome());
        stmt.setDate(2, Date.valueOf(idoso.getDataNascimento()));
        stmt.setString(3, idoso.getBiografiaBreve());
    }

    private Idoso mapearIdoso(ResultSet rs) throws SQLException {
        Idoso idoso = new Idoso();
        idoso.setId(rs.getInt("id"));
        idoso.setNome(rs.getString("nome"));
        idoso.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
        idoso.setBiografiaBreve(rs.getString("biografia_breve"));
        return idoso;
    }
}
