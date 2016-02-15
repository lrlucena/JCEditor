package com.cristian;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.TreePath;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;

/**
 * Classe que cria a interface principal e manipula parte dos eventos
 *
 * @author Cristian Henrique (cristianmsbr@gmail.com)
 * @version 2.0
 * @since Desde a primeira versão
 */
public class JCEditor extends JFrame {

	private AreaDeTexto at;
	private JTabbedPane arquivos;
	private final Font roboto = new Font("Roboto Light", Font.PLAIN, 14);
	private final JLabel separador = new JLabel("   ");
	private final JLabel separador2 = new JLabel("   ");
	private JLabel fonteAtual, linguagem;
	private JToolBar barraS;
	private JRadioButtonMenuItem gerarEstrutura, dobrarCodigo, quebrarLinha;
	private JMenuBar barraDeMenu;
	private JMenu menu, editar, sobre, preferencias, lookAndFeel, formatar,
			linguagemMenu, tema, projeto;
	private InputStream in;
	private JButton bExecutarPotigol;
	private Image icone;
	private final ButtonGroup bg2 = new ButtonGroup();
	private String fonteEscolhida = "Monospaced";
	private int tamanhoFonte = 12;
	private String titulo, auxArquivo, auxLinguagem;
	public String sLAF, sTema;
	private final List<AreaDeTexto> lista = new ArrayList<>();
	private final List<String> arquivosAbertos = new ArrayList<>();
	private final JRadioButtonMenuItem[] menusAparencia = new JRadioButtonMenuItem[14];
	private JScrollPane scrollPane;
	private JSplitPane painelSeparador, painelPrincipal;
	private ArvoreDeProjetos adp;
	private TerminalPotigol terminal;
	private final String sistemaOperacional = System.getProperty("os.name");

	/**
	 * O construtor define um título e chama o método de construção da interface
	 * gráfica.
	 */
	public JCEditor() {
		setTitle("JCEditor");
		construirGUI();
	}

	/**
	 * Cria e configura a parte gráfica da janela principal. São utilizados
	 * métodos auxiliares como "configMenu" e "configRadioMenus" para facilitar
	 * e reduzir as linhas de código para a criação desses componentes (JMenu e
	 * JRadioButtonMenuItem).
	 */
	public void construirGUI() {
		/* Instâncias de alguns objetos que serão utilizados. */
		barraDeMenu = new JMenuBar();
		arquivos = new JTabbedPane();
		barraS = new JToolBar();
		at = new AreaDeTexto();

		/* Cria objetos de menu */
		menu = new JMenu("Arquivo");
		editar = new JMenu("Editar");
		projeto = new JMenu("Projeto");
		formatar = new JMenu("Formatar");
		linguagemMenu = new JMenu("Linguagem");
		preferencias = new JMenu("Preferências");
		lookAndFeel = new JMenu("LAF");
		tema = new JMenu("Tema");
		sobre = new JMenu("Sobre");

		menu.setMnemonic('A');
		editar.setMnemonic('E');
		projeto.setMnemonic('R');
		sobre.setMnemonic('S');
		formatar.setMnemonic('F');
		preferencias.setMnemonic('P');
		linguagemMenu.setMnemonic('L');

		/*
		 * Cria a primeira aba do programa, esta aba é adicionada a uma
		 * ArrayList.
		 */
		lista.add(at);
		arquivos.addTab("Sem nome", at);
		arquivos.setToolTipTextAt(arquivos.getSelectedIndex(), "Sem nome");

		int i = arquivos.getSelectedIndex(); // índice a aba atual
		arquivos.setTabComponentAt(i, new ButtonTabComponent(arquivos, lista,
				arquivosAbertos)); // adiciona o botão de fechar à aba
		adicionarDocumentListener();

		/* Permite a navegação entre as abas utilizando Ctrl+Tab. */
		Set<KeyStroke> chave = new HashSet<>();
		chave.add(KeyStroke.getKeyStroke("TAB"));
		arquivos.setFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, chave);

		barraDeMenu.setBorder(null);
		barraS.setBorderPainted(false);
		tema.setIcon(icon("escolherTema.png"));
		lookAndFeel.setIcon(icon("pincel.png"));

		configMenu("Novo", "novo.png", novoListener, KeyEvent.VK_N,
				ActionEvent.CTRL_MASK, menu);
		configMenu("Abrir", "abrir.png", abrirListener, KeyEvent.VK_O,
				ActionEvent.CTRL_MASK, menu);
		configMenu("Salvar", "salvar.png", salvarListener, KeyEvent.VK_S,
				ActionEvent.CTRL_MASK, menu);
		configMenu("Salvar como", "salvarComo.png", salvarComoListener,
				KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK, menu);
		menu.addSeparator();
		configMenu("Imprimir", "imprimir.png", imprimirPotigolListener,
				KeyEvent.VK_P, ActionEvent.CTRL_MASK, menu);
		configMenu("Executar Potigol", "play.png", executarPotigolListener,
				KeyEvent.VK_F9, 0, menu);
		configMenu("Fechar aba", "fecharAba.png", fecharAbaListener,
				KeyEvent.VK_W, ActionEvent.CTRL_MASK, menu);
		menu.addSeparator();
		configMenu("Sair", "sair.png", sairListener, KeyEvent.VK_F4,
				InputEvent.ALT_DOWN_MASK, menu);
		configMenu("Desfazer", "desfazer.png", desfazerListener, KeyEvent.VK_Z,
				ActionEvent.CTRL_MASK, editar);
		configMenu("Refazer", "refazer.png", refazerListener, KeyEvent.VK_Y,
				ActionEvent.CTRL_MASK, editar);
		editar.addSeparator();
		configMenu("Recortar", "recortar.png", recortarListener, KeyEvent.VK_X,
				ActionEvent.CTRL_MASK, editar);
		configMenu("Copiar", "copiar.png", copiarListener, KeyEvent.VK_C,
				ActionEvent.CTRL_MASK, editar);
		configMenu("Colar", "colar.png", colarListener, KeyEvent.VK_V,
				ActionEvent.CTRL_MASK, editar);
		editar.addSeparator();
		configMenu("Selecionar tudo", "selecionarTudo.png",
				selecionarTudoListener, KeyEvent.VK_A, ActionEvent.CTRL_MASK,
				editar);
		configMenu("Potigol", "potigol.png", sobrePotigolListener,
				KeyEvent.VK_I, ActionEvent.CTRL_MASK, sobre);
		configMenu("Sobre este PC", "config.png", sobrePCListener,
				KeyEvent.VK_F3, 0, sobre);
		configMenu("Versão", "versaoIcone.png", versaoListener, KeyEvent.VK_F1,
				0, sobre);
		configMenu("Pesquisar", "pesquisar.png", pesquisarListener,
				KeyEvent.VK_F, ActionEvent.CTRL_MASK, formatar);
		configMenu("Fonte", "fonte.png", escolherFonteListener, KeyEvent.VK_R,
				ActionEvent.CTRL_MASK, formatar);
		formatar.addSeparator();
		configMenu("Normal", "fontePadrao.png", fontePadraoListener,
				KeyEvent.VK_0, Event.CTRL_MASK, formatar);
		configMenu("Aumentar", "aumentarFonte.png", aumentarFonteListener,
				KeyEvent.VK_EQUALS, ActionEvent.CTRL_MASK, formatar);
		configMenu("Diminuir", "diminuirFonte.png", diminuirFonteListener,
				KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK, formatar);
		formatar.addSeparator();
		configMenu("Adicionar", "addProjeto.png", addProjetoListener,
				KeyEvent.VK_O, Event.CTRL_MASK | Event.SHIFT_MASK, projeto);
		configMenu("Remover", "remover.png", removerProjetoListener,
				KeyEvent.VK_D, Event.CTRL_MASK | Event.SHIFT_MASK, projeto);
		configMenu("Propriedades", "propriedades.png",
				propriedadesProjetoListener, KeyEvent.VK_A, Event.CTRL_MASK
						| Event.SHIFT_MASK, projeto);

