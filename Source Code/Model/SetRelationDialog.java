//finestra di dialogo per impostare una relazione

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

	public class SetRelationDialog extends JDialog
	{
		private OntologyIndividual sources[]; //potenziali individui sorgenti
		private OntologyIndividual targets[]; //potenziali individui target
		private Relationship relation; //relazione da impostare	

		public SetRelationDialog(Frame owner, OntologyIndividual sources[], Relationship r, OntologyIndividual targets[])
		{
			super(owner,"Set Relationship",true);
			this.sources = sources;
			this.targets = targets;
			this.relation = r;
			setGui();
		}

		//imposta l'interfaccia grafica
		private void setGui()
		{
			JLabel label1 = new JLabel("Source");
			JLabel label2 = new JLabel("Relation");
			JLabel label3 = new JLabel("Target");
			String arr1[] = new String[this.sources.length];
			for(int i = 0; i < arr1.length; i++)
				arr1[i] = sources[i].getName();
			JComboBox<String> sourcesBox = new JComboBox<String>(arr1);
			sourcesBox.setEditable(false);
			sourcesBox.setSelectedIndex(-1);
			String arr2[] = new String[this.targets.length];
			for(int i = 0; i < arr2.length; i++)
				arr2[i] = targets[i].getName();
			JComboBox<String> targetsBox = new JComboBox<String>(arr2);
			targetsBox.setEditable(false);
			targetsBox.setSelectedIndex(-1);
			JTextField relationField = new JTextField(this.relation.getName());
			relationField.setEditable(false);
			relationField.setBackground(Color.WHITE);
			JButton saveButton = new JButton("OK");
			saveButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						if (sourcesBox.getSelectedIndex() >= 0 && targetsBox.getSelectedIndex() >= 0)
						{
							SetRelationDialog.this.relation.setIndividual(SetRelationDialog.this
							.targets[targetsBox.getSelectedIndex()]);
							MainFrame mf = (MainFrame) SetRelationDialog.this.getOwner();
							mf.setSourceIndividual(SetRelationDialog.this.sources[sourcesBox.getSelectedIndex()]);
							mf.setTargetRelation(SetRelationDialog.this.relation);
							mf.setRelationValue();
							SetRelationDialog.this.setVisible(false);
						}
						else
							JOptionPane.showMessageDialog(SetRelationDialog.this,"You have select source and target 									individuals","Check input",JOptionPane.WARNING_MESSAGE);
					}
				}
			);
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						SetRelationDialog.this.setVisible(false);
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
			for_horizontal.addGroup(layout.createParallelGroup(h_align).addComponent(label1).addComponent(sourcesBox));
			for_horizontal.addGroup(layout.createParallelGroup(h_align).addComponent(label2).addComponent(relationField));
			for_horizontal.addGroup(layout.createParallelGroup(h_align).addComponent(label3).addComponent(targetsBox).addComponent(saveButton));
			for_horizontal.addGroup(layout.createParallelGroup(h_align).addComponent(cancelButton));
			layout.setHorizontalGroup(for_horizontal);
			GroupLayout.SequentialGroup for_vertical = layout.createSequentialGroup();
			for_vertical.addGroup(layout.createParallelGroup(v_align).addComponent(label1).addComponent(label2).addComponent(label3));
			for_vertical.addGroup(layout.createParallelGroup(v_align).addComponent(sourcesBox).addComponent(relationField)
			.addComponent(targetsBox));
			for_vertical.addGroup(layout.createParallelGroup(v_align).addComponent(saveButton).addComponent(cancelButton));
			layout.setVerticalGroup(for_vertical);
			this.add(panel);
		}
	}