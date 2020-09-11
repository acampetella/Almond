//finestra di dialogo per impostare un attributo

	import javax.swing.JDialog;
	import java.awt.Frame;
	import javax.swing.JComboBox;
	import javax.swing.JTextField;
	import javax.swing.JLabel;
	import javax.swing.JButton;
	import javax.swing.JPanel;
	import javax.swing.GroupLayout;
	import java.awt.Color;
	import java.awt.event.ActionListener;
	import java.awt.event.ActionEvent;
	import javax.swing.JOptionPane;

	public class SetAttributeDialog extends JDialog
	{
		private OntologyIndividual sources[]; //potenziali individui sorgenti
		private Attribute attribute; //attributo da modificare	

		public SetAttributeDialog(Frame owner, OntologyIndividual sources[], Attribute a)
		{
			super(owner,"Set Attribute",true);
			this.sources = sources;
			this.attribute = a;
			setGui();
		}

		//imposta l'interfaccia grafica
		private void setGui()
		{
			JLabel label1 = new JLabel("Individual");
			JLabel label2 = new JLabel("Attribute");
			JLabel label3 = new JLabel("Datatype");
			JLabel label4 = new JLabel("Value");
			String arr[] = new String[this.sources.length];
			for(int i = 0; i < arr.length; i++)
				arr[i] = sources[i].getName();
			JComboBox<String> indBox = new JComboBox<String>(arr);
			indBox.setEditable(false);
			indBox.setSelectedIndex(-1);
			JTextField attributeField = new JTextField(this.attribute.getName());
			attributeField.setEditable(false);
			attributeField.setBackground(Color.WHITE);
			JTextField datatypeField = new JTextField(this.attribute.getDatatype().toString());
			datatypeField.setEditable(false);
			datatypeField.setBackground(Color.WHITE);
			JTextField valueField = new JTextField(20);
			JButton saveButton = new JButton("OK");
			saveButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						if (indBox.getSelectedIndex() >= 0 && !valueField.getText().equals(""))
						{
							SetAttributeDialog.this.attribute.setValue(valueField.getText());
							MainFrame mf = (MainFrame) SetAttributeDialog.this.getOwner();
							mf.setSourceIndividual(SetAttributeDialog.this.sources[indBox.getSelectedIndex()]);
							mf.setTargetAttribute(SetAttributeDialog.this.attribute);
							mf.setAttributeValue();
							SetAttributeDialog.this.setVisible(false);
						}
						else
							JOptionPane.showMessageDialog(SetAttributeDialog.this,"You have select source individual and " +  
							"insert an attribute value","Check input",JOptionPane.WARNING_MESSAGE);
					}
				}
			);
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						SetAttributeDialog.this.setVisible(false);
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
			for_horizontal.addGroup(layout.createParallelGroup(h_align).addComponent(label1).addComponent(indBox));
			for_horizontal.addGroup(layout.createParallelGroup(h_align).addComponent(label2).addComponent(attributeField));
			for_horizontal.addGroup(layout.createParallelGroup(h_align).addComponent(label3).addComponent(datatypeField));
			for_horizontal.addGroup(layout.createParallelGroup(h_align).addComponent(label4).addComponent(valueField).addComponent(saveButton));
			for_horizontal.addGroup(layout.createParallelGroup(h_align).addComponent(cancelButton));
			layout.setHorizontalGroup(for_horizontal);
			GroupLayout.SequentialGroup for_vertical = layout.createSequentialGroup();
			for_vertical.addGroup(layout.createParallelGroup(v_align).addComponent(label1).addComponent(label2).addComponent(label3)
			.addComponent(label4));
			for_vertical.addGroup(layout.createParallelGroup(v_align).addComponent(indBox).addComponent(attributeField)
			.addComponent(datatypeField).addComponent(valueField));
			for_vertical.addGroup(layout.createParallelGroup(v_align).addComponent(saveButton).addComponent(cancelButton));
			layout.setVerticalGroup(for_vertical);
			this.add(panel);
		}
	}