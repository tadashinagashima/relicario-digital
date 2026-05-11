package com.mycompany.relicariodigital.view;

import com.mycompany.relicariodigital.controller.EntrevistaController;
import com.mycompany.relicariodigital.controller.GestaoPerfilController;
import com.mycompany.relicariodigital.model.Idoso;
import com.mycompany.relicariodigital.model.Relato;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

public class TelaCadastrarRelato extends JFrame {

    private static final Color FUNDO = new Color(232, 243, 255);
    private static final Color AZUL = new Color(0, 153, 255);

    private final EntrevistaController entrevistaController;
    private final GestaoPerfilController perfilController;
    private JComboBox<Idoso> comboIdosos;
    private JTextArea areaTextoBruto;
    private JTextArea areaCronica;
    private JButton botaoGerar;
    private JButton botaoSalvar;

    public TelaCadastrarRelato() {
        super("Nova entrevista");
        this.entrevistaController = new EntrevistaController();
        this.perfilController = new GestaoPerfilController();
        configurarJanela();
        montarTela();
        carregarIdosos();
    }

    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(960, 650));
    }

    private void montarTela() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(16, 16));
        painelPrincipal.setBackground(FUNDO);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setContentPane(painelPrincipal);

        painelPrincipal.add(criarCabecalho(), BorderLayout.NORTH);
        painelPrincipal.add(criarCentro(), BorderLayout.CENTER);
        painelPrincipal.add(criarBarraBotoes(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout(12, 0));
        cabecalho.setOpaque(false);

        JButton voltar = new JButton("Voltar");
        voltar.addActionListener(e -> voltarInicio());
        cabecalho.add(voltar, BorderLayout.WEST);

        JLabel titulo = new JLabel("Entrevista e geracao de cronica", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        cabecalho.add(titulo, BorderLayout.CENTER);

        comboIdosos = new JComboBox<>();
        comboIdosos.setPreferredSize(new Dimension(260, 34));
        cabecalho.add(comboIdosos, BorderLayout.EAST);

        return cabecalho;
    }

    private JPanel criarCentro() {
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);

        areaTextoBruto = criarAreaTexto();
        areaCronica = criarAreaTexto();
        areaCronica.setEditable(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 8);

        centro.add(criarPainelTexto("Texto bruto da entrevista", areaTextoBruto), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 8, 0, 0);
        centro.add(criarPainelTexto("Cronica gerada para revisao", areaCronica), gbc);

        return centro;
    }

    private JTextArea criarAreaTexto() {
        JTextArea area = new JTextArea();
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        return area;
    }

    private JScrollPane criarPainelTexto(String titulo, JTextArea area) {
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createTitledBorder(titulo));
        scroll.setPreferredSize(new Dimension(420, 420));
        return scroll;
    }

    private JPanel criarBarraBotoes() {
        JPanel botoes = new JPanel(new GridBagLayout());
        botoes.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 6, 0, 6);

        botaoGerar = criarBotaoAzul("Gerar cronica com IA");
        botaoGerar.addActionListener(e -> gerarCronica());
        botoes.add(botaoGerar, gbc);

        botaoSalvar = criarBotaoAzul("Salvar relato revisado");
        botaoSalvar.addActionListener(e -> salvarRelato());
        gbc.gridx = 1;
        botoes.add(botaoSalvar, gbc);

        JButton limpar = new JButton("Limpar");
        limpar.addActionListener(e -> limparTela());
        gbc.gridx = 2;
        botoes.add(limpar, gbc);

        return botoes;
    }

    private JButton criarBotaoAzul(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(AZUL);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botao.setPreferredSize(new Dimension(210, 42));
        return botao;
    }

    private void carregarIdosos() {
        try {
            List<Idoso> idosos = perfilController.carregarListaPerfil();
            comboIdosos.removeAllItems();
            for (Idoso idoso : idosos) {
                comboIdosos.addItem(idoso);
            }

            if (idosos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cadastre um perfil antes de registrar entrevistas.");
            }
        } catch (SQLException erro) {
            mostrarErro("Nao foi possivel carregar os perfis.", erro);
        }
    }

    private void gerarCronica() {
        Idoso idoso = getIdosoSelecionado();
        if (idoso == null) {
            JOptionPane.showMessageDialog(this, "Selecione um idoso.");
            return;
        }

        String textoBruto = areaTextoBruto.getText();
        if (textoBruto.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o texto bruto da entrevista.");
            return;
        }

        setProcessando(true);
        areaCronica.setText("Processando com a API Gemini...");

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return entrevistaController.gerarCronica(idoso.getId(), textoBruto);
            }

            @Override
            protected void done() {
                try {
                    areaCronica.setText(get());
                    areaCronica.setCaretPosition(0);
                } catch (Exception erro) {
                    areaCronica.setText("");
                    mostrarErro("Nao foi possivel gerar a cronica.", erro);
                } finally {
                    setProcessando(false);
                }
            }
        };

        worker.execute();
    }

    private void salvarRelato() {
        Idoso idoso = getIdosoSelecionado();
        if (idoso == null) {
            JOptionPane.showMessageDialog(this, "Selecione um idoso.");
            return;
        }

        try {
            Relato relato = entrevistaController.salvarRelato(
                    idoso.getId(),
                    areaTextoBruto.getText(),
                    areaCronica.getText()
            );
            JOptionPane.showMessageDialog(this, "Relato " + relato.getNumero() + " salvo para " + idoso.getNome() + ".");
            limparTela();
        } catch (Exception erro) {
            mostrarErro("Nao foi possivel salvar o relato.", erro);
        }
    }

    private Idoso getIdosoSelecionado() {
        Object selecionado = comboIdosos.getSelectedItem();
        if (selecionado instanceof Idoso) {
            return (Idoso) selecionado;
        }
        return null;
    }

    private void setProcessando(boolean processando) {
        botaoGerar.setEnabled(!processando);
        botaoSalvar.setEnabled(!processando);
        setCursor(new Cursor(processando ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
    }

    private void limparTela() {
        areaTextoBruto.setText("");
        areaCronica.setText("");
        areaTextoBruto.requestFocus();
    }

    private void voltarInicio() {
        new TelaInicial().setVisible(true);
        dispose();
    }

    private void mostrarErro(String mensagem, Exception erro) {
        JOptionPane.showMessageDialog(this, mensagem + "\n" + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
