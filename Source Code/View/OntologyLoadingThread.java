//Thread per il caricamento dell'ontologia da file esterno

	//import delle classi necessarie
	import java.awt.Toolkit;
	import java.awt.Dimension;
	import javax.swing.JFrame;
	import java.awt.Frame;
	import javax.swing.JOptionPane;
	import javax.swing.JFileChooser;
	import java.io.File;

	public class OntologyLoadingThread implements Runnable
	{
		private Frame owner;
		private File file;

		public OntologyLoadingThread(Frame owner)
		{
			this.owner = owner;
			this.file = null;
		}

		public OntologyLoadingThread(Frame owner, File f)
		{
			this.owner = owner;
			this.file = f;
		}		

		public void run()
		{
			try
			{
				MainFrame frame = null;
				if (this.file == null)
					frame = new MainFrame();
				else
					frame = new MainFrame(this.file);
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				int screenWidth = (int) screenSize.getWidth();
				int screenHeight = (int) screenSize.getHeight();
				int width = (int) (screenWidth * 0.8);
				int height = (int) (screenHeight * 0.8);
				frame.setSize(width,height);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				this.owner.setVisible(false);
				frame.setVisible(true);
				frame.setLocationRelativeTo(null);
			}

			catch(Exception e)
			{
				if (e.getClass().getName().equals("org.semanticweb.owlapi.io.OWLOntologyInputSourceException"))
				{
					JOptionPane.showMessageDialog(null,"Source file not found. You have load " + 
					"ontology manually!!!","No Source File",JOptionPane.WARNING_MESSAGE);
					this.manuallyLoading();
				}
				else
					JOptionPane.showMessageDialog(null,e.getMessage(),"Exception",JOptionPane.ERROR_MESSAGE);
			}
		}

		//caricamento manuale dell'ontologia
		private void manuallyLoading()
		{
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fc.setMultiSelectionEnabled(false);
			int result = fc.showOpenDialog(this.owner);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				OntologyLoadingThread olt = new OntologyLoadingThread(this.owner,fc.getSelectedFile());
				Thread t = new Thread(olt);
				t.start();	
			}
			else
			{
				JOptionPane.showMessageDialog(null,"The application will be terminated",
				"No Source File selected",JOptionPane.WARNING_MESSAGE);
				System.exit(0);
			}
		}

	}