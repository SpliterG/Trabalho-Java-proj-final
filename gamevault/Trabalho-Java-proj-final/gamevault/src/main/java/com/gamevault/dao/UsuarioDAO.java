package com.gamevault.dao;

import com.gamevault.model.Usuario;
import com.gamevault.model.Usuario.Tipo;
import com.gamevault.util.ConexaoDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // ---------------------------------------------------------------
    // Autenticação
    // ---------------------------------------------------------------

    /**
     * Autentica o usuário. Retorna o objeto Usuario em caso de sucesso,
     * ou null se login/senha forem inválidos.
     */
    public Usuario autenticar(String login, String senha) {
        String sql = "SELECT id, login, senha, tipo FROM usuarios WHERE login = ? AND senha = ?";

        try (Connection conn = ConexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, senha); // Fase 2: substituir por hash (BCrypt)

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ---------------------------------------------------------------
    // CRUD
    // ---------------------------------------------------------------

    public boolean inserir(Usuario u) {
        String sql = "INSERT INTO usuarios (login, senha, tipo) VALUES (?, ?, ?::tipo_usuario)";

        try (Connection conn = ConexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.getLogin());
            stmt.setString(2, u.getSenha());
            stmt.setString(3, u.getTipo().name().toLowerCase());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean atualizar(Usuario u) {
        String sql = "UPDATE usuarios SET login = ?, senha = ?, tipo = ?::tipo_usuario WHERE id = ?";

        try (Connection conn = ConexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.getLogin());
            stmt.setString(2, u.getSenha());
            stmt.setString(3, u.getTipo().name().toLowerCase());
            stmt.setInt(4, u.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = ConexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, login, senha, tipo FROM usuarios ORDER BY login";

        try (Connection conn = ConexaoDB.obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean loginExiste(String login) {
        String sql = "SELECT 1 FROM usuarios WHERE login = ?";

        try (Connection conn = ConexaoDB.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ---------------------------------------------------------------
    // Mapeamento
    // ---------------------------------------------------------------

    private Usuario mapear(ResultSet rs) throws SQLException {
        int id       = rs.getInt("id");
        String login = rs.getString("login");
        String senha = rs.getString("senha");
        Tipo tipo    = Tipo.valueOf(rs.getString("tipo").toUpperCase());
        return new Usuario(id, login, senha, tipo);
    }
}
