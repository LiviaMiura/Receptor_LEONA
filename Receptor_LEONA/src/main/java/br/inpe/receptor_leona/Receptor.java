package br.inpe.receptor_leona;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.media.control.BufferControl;
import javax.media.protocol.DataSource;
import javax.media.rtp.Participant;
import javax.media.rtp.RTPControl;
import javax.media.rtp.RTPManager;
import javax.media.rtp.ReceiveStream;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.SessionListener;
import javax.media.rtp.event.ByeEvent;
import javax.media.rtp.event.NewParticipantEvent;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.event.RemotePayloadChangeEvent;
import javax.media.rtp.event.SessionEvent;
import javax.media.rtp.event.StreamMappedEvent;

public class Receptor implements ReceiveStreamListener, SessionListener,
		ControllerListener {
	RTPManager mgrs[] = null;
	boolean dataReceived = false;
	Object dataSync = new Object();
	VideoFrame playerWindow = null;
	ReceptorGUI gui;
	SincroniaThread thread;
	private Socket client = null;
	private DataOutputStream out = null;

	public SincroniaThread getThread() {
		return thread;
	}

	public ReceptorGUI getGui() {
		return gui;
	}

	public Receptor(VideoFrame playerWindow, ReceptorGUI gui) {
		this.playerWindow = playerWindow;
		this.gui = gui;
	}

	protected boolean initialize() {
		try {
			client = new Socket(gui.getServidor(), Integer.parseInt(gui
					.getPorta()));
			out = new DataOutputStream(client.getOutputStream());
			out.writeInt(1);

			InetAddress ipAddr;
			SessionAddress localAddr = new SessionAddress();
			SessionAddress destAddr;
			mgrs = new RTPManager[1];
			SessionLabel sessionLabel;
			try {
				sessionLabel = new SessionLabel(gui.getServidor() + "/"
						+ gui.getPortaRTP());
			} catch (IllegalArgumentException e) {
				System.err.println("Falha ao ler endereço do stream: "
						+ gui.getServidor() + ":" + gui.getPortaRTP());
				return false;
			}
			System.err.println("Sess�o de RTP aberta para:" + sessionLabel.addr
					+ " porta: " + sessionLabel.port + " ttl: "
					+ sessionLabel.ttl);
			mgrs[0] = (RTPManager) RTPManager.newInstance();
			mgrs[0].addSessionListener(this);
			mgrs[0].addReceiveStreamListener(this);
			ipAddr = InetAddress.getByName(sessionLabel.addr);
			if (ipAddr.isMulticastAddress()) {
				localAddr = new SessionAddress(ipAddr, sessionLabel.port,
						sessionLabel.ttl);
				destAddr = new SessionAddress(ipAddr, sessionLabel.port,
						sessionLabel.ttl);
			} else {
				localAddr = new SessionAddress(InetAddress.getLocalHost(),
						sessionLabel.port);
				destAddr = new SessionAddress(ipAddr, sessionLabel.port);
			}
			mgrs[0].initialize(localAddr);
			BufferControl bc = (BufferControl) mgrs[0]
					.getControl("javax.media.control.BufferControl");
			if (bc != null)
				bc.setBufferLength(350);
			mgrs[0].addTarget(destAddr);
		} catch (Exception e) {
			System.err.println("Não foi possivel criar a conexão RTP: "
					+ e.getMessage());
			return false;
		}
		thread = new SincroniaThread(true, true, dataSync, this);
		return false;
	}

	public void close() {
		for (int i = 0; i < mgrs.length; i++) {
			if (mgrs[i] != null) {
				mgrs[i].removeTargets("Fechando sessão com o servidor.");
				mgrs[i].dispose();
				mgrs[i] = null;
			}
		}
	}

	public synchronized void update(SessionEvent evt) {
		System.out.println("1� update");
		if (evt instanceof NewParticipantEvent) {
			Participant p = ((NewParticipantEvent) evt).getParticipant();
			System.err.println("Um novo participante foi detectado.1111: "
					+ p.getCNAME());
		}
	}

	public synchronized void update(ReceiveStreamEvent evt) {
		System.out.println("2� update");
		Participant participant = evt.getParticipant();
		ReceiveStream stream = evt.getReceiveStream();
		System.err.println("Um novo participante foi detectado.222: ");
		if (evt instanceof RemotePayloadChangeEvent) {
			System.err.println("  - Recebido um RTP PayloadChangeEvent.");
			System.err.println("Não foi possível carregar mudanças.");
			System.exit(0);
		} else if (evt instanceof NewReceiveStreamEvent) {
			try {
				stream = ((NewReceiveStreamEvent) evt).getReceiveStream();
				DataSource ds = stream.getDataSource();
				RTPControl ctl = (RTPControl) ds
						.getControl("javax.media.rtp.RTPControl");
				if (ctl != null) {
					System.err.println("Recebido um novo RTP stream: "
							+ ctl.getFormat());
				} else
					System.err.println("Recebido um novo RTP stream");
				if (participant == null)
					System.err
							.println("O servido desse stream ainda n�o foi identificado.");
				else {
					System.err.println("O stream vem de:"
							+ participant.getCNAME());
				}
				Player p = javax.media.Manager.createPlayer(ds);
				if (p == null)
					return;
				p.addControllerListener(this);
				p.realize();
				playerWindow = new VideoFrame(p, stream, this);
				synchronized (dataSync) {
					dataReceived = true;
					dataSync.notifyAll();
				}
			} catch (Exception e) {
				System.err.println("NewReceiveStreamEvent exception "
						+ e.getMessage());
				return;
			}
		} else if (evt instanceof StreamMappedEvent) {
			if (stream != null && stream.getDataSource() != null) {
				DataSource ds = stream.getDataSource();
				RTPControl ctl = (RTPControl) ds
						.getControl("javax.media.rtp.RTPControl");
				System.err.println("  - The previously unidentified stream ");
				if (ctl != null)
					System.err.println("      " + ctl.getFormat());
				System.err.println("      had now been identified as sent by: "
						+ participant.getCNAME());
			}
		} else if (evt instanceof ByeEvent) {
			System.err.println("Conex�o terminada por: "
					+ participant.getCNAME());
			playerWindow.close();
		}
	}

	public synchronized void controllerUpdate(ControllerEvent ce) {
		System.out.println("1� controllerUpdate");
		playerWindow.setPlayer((Player) ce.getSourceController());
		if (playerWindow.getPlayer() == null)
			return;
		if (ce instanceof RealizeCompleteEvent) {
			System.err.println("tentando inciar player");
			playerWindow.initialize();
			playerWindow.getPlayer().start();
		}
		if (ce instanceof ControllerErrorEvent) {
			playerWindow.getPlayer().removeControllerListener(this);
			playerWindow.close();
			System.err.println("Erro interno ao abrir player: " + ce);
		}
	}

	public void enviaComando(String string) {
		if (client != null && out != null) {
			try {
				out.writeBytes(string);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}