package com.mycompany.relicariodigital.View;

import com.mycompany.relicariodigital.Controller.AcervoController;
import com.mycompany.relicariodigital.Controller.EntrevistaController;
import com.mycompany.relicariodigital.Controller.GestaoPerfilController;
import com.mycompany.relicariodigital.Model.Idoso;
import com.mycompany.relicariodigital.Model.Relato;
import com.mycompany.relicariodigital.Service.LoginService;
import com.mycompany.relicariodigital.Service.PdfExporter;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;

public class MainFrame extends JFrame {

    private static final String FONTE_PADRAO = "Segoe UI";
    private static final Color COR_FUNDO = new Color(243, 246, 250);
    private static final Color COR_SUPERFICIE = Color.WHITE;
    private static final Color COR_TEXTO = new Color(32, 41, 57);
    private static final Color COR_TEXTO_SUAVE = new Color(93, 103, 120);
    private static final Color COR_BORDA = new Color(218, 225, 235);
    private static final Color COR_PRIMARIA = new Color(36, 99, 235);
    private static final Color COR_PERIGO = new Color(190, 18, 60);

    private final LoginService loginService = new LoginService();
    private final GestaoPerfilController perfilController = new GestaoPerfilController();
    private final EntrevistaController entrevistaController = new EntrevistaController();
    private final AcervoController acervoController = new AcervoController();
    private final PdfExporter pdfExporter = new PdfExporter();

    private final CardLayout layoutPrincipal = new CardLayout();
    private final JPanel painelPrincipal = new JPanel(layoutPrincipal);
    private final JTabbedPane abas = new JTabbedPane();
    private final SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");

    private JTextField campoUsuario;
    private JPasswordField campoSenha;
    private JLabel labelId;
    private JTextField campoNome;
    private JTextField campoNascimento;
    private JTextArea campoBio;
    private JList<Idoso> listaPerfis;
    private DefaultListModel<Idoso> modeloPerfis;
    private JComboBox<Idoso> comboIdososEntrevista;
    private JTextArea campoTextoBruto;
    private JTextArea campoCronicaRevisao;
    private Relato relatoEmRevisao;
    private JTextField campoBuscaAcervo;
    private JList<Idoso> listaIdososAcervo;
    private JList<Relato> listaRelatosAcervo;
    private DefaultListModel<Idoso> modeloIdososAcervo;
    private DefaultListModel<Relato> modeloRelatosAcervo;
    private JTextArea campoLeituraCronica;
    private final Map<Component, Color> coresOriginaisFundo = new IdentityHashMap<>();
    private final Map<Component, Color> coresOriginaisTexto = new IdentityHashMap<>();
    private final Map<Component, Color> coresOriginaisCursor = new IdentityHashMap<>();
    private final Map<Component, Integer> tamanhosOriginaisFonte = new IdentityHashMap<>();
    private int tamanhoFonte = 18;
    private boolean altoContraste = false;
    private int idPerfilSelecionado = 0;

