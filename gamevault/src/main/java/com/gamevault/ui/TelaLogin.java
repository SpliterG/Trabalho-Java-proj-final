package com.gamevault.ui;

import com.gamevault.dao.UsuarioDAO;
import com.gamevault.model.Usuario;
import com.gamevault.model.Usuario.Tipo;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.text.JTextComponent;

/**
 * Tela de Login do GameVault.
 * Visual dark/gamer com suporte a login, criação de usuário e diferenciação
 * de perfil (Comum / Admin).
 */
public class TelaLogin extends JFrame {

    // ── Paleta ──────────────────────────────────────────────────────
    private static final Color COR_FUNDO       = new Color(10, 10, 18);
    private static final Color COR_CARD        = new Color(15, 12, 30, 235);
    private static final Color COR_BORDA       = new Color(120, 80, 255, 80);
    private static final Color COR_ACENTO      = new Color(80, 50, 220);
    private static final Color COR_ACENTO2     = new Color(6, 182, 212);
    private static final Color COR_TEXTO       = new Color(226, 217, 243);
    private static final Color COR_TEXTO_MUTED = new Color(150, 130, 200, 160);
    private static final Color COR_INPUT_BG    = new Color(255, 255, 255, 10);
    private static final Color COR_ROXO        = new Color(167, 139, 250);

    // ── Fontes ──────────────────────────────────────────────────────
    private static final Font FONTE_TITULO  = new Font("Monospaced", Font.BOLD, 15);
    private static final Font FONTE_LABEL   = new Font("SansSerif", Font.BOLD, 11);
    private static final Font FONTE_INPUT   = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONTE_BTN     = new Font("Monospaced", Font.BOLD, 12);
    private static final Font FONTE_SUBTITU = new Font("SansSerif", Font.PLAIN, 11);

    // ── Componentes ─────────────────────────────────────────────────
    private JTextField campologin;
    private JPasswordField campoSenha;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // ════════════════════════════════════════════════════════════════
    public TelaLogin() {
        configurarJanela();
        construirInterface();
        setVisible(true);
    }

    // ── Configuração da janela ───────────────────────────────────────
    private void configurarJanela() {
        setTitle("GameVault — Login");
        setSize(460, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);

        // Janela com cantos arredondados
        setShape(new RoundRectangle2D.Double(0, 0, 460, 560, 18, 18));

        // Painel de fundo personalizado
        JPanel fundo = new PainelFundo();
        fundo.setLayout(new GridBagLayout());
        setContentPane(fundo);
    }

    // ── Construção da interface ──────────────────────────────────────
    private void construirInterface() {
        JPanel card = criarCard();
        card.add(criarBarraTitulo(), BorderLayout.NORTH);
        card.add(criarCorpoLogin(), BorderLayout.CENTER);
        card.add(criarRodape(), BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        getContentPane().add(card, gbc);

        SwingUtilities.invokeLater(() -> getRootPane().setDefaultButton(
                (JButton) ((JPanel) card.getComponent(1)).getComponent(4)
        ));
    }

    // ── Card central ────────────────────────────────────────────────
    private JPanel criarCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COR_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                // Borda roxa
                g2.setColor(COR_BORDA);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);

                // Cantos decorativos
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(COR_ROXO);
                g2.drawLine(0, 0, 18, 0);
                g2.drawLine(0, 0, 0, 18);

