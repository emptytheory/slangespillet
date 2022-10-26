public class Kontroll {
	private Modell modell;
	private Visning visning;
	private int[] programargumenter = new int[2];

	public Kontroll(String[] programargumenter) {
		this.programargumenter[0] = tikkPerMinutt(programargumenter);
		this.programargumenter[1] = stoerrelse(programargumenter);
		visning = new Visning(this);
		modell = new Modell(visning, this.programargumenter);
	}

	public void settNesteRetning(String retningnoekkel) throws IllegalArgumentException {
		modell.settNesteRetning(retningnoekkel);
	}

	public void avslutt() {
		System.exit(0);
	}

	public boolean oppdaterTilstand() {
		return modell.oppdaterTilstand();
	}

	public String[][] hentBrettetsTilstand() {
		return modell.hentBrettetsTilstand();
	}

	public int hentSlangensLengde() {
		return modell.hentSlangensLengde();

	}

	public int hentTikkPerMinutt() {
		return programargumenter[0];
	}

	public int hentStoerrelse() {
		return programargumenter[1];
	}

	public void start() {
		Klokke klokketraad = new Klokke(this);
		visning.tegnBrettet();
		klokketraad.start();
	}

	private int tikkPerMinutt(String[] programargumenter) {
		return positivtHeltallPaaIndeks(programargumenter, 0)
				? Integer.parseInt(programargumenter[0])
				: 30;
	}

	private int stoerrelse(String[] programargumenter) {
		return positivtHeltallPaaIndeks(programargumenter, 1)
				? Math.min(20, Math.max(4, Integer.parseInt(programargumenter[1])))
				: 12;
	}

	@SuppressWarnings("unused")
	private boolean positivtHeltallPaaIndeks(String[] kanskjeTallarray, int indeks) {
		if (kanskjeTallarray.length <= indeks) {
			return false;
		}

		try {
			int i = Integer.parseInt(kanskjeTallarray[indeks]);
		} catch (NumberFormatException u) {
			return false;
		}

		if (Integer.parseInt(kanskjeTallarray[indeks]) < 0) {
			return false;
		}

		return true;
	}

}
