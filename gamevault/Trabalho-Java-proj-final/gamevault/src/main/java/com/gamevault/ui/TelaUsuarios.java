package com.gamevault.ui;

import com.gamevault.dao.UsuarioDAO;
import com.gamevault.model.Usuario;
import com.gamevault.model.Usuario.Tipo;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Tela de Gerenciamento de Usuários.
 * Navegação interna: Listagem <-> Formulário de inserção.
 * Somente acessível para administradores.
 */
public class TelaUsuarios {

    // ── Paleta ──────────────────────────────────────────────────────
    private static final Color COR_BG        = new Color(10, 10, 18);
    private static final Color COR_CARD      = new Color(13, 11, 30);
    private static final Color COR_BORDA     = new Color(120, 80, 255, 50);
    private static final Color COR_ACENTO    = new Color(80, 50, 220);
    private static final Color COR_ROXO      = new Color(167, 139, 250);
    private static final Color COR_TEXTO     = new Color(226, 217, 243);
    private static final Color COR_MUTED     = new Color(150, 130, 200, 150);
    private static final Color COR_INPUT_BG  = new Color(30, 25, 50);
    private static final Color COR_DANGER    = new Color(220, 80, 80);
    private static final Color COR_SUCCESS   = new Color(34, 197, 94);

    // ── Fontes ──────────────────────────────────────────────────────
    private static final Font FONTE_TITULO = new Font("Monospaced", Font.BOLD, 14);
    private static final Font FONTE_LABEL  = new Font("SansSerif", Font.BOLD, 11);
    private static final Font FONTE_INPUT  = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONTE_BTN    = new Font("SansSerif", Font.BOLD, 12);
    private static final Font FONTE_SMALL  = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font FONTE_TABLE  = new Font("SansSerif", Font.PLAIN, 12);

    // ── Estado ──────────────────────────────────────────────────────
    private final Usuario usuarioLogado;
    private final UsuarioDAO dao = new UsuarioDAO();
    private JPanel painelRaiz;
    private CardLayout cardLayout;

    private static final String CARD_LISTA     = "LISTA";
    private static final String CARD_INSERIR   = "INSERIR";

