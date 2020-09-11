//finestra di dialogo per visualizzare un messaggio in una Text Area

	import javax.swing.JDialog;
	import java.awt.Frame;
	import javax.swing.JTextArea;
	import javax.swing.JPanel;
	import javax.swing.JScrollPane;

	public class TextAreaDialog extends JDialog
	{
		String info; //informazione da visualizzare	

		public TextAreaDialog(Frame owner, String title, String info)
		{
			super(owner,title,true);
			this.info = info;
			setGui();
		}

		//imposta l'interfaccia grafica
		private void setGui()
		{
			JTextArea ta = new JTextArea(this.info);
			ta.setEditable(false);
			//ta.setLineWrap(true);
			JScrollPane taSP = new JScrollPane(ta);
			JPanel panel = new JPanel();
			panel.add(taSP);
			this.add(panel);
		}
	}