package com.gamevault.ui;

import com.gamevault.dao.GeneroDAO;
import com.gamevault.model.Genero;
import com.gamevault.model.Usuario;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Tela de Gêneros de Jogo.
 * Admin: listagem + inserir + editar + excluir.
 * Comum: somente visualização da listagem.
 */
public class TelaGeneros {

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
    private final GeneroDAO dao = new GeneroDAO();
    private JPanel painelRaiz;
    private CardLayout cardLayout;

    // Referências para o formulário
    private JTextField campoNome;
    private JLabel lblFeedback;
    private JLabel lblTituloForm;
    private JLabel lblSubForm;
    private Genero generoEmEdicao = null;

    private static final String CARD_LISTA  = "LISTA";
    private static final String CARD_FORM   = "FORM";

    // ════════════════════════════════════════════════════════════════
    public TelaGeneros(Usuario usuario) {
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

        JLabel titulo = new JLabel("Gêneros de Jogo");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(COR_ROXO);

        JLabel sub = new JLabel(usuarioLogado.isAdmin()
                ? "Gerencie os gêneros cadastrados no sistema."
                : "Visualização dos gêneros disponíveis no sistema.");
        sub.setFont(FONTE_SMALL);
        sub.setForeground(COR_MUTED);

        esquerda.add(titulo);
        esquerda.add(Box.createVerticalStrut(4));
        esquerda.add(sub);

        cabecalho.add(esquerda, BorderLayout.WEST);

        // Botão novo — somente admin
        if (usuarioLogado.isAdmin()) {
            JButton btnNovo = criarBotaoPrimario("+ Novo Gênero");
            btnNovo.addActionListener(e -> abrirFormularioInserir());
            cabecalho.add(btnNovo, BorderLayout.EAST);
        }

        // ── Modelo da tabela ──
        String[] colunas = usuarioLogado.isAdmin()
                ? new String[]{"#", "Nome", "Ações"}
                : new String[]{"#", "Nome"};

        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        carregarGeneros(model);

        JTable tabela = new JTable(model);
        estilizarTabela(tabela);

        // Coluna de ações (somente admin)
        if (usuarioLogado.isAdmin()) {
            tabela.getColumn("Ações").setCellRenderer(new AcoesRenderer());
            tabela.getColumn("Ações").setCellEditor(new AcoesEditor(model, tabela));
            tabela.getColumn("Ações").setPreferredWidth(160);
            tabela.getColumn("Ações").setMaxWidth(180);
        }

        JScrollPane scroll = new JScrollPane(tabela);
        estilizarScroll(scroll);

        painel.add(cabecalho, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    private void carregarGeneros(DefaultTableModel model) {
        model.setRowCount(0);
        List<Genero> lista = dao.listar();
        for (Genero g : lista) {
            if (usuarioLogado.isAdmin()) {
                model.addRow(new Object[]{g.getId(), g.getNome(), g});
            } else {
                model.addRow(new Object[]{g.getId(), g.getNome()});
            }
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
            generoEmEdicao = null;
            recarregarLista();
        });
        btnVoltar.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTituloForm = new JLabel("Novo Gênero");
        lblTituloForm.setFont(FONTE_TITULO);
        lblTituloForm.setForeground(COR_ROXO);
        lblTituloForm.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblSubForm = new JLabel("Preencha o nome do novo gênero.");
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

        campoNome = new JTextField();
        estilizarCampo(campoNome);

        lblFeedback = new JLabel(" ");
        lblFeedback.setFont(FONTE_SMALL);
        lblFeedback.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel linhaLabel = new JPanel();
        linhaLabel.setOpaque(false);
        linhaLabel.setLayout(new BoxLayout(linhaLabel, BoxLayout.Y_AXIS));
        linhaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel("NOME DO GÊNERO");
        lbl.setFont(FONTE_LABEL);
        lbl.setForeground(COR_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        campoNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoNome.setMaximumSize(new Dimension(400, 36));
        campoNome.setPreferredSize(new Dimension(400, 36));

        linhaLabel.add(lbl);
        linhaLabel.add(Box.createVerticalStrut(5));
        linhaLabel.add(campoNome);

        // Botões
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSalvar   = criarBotaoPrimario("Salvar");
        JButton btnCancelar = criarBotaoSecundario("Cancelar");

        btnCancelar.addActionListener(e -> {
            generoEmEdicao = null;
            campoNome.setText("");
            lblFeedback.setText(" ");
            recarregarLista();
        });

        btnSalvar.addActionListener(e -> salvar());

        btnPanel.add(btnSalvar);
        btnPanel.add(btnCancelar);

        card.add(linhaLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(lblFeedback);
        card.add(Box.createVerticalStrut(8));
        card.add(btnPanel);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(card);

        painel.add(cabecalho, BorderLayout.NORTH);
        painel.add(wrapper, BorderLayout.CENTER);
        return painel;
    }

    private void abrirFormularioInserir() {
        generoEmEdicao = null;
        campoNome.setText("");
        lblFeedback.setText(" ");
        lblTituloForm.setText("Novo Gênero");
        lblSubForm.setText("Preencha o nome do novo gênero.");
        cardLayout.show(painelRaiz, CARD_FORM);
    }

    private void abrirFormularioEditar(Genero g) {
        generoEmEdicao = g;
        campoNome.setText(g.getNome());
        lblFeedback.setText(" ");
        lblTituloForm.setText("Editar Gênero");
        lblSubForm.setText("Altere o nome do gênero selecionado.");
        cardLayout.show(painelRaiz, CARD_FORM);
    }

    private void salvar() {
        String nome = campoNome.getText().trim();

        if (nome.isEmpty()) {
            lblFeedback.setText("O nome do gênero não pode ser vazio.");
            lblFeedback.setForeground(COR_DANGER);
            return;
        }

        int idIgnorar = generoEmEdicao != null ? generoEmEdicao.getId() : 0;
        if (dao.nomeExiste(nome, idIgnorar)) {
            lblFeedback.setText("Já existe um gênero com este nome.");
            lblFeedback.setForeground(COR_DANGER);
            return;
        }

        boolean ok;
        if (generoEmEdicao == null) {
            ok = dao.inserir(new Genero(0, nome));
        } else {
            generoEmEdicao.setNome(nome);
            ok = dao.atualizar(generoEmEdicao);
        }

        if (ok) {
            generoEmEdicao = null;
            campoNome.setText("");
            lblFeedback.setText(" ");
            recarregarLista();
        } else {
            lblFeedback.setText("Erro ao salvar. Tente novamente.");
            lblFeedback.setForeground(COR_DANGER);
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Renderer e Editor de ações na tabela
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
        private Genero generoAtual;

        AcoesEditor(DefaultTableModel model, JTable tabela) {
            this.model  = model;
            this.tabela = tabela;

            painel.setOpaque(true);
            painel.setBackground(new Color(13, 11, 30));
            estilizarBtnTabela(btnEdit, COR_ACENTO, Color.WHITE);
            estilizarBtnTabelaDanger(btnDel);

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                if (generoAtual != null) abrirFormularioEditar(generoAtual);
            });

            btnDel.addActionListener(e -> {
                fireEditingStopped();
                if (generoAtual == null) return;
                int op = JOptionPane.showConfirmDialog(painel,
                        "Excluir o gênero \"" + generoAtual.getNome() + "\"?",
                        "Confirmar Exclusão",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (op == JOptionPane.YES_OPTION) {
                    if (dao.excluir(generoAtual.getId())) {
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
            generoAtual = (v instanceof Genero) ? (Genero) v : null;
            return painel;
        }

        @Override public Object getCellEditorValue() { return generoAtual; }
    }

    // ════════════════════════════════════════════════════════════════
    // Utilitários de estilo
    // ════════════════════════════════════════════════════════════════

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
        tabela.getColumnModel().getColumn(1).setPreferredWidth(300);
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