    // ════════════════════════════════════════════════════════════════
    public TelaUsuarios(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public JPanel getPainelConteudo() {
        cardLayout = new CardLayout();
        painelRaiz = new JPanel(cardLayout);
        painelRaiz.setBackground(COR_BG);

        painelRaiz.add(criarPainelLista(),    CARD_LISTA);
        painelRaiz.add(criarPainelInserir(),  CARD_INSERIR);

        cardLayout.show(painelRaiz, CARD_LISTA);
        return painelRaiz;
    }

    // ════════════════════════════════════════════════════════════════
    // PAINEL LISTAGEM
    // ════════════════════════════════════════════════════════════════

    private JPanel criarPainelLista() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_BG);
        painel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // ── Cabeçalho ──
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel esquerda = new JPanel();
        esquerda.setOpaque(false);
        esquerda.setLayout(new BoxLayout(esquerda, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Gerenciamento de Usuários");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(COR_ROXO);

        JLabel sub = new JLabel("Lista de todos os usuários cadastrados no sistema.");
        sub.setFont(FONTE_SMALL);
        sub.setForeground(COR_MUTED);

        esquerda.add(titulo);
        esquerda.add(Box.createVerticalStrut(4));
        esquerda.add(sub);

        JButton btnNovo = criarBotaoPrimario("+ Novo Usuário");
        btnNovo.addActionListener(e -> cardLayout.show(painelRaiz, CARD_INSERIR));

        cabecalho.add(esquerda, BorderLayout.WEST);
        cabecalho.add(btnNovo, BorderLayout.EAST);

        // ── Tabela ──
        String[] colunas = {"#", "Login", "Tipo"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        carregarUsuariosNaTabela(model);

        JTable tabela = new JTable(model);
        estilizarTabela(tabela);

        JScrollPane scroll = new JScrollPane(tabela);
        estilizarScroll(scroll);

        painel.add(cabecalho, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    private void carregarUsuariosNaTabela(DefaultTableModel model) {
        model.setRowCount(0);
        List<Usuario> lista = dao.listarTodos();
        for (Usuario u : lista) {
            model.addRow(new Object[]{
                u.getId(),
                u.getLogin(),
                u.getTipo() == Tipo.ADMIN ? "Administrador" : "Comum"
            });
        }
    }

    // ════════════════════════════════════════════════════════════════
    // PAINEL INSERIR
    // ════════════════════════════════════════════════════════════════

    private JPanel criarPainelInserir() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_BG);
        painel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // ── Cabeçalho com voltar ──
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel esquerda = new JPanel();
        esquerda.setOpaque(false);
        esquerda.setLayout(new BoxLayout(esquerda, BoxLayout.Y_AXIS));

        JButton btnVoltar = criarBotaoVoltar();
        btnVoltar.addActionListener(e -> cardLayout.show(painelRaiz, CARD_LISTA));

        JLabel titulo = new JLabel("Novo Usuário");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(COR_ROXO);

        JLabel sub = new JLabel("Preencha os dados para cadastrar um novo usuário.");
        sub.setFont(FONTE_SMALL);
        sub.setForeground(COR_MUTED);

        esquerda.add(btnVoltar);
        esquerda.add(Box.createVerticalStrut(10));
        esquerda.add(titulo);
        esquerda.add(Box.createVerticalStrut(4));
        esquerda.add(sub);

        cabecalho.add(esquerda, BorderLayout.WEST);

        // ── Formulário ──
        JPanel card = criarCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        card.setMaximumSize(new Dimension(480, Integer.MAX_VALUE));

        // Campo Login
        JTextField campoLogin = new JTextField();
        estilizarCampo(campoLogin);

        // Campo Senha
        JPasswordField campoSenha = new JPasswordField();
        estilizarCampo(campoSenha);

        // Campo Confirmar Senha
        JPasswordField campoConfirmar = new JPasswordField();
        estilizarCampo(campoConfirmar);

        // Combo Tipo
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"Comum", "Administrador"});
        estilizarCombo(comboTipo);

        // Label de feedback
        JLabel lblFeedback = new JLabel(" ");
        lblFeedback.setFont(FONTE_SMALL);
        lblFeedback.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(criarLinhaFormulario("LOGIN", campoLogin));
        card.add(Box.createVerticalStrut(14));
        card.add(criarLinhaFormulario("SENHA", campoSenha));
        card.add(Box.createVerticalStrut(14));
        card.add(criarLinhaFormulario("CONFIRMAR SENHA", campoConfirmar));
        card.add(Box.createVerticalStrut(14));
        card.add(criarLinhaFormulario("TIPO DE USUÁRIO", comboTipo));
        card.add(Box.createVerticalStrut(20));
        card.add(lblFeedback);
        card.add(Box.createVerticalStrut(8));

        // Botões
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSalvar   = criarBotaoPrimario("Salvar");
        JButton btnCancelar = criarBotaoSecundario("Cancelar");

        btnCancelar.addActionListener(e -> {
            limparFormulario(campoLogin, campoSenha, campoConfirmar, comboTipo, lblFeedback);
            cardLayout.show(painelRaiz, CARD_LISTA);
        });

        btnSalvar.addActionListener(e -> {
            String login    = campoLogin.getText().trim();
            String senha    = new String(campoSenha.getPassword()).trim();
            String confirma = new String(campoConfirmar.getPassword()).trim();
            Tipo tipo       = comboTipo.getSelectedIndex() == 1 ? Tipo.ADMIN : Tipo.COMUM;

            String erro = validar(login, senha, confirma);
            if (erro != null) {
                lblFeedback.setText(erro);
                lblFeedback.setForeground(COR_DANGER);
                return;
            }

            Usuario novo = new Usuario(0, login, senha, tipo);
            if (dao.inserir(novo)) {
                limparFormulario(campoLogin, campoSenha, campoConfirmar, comboTipo, lblFeedback);
                // Volta para lista atualizada
                painelRaiz.remove(painelRaiz.getComponent(0));
                painelRaiz.add(criarPainelLista(), CARD_LISTA, 0);
                cardLayout.show(painelRaiz, CARD_LISTA);
                painelRaiz.revalidate();
                painelRaiz.repaint();
            } else {
                lblFeedback.setText("Erro ao salvar. Tente novamente.");
                lblFeedback.setForeground(COR_DANGER);
            }
        });

        btnPanel.add(btnSalvar);
        btnPanel.add(btnCancelar);
        card.add(btnPanel);

        // Wrapper para limitar largura do card
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(card);

        painel.add(cabecalho, BorderLayout.NORTH);
        painel.add(wrapper, BorderLayout.CENTER);
        return painel;
    }