		/*
		 * Código de configuração dos menus de Look And Feel Sua estrutura é
		 * semelhante a do método "configMenu" exceto por utilizar um
		 * ButtonGroup(para que só exista um botão selecionado) e também pelo
		 * fato de não existir um ícone
		 */
		ButtonGroup bg = new ButtonGroup();
		menusAparencia[0] = configRadioMenus("JCE", lafPadraoListener, bg,
				lookAndFeel);
		menusAparencia[1] = configRadioMenus("Nimbus",
				lafListener("javax.swing.plaf.nimbus.NimbusLookAndFeel"), bg,
				lookAndFeel);
		menusAparencia[2] = configRadioMenus("Metal",
				lafListener("javax.swing.plaf.metal.MetalLookAndFeel"), bg,
				lookAndFeel);
		menusAparencia[3] = configRadioMenus("Sistema",
				lafListener(UIManager.getSystemLookAndFeelClassName()), bg,
				lookAndFeel);
		menusAparencia[4] = configRadioMenus("Motif",
				lafListener("com.sun.java.swing.plaf.motif.MotifLookAndFeel"),
				bg, lookAndFeel);

		/* Código de configuração dos menus de temas */
		ButtonGroup bg3 = new ButtonGroup();
		menusAparencia[5] = configRadioMenus("JCE", temaListener("jce"), bg3,
				tema);
		menusAparencia[6] = configRadioMenus("Dark", temaListener("dark"), bg3,
				tema);
		menusAparencia[7] = configRadioMenus("Dark II", temaListener("darkii"),
				bg3, tema);
		menusAparencia[8] = configRadioMenus("Default",
				temaListener("default"), bg3, tema);
		menusAparencia[9] = configRadioMenus("Default-Alt",
				temaListener("default-alt"), bg3, tema);
		menusAparencia[10] = configRadioMenus("Eclipse",
				temaListener("eclipse"), bg3, tema);
		menusAparencia[11] = configRadioMenus("IDEA", temaListener("idea"),
				bg3, tema);
		menusAparencia[12] = configRadioMenus("IDLE", temaListener("idle"),
				bg3, tema);
		menusAparencia[13] = configRadioMenus("Visual Studio",
				temaListener("vs"), bg3, tema);

