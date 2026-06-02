package com.gamevault.ui;

import com.gamevault.model.Usuario;

import javax.swing.*;

/**
 * Tela inicial do sistema.
 * Será implementada na próxima etapa do projeto.
 */
public class TelaInicial extends JFrame {

    public TelaInicial(Usuario usuario) {
        setTitle("GameVault — Bem-vindo, " + usuario.getLogin());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel placeholder = new JLabel(
            "<html><center>Tela Inicial em construção...<br>Usuário: <b>"
            + usuario.getLogin() + "</b> | Tipo: <b>" + usuario.getTipo() + "</b></center></html>",
            SwingConstants.CENTER
        );
        add(placeholder);
        setVisible(true);
    }
}