    // ════════════════════════════════════════════════════════════════
    // Validação
    // ════════════════════════════════════════════════════════════════

    private String validar(String login, String senha, String confirma) {
        if (login.isEmpty())              return "Login não pode ser vazio.";
        if (login.length() < 3)          return "Login deve ter ao menos 3 caracteres.";
        if (senha.isEmpty())              return "Senha não pode ser vazia.";
        if (senha.length() < 4)          return "Senha deve ter ao menos 4 caracteres.";
        if (!senha.equals(confirma))      return "As senhas não conferem.";
        if (dao.loginExiste(login))       return "Este login já está em uso.";
        return null;
    }

    private void limparFormulario(JTextField login, JPasswordField senha,
                                   JPasswordField confirma, JComboBox<?> combo, JLabel feedback) {
        login.setText("");
        senha.setText("");
        confirma.setText("");
        combo.setSelectedIndex(0);
        feedback.setText(" ");
    }

    // ════════════════════════════════════════════════════════════════
    // Utilitários de estilo
    // ════════════════════════════════════════════════════════════════

    private JPanel criarLinhaFormulario(String label, JComponent campo) {
        JPanel linha = new JPanel();
        linha.setOpaque(false);
        linha.setLayout(new BoxLayout(linha, BoxLayout.Y_AXIS));
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONTE_LABEL);
        lbl.setForeground(COR_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(400, 36));
        campo.setPreferredSize(new Dimension(400, 36));

        linha.add(lbl);
        linha.add(Box.createVerticalStrut(5));
        linha.add(campo);
        return linha;
    }

    private JPanel criarCard() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COR_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COR_BORDA);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }

    private void estilizarCampo(JTextComponent campo) {
        campo.setBackground(COR_INPUT_BG);
        campo.setForeground(COR_TEXTO);
        campo.setCaretColor(COR_ROXO);
        campo.setFont(FONTE_INPUT);
        campo.setOpaque(true);
        campo.setMinimumSize(new Dimension(0, 36));
        campo.setPreferredSize(new Dimension(400, 36));
        campo.setMaximumSize(new Dimension(400, 36));
        campo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(120, 80, 255, 60), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }

    private void estilizarCombo(JComboBox<?> combo) {
        combo.setBackground(COR_INPUT_BG);
        combo.setForeground(COR_TEXTO);
        combo.setFont(FONTE_INPUT);
        combo.setPreferredSize(new Dimension(400, 36));
        combo.setMaximumSize(new Dimension(400, 36));
    }

    private void estilizarTabela(JTable tabela) {
        tabela.setBackground(COR_CARD);
        tabela.setForeground(COR_TEXTO);
        tabela.setFont(FONTE_TABLE);
        tabela.setRowHeight(36);
        tabela.setGridColor(new Color(120, 80, 255, 25));
        tabela.setSelectionBackground(new Color(80, 50, 220, 60));
        tabela.setSelectionForeground(COR_ROXO);
        tabela.setShowVerticalLines(false);
        tabela.setIntercellSpacing(new Dimension(0, 1));
        tabela.setFocusable(false);

        JTableHeader header = tabela.getTableHeader();
        header.setBackground(new Color(18, 14, 40));
        header.setForeground(COR_MUTED);
        header.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COR_BORDA));
        header.setReorderingAllowed(false);

        // Largura das colunas
        tabela.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabela.getColumnModel().getColumn(0).setMaxWidth(60);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(250);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(120);
    }

    private void estilizarScroll(JScrollPane scroll) {
        scroll.setBorder(BorderFactory.createLineBorder(new Color(120, 80, 255, 50), 1));
        scroll.getViewport().setBackground(COR_CARD);
        scroll.setBackground(COR_CARD);
    }

    private JButton criarBotaoPrimario(String texto) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
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
        btn.setPreferredSize(new Dimension(130, 34));
        return btn;
    }

    private JButton criarBotaoSecundario(String texto) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                        ? new Color(120, 80, 255, 20) : new Color(0, 0, 0, 0);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(120, 80, 255, 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONTE_BTN);
        btn.setForeground(COR_ROXO);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 34));
        return btn;
    }

    private JButton criarBotaoVoltar() {
        JButton btn = new JButton("← Voltar");
        btn.setFont(FONTE_SMALL);
        btn.setForeground(COR_MUTED);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(COR_ROXO); }
            public void mouseExited(MouseEvent e)  { btn.setForeground(COR_MUTED); }
        });
        return btn;
    }
}
