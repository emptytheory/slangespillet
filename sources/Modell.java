import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

// All forandring av spillets tilstand skjer gjennom kall på metoder definert i denne klassen.
// Og det er kun metoder som er definert i Kontroll som skal kalle på disse.
public class Modell {
	private Visning visning;
	private String[][] rutematrise;
	private HashSet<String> rutetilstander = new HashSet<String>(Arrays.asList("blank", "skatt", "kropp", "hode"));
	private HashMap<String, int[]> retninger = new HashMap<String, int[]>();
	private int[] naavaerendeRetning = { 0, 0 };
	private int[] nesteRetning = { 0, 0 };
	private ArrayList<int[]> slangensKoordinater;
	private int lengde = 1;
	private int tikkPerMinutt;
	private int stoerrelse;

	public Modell(Visning visning, int[] programargumenter) {
		this.visning = visning;

		lesProgramargumenter(programargumenter);
		fyllRetninger();
		initierRutematrise();
		settStartrute(stoerrelse / 2, stoerrelse / 2);
		settNesteRetning("hoeyre");
		genererSkatter(10);
	}

	public boolean oppdaterTilstand() {
		naavaerendeRetning = nesteRetning;
		int[] hodetsGamlePosisjon = slangensKoordinater.get(lengde - 1);
		String nabotilstand = nabotilstand(hodetsGamlePosisjon, nesteRetning);

		if (!"blank".equals(nabotilstand) && !"skatt".equals(nabotilstand))
			return false;

		int[] hodetsNyePosisjon = nabokoordinater(hodetsGamlePosisjon, nesteRetning);
		settRute(hodetsNyePosisjon, "hode");
		settRute(hodetsGamlePosisjon, "kropp");
		slangensKoordinater.add(nabokoordinater(hodetsGamlePosisjon, nesteRetning));

		if ("blank".equals(nabotilstand)) {
			int[] halensPosisjon = slangensKoordinater.remove(0);
			settRute(halensPosisjon, "blank");
		} else {
			genererSkatter(1);
		}
		lengde = slangensKoordinater.size();
		visning.tegnBrettet();
		return true;
	}

	public void settNesteRetning(String retningnoekkel) throws IllegalArgumentException {
		if (!retninger.keySet().contains(retningnoekkel))
			throw new IllegalArgumentException("'" + retningnoekkel + "'" + " er ikke en gyldig retning.");

		int[] nyRetning = retninger.get(retningnoekkel);

		if (!(naavaerendeRetning[0] + nyRetning[0] == 0 && naavaerendeRetning[1] + nyRetning[1] == 0)) {
			this.nesteRetning = nyRetning;
		}
	}

	public int hentSlangensLengde() {
		return lengde;
	}

	public String[][] hentBrettetsTilstand() {
		return rutematrise;
	}

	public int hentTikkPerMinutt() {
		return tikkPerMinutt;
	}

	public int hentStoerrelse() {
		return stoerrelse;
	}

	private void settRute(int[] koordinater, String tilstand) throws IllegalArgumentException {
		if (koordinater[0] < 0 || koordinater[0] >= stoerrelse)
			throw new IllegalArgumentException("Ingen rad med indeks " + koordinater[0] + ".");
		if (koordinater[1] < 0 || koordinater[1] >= stoerrelse)
			throw new IllegalArgumentException("Ingen kolonne med indeks " + koordinater[1] + ".");
		if (!rutetilstander.contains(tilstand))
			throw new IllegalArgumentException("'" + tilstand + "'" + " er ikke en gyldig tilstand.");

		rutematrise[koordinater[0]][koordinater[1]] = tilstand;
	}

	private void settStartrute(int rad, int kolonne) {
		slangensKoordinater = new ArrayList<int[]>(Arrays.asList(new int[] { rad, kolonne }));
		settRute(slangensKoordinater.get(0), "hode");
	}

	private void fyllRetninger() {
		retninger.put("opp", new int[] { -1, 0 });
		retninger.put("hoeyre", new int[] { 0, 1 });
		retninger.put("ned", new int[] { 1, 0 });
		retninger.put("venstre", new int[] { 0, -1 });
	}

	private void initierRutematrise() {
		rutematrise = new String[stoerrelse][stoerrelse];
		for (int rad = 0; rad < stoerrelse; rad++) {
			for (int kolonne = 0; kolonne < stoerrelse; kolonne++) {
				rutematrise[rad][kolonne] = "blank";
			}
		}
	}

	private void genererSkatter(int antall) {
		if (antall > stoerrelse * stoerrelse)
			antall = stoerrelse * stoerrelse;

		int rad = trekk(0, stoerrelse - 1);
		int kolonne = trekk(0, stoerrelse - 1);

		for (int i = 0; i < antall; i++) {
			while (!rutematrise[rad][kolonne].equals("blank")) {
				rad = trekk(0, stoerrelse - 1);
				kolonne = trekk(0, stoerrelse - 1);
			}
			settRute(new int[] { rad, kolonne }, "skatt");
		}
	}

	private int[] nabokoordinater(int[] posisjon, int[] retning) {
		int[] nabokoordinater = { posisjon[0] + retning[0], posisjon[1] + retning[1] };

		if (nabokoordinater[0] < 0 || nabokoordinater[0] >= stoerrelse ||
				nabokoordinater[1] < 0 || nabokoordinater[1] >= stoerrelse)
			return null;

		return nabokoordinater;
	}

	private String nabotilstand(int[] posisjon, int[] retning) {
		if (nabokoordinater(posisjon, retning) == null)
			return null;

		int rad = nabokoordinater(posisjon, retning)[0];
		int kolonne = nabokoordinater(posisjon, retning)[1];
		return rutematrise[rad][kolonne];
	}

	private void lesProgramargumenter(int[] programargumenter) {
		tikkPerMinutt = programargumenter[0];
		stoerrelse = programargumenter[1];
	}

	private int trekk(int a, int b) {
		return (int) (Math.random() * (b - a + 1)) + a;
	}
}
