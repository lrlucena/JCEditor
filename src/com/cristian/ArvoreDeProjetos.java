package com.cristian;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * Classe responsável por criar a árvore de gerenciamento de projetos
 *
 * @author Cristian Henrique (cristianmsbr@gmail.com)
 * @version 1.8
 * @since Terceira atualização
 */
public class ArvoreDeProjetos extends JPanel {

	private File arq;
	private JTree arvore;
	private Map<String, String> arquivos = new HashMap<>();
	private final List<String> projetosList = new ArrayList<>();
	private final DefaultMutableTreeNode pai;
	private final File dir = new File(System.getProperty("user.home")
			+ "/ConfigJCE/projetos.list");
	private int ret;
	private int numArquivos;

	/**
	 * Cria o pai dos outros nós, e adiciona eventos de Drag and Drop.
	 */
	public ArvoreDeProjetos() {
		pai = new DefaultMutableTreeNode("root");
		arvore = new JTree(pai);
		arvore.setRootVisible(false);

		/*
		 * Caso o local clicado na JTree seja válido (!= null), o caminho do
		 * arquivo é adicionado a variável arq
		 */
		arvore.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {
				TreePath tp = arvore.getPathForLocation(ev.getX(), ev.getY());
				if (tp != null) {
					arq = new File(arquivos.get(arvore
							.getLastSelectedPathComponent().toString()));
				}
			}
		});

		/* Permite abrir um projeto apenas arrastando e soltando */
		DropTarget dt = new DropTarget(arvore, new DropTargetListener() {
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
					@SuppressWarnings("unchecked")
					List<File> lista2 = (List<File>) ev.getTransferable()
							.getTransferData(DataFlavor.javaFileListFlavor);
					for (File arquivoD : lista2) {
						if (arquivoD.isDirectory()) {
							adicionarFilhos(arquivoD);
						}
					}
				} catch (UnsupportedFlavorException | IOException ex) {
				}
			}
		});

		listarProjetos();
		this.setLayout(new BorderLayout());
		this.add(arvore, BorderLayout.CENTER);
		this.add(arvore);
	}

	/**
	 * Insere o nó informado na JTree e adiciona o caminho do diretório na
	 * ArrayList de projetos.
	 *
	 * @param diretorio
	 *            File - projeto a ser adicionado
	 */
	public void adicionarFilhos(File diretorio) {
		DefaultMutableTreeNode filho = new DefaultMutableTreeNode(
				diretorio.getName());
		pai.add(filho);
		((DefaultTreeModel) arvore.getModel()).insertNodeInto(filho, pai, 0);
		((DefaultTreeModel) arvore.getModel()).reload();
		listarArquivos(diretorio.toString(), filho, true);
		projetosList.add(diretorio.toString());
		repaint();
	}

	/**
	 * Remove o nó selecionado da JTree e da ArrayList de projetos.
	 */
	public void removerProjeto() {
		DefaultMutableTreeNode selecao = (DefaultMutableTreeNode) arvore
				.getLastSelectedPathComponent();
		if (selecao == null) {
			JOptionPane.showMessageDialog(null, "Selecione um projeto.");
			return;
		}

		((DefaultTreeModel) arvore.getModel()).removeNodeFromParent(selecao);
		String remProj = null;

		for (String s : projetosList) {
			if (s.contains(selecao.toString())) {
				remProj = s;
			}
		}

		projetosList.remove(remProj);
	}

	/**
	 * Informa algumas propriedades do projeto selecionado (nome, tamanho
	 * aproximado e quantidade total de arquivos e diretórios).
	 */
	public void propriedadesProjeto() {
		DefaultMutableTreeNode selecao = (DefaultMutableTreeNode) arvore
				.getLastSelectedPathComponent();
		if (selecao == null) {
			JOptionPane.showMessageDialog(null, "Selecione um projeto.");
			return;
		}

		for (String s : projetosList) {
			if (s.contains(selecao.toString())) {
				tamanho(new File(s));
				String str = String.format("%.2f MB", ret / 1_048_576.0);
				JOptionPane.showMessageDialog(null,
						"Nome: " + selecao.toString() + "\nTamanho: " + str
								+ "\n" + "Total de arquivos: " + numArquivos,
						"Propriedades", JOptionPane.PLAIN_MESSAGE);
				numArquivos = 0;
			}
		}
	}

	/**
	 * Retorna o tamanho aproximado do diretório informado e também pega a
	 * quantidade de arquivos (armazenada em numArquivos).
	 *
	 * @param dir
	 *            File - diretório que será analisado
	 */
	public long tamanho(File dir) {
		ret = 0;
		numArquivos += dir.listFiles().length;

		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				ret += tamanho(f);
			} else {
				ret += f.length();
			}
		}
		return ret;
	}

	/**
	 * Método que lista os projetos salvos (os caminhos são salvos no arquivo
	 * projetos.list), este método é chamado toda vez que o programa é iniciado.
	 */
	public void listarProjetos() {
		try {
			FileReader fr = new FileReader(dir);
			try (BufferedReader leitor = new BufferedReader(fr)) {
				String linha = leitor.readLine();

				while (linha != null) {
					adicionarFilhos(new File(linha));
					linha = leitor.readLine();
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Salva o caminho dos projetos abertos, este método é chamado antes de o
	 * programa ser fechado.
	 */
	public void salvarProjetos() {
		try (FileWriter fw = new FileWriter(dir)) {
			for (String s : projetosList) {
				fw.write(s + "\n");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Lista os arquivos do diretório informado e os adiciona a um nó na JTree.
	 *
	 * @param caminho
	 *            String - local do diretório a ser listado
	 * @param principal
	 *            DefaultMutableTreeNode - nó no qual serão adicionados os
	 *            arquivos.
	 * @param recursivo
	 *            Boolean - permite listar os arquivos de forma recursiva
	 */
	public void listarArquivos(String caminho,
			DefaultMutableTreeNode principal, boolean recursivo) {
		File[] filhos = new File(caminho).listFiles();

		for (File filho : filhos) {
			DefaultMutableTreeNode no = new DefaultMutableTreeNode(
					filho.getName());
			arquivos.put(filho.getName(), filho.toString());
			if (filho.isDirectory() && recursivo) {
				principal.add(no);
				listarArquivos(filho.getPath(), no, recursivo);
			} else if (!filho.isDirectory()) {
				principal.add(no);
			}
		}
	}

	/**
	 * Acessa o conteúdo do objeto File.
	 */
	public File getArq() {
		return this.arq;
	}

	/**
	 * Acessa o conteúdo da JTree.
	 */
	public JTree getArvore() {
		return this.arvore;
	}
}
