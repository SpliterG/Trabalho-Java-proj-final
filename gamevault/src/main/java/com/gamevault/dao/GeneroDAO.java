package com.gamevault.dao;

import com.gamevault.model.Genero;
import com.gamevault.util.ConexaoDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GeneroDAO {

    public List<Genero> listar() {
        List<Genero> lista = new ArrayList<>();
        String sql = "SELECT id, nome FROM generos ORDER BY nome";

        try (Connection conn = ConexaoDB.obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Genero(rs.getInt("id"), rs.getString("nome")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean inserir(Genero g) {
        String sql = "INSERT INTO generos (nome) VALUES (?)";

        try (Connection conn = ConexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, g.getNome().trim());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean atualizar(Genero g) {
        String sql = "UPDATE generos SET nome = ? WHERE id = ?";

        try (Connection conn = ConexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, g.getNome().trim());
            stmt.setInt(2, g.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM generos WHERE id = ?";

        try (Connection conn = ConexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean nomeExiste(String nome, int idIgnorar) {
        String sql = "SELECT 1 FROM generos WHERE LOWER(nome) = LOWER(?) AND id <> ?";

        try (Connection conn = ConexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome.trim());
            stmt.setInt(2, idIgnorar);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
