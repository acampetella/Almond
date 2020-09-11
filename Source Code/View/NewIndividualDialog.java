//finestra di dialogo per definire un nuovo individuo

	import javax.swing.JDialog;
	import java.awt.Frame;
	import javax.swing.JTextField;
	import javax.swing.JLabel;
	import javax.swing.JButton;
	import javax.swing.JPanel;
	import javax.swing.GroupLayout;
	import java.awt.Color;
	import java.awt.event.ActionListener;
	import java.awt.event.ActionEvent;
	import javax.swing.JOptionPane;

	public class NewIndividualDialog extends JDialog
	{
		private OntologyClass ontologyClass;
		private OntologyDescriptionCreator creator;		

		public NewIndividualDialog(Frame owner, OntologyClass c, OntologyDescriptionCreator creator)
		{
			super(owner,"New Individual",true);
			this.ontologyClass = c;
			this.creator = creator;
			setGui();
		}

		//imposta l'interfaccia grafica
		private void setGui()
		{
			JLabel label1 = new JLabel("Class");
			JLabel label2 = new JLabel("Name");
			JTextField classNameField = new JTextField(this.ontologyClass.getName());
			classNameField.setEditable(false);
			classNameField.setBackground(Color.WHITE);
			JTextField indNameField = new JTextField(20);
			JButton saveButton = new JButton("OK");
			saveButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						String name = indNameField.getText();
						if (!name.equals(""))
						{
							OntologyIndividual ind = 
							NewIndividualDialog.this.creator.createIndividual(name,NewIndividualDialog.this.ontologyClass);
							MainFrame mf = (MainFrame) NewIndividualDialog.this.getOwner();
							mf.addIndividual(ind);
							NewIndividualDialog.this.setVisible(false);
						}
						else
							JOptionPane.showMessageDialog(NewIndividualDialog.this,"Individual name doesn't define",
							"Check input",JOptionPane.WARNING_MESSAGE);
					}
				}
			);
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						NewIndividualDialog.this.setVisible(false);
					}
				}
			);
			JPanel panel = new JPanel();
			GroupLayout layout = new GroupLayout(panel);
			panel.setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			GroupLayout.Alignment h_align = GroupLayout.Alignment.TRAILING;
			GroupLayout.Alignment v_align = GroupLayout.Alignment.BASELINE;
			GroupLayout.SequentialGroup for_horizontal = layout.createSequentialGroup();
			for_horizontal.addGroup(layout.createParallelGroup(h_align).addComponent(label1).addComponent(label2));
			for_horizontal.addGroup(layout.createParallelGroup(h_align).addComponent(classNameField).addComponent(indNameField)
			.addComponent(saveButton));
			for_horizontal.addGroup(layout.createParallelGroup(h_align).addComponent(cancelButton));
			layout.setHorizontalGroup(for_horizontal);
			GroupLayout.SequentialGroup for_vertical = layout.createSequentialGroup();
			for_vertical.addGroup(layout.createParallelGroup(v_align).addComponent(label1).addComponent(classNameField));
			for_vertical.addGroup(layout.createParallelGroup(v_align).addComponent(label2).addComponent(indNameField));
			for_vertical.addGroup(layout.createParallelGroup(v_align).addComponent(saveButton).addComponent(cancelButton));
			layout.setVerticalGroup(for_vertical);
			this.add(panel);
		}
	}