                g2.setColor(COR_ACENTO2);
                g2.drawLine(getWidth() - 1, getHeight() - 1, getWidth() - 19, getHeight() - 1);
                g2.drawLine(getWidth() - 1, getHeight() - 1, getWidth() - 1, getHeight() - 19);

                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(360, 460));
        return card;
    }

    // ── Barra de título (arraste) ────────────────────────────────────
    private JPanel criarBarraTitulo() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        barra.setBorder(BorderFactory.createEmptyBorder(12, 14, 4, 14));

        // Botão fechar
        JButton btnFechar = new JButton("✕");
        estilizarBtnFechar(btnFechar);
        btnFechar.addActionListener(e -> System.exit(0));
        barra.add(btnFechar, BorderLayout.EAST);

        // Arrastar janela
        MouseAdapter arraste = criarListenerArraste();
        barra.addMouseListener(arraste);
        barra.addMouseMotionListener(arraste);

        return barra;
    }

    // ── Corpo principal ──────────────────────────────────────────────
    private JPanel criarCorpoLogin() {
        JPanel corpo = new JPanel();
        corpo.setOpaque(false);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setBorder(BorderFactory.createEmptyBorder(4, 32, 16, 32));

        corpo.add(criarLogoZone());
        corpo.add(Box.createVerticalStrut(20));
        corpo.add(criarCampoComLabel("LOGIN", false));
        corpo.add(Box.createVerticalStrut(12));
        corpo.add(criarCampoComLabel("SENHA", true));
        corpo.add(Box.createVerticalStrut(20));
        corpo.add(criarBotaoEntrar());
        corpo.add(Box.createVerticalStrut(14));
        corpo.add(criarDivisor());
        corpo.add(Box.createVerticalStrut(14));
        corpo.add(criarBotaoCriarUsuario());

        return corpo;
    }

    // ── Logo ─────────────────────────────────────────────────────────
    private JPanel criarLogoZone() {
        JPanel zona = new JPanel();
        zona.setOpaque(false);
        zona.setLayout(new BoxLayout(zona, BoxLayout.Y_AXIS));
        zona.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ícone
        JLabel icone = new JLabel("🎮") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(80, 50, 220, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COR_BORDA);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icone.setFont(new Font("Serif", Font.PLAIN, 26));
        icone.setHorizontalAlignment(SwingConstants.CENTER);
        icone.setPreferredSize(new Dimension(52, 52));
        icone.setMaximumSize(new Dimension(52, 52));
        icone.setAlignmentX(Component.CENTER_ALIGNMENT);
        icone.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        JLabel titulo = new JLabel("GAMEVAULT");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(COR_ROXO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("SISTEMA DE CLASSIFICAÇÃO DE JOGOS");
        sub.setFont(FONTE_SUBTITU);
        sub.setForeground(COR_TEXTO_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        zona.add(icone);
        zona.add(Box.createVerticalStrut(10));
        zona.add(titulo);
        zona.add(Box.createVerticalStrut(4));
        zona.add(sub);
        return zona;
    }

    // ── Campo com label ──────────────────────────────────────────────
    private JPanel criarCampoComLabel(String label, boolean isSenha) {
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONTE_LABEL);
        lbl.setForeground(COR_TEXTO_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComponent campo;
        if (isSenha) {
            campoSenha = new JPasswordField();
            estilizarCampo(campoSenha);
            campo = campoSenha;
        } else {
            campologin = new JTextField();
            estilizarCampo(campologin);
            campo = campologin;
        }
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        painel.add(lbl);
        painel.add(Box.createVerticalStrut(5));
        painel.add(campo);
        return painel;
    }

    // ── Botão Entrar ─────────────────────────────────────────────────
    private JButton criarBotaoEntrar() {
        JButton btn = new JButton("▶  ENTRAR NO SISTEMA") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed() ? COR_ACENTO.darker()
                         : getModel().isRollover() ? new Color(100, 70, 240) : COR_ACENTO;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONTE_BTN);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.addActionListener(e -> realizarLogin());

        // Enter também faz login
        //getRootPane().setDefaultButton(btn);
        return btn;
    }

    // ── Divisor ──────────────────────────────────────────────────────
    private JPanel criarDivisor() {
        JPanel div = new JPanel(new BorderLayout(8, 0));
        div.setOpaque(false);
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JPanel linha1 = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(120, 80, 255, 50));
                g.fillRect(0, getHeight() / 2, getWidth(), 1);
            }
        };
        linha1.setOpaque(false);

        JPanel linha2 = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(120, 80, 255, 50));
                g.fillRect(0, getHeight() / 2, getWidth(), 1);
            }
        };
        linha2.setOpaque(false);

        JLabel ou = new JLabel("OU");
        ou.setFont(FONTE_SUBTITU);
        ou.setForeground(COR_TEXTO_MUTED);

        div.add(linha1, BorderLayout.WEST);
        div.add(ou, BorderLayout.CENTER);
        div.add(linha2, BorderLayout.EAST);
        return div;
    }

    // ── Botão Criar Usuário ──────────────────────────────────────────
    private JButton criarBotaoCriarUsuario() {
        JButton btn = new JButton("+ CRIAR NOVO USUÁRIO") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                        ? new Color(120, 80, 255, 20) : new Color(0, 0, 0, 0);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(getModel().isRollover()
                        ? new Color(168, 85, 247, 130) : COR_BORDA);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(COR_ROXO);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.addActionListener(e -> abrirDialogCriarUsuario());
        return btn;
    }

    // ── Rodapé ───────────────────────────────────────────────────────
    private JPanel criarRodape() {
        JPanel rodape = new JPanel();
        rodape.setOpaque(false);
        JLabel info = new JLabel("v1.0 — Tema 3: Classificação de Jogos");
        info.setFont(new Font("SansSerif", Font.PLAIN, 10));
        info.setForeground(new Color(120, 100, 160, 90));
        rodape.add(info);
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        return rodape;
    }

    // ════════════════════════════════════════════════════════════════
    // Lógica de negócio
    // ════════════════════════════════════════════════════════════════
    private void realizarLogin() {
        String login = campologin.getText().trim();
        String senha = new String(campoSenha.getPassword()).trim();

        if (login.isEmpty() || senha.isEmpty()) {
            mostrarErro("Preencha login e senha.");
            return;
        }

        // TESTE TEMPORÁRIO — remover depois
        try (java.sql.Connection conn = com.gamevault.util.ConexaoDB.obterConexao()) {
            JOptionPane.showMessageDialog(this, "Conexão OK!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "ERRO DE CONEXÃO:\n" + ex.getMessage());
            return;
        }

        Usuario usuario = usuarioDAO.autenticar(login, senha);

        if (usuario == null) {
            mostrarErro("Login ou senha inválidos.");
            campoSenha.setText("");
            return;
        }

        dispose();
        new TelaInicial(usuario);
    }

    private void abrirDialogCriarUsuario() {
        JDialog dialog = new JDialog(this, "Criar Novo Usuário", true);
        dialog.setSize(340, 310);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);
        dialog.setShape(new RoundRectangle2D.Double(0, 0, 340, 310, 14, 14));

        JPanel painel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(12, 9, 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(COR_BORDA);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Título
        JLabel titulo = new JLabel("NOVO USUÁRIO");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(COR_ROXO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(titulo);
        painel.add(Box.createVerticalStrut(18));

        // Campo Login
        JLabel lblLogin = new JLabel("LOGIN");
        lblLogin.setFont(FONTE_LABEL);
        lblLogin.setForeground(COR_TEXTO_MUTED);
        lblLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField campoNovoLogin = new JTextField();
        estilizarCampo(campoNovoLogin);
        campoNovoLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        campoNovoLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Campo Senha
        JLabel lblSenha = new JLabel("SENHA");
        lblSenha.setFont(FONTE_LABEL);
        lblSenha.setForeground(COR_TEXTO_MUTED);
        lblSenha.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField campoNovaSenha = new JPasswordField();
        estilizarCampo(campoNovaSenha);
        campoNovaSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        campoNovaSenha.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tipo de usuário
        JLabel lblTipo = new JLabel("TIPO DE USUÁRIO");
        lblTipo.setFont(FONTE_LABEL);
        lblTipo.setForeground(COR_TEXTO_MUTED);
        lblTipo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"Comum", "Administrador"});
        estilizarCombo(comboTipo);
        comboTipo.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboTipo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        painel.add(lblLogin);
        painel.add(Box.createVerticalStrut(4));
        painel.add(campoNovoLogin);
        painel.add(Box.createVerticalStrut(10));
        painel.add(lblSenha);
        painel.add(Box.createVerticalStrut(4));
        painel.add(campoNovaSenha);
        painel.add(Box.createVerticalStrut(10));
        painel.add(lblTipo);
        painel.add(Box.createVerticalStrut(4));
        painel.add(comboTipo);
        painel.add(Box.createVerticalStrut(18));

        // Botões
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnCancelar = new JButton("Cancelar");
        estilizarBtnSecundario(btnCancelar);
        btnCancelar.addActionListener(e -> dialog.dispose());

        JButton btnSalvar = new JButton("Criar");
        estilizarBtnPrimario(btnSalvar);
        btnSalvar.addActionListener(e -> {
            String novoLogin = campoNovoLogin.getText().trim();
            String novaSenha = new String(campoNovaSenha.getPassword()).trim();
            Tipo tipo = comboTipo.getSelectedIndex() == 1 ? Tipo.ADMIN : Tipo.COMUM;

            if (novoLogin.isEmpty() || novaSenha.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha todos os campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (usuarioDAO.loginExiste(novoLogin)) {
                JOptionPane.showMessageDialog(dialog, "Este login já está em uso.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Usuario novo = new Usuario(0, novoLogin, novaSenha, tipo);
            if (usuarioDAO.inserir(novo)) {
                JOptionPane.showMessageDialog(dialog, "Usuário criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Erro ao criar usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancelar);
        btnPanel.add(btnSalvar);
        painel.add(btnPanel);

        dialog.setContentPane(painel);
        dialog.setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════
    // Utilitários de estilo
    // ════════════════════════════════════════════════════════════════

    private void estilizarCampo(JTextComponent campo) {
        campo.setBackground(new Color(30, 25, 50));  // cor sólida, sem alpha
        campo.setForeground(COR_TEXTO);
        campo.setCaretColor(COR_ROXO);
        campo.setFont(FONTE_INPUT);
        campo.setOpaque(true);
        campo.setMinimumSize(new Dimension(0, 36));
        campo.setPreferredSize(new Dimension(0, 36));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        campo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(6, COR_BORDA),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }


    private void estilizarCombo(JComboBox<?> combo) {
        combo.setBackground(new Color(15, 12, 30));
        combo.setForeground(COR_TEXTO);
        combo.setFont(FONTE_INPUT);
        combo.setBorder(new RoundedBorder(6, COR_BORDA));
    }

    private void estilizarBtnFechar(JButton btn) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(COR_TEXTO_MUTED);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e)  { btn.setForeground(COR_TEXTO_MUTED); }
        });
    }

    private void estilizarBtnPrimario(JButton btn) {
        btn.setBackground(COR_ACENTO);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void estilizarBtnSecundario(JButton btn) {
        btn.setBackground(new Color(30, 20, 50));
        btn.setForeground(COR_ROXO);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, COR_BORDA),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro de Login", JOptionPane.ERROR_MESSAGE);
    }

    // ── Listener para arrastar a janela sem barra de título ──────────
    private MouseAdapter criarListenerArraste() {
        return new MouseAdapter() {
            private Point origem;

            @Override public void mousePressed(MouseEvent e)  { origem = e.getPoint(); }
            @Override public void mouseDragged(MouseEvent e) {
                if (origem != null) {
                    Point loc = getLocation();
                    setLocation(loc.x + e.getX() - origem.x,
                                loc.y + e.getY() - origem.y);
                }
            }
        };
    }

    // ════════════════════════════════════════════════════════════════
    // Classes internas auxiliares
    // ════════════════════════════════════════════════════════════════

    /** Painel de fundo com grade pontilhada */
    private static class PainelFundo extends JPanel {
        PainelFundo() { setBackground(COR_FUNDO); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(80, 50, 220, 18));
            int gap = 32;
            for (int x = 0; x < getWidth(); x += gap) {
                g2.drawLine(x, 0, x, getHeight());
            }
            for (int y = 0; y < getHeight(); y += gap) {
                g2.drawLine(0, y, getWidth(), y);
            }
        }
    }

    /** Borda com cantos arredondados personalizada */
    private static class RoundedBorder implements Border {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }

        @Override public Insets getBorderInsets(Component c) { return new Insets(4, 8, 4, 8); }
        @Override public boolean isBorderOpaque() { return false; }
    }

    // ════════════════════════════════════════════════════════════════
    // Main (teste isolado da tela)
    // ════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(TelaLogin::new);
    }
}
