//finestra di dialogo per visualizzare un individuo

	import javax.swing.JDialog;
	import java.awt.Dialog;
	import javax.swing.JPanel;
	import javax.swing.JScrollPane;
	import javax.swing.JButton;
	import javax.swing.JTable;
	import javax.swing.JTabbedPane;
	import java.awt.BorderLayout;
	import java.awt.event.ActionListener;
	import java.awt.event.ActionEvent;
	import javax.swing.JOptionPane;
	import java.awt.Frame;

	public class IndividualViewDialog extends JDialog
	{
		private Frame parent;
		private OntologyIndividual individual;

		public IndividualViewDialog(Dialog owner, Frame parent, OntologyIndividual ind)
		{
			super(owner,"Individual: " + ind.getName() + " Class: " + ind.getType().getName(),true);
			this.parent = parent;
			this.individual = ind;
			setGui();
		}

		//imposta l'interfaccia grafica
		private void setGui()
		{
			Attribute attributes[] = this.individual.getAttributes();
			Relationship relations[] = this.individual.getRelations();
			JTabbedPane tp = new JTabbedPane();
			JTable attTable = new JTable(new AttributeTableModel(attributes));
			JTable relTable = new JTable(new RelationTableModel(relations));
			JScrollPane sp1 = new JScrollPane(attTable);
			JScrollPane sp2 = new JScrollPane(relTable);
			JPanel attPanel = new JPanel();
			attPanel.add(sp1);
			JPanel relPanel = new JPanel();
			relPanel.add(sp2);
			JButton removeButton = new JButton("Remove");
			removeButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						int index = tp.getSelectedIndex();
						if (index > -1)
						{
							if (index == 0 && attTable.getSelectedRow() > -1)
							{
								int result = JOptionPane.showConfirmDialog(IndividualViewDialog.this,
								"Are you sure to remove the selected attribute ?",
								"Remove attribute",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
								if (result == JOptionPane.YES_OPTION)
								{
									MainFrame mf = (MainFrame) IndividualViewDialog.this.parent;
									mf.removeAttribute(IndividualViewDialog.this.individual,
									attributes[attTable.getSelectedRow()]);
									attTable.setModel(new AttributeTableModel(
									IndividualViewDialog.this.individual.getAttributes()));
								}
							}
							if (index == 1 && relTable.getSelectedRow() > -1)
							{
								int result = JOptionPane.showConfirmDialog(IndividualViewDialog.this,
								"Are you sure to remove the selected relation ?",
								"Remove relationship",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
								if (result == JOptionPane.YES_OPTION)
								{
									MainFrame mf = (MainFrame) IndividualViewDialog.this.parent;
									mf.removeRelation(IndividualViewDialog.this.individual,
									relations[relTable.getSelectedRow()]);
									relTable.setModel(new RelationTableModel(
									IndividualViewDialog.this.individual.getRelations()));
								}
							}
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
						IndividualViewDialog.this.setVisible(false);	
					}
				}
			);
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(removeButton);
			buttonPanel.add(closeButton);
			tp.addTab("Attributes",attPanel);
			tp.addTab("Relations",relPanel);
			JPanel mainPanel = new JPanel();
			mainPanel.add(tp);
			this.add(mainPanel,BorderLayout.CENTER);
			this.add(buttonPanel,BorderLayout.SOUTH);
		}
	}