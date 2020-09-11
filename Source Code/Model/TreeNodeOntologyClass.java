//classe che converte un oggetto OntologyClass in un TreeNode

	import javax.swing.tree.TreeNode;
	import javax.swing.tree.DefaultMutableTreeNode;	

	public class TreeNodeOntologyClass extends DefaultMutableTreeNode
	{
		private OntologyClass ontologyClass;

		//costruttore
		public TreeNodeOntologyClass(OntologyClass c)
		{
			super(c);
			this.ontologyClass = c;
			this.createTree();
		}

		//restituisce la classe incapsulata nel nodo
		public OntologyClass getOntologyClass()
		{
			return this.ontologyClass;
		}

		private void createTree()
		{
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.getRoot();
			Attribute attributes[] = this.ontologyClass.getAttributes();
			Relationship relations[] = this.ontologyClass.getRelations();
			OntologyClass sons[] = this.ontologyClass.getSons();
			if (attributes.length > 0)
			{
				DefaultMutableTreeNode dataProperties = new DefaultMutableTreeNode("Attributes");
				for(Attribute a : attributes)
				{
					DefaultMutableTreeNode dataProperty = new DefaultMutableTreeNode(a);
					dataProperties.add(dataProperty);
				}
				root.add(dataProperties);
			}
			if (relations.length > 0)
			{
				DefaultMutableTreeNode objectProperties = new DefaultMutableTreeNode("Relations");
				for(Relationship rel : relations)
				{
					DefaultMutableTreeNode objectProperty = new DefaultMutableTreeNode(rel);
					objectProperties.add(objectProperty);
				}
				root.add(objectProperties);
			}
			if (sons.length > 0)
			{
				DefaultMutableTreeNode subClasses = new DefaultMutableTreeNode("Sub Classes");
				for(OntologyClass c : sons)
				{
					TreeNodeOntologyClass tn = new TreeNodeOntologyClass(c);
					subClasses.add(tn);	
				}
				root.add(subClasses);
			}
		}
	}