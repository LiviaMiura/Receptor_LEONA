package br.inpe.receptor_leona;

import java.awt.BorderLayout;

import javax.media.Player;
import javax.media.rtp.ReceiveStream;
import javax.swing.JPanel;

public class VideoFrame {

	Player player;
	ReceiveStream stream;
	Receptor receptor;

	VideoFrame(Player player, ReceiveStream strm, Receptor receptor) {
		this.player = player;
		this.stream = strm;
		this.receptor = receptor;
	}

	public void initialize() {
		System.out.println("inicio video frame");
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		if (player.getVisualComponent() != null) {
			panel.add("Center", player.getVisualComponent());
		}
		receptor.getGui().getPainelCentral().add(panel, BorderLayout.EAST);
		receptor.getGui().pack();
	}

	public void close() {
		player.close();
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}