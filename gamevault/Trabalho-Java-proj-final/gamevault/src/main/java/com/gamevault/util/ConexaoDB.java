package com.gamevault.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gerencia a conexão com o banco de dados PostgreSQL.
 * Edite as constantes abaixo conforme seu ambiente.
 */
public class ConexaoDB {

    private static final String URL    = "jdbc:postgresql://localhost:5432/Gamevault";
    private static final String USUARIO = "postgres";
    private static final String SENHA   = "postgres";

    private ConexaoDB() {}

    public static Connection obterConexao() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL não encontrado. Adicione o JAR ao classpath.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
