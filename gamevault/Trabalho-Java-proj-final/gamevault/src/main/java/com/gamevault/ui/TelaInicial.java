package com.gamevault.ui;

import com.gamevault.model.Usuario;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Tela principal do GameVault.
 * Layout estilo ERP: barra lateral com navegação + área de conteúdo à direita.
 * Menu exibido conforme perfil: Admin vê Gêneros + Usuários; Comum vê só Gêneros.
 */
public class TelaInicial extends JFrame {

    // ── Paleta ──────────────────────────────────────────────────────
    private static final Color COR_BG         = new Color(10, 10, 18);
    private static final Color COR_SIDEBAR    = new Color(13, 11, 30);
    private static final Color COR_BORDA      = new Color(120, 80, 255, 50);
    private static final Color COR_ACENTO     = new Color(80, 50, 220);
    private static final Color COR_ROXO       = new Color(167, 139, 250);
    private static final Color COR_TEXTO      = new Color(226, 217, 243);
    private static final Color COR_MUTED      = new Color(150, 130, 200, 150);
    private static final Color COR_DANGER     = new Color(248, 113, 113, 180);
    private static final Color COR_NAV_ATIVO  = new Color(80, 50, 220, 45);
    private static final Color COR_NAV_HOVER  = new Color(120, 80, 255, 25);
    private static final Color COR_TOPBAR     = new Color(13, 11, 30);
    private static final Color COR_CARD_BG    = new Color(13, 11, 30);

    // ── Fontes ──────────────────────────────────────────────────────
    private static final Font FONTE_LOGO    = new Font("Monospaced", Font.BOLD, 12);
    private static final Font FONTE_NAV     = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FONTE_SECTION = new Font("SansSerif", Font.BOLD, 9);
    private static final Font FONTE_TITULO  = new Font("Monospaced", Font.BOLD, 14);
    private static final Font FONTE_NORMAL  = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONTE_SMALL   = new Font("SansSerif", Font.PLAIN, 11);

    // ── Estado ──────────────────────────────────────────────────────
    private final Usuario usuarioLogado;
    private JPanel painelConteudo;
    private JLabel labelPaginaAtual;
    private NavItem itemAtivo;

    // ════════════════════════════════════════════════════════════════
    public TelaInicial(Usuario usuario) {
        this.usuarioLogado = usuario;
        configurarJanela();
        construirInterface();
        setVisible(true);
    }

    private void configurarJanela() {
        setTitle("GameVault");
        setSize(900, 600);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel fundo = new JPanel(new BorderLayout());
        fundo.setBackground(COR_BG);
        setContentPane(fundo);
    }

    private void construirInterface() {
        getContentPane().add(criarSidebar(), BorderLayout.WEST);
        getContentPane().add(criarAreaPrincipal(), BorderLayout.CENTER);
    }

