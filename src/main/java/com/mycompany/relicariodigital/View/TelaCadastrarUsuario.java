package com.mycompany.relicariodigital.view;

import com.mycompany.relicariodigital.controller.GestaoPerfilController;
import com.mycompany.relicariodigital.model.Idoso;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class TelaCadastrarUsuario extends JFrame {

    private static final Color FUNDO = new Color(232, 243, 255);
    private static final Color AZUL = new Color(0, 153, 255);
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final GestaoPerfilController controller;
    private DefaultTableModel modeloTabela;
    private JTable tabelaPerfis;
    private JTextField campoBusca;
    private JTextField campoNome;
    private JTextField campoDataNascimento;
    private JTextArea campoBiografia;
    private Integer idSelecionado;

    public TelaCadastrarUsuario() {
        super("Gestao de perfis");
        this.controller = new GestaoPerfilController();
        configurarJanela();
        montarTela();
        carregarTabela("");
    }

    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(920, 600));
    }

    private void montarTela() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(16, 16));
        painelPrincipal.setBackground(FUNDO);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setContentPane(painelPrincipal);

        painelPrincipal.add(criarCabecalho(), BorderLayout.NORTH);
        painelPrincipal.add(criarFormulario(), BorderLayout.WEST);
        painelPrincipal.add(criarTabela(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout(12, 0));
        cabecalho.setOpaque(false);

        JButton voltar = new JButton("Voltar");
        voltar.addActionListener(e -> voltarInicio());
        cabecalho.add(voltar, BorderLayout.WEST);

        JLabel titulo = new JLabel("Gestao de perfis de idosos", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        cabecalho.add(titulo, BorderLayout.CENTER);

        JPanel busca = new JPanel(new BorderLayout(8, 0));
        busca.setOpaque(false);
        campoBusca = new JTextField();
        campoBusca.setPreferredSize(new Dimension(220, 32));
        JButton botaoBuscar = new JButton("Buscar");
        botaoBuscar.addActionListener(e -> carregarTabela(campoBusca.getText()));
        campoBusca.addActionListener(e -> carregarTabela(campoBusca.getText()));
        busca.add(campoBusca, BorderLayout.CENTER);
        busca.add(botaoBuscar, BorderLayout.EAST);
        cabecalho.add(busca, BorderLayout.EAST);

        return cabecalho;
    }

    private JPanel criarFormulario() {
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);
        formulario.setPreferredSize(new Dimension(330, 420));
        formulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Dados do perfil"),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(5, 0, 5, 0);

        campoNome = new JTextField();
        campoDataNascimento = new JTextField();
        campoDataNascimento.setToolTipText("Use o formato dd/MM/aaaa");
        campoBiografia = new JTextArea(8, 20);
        campoBiografia.setLineWrap(true);
        campoBiografia.setWrapStyleWord(true);

        adicionarCampo(formulario, gbc, "Nome", campoNome);
        adicionarCampo(formulario, gbc, "Data de nascimento (dd/mm/aaaa)", campoDataNascimento);

        gbc.gridy++;
        formulario.add(new JLabel("Pequena biografia"), gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        formulario.add(new JScrollPane(campoBiografia), gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        formulario.add(criarBotoesFormulario(), gbc);

        return formulario;
    }

    private void adicionarCampo(JPanel painel, GridBagConstraints gbc, String rotulo, JTextField campo) {
        gbc.gridy++;
        painel.add(new JLabel(rotulo), gbc);
        gbc.gridy++;
        painel.add(campo, gbc);
    }

    private JPanel criarBotoesFormulario() {
        JPanel botoes = new JPanel(new GridBagLayout());
        botoes.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(5, 0, 5, 0);

        JButton salvar = criarBotaoAzul("Salvar");
        salvar.addActionListener(e -> salvarPerfil());
        botoes.add(salvar, gbc);

        gbc.gridy = 1;
        JButton limpar = new JButton("Limpar");
        limpar.addActionListener(e -> limparFormulario());
        botoes.add(limpar, gbc);

        gbc.gridy = 2;
        JButton excluir = new JButton("Excluir selecionado");
        excluir.addActionListener(e -> excluirPerfil());
        botoes.add(excluir, gbc);

        return botoes;
    }

    private JScrollPane criarTabela() {
        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "Nascimento", "Biografia"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaPerfis = new JTable(modeloTabela);
        tabelaPerfis.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaPerfis.setRowHeight(26);
        tabelaPerfis.getColumnModel().getColumn(0).setMaxWidth(60);
        tabelaPerfis.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormularioComSelecao();
            }
        });

        JScrollPane scroll = new JScrollPane(tabelaPerfis);
        scroll.setBorder(BorderFactory.createTitledBorder("Perfis cadastrados"));
        return scroll;
    }

    private JButton criarBotaoAzul(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(AZUL);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return botao;
    }

    private void salvarPerfil() {
        try {
            LocalDate dataNascimento = lerDataNascimento();
            if (idSelecionado == null) {
                controller.salvarNovoPerfil(campoNome.getText(), dataNascimento, campoBiografia.getText());
                JOptionPane.showMessageDialog(this, "Perfil cadastrado com sucesso.");
            } else {
                controller.atualizarPerfil(idSelecionado, campoNome.getText(), dataNascimento, campoBiografia.getText());
                JOptionPane.showMessageDialog(this, "Perfil atualizado com sucesso.");
            }

            limparFormulario();
            carregarTabela(campoBusca.getText());
        } catch (Exception erro) {
            mostrarErro("Nao foi possivel salvar o perfil.", erro);
        }
    }

    private void excluirPerfil() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um perfil na tabela.");
            return;
        }

        int opcao = JOptionPane.showConfirmDialog(
                this,
                "Excluir este perfil tambem remove os relatos vinculados. Deseja continuar?",
                "Confirmar exclusao",
                JOptionPane.YES_NO_OPTION
        );

        if (opcao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.excluirPerfil(idSelecionado);
            limparFormulario();
            carregarTabela(campoBusca.getText());
            JOptionPane.showMessageDialog(this, "Perfil excluido.");
        } catch (Exception erro) {
            mostrarErro("Nao foi possivel excluir o perfil.", erro);
        }
    }

    private void carregarTabela(String termo) {
        try {
            List<Idoso> perfis = controller.buscarPerfis(termo);
            modeloTabela.setRowCount(0);
            for (Idoso idoso : perfis) {
                modeloTabela.addRow(new Object[]{
                    idoso.getId(),
                    idoso.getNome(),
                    idoso.getDataNascimento().format(FORMATO_DATA),
                    idoso.getBiografiaBreve()
                });
            }
        } catch (SQLException erro) {
            mostrarErro("Nao foi possivel carregar os perfis.", erro);
        }
    }

    private void preencherFormularioComSelecao() {
        int linha = tabelaPerfis.getSelectedRow();
        if (linha < 0) {
            return;
        }

        idSelecionado = (Integer) modeloTabela.getValueAt(linha, 0);
        campoNome.setText(String.valueOf(modeloTabela.getValueAt(linha, 1)));
        campoDataNascimento.setText(String.valueOf(modeloTabela.getValueAt(linha, 2)));
        Object biografia = modeloTabela.getValueAt(linha, 3);
        campoBiografia.setText(biografia == null ? "" : biografia.toString());
    }

    private LocalDate lerDataNascimento() {
        try {
            return LocalDate.parse(campoDataNascimento.getText().trim(), FORMATO_DATA);
        } catch (DateTimeParseException erro) {
            throw new IllegalArgumentException("Use uma data valida no formato dd/mm/aaaa.");
        }
    }

    private void limparFormulario() {
        idSelecionado = null;
        tabelaPerfis.clearSelection();
        campoNome.setText("");
        campoDataNascimento.setText("");
        campoBiografia.setText("");
        campoNome.requestFocus();
    }

    private void voltarInicio() {
        new TelaInicial().setVisible(true);
        dispose();
    }

    private void mostrarErro(String mensagem, Exception erro) {
        JOptionPane.showMessageDialog(this, mensagem + "\n" + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
