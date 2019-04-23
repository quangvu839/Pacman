import java.awt.EventQueue;
import javax.swing.JFrame;

public class Pacman extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			Pacman pac = new Pacman(); // start new game
			pac.setVisible(true); // make visible
		});

	}
	public Pacman() {
		startGame(); // start a game
	}
	
	private void startGame() {
		add(new Board()); // create board
		setTitle("Pacman");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(380,420); // original size
		setLocationRelativeTo(null);
	}

	

}
