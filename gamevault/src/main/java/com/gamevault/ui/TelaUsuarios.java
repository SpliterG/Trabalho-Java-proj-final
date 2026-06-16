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
 * Listagem com editar/excluir + formulário de inserção/edição via CardLayout.
 */
public class TelaUsuarios {

    // ── Paleta ──────────────────────────────────────────────────────
    private static final Color COR_BG       = new Color(10, 10, 18);
    private static final Color COR_CARD     = new Color(13, 11, 30);
    private static final Color COR_BORDA    = new Color(120, 80, 255, 50);
    private static final Color COR_ACENTO   = new Color(80, 50, 220);
    private static final Color COR_ROXO     = new Color(167, 139, 250);
    private static final Color COR_TEXTO    = new Color(226, 217, 243);
    private static final Color COR_MUTED    = new Color(150, 130, 200, 150);
    private static final Color COR_INPUT_BG = new Color(30, 25, 50);
    private static final Color COR_DANGER   = new Color(220, 80, 80);

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

    // Campos do formulário
    private JTextField campoLogin;
    private JPasswordField campoSenha;
    private JPasswordField campoConfirmar;
    private JComboBox<String> comboTipo;
    private JLabel lblFeedback;
    private JLabel lblTituloForm;
    private JLabel lblSubForm;
    private Usuario usuarioEmEdicao = null;

    private static final String CARD_LISTA = "LISTA";
    private static final String CARD_FORM  = "FORM";