    public MainFrame() {
        setTitle("Relicario Digital - TEDI");
        setMinimumSize(new Dimension(1100, 740));
        setSize(1180, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        formatoData.setLenient(false);
        painelPrincipal.add(criarTelaLogin(), "login");
        painelPrincipal.add(criarTelaSistema(), "sistema");
        add(painelPrincipal);
        aplicarAcessibilidade();
    }

    private JPanel criarTelaLogin() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(COR_FUNDO);
        GridBagConstraints gbc = criarGbc();

        JPanel caixaLogin = criarPainelSecao(new GridBagLayout());
        GridBagConstraints gbcLogin = criarGbc();

        JLabel titulo = new JLabel("Relicario Digital");
        titulo.setFont(new Font(FONTE_PADRAO, Font.BOLD, 30));
        titulo.setForeground(COR_TEXTO);
        caixaLogin.add(titulo, gbcLogin);

        gbcLogin.gridy++;
        JLabel subtitulo = new JLabel("Acesso dos mediadores TEDI");
        subtitulo.setForeground(COR_TEXTO_SUAVE);
        caixaLogin.add(subtitulo, gbcLogin);

        gbcLogin.gridy++;
        caixaLogin.add(criarRotulo("Usuario do mediador"), gbcLogin);
        gbcLogin.gridy++;
        campoUsuario = new JTextField("mediador", 18);
        estilizarCampoTexto(campoUsuario);
        caixaLogin.add(campoUsuario, gbcLogin);

        gbcLogin.gridy++;
        caixaLogin.add(criarRotulo("Senha"), gbcLogin);
        gbcLogin.gridy++;
        campoSenha = new JPasswordField("tedi123", 18);
        estilizarCampoTexto(campoSenha);
        caixaLogin.add(campoSenha, gbcLogin);

        gbcLogin.gridy++;
        JButton botaoEntrar = criarBotao("Entrar", true);
        botaoEntrar.addActionListener(e -> autenticar());
        caixaLogin.add(botaoEntrar, gbcLogin);

        painel.add(caixaLogin, gbc);
        return painel;
    }

