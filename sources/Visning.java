import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import javax.swing.*;

public class Visning {
	private Kontroll kontroll;
	private final int LENGDEENHET = 50;
	private int stoerrelse;
	private JFrame vindu;
	private JPanel hovedpanel, rutenettpanel, nordpanel, soerpanel;
	private JLabel lengde, blank1, blank2;
	private JLabel[][] rutenett;
	private JButton opp, hoeyre, ned, venstre, avslutt;
	private HashMap<String, String> symboler = new HashMap<>();
	private HashMap<String, Color> forgrunnsfarger = new HashMap<>();
	private HashMap<String, Color> bakgrunnsfarger = new HashMap<>();

	public Visning(Kontroll kontroll) {
		this.kontroll = kontroll;

		try {
			UIManager.setLookAndFeel(
					UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			System.exit(1);
		}

		vindu = new JFrame("Slangespillet");
		vindu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		settSymbolerHodeKroppSkattBlank("o", "+", "$", " ");
		settForgrunnsfargerHodeKroppSkattBlank(Color.BLACK, Color.BLACK, Color.RED, Color.WHITE);
		settBakgrunnsfargerHodeKroppSkattBlank(Color.GREEN, Color.GREEN, Color.WHITE, Color.WHITE);

		// Initier rutenett
		stoerrelse = kontroll.hentStoerrelse();
		rutenett = new JLabel[stoerrelse][stoerrelse];
		for (int rad = 0; rad < stoerrelse; rad++) {
			for (int kolonne = 0; kolonne < stoerrelse; kolonne++) {
				rutenett[rad][kolonne] = new JLabel();
				rutenett[rad][kolonne].setBorder(BorderFactory.createLineBorder(Color.BLACK));
				rutenett[rad][kolonne].setFont(new Font(Font.MONOSPACED, Font.BOLD, LENGDEENHET / 2));
				rutenett[rad][kolonne].setPreferredSize(new Dimension(LENGDEENHET, LENGDEENHET));
				rutenett[rad][kolonne].setHorizontalAlignment(SwingConstants.CENTER);
				rutenett[rad][kolonne].setVerticalAlignment(SwingConstants.CENTER);
			}
		}

		// Initier lengdelabel
		lengde = new JLabel("0");
		lengde.setFont(new Font(Font.MONOSPACED, Font.BOLD, LENGDEENHET / 2));
		lengde.setPreferredSize(new Dimension(LENGDEENHET, LENGDEENHET));
		lengde.setHorizontalAlignment(SwingConstants.CENTER);
		lengde.setVerticalAlignment(SwingConstants.CENTER);

		// Initier blanke labels
		blank1 = new JLabel();
		blank1.setPreferredSize(new Dimension(LENGDEENHET, LENGDEENHET));
		blank2 = new JLabel();
		blank2.setPreferredSize(new Dimension(LENGDEENHET, LENGDEENHET));

		// Initier rutenettpanel
		rutenettpanel = new JPanel();
		rutenettpanel.setLayout(new GridLayout(stoerrelse, stoerrelse));
		for (int rad = 0; rad < stoerrelse; rad++) {
			for (int kolonne = 0; kolonne < stoerrelse; kolonne++) {
				rutenettpanel.add(rutenett[rad][kolonne]);
			}
		}

		// Initier resterende paneler
		hovedpanel = new JPanel();
		hovedpanel.setLayout(new BorderLayout());

		nordpanel = new JPanel();
		nordpanel.setLayout(new BorderLayout());

		soerpanel = new JPanel();
		soerpanel.setLayout(new BorderLayout());

		// Initier knapper
		avslutt = new JButton("X");
		avslutt.setFont(new Font(Font.MONOSPACED, Font.BOLD, LENGDEENHET / 2));
		avslutt.setPreferredSize(new Dimension(LENGDEENHET, LENGDEENHET));
		avslutt.setHorizontalAlignment(SwingConstants.CENTER);
		avslutt.setVerticalAlignment(SwingConstants.CENTER);
		avslutt.addActionListener(new Avslutt());

		opp = new JButton("↑"); // "\u2191"
		opp.setFont(new Font(Font.MONOSPACED, Font.BOLD, LENGDEENHET / 2));
		opp.setPreferredSize(new Dimension(LENGDEENHET * stoerrelse, LENGDEENHET));
		opp.setHorizontalAlignment(SwingConstants.CENTER);
		opp.setVerticalAlignment(SwingConstants.CENTER);
		opp.addActionListener(new SettRetning("opp"));

		hoeyre = new JButton("→"); // "\u2192"
		hoeyre.setFont(new Font(Font.MONOSPACED, Font.BOLD, LENGDEENHET / 2));
		hoeyre.setPreferredSize(new Dimension(LENGDEENHET, LENGDEENHET * stoerrelse));
		hoeyre.setHorizontalAlignment(SwingConstants.CENTER);
		hoeyre.setVerticalAlignment(SwingConstants.CENTER);
		hoeyre.addActionListener(new SettRetning("hoeyre"));

		ned = new JButton("↓"); // "\u2193"
		ned.setFont(new Font(Font.MONOSPACED, Font.BOLD, LENGDEENHET / 2));
		ned.setPreferredSize(new Dimension(LENGDEENHET * stoerrelse, LENGDEENHET));
		ned.setHorizontalAlignment(SwingConstants.CENTER);
		ned.setVerticalAlignment(SwingConstants.CENTER);
		ned.addActionListener(new SettRetning("ned"));

		venstre = new JButton("←"); // "\u2190"
		venstre.setFont(new Font(Font.MONOSPACED, Font.BOLD, LENGDEENHET / 2));
		venstre.setPreferredSize(new Dimension(LENGDEENHET, LENGDEENHET * stoerrelse));
		venstre.setHorizontalAlignment(SwingConstants.CENTER);
		venstre.setVerticalAlignment(SwingConstants.CENTER);
		venstre.addActionListener(new SettRetning("venstre"));

		// Sett sammen
		nordpanel.add(opp, BorderLayout.CENTER);
		nordpanel.add(lengde, BorderLayout.WEST);
		nordpanel.add(avslutt, BorderLayout.EAST);

		soerpanel.add(blank1, BorderLayout.WEST);
		soerpanel.add(blank2, BorderLayout.EAST);
		soerpanel.add(ned, BorderLayout.CENTER);

		hovedpanel.add(rutenettpanel, BorderLayout.CENTER);
		hovedpanel.add(nordpanel, BorderLayout.NORTH);
		hovedpanel.add(hoeyre, BorderLayout.EAST);
		hovedpanel.add(soerpanel, BorderLayout.SOUTH);
		hovedpanel.add(venstre, BorderLayout.WEST);

		vindu.add(hovedpanel);
		vindu.addKeyListener(new KnappeLytter());
		vindu.setFocusable(true);

		vindu.pack();
		vindu.setVisible(true);
	}

	public void tegnBrettet() {
		String[][] brettetsTilstand = kontroll.hentBrettetsTilstand();

		for (int rad = 0; rad < stoerrelse; rad++) {
			for (int kolonne = 0; kolonne < stoerrelse; kolonne++) {
				rutenett[rad][kolonne].setText(symboler.get(brettetsTilstand[rad][kolonne]));
				rutenett[rad][kolonne].setForeground(forgrunnsfarger.get(brettetsTilstand[rad][kolonne]));
				rutenett[rad][kolonne].setOpaque(true);
				rutenett[rad][kolonne].setBackground(bakgrunnsfarger.get(brettetsTilstand[rad][kolonne]));
			}
		}
		lengde.setText(Integer.toString(kontroll.hentSlangensLengde()));
	}

	private void settSymbolerHodeKroppSkattBlank(String hode, String kropp, String skatt, String blank) {
		symboler.put("hode", hode);
		symboler.put("kropp", kropp);
		symboler.put("skatt", skatt);
		symboler.put("blank", blank);
	}

	private void settForgrunnsfargerHodeKroppSkattBlank(Color hode, Color kropp, Color skatt, Color blank) {
		forgrunnsfarger.put("hode", hode);
		forgrunnsfarger.put("kropp", kropp);
		forgrunnsfarger.put("skatt", skatt);
		forgrunnsfarger.put("blank", blank);
	}

	private void settBakgrunnsfargerHodeKroppSkattBlank(Color hode, Color kropp, Color skatt, Color blank) {
		bakgrunnsfarger.put("hode", hode);
		bakgrunnsfarger.put("kropp", kropp);
		bakgrunnsfarger.put("skatt", skatt);
		bakgrunnsfarger.put("blank", blank);
	}

	class SettRetning implements ActionListener {
		private String retningnoekkel;

		public SettRetning(String retningnoekkel) {
			this.retningnoekkel = retningnoekkel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			kontroll.settNesteRetning(retningnoekkel);
		}
	}

	class Avslutt implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			kontroll.avslutt();
		}
	}

	class KnappeLytter implements KeyListener {
		@Override
		public void keyPressed(KeyEvent e) {

			if (e.getKeyCode() == KeyEvent.VK_UP) {
				kontroll.settNesteRetning("opp");
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				kontroll.settNesteRetning("hoeyre");
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				kontroll.settNesteRetning("ned");
			}

			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				kontroll.settNesteRetning("venstre");
			}

			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				kontroll.avslutt();
			}

		}

		@Override
		public void keyTyped(KeyEvent e) {
			return;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			return;

		}
	}

}