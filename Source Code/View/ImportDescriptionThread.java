//Thread che esegue l'import di una descrizione da file

	import org.semanticweb.owlapi.model.OWLAxiom;
	import java.io.File;
	import javax.swing.JOptionPane;
	import java.awt.Frame;

	public class ImportDescriptionThread implements Runnable
	{
		private Frame owner;
		private File file;
		private AlmondOntologyManager manager;
		private ProgressBar bar;

		ImportDescriptionThread(Frame owner, AlmondOntologyManager m, File f)
		{
			this.owner = owner;
			this.manager = m;
			this.file = f;
			this.bar = new ProgressBar();
			this.bar.setTitle("Loading file...");
			this.bar.setVisible(true);
		}

		public void run()
		{
			try
			{
				AlmondOntologyManager manager2 = new AlmondOntologyManager(this.file);
				OWLAxiom assertions[] = manager2.getABoxAxioms();
				this.manager.addAxioms(assertions);
				OntologyDescription description = this.manager.getDescription();
				MainFrame mf = (MainFrame) this.owner;
				mf.setDescription(description);
				this.bar.setVisible(false);
			}

			catch(Exception e)
			{
				JOptionPane.showMessageDialog(null,e.getMessage(),"Exception",JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}