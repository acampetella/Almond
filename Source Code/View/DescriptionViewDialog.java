//finestra di dialogo per visualizzare gli individui di una descrizione

	import javax.swing.JDialog;
	import java.awt.Frame;
	import javax.swing.JPanel;
	import javax.swing.JScrollPane;
	import javax.swing.JList;
	import javax.swing.JButton;
	import java.awt.BorderLayout;
	import javax.swing.ListSelectionModel;
	import java.awt.event.ActionListener;
	import java.awt.event.ActionEvent;
	import javax.swing.JOptionPane;

	public class DescriptionViewDialog extends JDialog
	{
		private OntologyDescriptionCreator creator;	

		public DescriptionViewDialog(Frame owner)
		{
			super(owner,"Description Manager",true);
			this.creator = creator;
			setGui();
		}

		//imposta l'interfaccia grafica
		private void setGui()
		{
			MainFrame mf = (MainFrame) this.getOwner();
			OntologyDescription description = mf.getDescription();
			OntologyIndividual individuals[] = description.getIndividuals();
			JList<OntologyIndividual> list = new JList<OntologyIndividual>(individuals);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			JScrollPane taSP = new JScrollPane(list);
			JButton removeButton = new JButton("Remove");
			removeButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						if (list.getSelectedIndex() > -1)
						{
							int result = JOptionPane.showConfirmDialog(DescriptionViewDialog.this,
							"Are you sure to remove the selected individual ?","Remove individual",JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
							if (result == JOptionPane.YES_OPTION)
							{
								MainFrame mf = (MainFrame) DescriptionViewDialog.this.getOwner();
								mf.removeIndividual(individuals[list.getSelectedIndex()]);
								OntologyIndividual arr[] = mf.getDescription().getIndividuals();
								list.setListData(arr);
							}
						}	
					}
				}
			);
			JButton expandButton = new JButton("Expand");
			expandButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						if (list.getSelectedIndex() > -1)
						{
							MainFrame mf = (MainFrame) DescriptionViewDialog.this.getOwner();
							int parentHeight = DescriptionViewDialog.this.getHeight();
							int parentWidth = DescriptionViewDialog.this.getWidth();
							int height = (int)(parentHeight * 1);
							int width = (int)(parentWidth * 1);
							JDialog dialog = new IndividualViewDialog(DescriptionViewDialog.this,mf,list.getSelectedValue());
							dialog.setSize(width,height);
							dialog.setLocationRelativeTo(DescriptionViewDialog.this);
							dialog.setVisible(true);
						}	
					}
				}
			);
			JButton closeButton = new JButton("Close");
			closeButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						DescriptionViewDialog.this.setVisible(false);	
					}
				}
			);
			JPanel panel = new JPanel();
			JPanel buttonPanel = new JPanel();
			panel.add(taSP);
			buttonPanel.add(expandButton);
			buttonPanel.add(removeButton);
			buttonPanel.add(closeButton);
			this.add(panel,BorderLayout.CENTER);
			this.add(buttonPanel,BorderLayout.SOUTH);
		}
	}