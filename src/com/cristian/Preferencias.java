package com.cristian;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
* Classe responsável por abrir o programa com as devidas configurações
* @author   Cristian Henrique (cristianmsbr@gmail.com)
* @version  1.9
* @since    Terceira atualização
*/

public class Preferencias {
	private final String HOME = System.getProperty("user.home") + "/ConfigJCE/configJCE.conf";
	private File arquivoConfig = new File(System.getProperty("user.home") + "/ConfigJCE");
	private JCEditor editor;

	/**
	* Verifica se os arquivos necessários existem, em caso negativo, cria os arquivos com
	* configurações padrão.
	*/
	public void verificar() {
		if (arquivoConfig.exists()) {
			abrirPreferencias();
		} else {
			try {
				arquivoConfig.mkdir();
				salvarPreferencias("jce", "jce", "Monospaced", 12, "dobrarCodigo", "quebrarLinha");
				abrirPreferencias();
			} catch (Exception ex) { ex.printStackTrace(); }
		}

		File pastaPotigol = new File(System.getProperty("user.home") + "/ConfigJCE/.potigol");
		File arquivoDeConfiguracoes = new File(System.getProperty("user.dir") + "/configPotigol.zip");
		if (!pastaPotigol.exists()) {
			pastaPotigol.mkdir();
			File exec = new File(pastaPotigol + "/potigol.jar");
			if (!exec.exists()) {
				descompactar(arquivoDeConfiguracoes, pastaPotigol);
			}
		}

		abrirArquivos();
		if (!editor.getArquivosAbertos().isEmpty()) {
			editor.configAoAbrir();
		}
		editor.setVisible(true);
	}

	/**
	* Abre os arquivos da última execução através do caminho que está salvo no arquivo "arquivos.list".
	*/
	public void abrirArquivos() {
		try {
			FileReader fr = new FileReader(new File(System.getProperty("user.home") + "/ConfigJCE/arquivos.list"));
			BufferedReader leitor = new BufferedReader(fr);
			String linha = null;

			while ((linha = leitor.readLine()) != null) {
				File f = new File(linha);
				editor.adicionarAba(f);
			}
			leitor.close();
		} catch (Exception ex) { ex.printStackTrace(); }
	}

	/**
	* Método responsável por carregar as preferências do usuário (Look And Feel), tema,
	* fonte e tamanho da mesma.
	*/
	public void abrirPreferencias() {
		try {
			FileReader fr = new FileReader(new File(HOME));
			BufferedReader leitor = new BufferedReader(fr);
			String linha = null;

			while ((linha = leitor.readLine()) != null) {
				String sub = linha.substring(0, 1);
				String conteudo = linha.substring(linha.indexOf(" ") + 1, linha.length());

				if (sub.equals("1")) {
					if (conteudo.equals("jce")) {
						lafJCE();
					} else {
						UIManager.setLookAndFeel(conteudo);
					}
					editor = new JCEditor();
					editor.sLAF = conteudo;

					switch (conteudo) {
						case "jce":
							editor.getMenusDeAparencia()[0].setSelected(true);
							break;
						case "javax.swing.plaf.nimbus.NimbusLookAndFeel":
							editor.getMenusDeAparencia()[1].setSelected(true);
							break;
						case "javax.swing.plaf.metal.MetalLookAndFeel":
							editor.getMenusDeAparencia()[2].setSelected(true);
							break;
						case "com.sun.java.swing.plaf.motif.MotifLookAndFeel":
							editor.getMenusDeAparencia()[4].setSelected(true);
							break;
						default:
							editor.getMenusDeAparencia()[3].setSelected(true);
					}
				}

				if (sub.equals("2")) {
					editor.sTema = conteudo;

					switch (conteudo) {
						case "jce":
							editor.getMenusDeAparencia()[5].setSelected(true);
							break;
						case "dark":
							editor.getMenusDeAparencia()[6].setSelected(true);
							break;
						case "darkii":
							editor.getMenusDeAparencia()[7].setSelected(true);
							break;
						case "default":
							editor.getMenusDeAparencia()[8].setSelected(true);
							break;
						case "default-alt":
							editor.getMenusDeAparencia()[9].setSelected(true);
							break;
						case "eclipse":
							editor.getMenusDeAparencia()[10].setSelected(true);
							break;
						case "idea":
							editor.getMenusDeAparencia()[11].setSelected(true);
							break;
						case "idle":
							editor.getMenusDeAparencia()[12].setSelected(true);
							break;
						case "vs":
							editor.getMenusDeAparencia()[13].setSelected(true);
					}
				}

				if (sub.equals("3")) {
					editor.setFonteEscolhida(conteudo);
				}

				if (sub.equals("4")) {
					editor.setTamanhoFonte(Integer.parseInt(conteudo));
				}

				if (sub.equals("5") && conteudo.equals("dobrarCodigo")) {
					editor.getDobrarCodigo().setSelected(true);
				}

				if (sub.equals("6") && conteudo.equals("quebrarLinha")) {
					editor.getQuebrarLinha().setSelected(true);
				}
			}
			editor.carregarTema(editor.sTema);
			editor.updateFonte();
		} catch (Exception ex) { ex.printStackTrace(); }
	}