    // ════════════════════════════════════════════════════════════════
    // SIDEBAR
    // ════════════════════════════════════════════════════════════════

    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(COR_BORDA);
                g.fillRect(getWidth() - 1, 0, 1, getHeight());
            }
        };
        sidebar.setBackground(COR_SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));

        sidebar.add(criarSidebarHeader());
        sidebar.add(criarSeparador());

        sidebar.add(criarSecaoLabel("MENU"));
        NavItem itemInicio = new NavItem("⊞", "Início", false);
        itemInicio.addActionListener(e -> navegarPara(itemInicio, "Início", criarPainelBemVindo()));
        sidebar.add(itemInicio);

        sidebar.add(criarSecaoLabel("CATÁLOGO"));
        NavItem itemGeneros = new NavItem("◈", "Gêneros", false);
        itemGeneros.addActionListener(e -> navegarPara(itemGeneros, "Gêneros", new TelaGeneros(usuarioLogado).getPainelConteudo()));
        sidebar.add(itemGeneros);

        if (usuarioLogado.isAdmin()) {
            sidebar.add(criarSecaoLabel("ADMINISTRAÇÃO"));
            NavItem itemUsuarios = new NavItem("◉", "Usuários", false);
            itemUsuarios.addActionListener(e -> navegarPara(itemUsuarios, "Usuários", new TelaUsuarios(usuarioLogado).getPainelConteudo()));
            sidebar.add(itemUsuarios);
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(criarSeparador());
        NavItem itemSair = new NavItem("⏻", "Sair", true);
        itemSair.addActionListener(e -> confirmarSaida());
        sidebar.add(itemSair);
        sidebar.add(criarSidebarFooter());

        SwingUtilities.invokeLater(() -> navegarPara(itemInicio, "Início", criarPainelBemVindo()));
        return sidebar;
    }

    private JPanel criarSidebarHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(16, 14, 12, 14));
        header.setMaximumSize(new Dimension(200, 80));

        JLabel logo = new JLabel("GAMEVAULT");
        logo.setFont(FONTE_LOGO);
        logo.setForeground(COR_ROXO);

        boolean isAdmin     = usuarioLogado.isAdmin();
        String tipotexto    = isAdmin ? "ADMIN" : "COMUM";
        Color corBadge      = isAdmin ? new Color(80, 50, 220, 60) : new Color(6, 182, 212, 40);
        Color corBadgeFg    = isAdmin ? COR_ROXO : new Color(6, 182, 212);

        JPanel linhaBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaBadge.setOpaque(false);

        JLabel lblUser = new JLabel(usuarioLogado.getLogin());
        lblUser.setFont(FONTE_SMALL);
        lblUser.setForeground(COR_MUTED);

        JLabel badge = new JLabel(tipotexto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(corBadge);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("SansSerif", Font.BOLD, 9));
        badge.setForeground(corBadgeFg);
        badge.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));

        linhaBadge.add(lblUser);
        linhaBadge.add(Box.createHorizontalStrut(5));
        linhaBadge.add(badge);

        header.add(logo);
        header.add(Box.createVerticalStrut(5));
        header.add(linhaBadge);
        return header;
    }

    private JPanel criarSidebarFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));
        footer.setMaximumSize(new Dimension(200, 36));
        JLabel info = new JLabel("v1.0 — Tema 3");
        info.setFont(new Font("SansSerif", Font.PLAIN, 9));
        info.setForeground(new Color(120, 100, 160, 80));
        footer.add(info);
        return footer;
    }

    private JLabel criarSecaoLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONTE_SECTION);
        lbl.setForeground(new Color(120, 100, 160, 120));
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 14, 3, 14));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JSeparator criarSeparador() {
        JSeparator sep = new JSeparator();
        sep.setForeground(COR_BORDA);
        sep.setBackground(COR_SIDEBAR);
        sep.setMaximumSize(new Dimension(200, 1));
        return sep;
    }

    private void navegarPara(NavItem item, String pagina, JPanel conteudo) {
        if (itemAtivo != null) itemAtivo.setAtivo(false);
        item.setAtivo(true);
        itemAtivo = item;
        if (labelPaginaAtual != null) labelPaginaAtual.setText(pagina);
        painelConteudo.removeAll();
        painelConteudo.add(conteudo, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    // ════════════════════════════════════════════════════════════════
    // ÁREA PRINCIPAL
    // ════════════════════════════════════════════════════════════════

    private JPanel criarAreaPrincipal() {
        JPanel area = new JPanel(new BorderLayout());
        area.setBackground(COR_BG);
        area.add(criarTopbar(), BorderLayout.NORTH);
        painelConteudo = new JPanel(new BorderLayout());
        painelConteudo.setBackground(COR_BG);
        area.add(painelConteudo, BorderLayout.CENTER);
        return area;
    }

    private JPanel criarTopbar() {
        JPanel topbar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                // Linha acento topo
                g2.setColor(COR_ACENTO);
                g2.fillRect(0, 0, getWidth() / 3, 2);
                g2.setColor(COR_ROXO);
                g2.fillRect(getWidth() / 3, 0, getWidth() / 3, 2);
                g2.setColor(new Color(6, 182, 212));
                g2.fillRect(getWidth() * 2 / 3, 0, getWidth(), 2);
                // Borda inferior
                g2.setColor(COR_BORDA);
                g2.fillRect(0, getHeight() - 1, getWidth(), 1);
                g2.dispose();
            }
        };
        topbar.setBackground(COR_TOPBAR);
        topbar.setPreferredSize(new Dimension(0, 48));
        topbar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Breadcrumb
        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        esquerda.setOpaque(false);

        JLabel lblSistema = new JLabel("GAMEVAULT");
        lblSistema.setFont(FONTE_LOGO);
        lblSistema.setForeground(COR_ROXO);

        JLabel sep2 = new JLabel("›");
        sep2.setFont(FONTE_NORMAL);
        sep2.setForeground(new Color(120, 80, 255, 80));

        labelPaginaAtual = new JLabel("Início");
        labelPaginaAtual.setFont(FONTE_NORMAL);
        labelPaginaAtual.setForeground(COR_MUTED);

        esquerda.add(lblSistema);
        esquerda.add(sep2);
        esquerda.add(labelPaginaAtual);

        // Avatar
        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        direita.setOpaque(false);

        String iniciais = usuarioLogado.getLogin()
                .substring(0, Math.min(2, usuarioLogado.getLogin().length())).toUpperCase();
        JLabel avatar = new JLabel(iniciais) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(80, 50, 220, 80));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(120, 80, 255, 100));
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("SansSerif", Font.BOLD, 11));
        avatar.setForeground(COR_ROXO);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(30, 30));

        JLabel nomeUser = new JLabel(usuarioLogado.getLogin());
        nomeUser.setFont(FONTE_SMALL);
        nomeUser.setForeground(COR_MUTED);

        direita.add(nomeUser);
        direita.add(avatar);

        topbar.add(esquerda, BorderLayout.WEST);
        topbar.add(direita, BorderLayout.EAST);
        return topbar;
    }

    // ════════════════════════════════════════════════════════════════
    // PAINEL BEM-VINDO
    // ════════════════════════════════════════════════════════════════

    private JPanel criarPainelBemVindo() {
        JPanel painel = new JPanel();
        painel.setBackground(COR_BG);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JLabel titulo = new JLabel("BEM-VINDO, " + usuarioLogado.getLogin().toUpperCase());
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(COR_ROXO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Selecione uma opção no menu lateral ou use os atalhos abaixo.");
        sub.setFont(FONTE_NORMAL);
        sub.setForeground(COR_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        painel.add(titulo);
        painel.add(Box.createVerticalStrut(6));
        painel.add(sub);
        painel.add(Box.createVerticalStrut(24));

        int ncards = usuarioLogado.isAdmin() ? 2 : 1;
        JPanel grid = new JPanel(new GridLayout(1, ncards, 12, 0));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        grid.add(criarDashCard("◈", "Gêneros de Jogo",
                usuarioLogado.isAdmin() ? "Cadastrar, editar e excluir gêneros" : "Visualizar lista de gêneros"));
        if (usuarioLogado.isAdmin()) {
            grid.add(criarDashCard("◉", "Gerenciar Usuários", "Listar, inserir, editar e excluir usuários"));
        }

        painel.add(grid);
        painel.add(Box.createVerticalStrut(20));
        painel.add(criarInfoBar());
        return painel;
    }

    private JPanel criarDashCard(String icone, String titulo, String desc) {
        JPanel card = new JPanel() {
            private boolean hover = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? new Color(16, 13, 36) : COR_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(hover ? new Color(120, 80, 255, 100) : COR_BORDA);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lblIcone = new JLabel(icone);
        lblIcone.setFont(new Font("SansSerif", Font.PLAIN, 20));
        lblIcone.setForeground(COR_ROXO);
        lblIcone.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTitulo.setForeground(COR_TEXTO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(FONTE_SMALL);
        lblDesc.setForeground(COR_MUTED);
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSeta = new JLabel("Acessar →");
        lblSeta.setFont(FONTE_SMALL);
        lblSeta.setForeground(new Color(120, 80, 255, 130));
        lblSeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblIcone);
        card.add(Box.createVerticalStrut(10));
        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(3));
        card.add(lblDesc);
        card.add(Box.createVerticalStrut(10));
        card.add(lblSeta);
        return card;
    }

    private JPanel criarInfoBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COR_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(COR_BORDA);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bar.setOpaque(false);
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        bar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel ponto = new JLabel("●");
        ponto.setFont(new Font("SansSerif", Font.PLAIN, 8));
        ponto.setForeground(COR_ACENTO);

        String perfil = usuarioLogado.isAdmin() ? "Administrador" : "Comum";
        String msg    = usuarioLogado.isAdmin()
                ? "Acesso total ao sistema"
                : "Acesso somente leitura para gêneros";

        JLabel info = new JLabel("Logado como " + perfil + " — " + msg);
        info.setFont(FONTE_SMALL);
        info.setForeground(COR_MUTED);

        bar.add(ponto);
        bar.add(info);
        return bar;
    }

    // ════════════════════════════════════════════════════════════════
    // Saída
    // ════════════════════════════════════════════════════════════════

    private void confirmarSaida() {
        int op = JOptionPane.showConfirmDialog(this,
                "Deseja sair do sistema?", "Confirmar Saída",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (op == JOptionPane.YES_OPTION) {
            dispose();
            new TelaLogin();
        }
    }

    // ════════════════════════════════════════════════════════════════
    // NavItem
    // ════════════════════════════════════════════════════════════════

    private class NavItem extends JPanel {
        private boolean ativo = false;
        private boolean hover = false;
        private final boolean isDanger;
        private final java.util.List<ActionListener> listeners = new java.util.ArrayList<>();

        NavItem(String icone, String texto, boolean isDanger) {
            this.isDanger = isDanger;
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
            setMaximumSize(new Dimension(200, 38));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel lblIcone = new JLabel(icone);
            lblIcone.setFont(new Font("SansSerif", Font.PLAIN, 15));
            lblIcone.setForeground(isDanger ? COR_DANGER : COR_MUTED);

            JLabel lblTexto = new JLabel(texto);
            lblTexto.setFont(FONTE_NAV);
            lblTexto.setForeground(isDanger ? COR_DANGER : COR_MUTED);

            add(lblIcone);
            add(lblTexto);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) {
                    for (ActionListener l : listeners) l.actionPerformed(null);
                }
            });
        }

        void setAtivo(boolean ativo) {
            this.ativo = ativo;
            for (Component c : getComponents()) {
                if (c instanceof JLabel lbl) {
                    lbl.setForeground(ativo ? COR_ROXO : (isDanger ? COR_DANGER : COR_MUTED));
                }
            }
            repaint();
        }

        void addActionListener(ActionListener l) { listeners.add(l); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            if (ativo) {
                g2.setColor(COR_NAV_ATIVO);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(COR_ACENTO);
                g2.fillRect(0, 0, 2, getHeight());
            } else if (hover) {
                g2.setColor(COR_NAV_HOVER);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