		gerarEstrutura = new JRadioButtonMenuItem("Gerar estrutura");
		gerarEstrutura.setIcon(icon("estrutura.png"));
		gerarEstrutura.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
				ActionEvent.CTRL_MASK));
		formatar.add(gerarEstrutura);

		/*
		 * Código de configuração dos menus de linguagem, este método também é
		 * utilizado para a configuração dos itens de LAF e tema Sua estrutura é
		 * composta por: JRadioButtonMenuItem, String(nome no item de menu),
		 * ActionListener(recebe como argumento uma String contendo o nome da
		 * linguagem e sua sintaxe), ButtonGroup, JMenu
		 */
		configRadioMenus(
				"ActionScript",
				linguagemListener("ActionScript",
						SyntaxConstants.SYNTAX_STYLE_ACTIONSCRIPT), bg2,
				linguagemMenu);
		configRadioMenus(
				"Assembly",
				linguagemListener("Assembly",
						SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_X86), bg2,
				linguagemMenu);
		configRadioMenus(
				"Batch",
				linguagemListener("Windows Batch",
						SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH), bg2,
				linguagemMenu);
		configRadioMenus(
				"Clojure",
				linguagemListener("Clojure",
						SyntaxConstants.SYNTAX_STYLE_CLOJURE), bg2,
				linguagemMenu);
		configRadioMenus("CSS",
				linguagemListener("CSS", SyntaxConstants.SYNTAX_STYLE_CSS),
				bg2, linguagemMenu);
		configRadioMenus("C",
				linguagemListener("C", SyntaxConstants.SYNTAX_STYLE_C), bg2,
				linguagemMenu);
		configRadioMenus(
				"C++",
				linguagemListener("C++", SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS),
				bg2, linguagemMenu);
		configRadioMenus("C#",
				linguagemListener("C#", SyntaxConstants.SYNTAX_STYLE_CSHARP),
				bg2, linguagemMenu);
		configRadioMenus("D",
				linguagemListener("D", SyntaxConstants.SYNTAX_STYLE_D), bg2,
				linguagemMenu);
		configRadioMenus(
				"Delphi",
				linguagemListener("Delphi", SyntaxConstants.SYNTAX_STYLE_DELPHI),
				bg2, linguagemMenu);
		configRadioMenus(
				"Fortran",
				linguagemListener("Fortran",
						SyntaxConstants.SYNTAX_STYLE_FORTRAN), bg2,
				linguagemMenu);
		configRadioMenus(
				"Groovy",
				linguagemListener("Groovy", SyntaxConstants.SYNTAX_STYLE_GROOVY),
				bg2, linguagemMenu);
		configRadioMenus("HTML",
				linguagemListener("HTML", SyntaxConstants.SYNTAX_STYLE_HTML),
				bg2, linguagemMenu);
		configRadioMenus("Java",
				linguagemListener("Java", SyntaxConstants.SYNTAX_STYLE_JAVA),
				bg2, linguagemMenu);
		configRadioMenus(
				"JavaScript",
				linguagemListener("JavaScript",
						SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT), bg2,
				linguagemMenu);
		configRadioMenus(
				"Java Server Pages",
				linguagemListener("Java Server Pages",
						SyntaxConstants.SYNTAX_STYLE_JSP), bg2, linguagemMenu);
		configRadioMenus("JSON",
				linguagemListener("JSON", SyntaxConstants.SYNTAX_STYLE_JSON),
				bg2, linguagemMenu);
		configRadioMenus("LaTex",
				linguagemListener("LaTex", SyntaxConstants.SYNTAX_STYLE_LATEX),
				bg2, linguagemMenu);
		configRadioMenus("Lisp",
				linguagemListener("Lisp", SyntaxConstants.SYNTAX_STYLE_LISP),
				bg2, linguagemMenu);
		configRadioMenus("Lua",
				linguagemListener("Lua", SyntaxConstants.SYNTAX_STYLE_LUA),
				bg2, linguagemMenu);
		configRadioMenus(
				"Pascal",
				linguagemListener("Pascal", SyntaxConstants.SYNTAX_STYLE_DELPHI),
				bg2, linguagemMenu);
		configRadioMenus("Perl",
				linguagemListener("Perl", SyntaxConstants.SYNTAX_STYLE_PERL),
				bg2, linguagemMenu);
		configRadioMenus("PHP",
				linguagemListener("PHP", SyntaxConstants.SYNTAX_STYLE_PHP),
				bg2, linguagemMenu);
		configRadioMenus(
				"Plain text",
				linguagemListener("Texto simples",
						SyntaxConstants.SYNTAX_STYLE_NONE), bg2, linguagemMenu);
		configRadioMenus("Portugol", portugolListener, bg2, linguagemMenu);
		configRadioMenus("Potigol", potigolListener, bg2, linguagemMenu);
		configRadioMenus(
				"Properties",
				linguagemListener("Properties",
						SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE), bg2,
				linguagemMenu);
		configRadioMenus(
				"Python",
				linguagemListener("Python", SyntaxConstants.SYNTAX_STYLE_PYTHON),
				bg2, linguagemMenu);
		configRadioMenus("Ruby",
				linguagemListener("Ruby", SyntaxConstants.SYNTAX_STYLE_RUBY),
				bg2, linguagemMenu);
		configRadioMenus("Scala",
				linguagemListener("Scala", SyntaxConstants.SYNTAX_STYLE_SCALA),
				bg2, linguagemMenu);
		configRadioMenus(
				"Visual Basic",
				linguagemListener("Visual Basic",
						SyntaxConstants.SYNTAX_STYLE_VISUAL_BASIC), bg2,
				linguagemMenu);
		configRadioMenus(
				"Unix Shell",
				linguagemListener("Unix Shell",
						SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL), bg2,
				linguagemMenu);
		configRadioMenus("XML",
				linguagemListener("XML", SyntaxConstants.SYNTAX_STYLE_XML),
				bg2, linguagemMenu);

		configBtns("Novo arquivo", "novo25.png", novoListener);
		configBtns("Abrir arquivo", "abrir25.png", abrirListener);
		configBtns("Salvar arquivo", "salvar25.png", salvarListener);
		configBtns("Salvar como", "salvarComo25.png", salvarComoListener);
		barraS.add(separador);
		configBtns("Copiar", "copiar25.png", copiarListener);
		configBtns("Colar", "colar25.png", colarListener);
		configBtns("Recortar", "recortar25.png", recortarListener);
		configBtns("Desfazer", "desfazer25.png", desfazerListener);
		configBtns("Refazer", "refazer25.png", refazerListener);
		configBtns("Pesquisar", "pesquisar25.png", pesquisarListener);
		barraS.add(separador2);
		bExecutarPotigol = configBtns("Executar Potigol", "play25.png",
				executarPotigolListener);
		bExecutarPotigol.setEnabled(false);
		configBtns("Imprimir", "imprimir25.png", imprimirPotigolListener);

		/* Define o tamanho do ícone com base no SO */
		if (sistemaOperacional.equals("Linux")
				|| sistemaOperacional.equals("Mac OS X")) {
			icone = icon("jceIcone.png").getImage();
		} else {
			icone = icon("jceIcone32.png").getImage();
		}

		/*
		 * Cria o painel que exibirá a barra de memória e informações sobre
		 * linguagem e fonte.
		 */
		JPanel panel = new JPanel();
		JPanel panel2 = new JPanel();
		panel2.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		fonteAtual = new JLabel("Monospaced / Font.PLAIN / " + tamanhoFonte
				+ "  |   ");
		fonteAtual.setFont(new Font("Roboto Light", Font.BOLD, 12));
		fonteAtual.setForeground(new Color(234, 234, 235));
		panel.setBackground(new Color(91, 91, 91));
		panel2.setBackground(new Color(91, 91, 91));

		linguagem = new JLabel(lista.get(arquivos.getSelectedIndex())
				.getLinguagem() + "   ");
		linguagem.setFont(new Font("Roboto Light", Font.BOLD, 12));
		linguagem.setForeground(new Color(234, 234, 235));

		BarraDeMemoria bm = new BarraDeMemoria();
		panel2.add(bm);
		panel.add(panel2);
		panel.add(fonteAtual);
		panel.add(linguagem);

		/*
		 * Evento que verifica se o arquivo foi modificado quando o usuário
		 * clicar no botão fechar, se o arquivo foi modificado o usuário poderá
		 * escolher entre salvar ou não. Também é levado em consideração se o
		 * arquivo existe ou se é nulo
		 */
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				salvarAoSair();
			}
		});

		/*
		 * Evento de troca de aba. É responsável por definir o título do JFrame
		 * com base no arquivo que está na aba atual, também faz com que o botão
		 * de execução de arquivos Potigol seja liberado, para isso, leva em
		 * conta a variável booleana "isPotigol", esta variável tem valor true
		 * quando a extensão do arquivo for .poti(arquivos Potigol)
		 */
		ChangeListener changeListener = ev -> {
			JTabbedPane source = (JTabbedPane) ev.getSource();
			int index = source.getSelectedIndex();
			if (index == arquivos.getSelectedIndex()) {
				definirTitulo();
				updateLanguage(lista.get(arquivos.getSelectedIndex())
						.getLinguagem());
			}

			if (lista.get(arquivos.getSelectedIndex()).isPotigol()
					&& lista.get(arquivos.getSelectedIndex()).getArquivo() != null) {
				bExecutarPotigol.setEnabled(true);
			} else {
				bExecutarPotigol.setEnabled(false);
			}
		};

		/* Adiciona o evento ChangeListener e o evento de arrastar e soltar */
		arquivos.addChangeListener(changeListener);
		arrastarESoltar();

		dobrarCodigo = new JRadioButtonMenuItem("Dobrar código");
		dobrarCodigo.setIcon(icon("dobrarCodigo.png"));
		dobrarCodigo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
		dobrarCodigo.addActionListener(dobrarCodigoListener);

		quebrarLinha = new JRadioButtonMenuItem("Quebrar linha");
		quebrarLinha.setIcon(icon("quebrarLinha.png"));
		quebrarLinha.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				ActionEvent.CTRL_MASK));
		quebrarLinha.addActionListener(quebrarLinhaListener);

		preferencias.add(lookAndFeel);
		preferencias.add(tema);
		preferencias.addSeparator();
		preferencias.add(dobrarCodigo);
		preferencias.add(quebrarLinha);

		/* Adiciona os menus na barra principal */
		barraDeMenu.add(menu);
		barraDeMenu.add(editar);
		barraDeMenu.add(projeto);
		barraDeMenu.add(formatar);
		barraDeMenu.add(linguagemMenu);
		barraDeMenu.add(preferencias);
		barraDeMenu.add(sobre);

		/*
		 * Abre o arquivo selecionado na JTree quando o usuário clicar duas
		 * vezes sobre ele, exceto para pastas.
		 */
		adp = new ArvoreDeProjetos();
		adp.getArvore().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {
				TreePath tp = adp.getArvore().getPathForLocation(ev.getX(),
						ev.getY());

				if (tp != null && !adp.getArq().isDirectory()
						&& ev.getClickCount() == 2) {
					adicionarAba(adp.getArq());
					lista.get(arquivos.getSelectedIndex()).getRSyntax()
							.requestFocus();

					if (lista.get(arquivos.getSelectedIndex()).isPotigol()) {
						bExecutarPotigol.setEnabled(true);
					}
				}
			}
		});

		/* Atalho para fechar a aba. */
		arquivos.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {
				if (ev.getButton() == MouseEvent.BUTTON2) {
					fecharAba(arquivos.getSelectedIndex());
				}
			}
		});

		scrollPane = new JScrollPane(adp);
		scrollPane.setBorder(null);
		painelSeparador = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				scrollPane, arquivos);
		painelSeparador.setDividerLocation(0);
		painelSeparador.setOneTouchExpandable(true);
		painelSeparador.setBorder(null);

		terminal = new TerminalPotigol();
		painelPrincipal = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
				painelSeparador, terminal);
		painelPrincipal.setDividerLocation(600);
		painelPrincipal.setOneTouchExpandable(true);
		painelPrincipal.setBorder(null);

		getContentPane().add(BorderLayout.NORTH, barraS);
		getContentPane().add(BorderLayout.SOUTH, panel); // apenas define o
															// layout dos
															// componentes
		getContentPane().add(BorderLayout.CENTER, painelPrincipal);
		this.setJMenuBar(barraDeMenu);
		this.setIconImage(icone);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setSize(844, 635);
		this.setLocationRelativeTo(null);
		this.setMinimumSize(new Dimension(468, 328));
	}

	/**
	 * Método que define o nome do JFrame. Leva em consideração se o arquivo
	 * existe e se foi modificado
	 */
	private void definirTitulo() {
		titulo = "Sem nome - JCEditor";
		AreaDeTexto area = lista.get(arquivos.getSelectedIndex());
		if (area.getArquivo() != null && area.arquivoModificado()) {
			titulo = area.getArquivo().toString() + " •- JCEditor";
			arquivos.setTitleAt(arquivos.getSelectedIndex(), "• "
					+ area.getArquivo().getName());
		} else if (area.getArquivo() != null) {
			titulo = lista.get(arquivos.getSelectedIndex()).getArquivo()
					.toString()
					+ " - JCEditor";
			arquivos.setTitleAt(arquivos.getSelectedIndex(), area.getArquivo()
					.getName());
		} else if (area.arquivoModificado()) {
			titulo = "Sem nome •- JCEditor";
			arquivos.setTitleAt(arquivos.getSelectedIndex(), "• Sem nome");
		}
		setTitle(titulo);
	}

	/**
	 * Método que cria os menus para as funções do programa (copiar, colar,
	 * abrir, etc.)
	 *
	 * @param nome
	 *            String - nome que será dado ao JMenuItem
	 * @param img
	 *            String - caminho da imagem PNG do JMenuItem
	 * @param ev
	 *            ActionListener - evento que será executado ao pressionar o
	 *            menu
	 * @param ac
	 *            int - accelerator (KeyEvent)
	 * @param ac2
	 *            int - accelerator (ActionEvent)
	 * @param principal
	 *            JMenu - menu ao qual o JMenuItem pertence
	 */

	private void configMenu(String nome, String img, ActionListener ev, int ac,
			int ac2, JMenu principal) {
		JMenuItem itemDeMenu = new JMenuItem(nome);
		itemDeMenu.setIcon(icon(img));
		itemDeMenu.addActionListener(ev);
		itemDeMenu.setFont(roboto);
		itemDeMenu.setAccelerator(KeyStroke.getKeyStroke(ac, ac2));
		principal.add(itemDeMenu);
	}

	/**
	 * Método que cria o ToolTipText, adiciona a imagem, ActionListener,
	 * efeito(classe EfeitoBtn) e, por fim, adiciona o JButton na JToolBar
	 * (barraS)
	 *
	 * @param toolTipText
	 *            String - ToolTipText que será exibido
	 * @param img
	 *            String - caminho da imagem do JButton
	 * @param ev
	 *            ActionListener - evento que será executado ao pressionar o
	 *            botão
	 */
	private JButton configBtns(String toolTipText, String img, ActionListener ev) {
		JButton btn = new JButton();
		btn.setToolTipText(toolTipText);
		btn.setIcon(icon("25x25/" + img));
		btn.addActionListener(ev);
		new EfeitoBtn(btn);
		barraS.add(btn);

		return btn;
	}

	/**
	 * Método que cria os JRadioButtonMenuItem(s)
	 *
	 * @param nome
	 *            String - nome do menu
	 * @param ev
	 *            ActionListener - evento que será executado ao pressionar o
	 *            menu
	 * @param bg
	 *            ButtonGroup - grupo a qual o menu pertence (não é possível ter
	 *            mais de um selecionado)
	 * @param mPrincipal
	 *            - menu ao qual o menu de rádio pertence
	 */
	private JRadioButtonMenuItem configRadioMenus(String nome,
			ActionListener ev, ButtonGroup grupo, JMenu mPrincipal) {
		JRadioButtonMenuItem botao = new JRadioButtonMenuItem(nome);
		botao.addActionListener(ev);
		botao.setFont(roboto);
		grupo.add(botao);

		mPrincipal.add(botao);
		return botao;
	}

	private ImageIcon icon(String name) {
		return new ImageIcon(getClass().getResource("imagens/" + name));
	}

	/**
	 * Este método é chamado quando o usuário clica no botão fechar ou no menu
	 * sair. Percorre todos os arquivos abertos em busca de modificações, em
	 * caso positivo, pergunta se o usuário deseja salvar o arquivo. Também
	 * chama o método que salva o Look and Feel, tema, fonte e tamanho da fonte
	 * e o método que salva uma lista com os arquivos abertos.
	 */
	private void salvarAoSair() {
		for (AreaDeTexto area : lista) {
			if (area.arquivoModificado()) {
				String nomeArquivo;
				if (area.getArquivo() == null) {
					nomeArquivo = "Sem nome";
				} else {
					nomeArquivo = area.getArquivo().getName();
				}
				int r = JOptionPane
						.showConfirmDialog(JCEditor.this,
								"Você deseja salvar o arquivo \"" + nomeArquivo
										+ "\"?", "Sair",
								JOptionPane.YES_NO_CANCEL_OPTION);
				area.setTexto(area.getRSyntax().getText());
				if (r == JOptionPane.OK_OPTION) {
					if (area.getArquivo() == null) {
						area.salvarComo();
					} else {
						area.salvar(area.getRSyntax().getText());
					}
				} else if (r == JOptionPane.CANCEL_OPTION) {
					return;
				}
			}
		}

		String dobCodigo = null, quebLinha = null;
		if (dobrarCodigo.isSelected()) {
			dobCodigo = "dobrarCodigo";
		}

		if (quebrarLinha.isSelected()) {
			quebLinha = "quebrarLinha";
		}

		new Preferencias().salvarPreferencias(sLAF, sTema, fonteEscolhida,
				tamanhoFonte, dobCodigo, quebLinha);
		new Preferencias().salvarArquivosAbertos(arquivosAbertos);
		adp.salvarProjetos();
		System.exit(0);
	}

	/**
	 * Método que adiciona eventos ao JTextArea atual, tais eventos definem o
	 * título do JFrame
	 */
	private void adicionarDocumentListener() {
		lista.get(arquivos.getSelectedIndex()).getRSyntax().getDocument()
				.addDocumentListener(new DocumentListener() {
					@Override
					public void changedUpdate(DocumentEvent ev) {
					}

					@Override
					public void insertUpdate(DocumentEvent ev) {
						lista.get(arquivos.getSelectedIndex())
								.arquivoModificado(true);
						definirTitulo();
					}

					@Override
					public void removeUpdate(DocumentEvent ev) {
						lista.get(arquivos.getSelectedIndex())
								.arquivoModificado(true);
						definirTitulo();
					}
				});
	}

	/**
	 * Método que adiciona o recurso de Drag-and-drop (arrastar e soltar) ao
	 * JTextArea da aba atual
	 */
	private void arrastarESoltar() {
		new DropTarget(lista.get(arquivos.getSelectedIndex()).getRSyntax(),
				new DropTargetListener() {
					@Override
					public void dragEnter(DropTargetDragEvent ev) {
					}

					@Override
					public void dragExit(DropTargetEvent ev) {
					}

					@Override
					public void dragOver(DropTargetDragEvent ev) {
					}

					@Override
					public void dropActionChanged(DropTargetDragEvent ev) {
					}

					@Override
					public void drop(DropTargetDropEvent ev) {
						try {
							ev.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
							List<File> lista2 = (List<File>) ev
									.getTransferable().getTransferData(
											DataFlavor.javaFileListFlavor);

							for (int i = 0; i < lista2.size(); i++) {
								File arquivoD = (File) lista2.get(i);

								if (arquivoD.isDirectory()) {
									adp.adicionarFilhos(arquivoD);
									return;
								}

								adicionarAba(arquivoD);
							}

						} catch (UnsupportedFlavorException | IOException ex) {
							ex.printStackTrace();
						}
					}
				});
	}

	/**
	 * Adiciona uma aba ao JTabbedPane, também adiciona os eventos que
	 * possibilitam a função de arrastar e soltar. Além de configurar a fonte e
	 * definir o título.
	 *
	 * @param arquivo
	 *            File - arquivo que será adicionado
	 */
	public void adicionarAba(File arquivo) {
		for (int i = 0; i < lista.size(); i++) {
			if (arquivos.getTitleAt(i).equals(arquivo.getName())
					&& lista.get(i).getArquivo().toString()
							.equals(arquivo.toString())) {
				arquivos.setSelectedIndex(i);
				return;
			}
		}

		if (!arquivo.exists()) {
			return;
		}

		at = new AreaDeTexto();
		lista.add(at);
		arquivos.addTab("Sem nome", at);
		arquivos.setSelectedIndex(lista.size() - 1);
		arquivos.setTabComponentAt(arquivos.getSelectedIndex(),
				new ButtonTabComponent(arquivos, lista, arquivosAbertos));
		arquivos.setTitleAt(arquivos.getSelectedIndex(), arquivo.getName());
		AreaDeTexto area = lista.get(arquivos.getSelectedIndex());
		area.abrir(arquivo);
		area.getRSyntax().discardAllEdits();
		area.arquivoModificado(false);
		area.getRSyntax().setCaretPosition(0);

		arquivos.setToolTipTextAt(arquivos.getSelectedIndex(),
				arquivo.toString());
		linguagem.setText(area.getLinguagem() + "   ");
		arquivosAbertos.add(arquivo.toString());
		carregarTema(sTema);

		if (lista.get(arquivos.getSelectedIndex()).isPotigol()) {
			bExecutarPotigol.setEnabled(true);
		}
	}

	/**
	 * Método que atualiza o LAF utilizando o método updateComponentTreeUI, da
	 * classe SwingUtilities Este método também retira a borda da barra de menu
	 * e do JTextArea
	 */
	private void atualizarLAF() {
		SwingUtilities.updateComponentTreeUI(this);
		barraDeMenu.setBorder(null);
		painelPrincipal.setBorder(null);
		painelSeparador.setBorder(null);
		scrollPane.setBorder(null);
		terminal.getBarra().setBorder(null);

		for (AreaDeTexto adt : lista) {
			adt.setBorder(null);
			adt.barraDeRolagem().setBorder(null);
			SwingUtilities.updateComponentTreeUI(adt.fileChooser());
		}
	}

	/**
	 * Método que carrega o tema a ser utilizado (cores da sintaxe)
	 *
	 * @param nomeDoTema
	 *            String - nome do tema a ser carregado (ex.: dark, darkii,
	 *            etc.)
	 */
	public void carregarTema(String nomeDoTema) {
		for (AreaDeTexto adt : lista) {
			in = getClass().getResourceAsStream("temas/" + nomeDoTema + ".xml");
			sTema = nomeDoTema;
			try {
				Theme.load(in).apply(adt.getRSyntax());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		if (dobrarCodigo.isSelected()) {
			lista.get(arquivos.getSelectedIndex()).getRSyntax()
					.setCodeFoldingEnabled(true);
			lista.get(arquivos.getSelectedIndex()).barraDeRolagem()
					.setFoldIndicatorEnabled(true);
		}

		if (quebrarLinha.isSelected()) {
			lista.get(arquivos.getSelectedIndex()).getRSyntax()
					.setLineWrap(true);
			lista.get(arquivos.getSelectedIndex()).getRSyntax()
					.setWrapStyleWord(true);
		}

		adicionarDocumentListener();
		updateFonte();
		arrastarESoltar();
		definirTitulo();
	}

	/**
	 * Método que atualiza o nome da linguagem (exibido no canto inferior
	 * direito)
	 *
	 * @param nome
	 *            String - nome da linguagem
	 */
	private void updateLanguage(String nome) {
		lista.get(arquivos.getSelectedIndex()).setLinguagem(nome);
		linguagem.setText(lista.get(arquivos.getSelectedIndex()).getLinguagem()
				+ "   ");
	}

	/**
	 * Método que atualiza o tamanho da fonte e a própria fonte utilizada em
	 * todos os campos de textos de todas as abas
	 */
	public void updateFonte() {
		for (AreaDeTexto adt : lista) {
			adt.getRSyntax().setFont(
					new Font(fonteEscolhida, Font.PLAIN, tamanhoFonte));
			adt.barraDeRolagem()
					.getGutter()
					.setLineNumberFont(
							new Font("Monospaced", Font.PLAIN, tamanhoFonte));
		}
		fonteAtual.setText(fonteEscolhida + " / Font.PLAIN / " + tamanhoFonte
				+ "  |   ");
	}

	/**
	 * Cria um JFileChooser para seleção de pasta e em seguida chama o método
	 * adicionarFilhos, da classe ArvoreDeProjetos, que adiciona um nó a JTree
	 * contendo o projeto selecionado.
	 */
	private void abrirProjeto() {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.showOpenDialog(this);
		adp.adicionarFilhos(new File(jfc.getSelectedFile().toString()));
	}

	/**
	 * Utilizado na classe Preferencias, apenas remove o primeiro índice do
	 * JTabbedPane em caso de existirem arquivos a serem abertos.
	 */
	public void configAoAbrir() {
		lista.remove(0);
		arquivos.remove(0);
		definirTitulo();
		updateLanguage(lista.get(arquivos.getSelectedIndex()).getLinguagem());
	}

	/**
	 * Retorna uma lista contendo todos os arquivos abertos.
	 */
	public List<String> getArquivosAbertos() {
		return this.arquivosAbertos;
	}

	/**
	 * Configura a fonte.
	 *
	 * @param f
	 *            String - nova fonte
	 */
	public void setFonteEscolhida(String f) {
		this.fonteEscolhida = f;
	}

	/**
	 * Configura o tamanho da fonte.
	 *
	 * @param tam
	 *            int - novo tamanho da fonte
	 */
	public void setTamanhoFonte(int tam) {
		this.tamanhoFonte = tam;
	}

	/**
	 * Retorna o menu de dobramento de código.
	 */
	public JRadioButtonMenuItem getDobrarCodigo() {
		return this.dobrarCodigo;
	}

	/**
	 * Retorna o menu de quebra de linha.
	 */
	public JRadioButtonMenuItem getQuebrarLinha() {
		return this.quebrarLinha;
	}

	/**
	 * Retorna a matriz que contém os itens de menu que fazem o controle da
	 * aparência (tema e LAF).
	 */
	public JRadioButtonMenuItem[] getMenusDeAparencia() {
		return this.menusAparencia;
	}

	/**
	 * Método que fecha a aba informada. Antes de fechar a aba, verifica se o
	 * arquivo foi modificado, em caso positivo, pergunta se o usuário deseja
	 * salvar o mesmo.
	 *
	 * @param indice
	 *            int - índice da aba que será fechada
	 */
	private void fecharAba(int indice) {
		if (indice != -1 && lista.size() != 1) {
			if (lista.get(indice).arquivoModificado()) {
				int r = JOptionPane.showConfirmDialog(null,
						"Você deseja salvar o arquivo?", "Fechar",
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (r == JOptionPane.OK_OPTION) {
					lista.get(indice).setTexto(
							lista.get(indice).getRSyntax().getText());
					if (lista.get(indice).getArquivo() == null) {
						lista.get(indice).salvarComo();
					} else {
						lista.get(indice).salvar(
								lista.get(indice).getRSyntax().getText());
					}
				} else if (r == JOptionPane.CANCEL_OPTION) {
					return;
				}
			}

			if (lista.get(indice).getArquivo() != null
					&& !arquivosAbertos.isEmpty()) {
				arquivosAbertos.remove(lista.get(indice).getArquivo()
						.toString());
			}
			lista.remove(indice);
			arquivos.remove(indice);
		}
	}

	/**
	 * Chama o JDialog que contém informações sobre o JCE
	 */
	private ActionListener versaoListener = ev -> new VersaoDialog();

	/**
	 * Responsável pelo evento de recortar a seleção do JTextArea atual
	 */
	private ActionListener recortarListener = ev -> {
		lista.get(arquivos.getSelectedIndex()).getRSyntax().cut();
	};

	/**
	 * Responsável pelo evento de copiar a seleção do JTextArea atual
	 */
	private ActionListener copiarListener = ev -> {
		lista.get(arquivos.getSelectedIndex()).getRSyntax().copy();
	};

	/**
	 * Responsável pelo evento de colar da área de transferência o texto no
	 * JTextArea atual
	 */
	private ActionListener colarListener = ev -> {
		lista.get(arquivos.getSelectedIndex()).getRSyntax().paste();
	};

	/**
	 * Fecha o programa
	 */
	private ActionListener sairListener = ev -> salvarAoSair();

	/**
	 * chama o método que adiciona uma aba ao JTabbedPane.
	 */
	private ActionListener abrirListener = ev -> {
		if (lista.get(arquivos.getSelectedIndex()).fileChooser()
				.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
			return;
		}

		adicionarAba(lista.get(arquivos.getSelectedIndex()).fileChooser()
				.getSelectedFile());
	};

	/**
	 * Responsável por salvar o arquivo selecionado pelo usuário. Antes de
	 * salvar, é feita uma verificação para constatar se o arquivo foi ou não
	 * modificado, se sim, ele apenas salva levando em consideração o caminho do
	 * arquivo existente, caso contrário, o usuário deverá informar um caminho.
	 * Em seguida, é definido a linguagem do JLabel e o título do JFrame.
	 */
	private ActionListener salvarListener = ev -> {
		AreaDeTexto area = lista.get(arquivos.getSelectedIndex());
		area.setTexto(area.getRSyntax().getText());
		if (area.getArquivo() == null) {
			area.salvarComo();
			arquivosAbertos.add(area.getArquivo().toString());
		} else if (area.arquivoModificado()) {
			area.salvar(area.getRSyntax().getText());
		}

		arquivos.setTitleAt(arquivos.getSelectedIndex(), area.getArquivo()
				.getName());
		arquivos.setToolTipTextAt(arquivos.getSelectedIndex(), area
				.getArquivo().toString());
		linguagem.setText(area.getLinguagem() + "   ");
		definirTitulo();

		if (area.isPotigol()) {
			bExecutarPotigol.setEnabled(true);
		}

	};

	/**
	 * Responsável por salvar o arquivo mesmo se ele já existe ou não foi
	 * modificado (recurso "salvar como")
	 */
	private ActionListener salvarComoListener = ev -> {
		AreaDeTexto area = lista.get(arquivos.getSelectedIndex());
		area.setTexto(area.getRSyntax().getText());
		if (area.getArquivo() == null) {
			area.salvarComo();
			area.setArquivo(null);
			area.arquivoModificado(true);
			definirTitulo();
		} else {
			auxArquivo = area.getArquivo().toString();
			auxLinguagem = area.getLinguagem();

			File arquivoAnterior = new File(auxArquivo);
			area.salvarComo();
			area.setArquivo(arquivoAnterior);
			updateLanguage(auxLinguagem);
			area.extensao(arquivoAnterior);
		}
	};

	/**
	 * Cria uma aba com um JTextArea vazio. Também possui códigos para definição
	 * de sintaxe e nome da linguagem.
	 */
	private ActionListener novoListener = ev -> {
		at = new AreaDeTexto();
		lista.add(at);
		arquivos.addTab("Sem nome", at);
		arquivos.setSelectedIndex(lista.size() - 1);

		int i = arquivos.getSelectedIndex();
		arquivos.setTabComponentAt(i, new ButtonTabComponent(arquivos, lista,
				arquivosAbertos));

		lista.get(arquivos.getSelectedIndex()).getRSyntax()
				.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
		arquivos.setToolTipTextAt(arquivos.getSelectedIndex(), "Sem nome");
		linguagem.setText(lista.get(arquivos.getSelectedIndex()).getLinguagem()
				+ "   ");
		bg2.clearSelection();
		carregarTema(sTema);
	};

	/**
	 * Abre a JToolBar que realiza a pesquisa por palavras no JTextArea da aba
	 * atual
	 */
	private ActionListener pesquisarListener = ev -> {
		Pesquisar p = lista.get(arquivos.getSelectedIndex()).getBarraPesquisa();
		p.setVisible(true);
		p.getFieldPesquisar().requestFocus();
	};

	/**
	 * Abre o JDialog que mostra informações sobre o computador (SO,
	 * arquitetura, versão do Java, etc.)
	 */
	private ActionListener sobrePCListener = ev -> new PropriedadesSistema();

	/**
	 * Responsável pelo evento de definir a fonte como padrão (tamanho 12)
	 */
	private ActionListener fontePadraoListener = ev -> {
		tamanhoFonte = 12;
		updateFonte();
	};

	/**
	 * Responsável pelo evento de aumentar a fonte
	 */
	private ActionListener aumentarFonteListener = ev -> {
		tamanhoFonte += 3;
		updateFonte();
	};

	/**
	 * Responsável pelo evento de diminuir a fonte
	 */
	private ActionListener diminuirFonteListener = ev -> {
		if (tamanhoFonte > 9) {
			tamanhoFonte -= 3;
			updateFonte();
		}
	};

	/**
	 * Define o Look And Feel padrão do editor (Nimbus, mas que possui
	 * alterações na JScrollBar), a String "sLAF" é utilizada pela classe
	 * MainClass para carregar o LAF toda vez que o programa for iniciado. A
	 * classe PainterScrollBar é utilizada para criar a aparência barra.
	 */
	private ActionListener lafPadraoListener = ev -> {
		try {
			UIManager
					.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			UIManager.getLookAndFeelDefaults().put(
					"ScrollBar:ScrollBarThumb[Enabled].backgroundPainter",
					new PainterScrollBar(new Color(69, 69, 69)));
			UIManager.getLookAndFeelDefaults().put(
					"ScrollBar:ScrollBarThumb[MouseOver].backgroundPainter",
					new PainterScrollBar(new Color(69, 69, 69)));
			UIManager.getLookAndFeelDefaults().put(
					"ScrollBar:ScrollBarTrack[Enabled].backgroundPainter",
					new PainterScrollBar(new Color(39, 39, 39)));

			UIManager.getLookAndFeelDefaults().put(
					"ScrollBar:\"ScrollBar.button\".size", 0);
			UIManager.getLookAndFeelDefaults().put(
					"ScrollBar.decrementButtonGap", 0);
			UIManager.getLookAndFeelDefaults().put(
					"ScrollBar.incrementButtonGap", 0);
			atualizarLAF();
			sLAF = "jce";
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}
		updateFonte();
	};

	/**
	 * Utiliza uma String passada como argumento no construtor para definir o
	 * Look And Feel e depois atualiza o mesmo.
	 */

	private ActionListener lafListener(String laf) {
		return ev -> {
			try {
				UIManager.setLookAndFeel(laf);
				atualizarLAF();
			} catch (ClassNotFoundException | InstantiationException
					| IllegalAccessException | UnsupportedLookAndFeelException ex) {
				ex.printStackTrace();
			}
			sLAF = laf;
			updateFonte();
		};
	}

	/**
	 * Evento que chama o método abrirProjeto.
	 */
	private ActionListener addProjetoListener = ev -> abrirProjeto();

	/**
	 * Chama o método que remove o projeto selecionado na JTree.
	 */
	private ActionListener removerProjetoListener = ev -> adp.removerProjeto();

	/**
	 * Chama o método que mostra informações básicas sobre o projeto (nome,
	 * quantidade total de arquivos e tamanho).
	 */
	private ActionListener propriedadesProjetoListener = ev -> {
		adp.propriedadesProjeto();
	};

	/**
	 * Evento responsável por capturar a fonte escolhida pelo usuário e a
	 * aplicar aos JTextArea(s)
	 */
	private ActionListener escolherFonteListener = ev -> {
		Object[] nomesFonte = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames();
		String auxFonte = fonteEscolhida;

		fonteEscolhida = (String) JOptionPane.showInputDialog(JCEditor.this,
				"Escolha a fonte", "Fonte", JOptionPane.PLAIN_MESSAGE, null,
				nomesFonte, "");
		if (fonteEscolhida == null) {
			fonteEscolhida = auxFonte;
		} else {
			updateFonte();
		}
	};

	/**
	 * Responsável por carregar o tema e aplicá-lo aos JTextArea(s)
	 */
	private ActionListener temaListener(String nomeDoTema) {
		return ev -> {
			carregarTema(nomeDoTema);
			updateFonte();
		};
	}

	/**
	 * Responsável por definir o nome da linguagem (JLabel do canto inferior
	 * direito) e sintaxe do JTextArea da aba atual. Se o JRadioButtonMenuItem
	 * "gerarEstrutura" estiver selecionado e o arquivo ainda não existir, será
	 * gerada a estrutura básica de determinadas linguagens.
	 */
	private ActionListener linguagemListener(String nomeLinguagem,
			String sintaxe) {
		return ev -> {
			lista.get(arquivos.getSelectedIndex()).getRSyntax()
					.setSyntaxEditingStyle(sintaxe);
			updateLanguage(nomeLinguagem);

			if (lista.get(arquivos.getSelectedIndex()).getArquivo() == null
					&& gerarEstrutura.isSelected()) {
				new GerarEstrutura(lista.get(arquivos.getSelectedIndex())
						.getRSyntax(), nomeLinguagem);
			}

			bExecutarPotigol.setEnabled(false);
		};
	}

	/**
	 * Adiciona suporte à linguagem Portugol.
	 */
	private ActionListener portugolListener = ev -> {
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory
				.getDefaultInstance();
		atmf.putMapping("text/portugol", "com.cristian.PortugolTokenMaker");
		lista.get(arquivos.getSelectedIndex()).getRSyntax()
				.setSyntaxEditingStyle("text/portugol");
		updateLanguage("Portugol");

		if (lista.get(arquivos.getSelectedIndex()).getArquivo() == null
				&& gerarEstrutura.isSelected()) {
			new GerarEstrutura(lista.get(arquivos.getSelectedIndex())
					.getRSyntax(), "Portugol");
		}

		bExecutarPotigol.setEnabled(false);
	};

	/**
	 * Adicionar suporte à linguagem Potigol. Verifica se o arquivo existe e se
	 * o índice é um código em Potigol (através da variável "isPotigol"), em
	 * caso positivo, libera a execução do código.
	 */
	private ActionListener potigolListener = ev -> {
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory
				.getDefaultInstance();
		atmf.putMapping("text/potigol", "com.cristian.PotigolTokenMaker");
		lista.get(arquivos.getSelectedIndex()).getRSyntax()
				.setSyntaxEditingStyle("text/potigol");
		updateLanguage("Potigol");

		if (lista.get(arquivos.getSelectedIndex()).getArquivo() != null
				&& lista.get(arquivos.getSelectedIndex()).isPotigol()) {
			bExecutarPotigol.setEnabled(true);
		}
	};

	/**
	 * Executa o código em Potigol. Antes de mais nada é verificado se o arquivo
	 * foi modificado, em caso potivo o arquivo é salvo. Em seguida, verifica-se
	 * o sistema operacional para decidir qual arquivo de execução do Potigol
	 * irá ser executado. São necessários alguns pequenos ajustes dependendo do
	 * SO.
	 */
	private ActionListener executarPotigolListener = ev -> {

		if (lista.get(arquivos.getSelectedIndex()).arquivoModificado()) {
			lista.get(arquivos.getSelectedIndex()).salvar(
					lista.get(arquivos.getSelectedIndex()).getRSyntax()
							.getText());
			definirTitulo();
		}

		if (lista.get(arquivos.getSelectedIndex()).isPotigol()
				&& lista.get(arquivos.getSelectedIndex()).getArquivo() != null) {
			terminal.executarComando(lista.get(arquivos.getSelectedIndex())
					.getArquivo());

		}
	};

	/**
	 * Responsável por abrir o dialógo de impressão e realizá-la.
	 */
	private ActionListener imprimirPotigolListener = ev -> {
		try {
			lista.get(arquivos.getSelectedIndex()).getRSyntax().print();
		} catch (PrinterException ex) {
			ex.printStackTrace();
		}
	};

	/**
	 * Chama o método que fecha a aba atual.
	 */
	private ActionListener fecharAbaListener = ev -> {
		fecharAba(arquivos.getSelectedIndex());
	};

	/**
	 * Evento que abre a página da linguagem Potigol
	 */
	private ActionListener sobrePotigolListener = ev -> {
		try {
			Desktop.getDesktop().browse(URI.create("http://potigol.github.io"));
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null,
					"Não foi possível abrir a página.", "Erro",
					JOptionPane.ERROR_MESSAGE);
		}
	};

	/**
	 * Evento que define se o recurso de dobramento de código será ativado ou
	 * não.
	 */
	private ActionListener dobrarCodigoListener = ev -> {
		for (AreaDeTexto area : lista) {
			area.barraDeRolagem().setFoldIndicatorEnabled(
					dobrarCodigo.isSelected());
			area.getRSyntax().setCodeFoldingEnabled(dobrarCodigo.isSelected());
		}
	};

	/**
	 * Evento que define se o recurso de quebra de linha será ativado ou não.
	 */
	private ActionListener quebrarLinhaListener = ev -> {
		for (AreaDeTexto area : lista) {
			area.getRSyntax().setLineWrap(quebrarLinha.isSelected());
			area.getRSyntax().setWrapStyleWord(quebrarLinha.isSelected());
		}
	};

	/**
	 * Evento de desfaz a última ação no JTextArea atual.
	 */
	private ActionListener desfazerListener = ev -> {
		lista.get(arquivos.getSelectedIndex()).getRSyntax().undoLastAction();
	};

	/**
	 * Evento de refaz a última ação no JTextArea atual.
	 */
	private ActionListener refazerListener = ev -> {
		lista.get(arquivos.getSelectedIndex()).getRSyntax().redoLastAction();
	};

	/**
	 * Evento que faz a seleção de todo o texto na aba atual.
	 */
	private ActionListener selecionarTudoListener = ev -> {
		lista.get(arquivos.getSelectedIndex()).getRSyntax().selectAll();
	};
}
