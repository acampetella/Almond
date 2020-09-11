//Frame iniziale dell'applicazione

	import java.awt.Graphics;
	import java.awt.Image;
	import java.awt.MediaTracker;
	import java.awt.Toolkit;
	import javax.swing.JFrame;
	import javax.swing.JPanel;
	import java.awt.Dimension;


	public class InitialFrame extends JFrame
	{
		public InitialFrame()
		{
			super("Loading Almond Ontology...");
			//modifica il font di default
			new ChangeFontSize(16);
			JPanel panel = new ImagePanel();
			this.add(panel);
		}

		public static void main(String args[])
		{
			int width = 1440;
			int height = 810;
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int screenWidth = (int) screenSize.getWidth();
			int screenHeight = (int) screenSize.getHeight();
			if (screenWidth < width)
				width = screenWidth;
			if (screenHeight < height)
				height = screenHeight;
			InitialFrame frame = new InitialFrame();
			frame.setSize(width,height);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			frame.setResizable(false);
			frame.setLocationRelativeTo(null);
			OntologyLoadingThread olt = new OntologyLoadingThread(frame);
			Thread t = new Thread(olt);
			t.start();
		}

		//classe cle implementa un JPanel con un'immagine di sfondo
		private class ImagePanel extends JPanel 
		{
			private Image img;

  			public ImagePanel() 
			{
    				this.img = Toolkit.getDefaultToolkit().createImage(this.getClass().getResource("little-owl.jpg"));
    				this.loadImage(this.img);
  			}

  			private void loadImage(Image img) 
			{
   	 			try 
				{
      					MediaTracker track = new MediaTracker(this);
      					track.addImage(img, 0);
      					track.waitForID(0);
    				} 

				catch (InterruptedException e) 
				{
      					e.printStackTrace();
    				}
  			}

  			protected void paintComponent(Graphics g) 
			{
    				this.setOpaque(false);
    				g.drawImage(this.img, 0, 0, null);
    				super.paintComponent(g);
  			}

  		}
	}