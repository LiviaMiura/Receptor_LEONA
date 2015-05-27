package br.inpe.receptor_leona;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ReceptorGUI extends JFrame {
	public static VideoFrame playerWindow;
	public static Receptor receptor = null;
	public static String session;
	public ReceptorGUI gui;
	public JPanel painelCentral;
	private final JTextField servidor;
	private final JTextField porta;
	private final JTextField portaRTP;

	public JPanel getPainelCentral() {
		return painelCentral;
	}

	public static void main(String args[]) {
		ReceptorGUI transmissorGUI = new ReceptorGUI();
		transmissorGUI.setSize(340, 330);
		transmissorGUI.setVisible(true);
                
                
                
                
                
                
	}

	public ReceptorGUI() {
		gui = this;
		setTitle("Receptor");
		JPanel painelMeio = new JPanel();
		JPanel painelServer = new JPanel();
		painelServer
				.setLayout(new BoxLayout(painelServer, BoxLayout.LINE_AXIS));
		JLabel nomeServer = new JLabel("Servidor: ");
		servidor = new JTextField();
		servidor.setText("150.163.67.80");
		porta = new JTextField();
		porta.setText("1234");
		painelServer.add(nomeServer);
		painelServer.add(servidor);
		JLabel nomePortaRTP = new JLabel("Porta RTP: ");
		painelServer.add(nomePortaRTP);
		portaRTP = new JTextField();
		portaRTP.setText("1235");
		painelServer.add(portaRTP);
		JLabel nomePorta = new JLabel("Porta TCP: ");
		painelServer.add(nomePorta);
		painelServer.add(porta);
                
		painelCentral = new JPanel();
		JPanel painelBotao = new JPanel();
		painelBotao.setLayout(new BoxLayout(painelBotao, BoxLayout.LINE_AXIS));
                
		final JButton iniciar = new JButton("Conectar");
		iniciar.setEnabled(true);
                
		final JButton parar = new JButton("Desconectar");
		parar.setEnabled(false);
                
                
		iniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				receptor = new Receptor(playerWindow, gui);
				if (!receptor.initialize()) {
					System.out.println("iniciou receptor********");
					iniciar.setEnabled(false);
					parar.setEnabled(true);
					receptor.getThread().setConectado(true);
					receptor.getThread().start();

				}
			}
		});
		parar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				receptor.getThread().setConectado(false);
				iniciar.setEnabled(true);
				parar.setEnabled(false);
				getPainelCentral().removeAll();
			}
		});
		painelBotao.add(iniciar);
		painelBotao.add(parar);

		JPanel painelComandos = new JPanel();
		painelComandos
				.setLayout(new BoxLayout(painelComandos, BoxLayout.Y_AXIS));
		JPanel painelComandosSuperior = new JPanel();
		JPanel painelComandosMeio = new JPanel();
		JPanel painelComandosInferior = new JPanel();
		JButton cima = new JButton();
		cima.setText("cima");
		cima.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (receptor != null) {
					receptor.enviaComando("cima");
				}
			}
		});
		painelComandosSuperior.add(cima);
		painelComandos.add(painelComandosSuperior);
		JButton esquerda = new JButton();
		esquerda.setText("esquerda");
		esquerda.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (receptor != null) {
					receptor.enviaComando("esquerda");
				}
			}
		});
		painelComandosMeio.add(esquerda);
		JButton direita = new JButton();
		direita.setText("direita");
		direita.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (receptor != null) {
					receptor.enviaComando("direita");
				}
			}
		});
		painelComandosMeio.add(direita);
		painelComandos.add(painelComandosMeio);
		JButton baixo = new JButton();
		baixo.setText("baixo");
		baixo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (receptor != null) {
					receptor.enviaComando("baixo");
				}
			}
		});
		painelComandosInferior.add(baixo);
		painelComandos.add(painelComandosInferior);
		painelMeio.add(painelCentral);
		painelMeio.add(painelComandos);
		getContentPane().add(painelServer, BorderLayout.PAGE_START);
		getContentPane().add(painelMeio, BorderLayout.CENTER);
		getContentPane().add(painelBotao, BorderLayout.PAGE_END);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public String getServidor() {
		return servidor.getText();
	}

	public String getPorta() {
		return porta.getText();
	}

	public String getPortaRTP() {
		return portaRTP.getText();
	}

}