	/**
	* Método responsável por salvar as preferências do usuário, este método é
	* chamado toda vez que o usuário fechar o programa.
	*/
	public void salvarPreferencias(String laf, String tema, String fonte, int tamFonte, String dobCodigo, String qLinha) {
		try {
			FileWriter fw = new FileWriter(HOME);
			fw.write("1 " + laf + "\n");
			fw.write("2 " + tema + "\n");
			fw.write("3 " + fonte + "\n");
			fw.write("4 " + tamFonte + "\n");
			fw.write("5 " + dobCodigo + "\n");
			fw.write("6 " + qLinha + "\n");
			fw.flush();
			fw.close();
		} catch (Exception ex) {  }
	}

	/**
	* Salva o caminho dos arquivos (contidos na ArrayList), para serem abertos novamente na próxima execução.
	*/
	public void salvarArquivosAbertos(List<String> lista) {
		try {
			FileWriter fw = new FileWriter(new File(System.getProperty("user.home") + "/ConfigJCE/arquivos.list"));
			for (String l : lista) {
				fw.write(l + "\n");
			}
			fw.flush();
			fw.close();
		} catch (Exception ex) {  }
	}

	/**
	* Método responsável por descompactar o arquivo de configurações do Potigol.
	* Se a descompactação for bem sucedida, será exibida uma mensagem dizendo isso.
	* Vale notar que em sistemas como o Linux, será necessária a primeira execução via
	* Terminal, utilizando o comando "java -jar JCEditor.jar". Em seguida, deve ser dada
	* a permissão para a execução do compilador do Potigol "chmod 777 ${HOME}/ConfigJCE/.potigol/potigol.jar".
	*/
	private static void descompactar(File arq, File dir) {
		ZipFile zip = null;
		File arquivo = null;
		InputStream is = null;
		OutputStream os = null;
		byte[] buffer = new byte[1024];

		try {
			zip = new ZipFile(arq);
			Enumeration<?> e = zip.entries();
			while (e.hasMoreElements()) {
				ZipEntry entrada = (ZipEntry) e.nextElement();
				arquivo = new File(dir, entrada.getName());

				if (entrada.isDirectory() && !arquivo.exists()) {
					arquivo.mkdirs();
					continue;
				}

				try {
					is = zip.getInputStream(entrada);
					os = new FileOutputStream(arquivo);
					int bytes = 0;

					while ((bytes = is.read(buffer)) > 0) {
						os.write(buffer, 0, bytes);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (Exception ex) { ex.printStackTrace(); }
					}

					if (os != null) {
						try {
							os.close();
						} catch (Exception ex) { ex.printStackTrace(); }
					}
				}
			}
			JOptionPane.showMessageDialog(null, "Potigol configurado com sucesso!");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (zip != null) {
				try {
					zip.close();
				} catch (Exception ex) { ex.printStackTrace(); }
			}
		}
	}

	/**
	* Configuração padrão da barra de rolagem para o LAF "JCE"
	*/
	private void lafJCE() {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

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
		} catch (Exception ex) { ex.printStackTrace(); }
	}
}