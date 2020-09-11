
	
	import javax.swing.JFrame;
	import javax.swing.JPanel;
	import javax.swing.JProgressBar;

	public class ProgressBar extends JFrame
	{

           public ProgressBar()
           {
              this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
              this.setResizable(false);
              final JProgressBar pb = new JProgressBar();
              pb.setIndeterminate(true);
              JPanel panel = new JPanel();
              panel.add(pb);
              this.add(panel);	
	      this.setSize(500,100);
              this.setLocationRelativeTo(null);
  
           }

	}