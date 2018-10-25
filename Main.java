import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new UI(1));
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setResizable(false);
		frame.setVisible(true);

	}
}