    private JPanel criarTelaSistema() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_FUNDO);
        painel.add(criarCabecalho(), BorderLayout.NORTH);
        abas.addTab("Perfis", criarAbaPerfis());
        abas.addTab("Entrevista", criarAbaEntrevista());
        abas.addTab("Revisao", criarAbaRevisao());
        abas.addTab("Acervo", criarAbaAcervo());
        abas.setBorder(new EmptyBorder(8, 12, 12, 12));
        abas.setBackground(COR_FUNDO);
        abas.setForeground(COR_TEXTO);
        painel.add(abas, BorderLayout.CENTER);
        atualizarListas();
        return painel;
    }

    private JPanel criarCabecalho() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_SUPERFICIE);
        painel.setBorder(new CompoundBorder(
                new LineBorder(COR_BORDA, 1, true),
                new EmptyBorder(16, 22, 16, 22)));

        JPanel textos = new JPanel(new BorderLayout());
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Relicario Digital");
        titulo.setFont(new Font(FONTE_PADRAO, Font.BOLD, 24));
        titulo.setForeground(COR_TEXTO);
        JLabel subtitulo = new JLabel("Memorias, entrevistas e acervo biografico");
        subtitulo.setForeground(COR_TEXTO_SUAVE);

        textos.add(titulo, BorderLayout.NORTH);
        textos.add(subtitulo, BorderLayout.SOUTH);
        painel.add(textos, BorderLayout.WEST);
        painel.add(criarBarraAcessibilidade(), BorderLayout.EAST);

        return painel;
    }

    private JPanel criarBarraAcessibilidade() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painel.setOpaque(false);
        JButton aumentar = criarBotao("A+", false);
        JButton diminuir = criarBotao("A-", false);
        JButton contraste = criarBotao("Alto contraste", false);

        aumentar.addActionListener(e -> {
            tamanhoFonte += 2;
            aplicarAcessibilidade();
        });
        diminuir.addActionListener(e -> {
            if (tamanhoFonte > 14) {
                tamanhoFonte -= 2;
                aplicarAcessibilidade();
            }
        });
        contraste.addActionListener(e -> {
            altoContraste = !altoContraste;
            aplicarAcessibilidade();
        });

        painel.add(aumentar);
        painel.add(diminuir);
        painel.add(contraste);
        return painel;
    }

    private JPanel criarAbaPerfis() {
        JPanel painel = criarAbaBase();

        modeloPerfis = new DefaultListModel<>();
        listaPerfis = new JList<>(modeloPerfis);
        estilizarLista(listaPerfis);
        listaPerfis.addListSelectionListener(e -> preencherFormularioPerfil());
        JScrollPane rolagemPerfis = criarRolagem(listaPerfis);
        rolagemPerfis.setPreferredSize(new Dimension(310, 500));
        painel.add(criarBloco("Perfis cadastrados", rolagemPerfis), BorderLayout.WEST);

        JPanel formulario = criarPainelSecao(new GridBagLayout());
        GridBagConstraints gbc = criarGbc();
        labelId = new JLabel("ID automatico");
        labelId.setForeground(COR_TEXTO_SUAVE);
        campoNome = new JTextField(25);
        campoNascimento = new JTextField(12);
        campoBio = new JTextArea(6, 25);
        campoBio.setLineWrap(true);
        campoBio.setWrapStyleWord(true);
        estilizarCampoTexto(campoNome);
        estilizarCampoTexto(campoNascimento);
        estilizarAreaTexto(campoBio);

        adicionarCampo(formulario, gbc, "Identificacao", labelId);
        adicionarCampo(formulario, gbc, "Nome", campoNome);
        adicionarCampo(formulario, gbc, "Nascimento (dd/MM/aaaa)", campoNascimento);
        adicionarCampo(formulario, gbc, "Biografia breve", criarRolagem(campoBio));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botoes.setOpaque(false);
        JButton novo = criarBotao("Limpar", false);
        JButton salvar = criarBotao("Salvar novo", true);
        JButton atualizar = criarBotao("Atualizar", false);
        JButton excluir = criarBotaoPerigo("Excluir");
        novo.addActionListener(e -> limparFormularioPerfil());
        salvar.addActionListener(e -> salvarNovoPerfil());
        atualizar.addActionListener(e -> atualizarPerfil());
        excluir.addActionListener(e -> excluirPerfil());
        botoes.add(novo);
        botoes.add(salvar);
        botoes.add(atualizar);
        botoes.add(excluir);

        gbc.gridy++;
        formulario.add(botoes, gbc);
        painel.add(formulario, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaEntrevista() {
        JPanel painel = criarAbaBase();

        JPanel linhaTopo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        linhaTopo.setOpaque(false);
        comboIdososEntrevista = new JComboBox<>();
        comboIdososEntrevista.setPreferredSize(new Dimension(320, 36));
        linhaTopo.add(criarRotulo("Participante"));
        linhaTopo.add(comboIdososEntrevista);
        painel.add(criarBloco("Dados da entrevista", linhaTopo), BorderLayout.NORTH);

        campoTextoBruto = new JTextArea();
        campoTextoBruto.setLineWrap(true);
        campoTextoBruto.setWrapStyleWord(true);
        campoTextoBruto.setText("Perguntas guia:\n- Onde essa lembranca aconteceu?\n- Quem estava junto?\n- O que marcou esse momento?\n- Que sentimento ficou?");
        estilizarAreaTexto(campoTextoBruto);
        painel.add(criarBloco("Relato bruto", criarRolagem(campoTextoBruto)), BorderLayout.CENTER);

        JButton processar = criarBotao("Gerar cronica com IA", true);
        processar.addActionListener(e -> processarEntrevista());
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rodape.setOpaque(false);
        rodape.add(processar);
        painel.add(rodape, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarAbaRevisao() {
        JPanel painel = criarAbaBase();
        campoCronicaRevisao = new JTextArea();
        campoCronicaRevisao.setLineWrap(true);
        campoCronicaRevisao.setWrapStyleWord(true);
        estilizarAreaTexto(campoCronicaRevisao);
        painel.add(criarBloco("Cronica para revisao", criarRolagem(campoCronicaRevisao)), BorderLayout.CENTER);

        JButton salvar = criarBotao("Salvar cronica finalizada", true);
        salvar.addActionListener(e -> salvarCronicaRevisada());
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rodape.setOpaque(false);
        rodape.add(salvar);
        painel.add(rodape, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarAbaAcervo() {
        JPanel painel = criarAbaBase();

        campoBuscaAcervo = new JTextField(22);
        estilizarCampoTexto(campoBuscaAcervo);
        JButton buscar = criarBotao("Buscar", true);
        buscar.addActionListener(e -> buscarAcervo());
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topo.setOpaque(false);
        topo.add(criarRotulo("Busca"));
        topo.add(campoBuscaAcervo);
        topo.add(buscar);
        painel.add(criarBloco("Pesquisar no acervo", topo), BorderLayout.NORTH);

        modeloIdososAcervo = new DefaultListModel<>();
        modeloRelatosAcervo = new DefaultListModel<>();
        listaIdososAcervo = new JList<>(modeloIdososAcervo);
        listaRelatosAcervo = new JList<>(modeloRelatosAcervo);
        estilizarLista(listaIdososAcervo);
        estilizarLista(listaRelatosAcervo);
        listaIdososAcervo.addListSelectionListener(e -> carregarRelatosDoIdoso());
        listaRelatosAcervo.addListSelectionListener(e -> mostrarRelatoSelecionado());

        JPanel listas = new JPanel(new BorderLayout(8, 8));
        listas.setOpaque(false);
        JScrollPane rolagemIdosos = criarRolagem(listaIdososAcervo);
        rolagemIdosos.setPreferredSize(new Dimension(280, 500));
        JScrollPane rolagemRelatos = criarRolagem(listaRelatosAcervo);
        rolagemRelatos.setPreferredSize(new Dimension(180, 500));
        listas.add(criarBloco("Participantes", rolagemIdosos), BorderLayout.WEST);
        listas.add(criarBloco("Relatos", rolagemRelatos), BorderLayout.CENTER);
        painel.add(listas, BorderLayout.WEST);

        campoLeituraCronica = new JTextArea();
        campoLeituraCronica.setEditable(false);
        campoLeituraCronica.setLineWrap(true);
        campoLeituraCronica.setWrapStyleWord(true);
        estilizarAreaTexto(campoLeituraCronica);
        painel.add(criarBloco("Leitura da cronica", criarRolagem(campoLeituraCronica)), BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setOpaque(false);
        JButton exportar = criarBotao("Gerar PDF", false);
        JButton imprimir = criarBotao("Imprimir", true);
        exportar.addActionListener(e -> exportarPdf());
        imprimir.addActionListener(e -> imprimirCronica());
        botoes.add(exportar);
        botoes.add(imprimir);
        painel.add(botoes, BorderLayout.SOUTH);
        return painel;
    }

    private void autenticar() {
        String usuario = campoUsuario.getText();
        String senha = new String(campoSenha.getPassword());

        if (loginService.autenticar(usuario, senha)) {
            atualizarListas();
            layoutPrincipal.show(painelPrincipal, "sistema");
        } else {
            JOptionPane.showMessageDialog(this, "Usuario ou senha invalidos.");
        }
    }

    private void salvarNovoPerfil() {
        Date nascimento = lerData();

        if (perfilController.salvarNovoPerfil(campoNome.getText(), nascimento, campoBio.getText())) {
            limparFormularioPerfil();
            atualizarListas();
            JOptionPane.showMessageDialog(this, "Perfil cadastrado.");
        } else {
            JOptionPane.showMessageDialog(this, "Informe pelo menos o nome.");
        }
    }

    private void atualizarPerfil() {
        if (idPerfilSelecionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione um perfil para atualizar.");
            return;
        }

        Date nascimento = lerData();
        int id = idPerfilSelecionado;

        if (perfilController.atualizarPerfil(id, campoNome.getText(), nascimento, campoBio.getText())) {
            atualizarListas();
            JOptionPane.showMessageDialog(this, "Perfil atualizado.");
        }
    }

    private void excluirPerfil() {
        if (idPerfilSelecionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione um perfil para excluir.");
            return;
        }

        int opcao = JOptionPane.showConfirmDialog(this, "Excluir perfil e relatos vinculados?");

        if (opcao == JOptionPane.YES_OPTION) {
            perfilController.excluirPerfil(idPerfilSelecionado);
            limparFormularioPerfil();
            atualizarListas();
        }
    }

    private void processarEntrevista() {
        Idoso idoso = (Idoso) comboIdososEntrevista.getSelectedItem();

        if (idoso == null) {
            JOptionPane.showMessageDialog(this, "Cadastre ou selecione um perfil antes da entrevista.");
            return;
        }

        campoCronicaRevisao.setText("Processando...");

        SwingWorker<Relato, Void> worker = new SwingWorker<Relato, Void>() {
            @Override
            protected Relato doInBackground() {
                return entrevistaController.processarRelato(idoso.getId(), campoTextoBruto.getText());
            }

            @Override
            protected void done() {
                try {
                    relatoEmRevisao = get();

                    if (relatoEmRevisao == null) {
                        campoCronicaRevisao.setText("");
                        JOptionPane.showMessageDialog(MainFrame.this, "Nao foi possivel gerar a cronica.");
                        return;
                    }

                    campoCronicaRevisao.setText(relatoEmRevisao.getCronicaGerada());
                    abas.setSelectedIndex(2);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Erro ao processar relato: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void salvarCronicaRevisada() {
        if (relatoEmRevisao == null) {
            JOptionPane.showMessageDialog(this, "Gere uma cronica antes de salvar.");
            return;
        }

        relatoEmRevisao.setCronicaGerada(campoCronicaRevisao.getText());
        entrevistaController.salvarRelato(relatoEmRevisao);
        relatoEmRevisao = null;
        campoCronicaRevisao.setText("");
        atualizarListas();
        JOptionPane.showMessageDialog(this, "Cronica salva no acervo.");
    }

    private void buscarAcervo() {
        modeloIdososAcervo.clear();
        modeloRelatosAcervo.clear();
        campoLeituraCronica.setText("");
        String termo = campoBuscaAcervo.getText();

        for (Idoso idoso : perfilController.buscarPerfilPorNome(termo)) {
            modeloIdososAcervo.addElement(idoso);
        }

        for (Relato relato : acervoController.buscarHistoriasPorTexto(termo)) {
            modeloRelatosAcervo.addElement(relato);
        }
    }

    private void exportarPdf() {
        Relato relato = listaRelatosAcervo.getSelectedValue();

        if (relato == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma cronica.");
            return;
        }

        Idoso idoso = acervoController.buscarIdoso(relato.getIdosoId());

        try {
            Path arquivo = pdfExporter.exportar(relato, idoso);
            JOptionPane.showMessageDialog(this, "PDF criado em: " + arquivo.toAbsolutePath());

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(arquivo.toFile());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao exportar PDF: " + e.getMessage());
        }
    }

    private void imprimirCronica() {
        try {
            campoLeituraCronica.print();
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, "Erro ao imprimir: " + e.getMessage());
        }
    }

    private void atualizarListas() {
        List<Idoso> idosos = perfilController.carregarListaPerfil();
        modeloPerfis.clear();
        modeloIdososAcervo.clear();
        comboIdososEntrevista.setModel(new DefaultComboBoxModel<Idoso>());

        for (Idoso idoso : idosos) {
            modeloPerfis.addElement(idoso);
            modeloIdososAcervo.addElement(idoso);
            comboIdososEntrevista.addItem(idoso);
        }

        modeloRelatosAcervo.clear();
    }

    private void carregarRelatosDoIdoso() {
        Idoso idoso = listaIdososAcervo.getSelectedValue();
        modeloRelatosAcervo.clear();
        campoLeituraCronica.setText("");

        if (idoso == null) {
            return;
        }

        for (Relato relato : acervoController.buscarHistoriasDoIdoso(idoso.getId())) {
            modeloRelatosAcervo.addElement(relato);
        }
    }

    private void mostrarRelatoSelecionado() {
        Relato relato = listaRelatosAcervo.getSelectedValue();

        if (relato != null) {
            campoLeituraCronica.setText(relato.getCronicaGerada());
        }
    }

    private void preencherFormularioPerfil() {
        Idoso idoso = listaPerfis.getSelectedValue();

        if (idoso == null) {
            return;
        }

        idPerfilSelecionado = idoso.getId();
        labelId.setText("ID " + idoso.getId());
        campoNome.setText(idoso.getNome());
        campoNascimento.setText(idoso.getDataNascimento() == null ? "" : formatoData.format(idoso.getDataNascimento()));
        campoBio.setText(idoso.getBiografiaBreve());
    }

    private void limparFormularioPerfil() {
        listaPerfis.clearSelection();
        idPerfilSelecionado = 0;
        labelId.setText("ID automatico");
        campoNome.setText("");
        campoNascimento.setText("");
        campoBio.setText("");
    }

    private Date lerData() {
        String texto = campoNascimento.getText().trim();

        if (texto.isEmpty()) {
            return null;
        }

        try {
            return formatoData.parse(texto);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Data invalida. Use dd/MM/aaaa.");
            return null;
        }
    }

    private JPanel criarAbaBase() {
        JPanel painel = new JPanel(new BorderLayout(14, 14));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(14, 14, 14, 14));
        return painel;
    }

    private JPanel criarPainelSecao(LayoutManager layout) {
        JPanel painel = new JPanel(layout);
        painel.setBackground(COR_SUPERFICIE);
        painel.setBorder(new CompoundBorder(
                new LineBorder(COR_BORDA, 1, true),
                new EmptyBorder(16, 16, 16, 16)));
        return painel;
    }

    private JPanel criarBloco(String titulo, Component conteudo) {
        JPanel painel = criarPainelSecao(new BorderLayout(0, 10));
        JLabel rotulo = new JLabel(titulo);
        rotulo.setFont(new Font(FONTE_PADRAO, Font.BOLD, tamanhoFonte));
        rotulo.setForeground(COR_TEXTO);
        painel.add(rotulo, BorderLayout.NORTH);
        painel.add(conteudo, BorderLayout.CENTER);
        return painel;
    }

    private JLabel criarRotulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setForeground(COR_TEXTO);
        rotulo.setFont(new Font(FONTE_PADRAO, Font.BOLD, tamanhoFonte));
        return rotulo;
    }

    private JScrollPane criarRolagem(Component conteudo) {
        JScrollPane rolagem = new JScrollPane(conteudo);
        rolagem.setBorder(new LineBorder(COR_BORDA, 1, true));
        rolagem.getViewport().setBackground(COR_SUPERFICIE);
        return rolagem;
    }

    private void estilizarCampoTexto(JTextComponent campo) {
        campo.setBackground(COR_SUPERFICIE);
        campo.setForeground(COR_TEXTO);
        campo.setCaretColor(COR_PRIMARIA);
        campo.setBorder(new CompoundBorder(
                new LineBorder(COR_BORDA, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
    }

    private void estilizarAreaTexto(JTextArea area) {
        estilizarCampoTexto(area);
        area.setMargin(new Insets(10, 10, 10, 10));
    }

    private void estilizarLista(JList<?> lista) {
        lista.setBackground(COR_SUPERFICIE);
        lista.setForeground(COR_TEXTO);
        lista.setSelectionBackground(new Color(219, 234, 254));
        lista.setSelectionForeground(COR_TEXTO);
        lista.setFixedCellHeight(36);
        lista.setBorder(new EmptyBorder(6, 6, 6, 6));
    }

    private JButton criarBotao(String texto, boolean primario) {
        JButton botao = new JButton(texto);

        if (primario) {
            estilizarBotao(botao, COR_PRIMARIA, Color.WHITE, COR_PRIMARIA);
        } else {
            estilizarBotao(botao, COR_SUPERFICIE, COR_TEXTO, COR_BORDA);
        }

        return botao;
    }

    private JButton criarBotaoPerigo(String texto) {
        JButton botao = new JButton(texto);
        estilizarBotao(botao, COR_SUPERFICIE, COR_PERIGO, new Color(254, 205, 211));
        return botao;
    }

    private void estilizarBotao(JButton botao, Color fundo, Color texto, Color borda) {
        botao.setBackground(fundo);
        botao.setForeground(texto);
        botao.setFocusPainted(false);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setBorder(new CompoundBorder(
                new LineBorder(borda, 1, true),
                new EmptyBorder(9, 14, 9, 14)));
    }

    private void adicionarCampo(JPanel painel, GridBagConstraints gbc, String rotulo, Component campo) {
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        painel.add(criarRotulo(rotulo), gbc);
        gbc.gridy++;
        painel.add(campo, gbc);
        gbc.gridy++;
    }

    private GridBagConstraints criarGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void aplicarAcessibilidade() {
        aplicarFonte(this);

        if (altoContraste) {
            aplicarAltoContraste(this);
        } else {
            restaurarCoresOriginais();
        }

        repaint();
        revalidate();
    }

    private void aplicarFonte(Component componente) {
        Font fonteAtual = componente.getFont();

        if (fonteAtual != null && !tamanhosOriginaisFonte.containsKey(componente)) {
            tamanhosOriginaisFonte.put(componente, fonteAtual.getSize());
        }

        int tamanhoOriginal = tamanhosOriginaisFonte.containsKey(componente)
                ? tamanhosOriginaisFonte.get(componente)
                : tamanhoFonte;
        int estilo = fonteAtual == null ? Font.PLAIN : fonteAtual.getStyle();
        int diferencaTitulo = Math.max(0, tamanhoOriginal - 18);
        int tamanhoAjustado = Math.max(12, tamanhoFonte + diferencaTitulo);
        componente.setFont(new Font(FONTE_PADRAO, estilo, tamanhoAjustado));

        if (componente instanceof Container) {
            Container container = (Container) componente;
            for (Component filho : container.getComponents()) {
                aplicarFonte(filho);
            }
        }
    }

    private void aplicarAltoContraste(Component componente) {
        if (!coresOriginaisFundo.containsKey(componente)) {
            coresOriginaisFundo.put(componente, componente.getBackground());
            coresOriginaisTexto.put(componente, componente.getForeground());
        }

        componente.setBackground(Color.BLACK);
        componente.setForeground(Color.WHITE);

        if (componente instanceof JTextComponent) {
            JTextComponent campoTexto = (JTextComponent) componente;

            if (!coresOriginaisCursor.containsKey(componente)) {
                coresOriginaisCursor.put(componente, campoTexto.getCaretColor());
            }

            campoTexto.setCaretColor(Color.WHITE);
        }

        if (componente instanceof Container) {
            Container container = (Container) componente;
            for (Component filho : container.getComponents()) {
                aplicarAltoContraste(filho);
            }
        }
    }

    private void restaurarCoresOriginais() {
        for (Map.Entry<Component, Color> entrada : coresOriginaisFundo.entrySet()) {
            entrada.getKey().setBackground(entrada.getValue());
        }

        for (Map.Entry<Component, Color> entrada : coresOriginaisTexto.entrySet()) {
            entrada.getKey().setForeground(entrada.getValue());
        }

        for (Map.Entry<Component, Color> entrada : coresOriginaisCursor.entrySet()) {
            if (entrada.getKey() instanceof JTextComponent) {
                ((JTextComponent) entrada.getKey()).setCaretColor(entrada.getValue());
            }
        }

        coresOriginaisFundo.clear();
        coresOriginaisTexto.clear();
        coresOriginaisCursor.clear();
    }

}
