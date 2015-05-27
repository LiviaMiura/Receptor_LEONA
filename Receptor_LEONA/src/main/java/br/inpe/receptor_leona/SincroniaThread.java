package br.inpe.receptor_leona;

public class SincroniaThread extends Thread {

	private boolean dataReceived;
	private boolean conectado;
	private Receptor receptor;

	public SincroniaThread(boolean conectado, boolean dataReceived,
			Object dataSync, Receptor receptor) {
		this.conectado = conectado;
		this.dataReceived = dataReceived;
		this.receptor = receptor;
	}

	public boolean isConectado() {
		return conectado;
	}

	public void setConectado(boolean conectado) {
		this.conectado = conectado;
	}

	public void run() {
		try {
			System.out.println("antes do while");
			while (conectado) {
                            System.out.println("entrei mais um passo dentro1");
				if (!dataReceived) {
                               
					System.err.println("Esperando dados chegarem via RTP...");
				}
			}
			System.err.println("Nenhum dado via RTP foi recebido.");
			receptor.close();
		} catch (Exception e) {
		}
	}
}
