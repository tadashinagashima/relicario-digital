package com.mycompany.relicariodigital.view;

import com.mycompany.relicariodigital.controller.AcervoController;
import com.mycompany.relicariodigital.model.Idoso;
import com.mycompany.relicariodigital.model.Relato;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class FeedHistorias extends JFrame {

    private static final Color FUNDO = new Color(232, 243, 255);
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AcervoController controller;
    private DefaultListModel<Idoso> modeloPerfis;
    private DefaultListModel<Relato> modeloRelatos;
    private JList<Idoso> listaPerfis;
    private JList<Relato> listaRelatos;
    private JTextField campoBusca;
    private JTextArea areaPerfil;
    private JTextArea areaCronica;

    public FeedHistorias() {
        super("Mural / Acervo Digital");
        this.controller = new AcervoController();
        configurarJanela();
        montarTela();
        carregarPerfis("");
    }

    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
    }

    private void montarTela() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(16, 16));
        painelPrincipal.setBackground(FUNDO);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setContentPane(painelPrincipal);

        painelPrincipal.add(criarCabecalho(), BorderLayout.NORTH);
        painelPrincipal.add(criarConteudo(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout(12, 0));
        cabecalho.setOpaque(false);

        JButton voltar = new JButton("Voltar");
        voltar.addActionListener(e -> voltarInicio());
        cabecalho.add(voltar, BorderLayout.WEST);

        JLabel titulo = new JLabel("Mural de historias", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        cabecalho.add(titulo, BorderLayout.CENTER);

        return cabecalho;
    }

    private JSplitPane criarConteudo() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, criarPainelPerfis(), criarPainelRelatos());
        split.setResizeWeight(0.32);
        split.setBorder(null);
        split.setOpaque(false);
        return split;
    }

    private JPanel criarPainelPerfis() {
        JPanel painel = new JPanel(new BorderLayout(8, 8));
        painel.setOpaque(false);
        painel.setBorder(BorderFactory.createTitledBorder("Perfis"));

        JPanel busca = new JPanel(new BorderLayout(8, 0));
        busca.setOpaque(false);
        campoBusca = new JTextField();
        JButton botaoBuscar = new JButton("Buscar");
        botaoBuscar.addActionListener(e -> carregarPerfis(campoBusca.getText()));
        campoBusca.addActionListener(e -> carregarPerfis(campoBusca.getText()));
        busca.add(campoBusca, BorderLayout.CENTER);
        busca.add(botaoBuscar, BorderLayout.EAST);
        painel.add(busca, BorderLayout.NORTH);

        modeloPerfis = new DefaultListModel<>();
        listaPerfis = new JList<>(modeloPerfis);
        listaPerfis.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        listaPerfis.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Idoso selecionado = listaPerfis.getSelectedValue();
                mostrarPerfil(selecionado);
                carregarRelatos(selecionado);
            }
        });
        painel.add(new JScrollPane(listaPerfis), BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelRelatos() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setOpaque(false);

        areaPerfil = new JTextArea(5, 20);
        areaPerfil.setEditable(false);
        areaPerfil.setLineWrap(true);
        areaPerfil.setWrapStyleWord(true);
        areaPerfil.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane perfilScroll = new JScrollPane(areaPerfil);
        perfilScroll.setBorder(BorderFactory.createTitledBorder("Dados do perfil"));
        painel.add(perfilScroll, BorderLayout.NORTH);

        modeloRelatos = new DefaultListModel<>();
        listaRelatos = new JList<>(modeloRelatos);
        listaRelatos.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        listaRelatos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarRelato(listaRelatos.getSelectedValue());
            }
        });

        areaCronica = new JTextArea();
        areaCronica.setEditable(false);
        areaCronica.setLineWrap(true);
        areaCronica.setWrapStyleWord(true);
        areaCronica.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JScrollPane relatosScroll = new JScrollPane(listaRelatos);
        relatosScroll.setBorder(BorderFactory.createTitledBorder("Relatos"));
        JScrollPane cronicaScroll = new JScrollPane(areaCronica);
        cronicaScroll.setBorder(BorderFactory.createTitledBorder("Cronica"));

        JSplitPane splitRelatos = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, relatosScroll, cronicaScroll);
        splitRelatos.setResizeWeight(0.22);
        splitRelatos.setPreferredSize(new Dimension(620, 420));
        painel.add(splitRelatos, BorderLayout.CENTER);

        return painel;
    }

    private void carregarPerfis(String termo) {
        try {
            List<Idoso> perfis = controller.buscarParticipantes(termo);
            modeloPerfis.clear();
            modeloRelatos.clear();
            areaPerfil.setText("");
            areaCronica.setText("");

            for (Idoso idoso : perfis) {
                modeloPerfis.addElement(idoso);
            }

            if (!perfis.isEmpty()) {
                listaPerfis.setSelectedIndex(0);
            }
        } catch (SQLException erro) {
            mostrarErro("Nao foi possivel carregar os perfis.", erro);
        }
    }

    private void carregarRelatos(Idoso idoso) {
        modeloRelatos.clear();
        areaCronica.setText("");

        if (idoso == null) {
            return;
        }

        try {
            List<Relato> relatos = controller.buscarHistoriasDoIdoso(idoso.getId());
            for (Relato relato : relatos) {
                modeloRelatos.addElement(relato);
            }

            if (relatos.isEmpty()) {
                areaCronica.setText("Este perfil ainda nao possui relatos salvos.");
            } else {
                listaRelatos.setSelectedIndex(0);
            }
        } catch (SQLException erro) {
            mostrarErro("Nao foi possivel carregar os relatos.", erro);
        }
    }

    private void mostrarPerfil(Idoso idoso) {
        if (idoso == null) {
            areaPerfil.setText("");
            return;
        }

        String data = idoso.getDataNascimento().format(FORMATO_DATA);
        String idade = calcularIdade(idoso.getDataNascimento());
        String biografia = idoso.getBiografiaBreve() == null || idoso.getBiografiaBreve().trim().isEmpty()
                ? "Sem biografia breve cadastrada."
                : idoso.getBiografiaBreve();

        areaPerfil.setText("Nome: " + idoso.getNome()
                + "\nNascimento: " + data + " (" + idade + ")"
                + "\n\n" + biografia);
        areaPerfil.setCaretPosition(0);
    }

    private void mostrarRelato(Relato relato) {
        if (relato == null) {
            return;
        }

        String data = relato.getDataRegistro() == null
                ? ""
                : "\nRegistrado em: " + relato.getDataRegistro().format(FORMATO_DATA);

        areaCronica.setText("Relato " + relato.getNumero() + data + "\n\n" + relato.getCronicaGerada());
        areaCronica.setCaretPosition(0);
    }

    private String calcularIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            return "idade nao informada";
        }
        int anos = Period.between(dataNascimento, LocalDate.now()).getYears();
        return anos + " anos";
    }

    private void voltarInicio() {
        new TelaInicial().setVisible(true);
        dispose();
    }

    private void mostrarErro(String mensagem, Exception erro) {
        JOptionPane.showMessageDialog(this, mensagem + "\n" + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
