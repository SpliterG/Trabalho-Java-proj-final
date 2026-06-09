package com.gamevault.ui;

import com.gamevault.model.Usuario;
import javax.swing.*;
import java.awt.*;

/**
 * Tela de Gêneros — implementada na próxima etapa.
 */
public class TelaGeneros {

    private final Usuario usuario;

    public TelaGeneros(Usuario usuario) {
        this.usuario = usuario;
    }

    public JPanel getPainelConteudo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(10, 10, 18));
        JLabel lbl = new JLabel("Tela de Gêneros — em construção", SwingConstants.CENTER);
        lbl.setForeground(new Color(167, 139, 250));
        lbl.setFont(new Font("Monospaced", Font.BOLD, 14));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }
}
