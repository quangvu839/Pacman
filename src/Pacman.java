import java.awt.EventQueue;
import javax.swing.JFrame;

public class Pacman extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Pacman() {
		initUI();
	}
	
	private void initUI() {
		add(new Board());
		setTitle("Pacman");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(380,420);
		setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			Pacman pac = new Pacman();
			pac.setVisible(true);
		});

	}

}
