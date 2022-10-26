public class Klokke extends Thread {

	private Kontroll kontroll;

	public Klokke(Kontroll kontroll) {
		this.kontroll = kontroll;
	}

	@Override
	public void run() {
		int tikkPerMinutt = (int) 60000 / kontroll.hentTikkPerMinutt();
		do {
			try {
				sleep(tikkPerMinutt);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (kontroll.oppdaterTilstand());
	}
}