    // ════════════════════════════════════════════════════════════════
    public TelaUsuarios(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public JPanel getPainelConteudo() {
        cardLayout = new CardLayout();
        painelRaiz = new JPanel(cardLayout);
        painelRaiz.setBackground(COR_BG);

        painelRaiz.add(criarPainelLista(), CARD_LISTA);
        painelRaiz.add(criarPainelForm(),  CARD_FORM);

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
        btnNovo.addActionListener(e -> abrirFormularioInserir());

        cabecalho.add(esquerda, BorderLayout.WEST);
        cabecalho.add(btnNovo, BorderLayout.EAST);

        // ── Tabela ──
        String[] colunas = {"#", "Login", "Tipo", "Ações"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        carregarUsuarios(model);

        JTable tabela = new JTable(model);
        estilizarTabela(tabela);

        tabela.getColumn("Ações").setCellRenderer(new AcoesRenderer());
        tabela.getColumn("Ações").setCellEditor(new AcoesEditor(model, tabela));
        tabela.getColumn("Ações").setPreferredWidth(180);
        tabela.getColumn("Ações").setMaxWidth(200);

        JScrollPane scroll = new JScrollPane(tabela);
        estilizarScroll(scroll);

        painel.add(cabecalho, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    private void carregarUsuarios(DefaultTableModel model) {
        model.setRowCount(0);
        List<Usuario> lista = dao.listarTodos();
        for (Usuario u : lista) {
            model.addRow(new Object[]{
                u.getId(),
                u.getLogin(),
                u.getTipo() == Tipo.ADMIN ? "Administrador" : "Comum",
                u
            });
        }
    }

    private void recarregarLista() {
        painelRaiz.remove(painelRaiz.getComponent(0));
        painelRaiz.add(criarPainelLista(), CARD_LISTA, 0);
        cardLayout.show(painelRaiz, CARD_LISTA);
        painelRaiz.revalidate();
        painelRaiz.repaint();
    }

    // ════════════════════════════════════════════════════════════════
    // PAINEL FORMULÁRIO (inserir / editar)
    // ════════════════════════════════════════════════════════════════

    private JPanel criarPainelForm() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_BG);
        painel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // ── Cabeçalho ──
        JPanel cabecalho = new JPanel();
        cabecalho.setOpaque(false);
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton btnVoltar = criarBotaoVoltar();
        btnVoltar.addActionListener(e -> {
            usuarioEmEdicao = null;
            limparFormulario();
            recarregarLista();
        });
        btnVoltar.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTituloForm = new JLabel("Novo Usuário");
        lblTituloForm.setFont(FONTE_TITULO);
        lblTituloForm.setForeground(COR_ROXO);
        lblTituloForm.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblSubForm = new JLabel("Preencha os dados para cadastrar um novo usuário.");
        lblSubForm.setFont(FONTE_SMALL);
        lblSubForm.setForeground(COR_MUTED);
        lblSubForm.setAlignmentX(Component.LEFT_ALIGNMENT);

        cabecalho.add(btnVoltar);
        cabecalho.add(Box.createVerticalStrut(10));
        cabecalho.add(lblTituloForm);
        cabecalho.add(Box.createVerticalStrut(4));
        cabecalho.add(lblSubForm);

        // ── Card formulário ──
        JPanel card = criarCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        campoLogin    = new JTextField();
        campoSenha    = new JPasswordField();
        campoConfirmar = new JPasswordField();
        comboTipo     = new JComboBox<>(new String[]{"Comum", "Administrador"});

        estilizarCampo(campoLogin);
        estilizarCampo(campoSenha);
        estilizarCampo(campoConfirmar);
        estilizarCombo(comboTipo);

        lblFeedback = new JLabel(" ");
        lblFeedback.setFont(FONTE_SMALL);
        lblFeedback.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(criarLinhaForm("LOGIN", campoLogin));
        card.add(Box.createVerticalStrut(14));
        card.add(criarLinhaForm("SENHA", campoSenha));
        card.add(Box.createVerticalStrut(14));
        card.add(criarLinhaForm("CONFIRMAR SENHA", campoConfirmar));
        card.add(Box.createVerticalStrut(14));
        card.add(criarLinhaForm("TIPO DE USUÁRIO", comboTipo));
        card.add(Box.createVerticalStrut(16));
        card.add(lblFeedback);
        card.add(Box.createVerticalStrut(8));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSalvar   = criarBotaoPrimario("Salvar");
        JButton btnCancelar = criarBotaoSecundario("Cancelar");

        btnCancelar.addActionListener(e -> {
            usuarioEmEdicao = null;
            limparFormulario();
            recarregarLista();
        });

        btnSalvar.addActionListener(e -> salvar());

        btnPanel.add(btnSalvar);
        btnPanel.add(btnCancelar);
        card.add(btnPanel);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(card);

        painel.add(cabecalho, BorderLayout.NORTH);
        painel.add(wrapper, BorderLayout.CENTER);
        return painel;
    }

    private void abrirFormularioInserir() {
        usuarioEmEdicao = null;
        limparFormulario();
        lblTituloForm.setText("Novo Usuário");
        lblSubForm.setText("Preencha os dados para cadastrar um novo usuário.");
        campoLogin.setEditable(true);
        cardLayout.show(painelRaiz, CARD_FORM);
    }

    private void abrirFormularioEditar(Usuario u) {
        usuarioEmEdicao = u;
        campoLogin.setText(u.getLogin());
        campoLogin.setEditable(false); // login não pode mudar na edição
        campoSenha.setText("");
        campoConfirmar.setText("");
        comboTipo.setSelectedIndex(u.getTipo() == Tipo.ADMIN ? 1 : 0);
        lblFeedback.setText(" ");
        lblTituloForm.setText("Editar Usuário");
        lblSubForm.setText("Altere os dados do usuário selecionado. Deixe a senha em branco para não alterar.");
        cardLayout.show(painelRaiz, CARD_FORM);
    }

    private void salvar() {
        String login    = campoLogin.getText().trim();
        String senha    = new String(campoSenha.getPassword()).trim();
        String confirma = new String(campoConfirmar.getPassword()).trim();
        Tipo tipo       = comboTipo.getSelectedIndex() == 1 ? Tipo.ADMIN : Tipo.COMUM;

        // Validações
        if (usuarioEmEdicao == null) {
            // Inserção — todos os campos obrigatórios
            if (login.isEmpty())          { feedback("Login não pode ser vazio."); return; }
            if (login.length() < 3)       { feedback("Login deve ter ao menos 3 caracteres."); return; }
            if (senha.isEmpty())          { feedback("Senha não pode ser vazia."); return; }
            if (senha.length() < 4)       { feedback("Senha deve ter ao menos 4 caracteres."); return; }
            if (!senha.equals(confirma))  { feedback("As senhas não conferem."); return; }
            if (dao.loginExiste(login))   { feedback("Este login já está em uso."); return; }

            if (dao.inserir(new Usuario(0, login, senha, tipo))) {
                limparFormulario();
                recarregarLista();
            } else {
                feedback("Erro ao salvar. Tente novamente.");
            }
        } else {
            // Edição — senha opcional
            if (!senha.isEmpty()) {
                if (senha.length() < 4)      { feedback("Senha deve ter ao menos 4 caracteres."); return; }
                if (!senha.equals(confirma)) { feedback("As senhas não conferem."); return; }
                usuarioEmEdicao.setSenha(senha);
            }
            usuarioEmEdicao.setTipo(tipo);

            if (dao.atualizar(usuarioEmEdicao)) {
                usuarioEmEdicao = null;
                limparFormulario();
                recarregarLista();
            } else {
                feedback("Erro ao salvar. Tente novamente.");
            }
        }
    }

    private void feedback(String msg) {
        lblFeedback.setText(msg);
        lblFeedback.setForeground(COR_DANGER);
    }

    private void limparFormulario() {
        campoLogin.setText("");
        campoLogin.setEditable(true);
        campoSenha.setText("");
        campoConfirmar.setText("");
        comboTipo.setSelectedIndex(0);
        lblFeedback.setText(" ");
    }

    // ════════════════════════════════════════════════════════════════
    // Renderer e Editor de ações
    // ════════════════════════════════════════════════════════════════

    private class AcoesRenderer implements TableCellRenderer {
        private final JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        private final JButton btnEdit = new JButton("Editar");
        private final JButton btnDel  = new JButton("Excluir");

        AcoesRenderer() {
            painel.setOpaque(true);
            estilizarBtnTabela(btnEdit, COR_ACENTO, Color.WHITE);
            estilizarBtnTabelaDanger(btnDel);
            painel.add(btnEdit);
            painel.add(btnDel);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            painel.setBackground(sel
                    ? new Color(80, 50, 220, 60) : new Color(13, 11, 30));
            return painel;
        }
    }

    private class AcoesEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        private final JButton btnEdit = new JButton("Editar");
        private final JButton btnDel  = new JButton("Excluir");
        private final DefaultTableModel model;
        private final JTable tabela;
        private Usuario usuarioAtual;

        AcoesEditor(DefaultTableModel model, JTable tabela) {
            this.model  = model;
            this.tabela = tabela;

            painel.setOpaque(true);
            painel.setBackground(new Color(13, 11, 30));
            estilizarBtnTabela(btnEdit, COR_ACENTO, Color.WHITE);
            estilizarBtnTabelaDanger(btnDel);

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                if (usuarioAtual != null) abrirFormularioEditar(usuarioAtual);
            });

            btnDel.addActionListener(e -> {
                fireEditingStopped();
                if (usuarioAtual == null) return;

                // Impede excluir o próprio usuário logado
                if (usuarioAtual.getId() == usuarioLogado.getId()) {
                    JOptionPane.showMessageDialog(painel,
                            "Você não pode excluir seu próprio usuário.",
                            "Atenção", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int op = JOptionPane.showConfirmDialog(painel,
                        "Excluir o usuário \"" + usuarioAtual.getLogin() + "\"?",
                        "Confirmar Exclusão",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (op == JOptionPane.YES_OPTION) {
                    if (dao.excluir(usuarioAtual.getId())) {
                        recarregarLista();
                    } else {
                        JOptionPane.showMessageDialog(painel,
                                "Erro ao excluir. Tente novamente.",
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            painel.add(btnEdit);
            painel.add(btnDel);
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v,
                boolean sel, int row, int col) {
            usuarioAtual = (v instanceof Usuario) ? (Usuario) v : null;
            return painel;
        }

        @Override public Object getCellEditorValue() { return usuarioAtual; }
    }

    // ════════════════════════════════════════════════════════════════
    // Utilitários de estilo
    // ════════════════════════════════════════════════════════════════

    private JPanel criarLinhaForm(String label, JComponent campo) {
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
        tabela.setRowHeight(38);
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

        tabela.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabela.getColumnModel().getColumn(0).setMaxWidth(60);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(120);
    }

    private void estilizarScroll(JScrollPane scroll) {
        scroll.setBorder(BorderFactory.createLineBorder(new Color(120, 80, 255, 50), 1));
        scroll.getViewport().setBackground(COR_CARD);
        scroll.setBackground(COR_CARD);
    }

    private void estilizarBtnTabela(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void estilizarBtnTabelaDanger(JButton btn) {
        btn.setBackground(new Color(60, 20, 20));
        btn.setForeground(new Color(248, 113, 113));
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 80, 80, 80), 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
        btn.setPreferredSize(new Dimension(140, 34));
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
