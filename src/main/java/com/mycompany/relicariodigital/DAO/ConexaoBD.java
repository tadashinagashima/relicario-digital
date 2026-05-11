package com.mycompany.relicariodigital.dao;

import com.mycompany.relicariodigital.service.Configuracao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {

    private ConexaoBD() {
    }

    public static Connection getConexao() throws SQLException {
        String url = Configuracao.get("db.url", "jdbc:postgresql://localhost:5432/relicario_digital");
        String usuario = Configuracao.get("db.user", "postgres");
        String senha = Configuracao.get("db.password", "postgres");
        return DriverManager.getConnection(url, usuario, senha);
    }
}
