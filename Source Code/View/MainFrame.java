//finestra principale dell'applicazione
//prevede un elemento JSplitPane con due pannelli: quello di sinistra visualizza sempre l'ontologia, quello di destra inizialmente è vuoto

	//import delle classi necessarie
	import java.io.File;
	import javax.swing.JTree;
	import javax.swing.JFrame;
	import javax.swing.tree.TreeNode;
	import javax.swing.JTextArea;
	import javax.swing.JPanel;
	import javax.swing.JSplitPane;
	import javax.swing.JScrollPane;
	import javax.swing.JMenuBar;
	import javax.swing.JMenu;
	import javax.swing.JMenuItem;
	import javax.swing.BorderFactory;
	import java.awt.event.ActionListener;
	import java.awt.event.ActionEvent;
	import javax.swing.JPopupMenu;
	import javax.swing.event.TreeSelectionListener;
	import javax.swing.event.TreeSelectionEvent;
	import javax.swing.tree.DefaultMutableTreeNode;
	import javax.swing.JDialog;
	import org.semanticweb.owlapi.model.OWLClass;
	import java.util.ArrayList;
	import javax.swing.JOptionPane;
	import javax.swing.JFileChooser;
	import org.semanticweb.owlapi.model.OWLAxiom;
	import javax.swing.SwingUtilities;
	import javax.swing.JProgressBar;

	public class MainFrame extends JFrame
	{
		private JPanel rightPanel; //pannello di destra
		private JTextArea textArea; //area dove vengono riportate le info della desrizione
		private JSplitPane sp; //oggetto che racchiude i due pannelli principali
		private JMenuBar bar; //barra dei menù
		private JMenu fileMenu, displayMenu, createMenu, closeMenu, saveMenu, saveDescriptionMenu, importMenu; //i vari menù
		private JMenuItem newDescriptionItem, exitItem, closeDescriptionItem, rdfxmlFormatItem, owlxmlFormatItem, manchesterFormatItem, 				turtleFormatItem, saveManifestFileItem, viewManifestItem, checkItem, viewDescriptionItem, importDescriptionItem; //le varie voci di menù
		private JTree tree; //oggetto grafico che contiene l'ontologia
		private AlmondOntologyManager manager; //gestore dell'ontologia
		private boolean changeMode = false; //vale true se vogliamo creare una nuova descrizione
		private OntologyDescriptionCreator creator; //gestore della descrizione
		private Ontology ontology; //ontologia di base
		private OntologyIndividual sourceIndividual; //individuo sorgente
		private Attribute targetAttribute; //attributo da modificare
		private Relationship targetRelation; //relazione da modificare

		//costruttore
		public MainFrame() throws Exception
		{
			this(new File("almond.owl"));
		}
		
		//altro costruttore
		public MainFrame(File f) throws Exception
		{
			//imposta il titolo della finestra chiamando il corrispondente costruttore della superclasse
			super("Almond Ontology");
			//crea l'ontologia caricandola dal file almond.owl
			this.manager = new AlmondOntologyManager(f);
			//imposta la variabile ontology
			this.ontology = this.manager.getAlmondOntology();
			//converte l'ontologia nel formato tree
			TreeNode root = this.manager.getAlmondOntologyAsTree();
			//crea l'oggetto grafico JTree
			this.tree = new JTree(root);
			//gestisce l'evento di selezione di un nodo dell'albero
			tree.addTreeSelectionListener(
				new TreeSelectionListener()
				{
					//in base al nodo selezionato viene caricato il menù di popup più opportuno
    					public void valueChanged(TreeSelectionEvent e) 
					{
						MainFrame.this.tree.setComponentPopupMenu(null);
        					DefaultMutableTreeNode node = (DefaultMutableTreeNode) MainFrame.this.tree.getLastSelectedPathComponent();
						if (node != null && !node.toString().equals("Attributes") && !node.toString().equals("Relations") && 
						!node.toString().equals("Sub Classes") && !node.isRoot() && MainFrame.this.changeMode)
						{
							String className = node.getClass().getCanonicalName();
							if (className.equals("TreeNodeOntologyClass"))
								MainFrame.this.tree.setComponentPopupMenu(MainFrame.this.getClassMenu());
							else
							{
								if (node.getParent().toString().equals("Attributes"))
									MainFrame.this.tree.setComponentPopupMenu(MainFrame.this.getAttributeMenu());
								else
									MainFrame.this.tree.setComponentPopupMenu(MainFrame.this.getRelationMenu());		
							}
						}
    					}
				}
			);
			//inserisce il JTree in un JScrollPane per visualizzare eventuali barre di scorrimento
			JScrollPane treeSP = new JScrollPane(this.tree);
			//crea i due pannelli principali
			JPanel leftPanel = new JPanel();
			this.rightPanel = new JPanel();
			leftPanel.add(treeSP);
			//crea il contentore che racchiude i due pannelli principali
			this.sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftPanel,this.rightPanel);
			sp.setOneTouchExpandable(true);
			//aggiunge il contenitore alla finestra
			this.add(sp);
			//imposta i menù a barra
			this.setMenu();
		}

		//aggiunge un nuovo individuo alla descrizione
		public void addIndividual(OntologyIndividual ind)
		{
			this.creator.addToDescription(ind);
			String s = "New Individual IRI: " + ind.getIRI().toString() + " Type: " + ind.getType().getName();
			this.writeToTextArea(s);
		}

		//imposta l'individuo sorgente
		public void setSourceIndividual(OntologyIndividual ind)
		{
			this.sourceIndividual = ind;
		}

		//imposta l'attributo da modificare
		public void setTargetAttribute(Attribute a)
		{
			this.targetAttribute = a;
		}

		//imposta la relazione da modificare
		public void setTargetRelation(Relationship r)
		{
			this.targetRelation = r;
		}

		//imposta il valore dell'attributo
		public void setAttributeValue()
		{
			this.creator.setAttributeValue(this.sourceIndividual,this.targetAttribute,this.targetAttribute.getValue());
			String s = "Set Attribute IRI: " + this.sourceIndividual.getIRI().toString() + " Attribute: " + this.targetAttribute.getName() + 
			" Value: " + this.targetAttribute.getValue();
			this.writeToTextArea(s);
			this.sourceIndividual = null;
			this.targetAttribute = null;
		}

		//imposta il valore della relazione
		public void setRelationValue()
		{
			this.creator.setRelationValue(this.targetRelation,this.sourceIndividual,
			this.sourceIndividual.getRelationValue(this.targetRelation));
			String s = "Set Relationship Name: " + this.targetRelation.getName() + " Source: " + this.sourceIndividual.getIRI().toString() + 
			" Target: " + this.sourceIndividual.getRelationValue(this.targetRelation).getIRI().toString();
			this.writeToTextArea(s);
			this.sourceIndividual = null;
			this.targetRelation = null;
		}

		//rimuove un individuo dalla descrizione
		public void removeIndividual(OntologyIndividual ind)
		{
			this.creator.removeFromDescription(ind);
			String s = "Removed Individual IRI: " + ind.getIRI().toString() + " Type: " + ind.getType().getName();
			this.writeToTextArea(s);
		}

		//rimuove un attributo di un individuo
		public void removeAttribute(OntologyIndividual ind, Attribute a)
		{
			this.creator.removeAttribute(ind,a);
			String s = "Removed Attribute Name: " + a.getName() + " Value: " + a.getValue() + " Individual: " + ind.getIRI().toString();
			this.writeToTextArea(s);
		}

		//rimuove una relazione di un individuo
		public void removeRelation(OntologyIndividual ind, Relationship r)
		{
			this.creator.removeRelation(ind,r);
			String s = "Removed Relation Name: " + r.getName() + " Source: " + ind.getIRI().toString() + " Target: " + 
			r.getIndividual().getIRI().toString();
			this.writeToTextArea(s);
		}

		//imposta la descrizione
		public void setDescription(OntologyDescription d)
		{
			this.creator = new OntologyDescriptionCreator(this.ontology);
			for(OntologyIndividual ind : d.getIndividuals())
				this.creator.addToDescription(ind);
			this.creator.setPrefix("https://almond.stanford.edu/Ontology/almond");
			//aggiunge l'area di testo al pannello di destra
			this.addTextArea();
			//modifica i menù in modo da poter gestire una descrizione
			this.modifyMenu();
			//abilita la flag changeMode
			this.changeMode = true;
			//seleziono il nodo root dell'ontologia
			this.tree.setSelectionInterval(0,0);
			this.setVisible(true);	
		}

		//restituisce la descrizione
		public OntologyDescription getDescription()
		{
			return this.creator.getDescription();
		}

		//verifica la consistenza dell KB
		private boolean checkDescription() throws Exception
		{
			this.manager.addDescription(this.creator.getDescription());
			boolean check = false;
			if (!this.manager.isConsistent())
			{
				OWLAxiom axioms[] = this.manager.getInconsistencies();
				int result = JOptionPane.showConfirmDialog(this,"KB is not consistent. Press YES button to see inconsistencies, " + 
				"No to continue","KB Check",JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE);
				if (result == JOptionPane.YES_OPTION)
				{
					String incs = "";
					for(OWLAxiom a : axioms)
						incs += a.toString() + "\n";
					int parentHeight = this.getHeight();
					int parentWidth = this.getWidth();
					int height = (int)(parentHeight * 0.5);
					int width = (int)(parentWidth * 0.8);
					JDialog dialog = new TextAreaDialog(this,"Inconsistencies list",incs);
					dialog.setSize(width,height);
					dialog.setLocationRelativeTo(this);
					dialog.setVisible(true);	
				}
			}
			else
				check = true;
			this.manager.removeABox();
			return check;
		}

		//scrive una stringa nell'area di testo
		private void writeToTextArea(String s)
		{
			this.textArea.append(s + "\n");
		}

		//aggiunge l'area di testo al pannello di destra
		private void addTextArea()
		{
			this.textArea = new JTextArea(100,100);
			this.textArea.setEditable(false);
			this.textArea.setLineWrap(true);
			JScrollPane taSP = new JScrollPane(this.textArea);
			this.rightPanel.removeAll();
			this.rightPanel.add(taSP);
		}

		//imposta i menù iniziali visibili nella barra
		private void setMenu()
		{
			this.bar = new JMenuBar();
			this.bar.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
			this.fileMenu = new JMenu("File");
			this.importMenu = new JMenu("Import");
			this.createMenu = new JMenu("New");
			//creo la voce di menù che permette l'import di una descrizione da file
			this.importDescriptionItem = new JMenuItem("Description");
			this.importDescriptionItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						
						JFileChooser fc = new JFileChooser();
						fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
						fc.setMultiSelectionEnabled(false);
						int result = fc.showOpenDialog(MainFrame.this);
						if (result == JFileChooser.APPROVE_OPTION)
						{
							SwingUtilities.invokeLater(
								new Runnable()
								{
									public void run()
									{
										try
										{	
											ImportDescriptionThread idt = 
											new ImportDescriptionThread(MainFrame.this,MainFrame.this.manager,
											fc.getSelectedFile());
											Thread thread = new Thread(idt);
											thread.start();
										}

										catch(Exception e)
										{
											JOptionPane.showMessageDialog(MainFrame.this,e.getMessage(),
											"Exception",JOptionPane.ERROR_MESSAGE);
										}
									}
								}
							);
						}
					}

				}
			);
			//crea la voce di menù che consente di creare una descrizione
			this.newDescriptionItem = new JMenuItem("Description");
			this.newDescriptionItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						//istanzia il gestore della descrizione
						MainFrame.this.creator = new OntologyDescriptionCreator(MainFrame.this.ontology);
						MainFrame.this.creator.setPrefix("https://almond.stanford.edu/Ontology/almond");
						//aggiunge l'area di testo al pannello di destra
						MainFrame.this.addTextArea();
						//modifica i menù in modo da poter gestire una descrizione
						MainFrame.this.modifyMenu();
						//abilita la flag changeMode
						MainFrame.this.changeMode = true;
						//seleziono il nodo root dell'ontologia
						MainFrame.this.tree.setSelectionInterval(0,0);
						MainFrame.this.setVisible(true);
					}
				}
			);
			//crea la voce di menù che consente di uscire dall'applicazione
			this.exitItem = new JMenuItem("Exit");
			this.exitItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						//comando di uscita dall'applicazione
						System.exit(0);
					}
				}
			);
			this.importMenu.add(this.importDescriptionItem);
			this.createMenu.add(this.newDescriptionItem);
			this.fileMenu.add(this.importMenu);
			this.fileMenu.add(this.createMenu);
			this.fileMenu.add(this.exitItem);
			this.bar.add(this.fileMenu);
			this.setJMenuBar(this.bar);
		}

		//modifica i menù della barra
		private void modifyMenu()
		{
			this.fileMenu.remove(this.importMenu);
			this.fileMenu.remove(this.createMenu);
			this.fileMenu.remove(this.exitItem);
			this.displayMenu = new JMenu("View");
			this.closeMenu = new JMenu("Close");
			this.saveMenu = new JMenu("Save");
			this.saveDescriptionMenu = new JMenu("Description");
			this.closeDescriptionItem = new JMenuItem("Description");
			this.closeDescriptionItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						MainFrame.this.restoreMenu();
						MainFrame.this.changeMode = false;
						MainFrame.this.tree.setComponentPopupMenu(null);
						MainFrame.this.tree.setSelectionInterval(0,0);
						MainFrame.this.setVisible(true);
					}
				}
			);
			this.rdfxmlFormatItem = new JMenuItem("RDF/XML Format");
			this.rdfxmlFormatItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						try
						{
							MainFrame.this.manager.addDescription(MainFrame.this.creator.getDescription());
							JFileChooser fc = new JFileChooser();
							fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
							fc.setMultiSelectionEnabled(false);
							int result = fc.showSaveDialog(MainFrame.this);
							if (result == JFileChooser.APPROVE_OPTION)
								MainFrame.this.manager.saveABox(fc.getSelectedFile(),
								AlmondOntologyManager.RDF_XML_FORMAT);
						}

						catch(Exception e)
						{
							JOptionPane.showMessageDialog(MainFrame.this,e.getMessage(),
							"Exception",JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			);
			this.owlxmlFormatItem = new JMenuItem("OWL/XML Format");
			this.owlxmlFormatItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						try
						{
							MainFrame.this.manager.addDescription(MainFrame.this.creator.getDescription());
							JFileChooser fc = new JFileChooser();
							fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
							fc.setMultiSelectionEnabled(false);
							int result = fc.showSaveDialog(MainFrame.this);
							if (result == JFileChooser.APPROVE_OPTION)
								MainFrame.this.manager.saveABox(fc.getSelectedFile(),
								AlmondOntologyManager.OWL_XML_FORMAT);
						}

						catch(Exception e)
						{
							JOptionPane.showMessageDialog(MainFrame.this,e.getMessage(),
							"Exception",JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			);
			this.manchesterFormatItem = new JMenuItem("Manchester Format");
			this.manchesterFormatItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						try
						{
							MainFrame.this.manager.addDescription(MainFrame.this.creator.getDescription());
							JFileChooser fc = new JFileChooser();
							fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
							fc.setMultiSelectionEnabled(false);
							int result = fc.showSaveDialog(MainFrame.this);
							if (result == JFileChooser.APPROVE_OPTION)
								MainFrame.this.manager.saveABox(fc.getSelectedFile(),
								AlmondOntologyManager.MANCHESTER_FORMAT);
						}

						catch(Exception e)
						{
							JOptionPane.showMessageDialog(MainFrame.this,e.getMessage(),
							"Exception",JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			);
			this.turtleFormatItem = new JMenuItem("Turtle Format");
			this.turtleFormatItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						try
						{
							MainFrame.this.manager.addDescription(MainFrame.this.creator.getDescription());
							JFileChooser fc = new JFileChooser();
							fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
							fc.setMultiSelectionEnabled(false);
							int result = fc.showSaveDialog(MainFrame.this);
							if (result == JFileChooser.APPROVE_OPTION)
								MainFrame.this.manager.saveABox(fc.getSelectedFile(),
								AlmondOntologyManager.TURTLE_FORMAT);
						}

						catch(Exception e)
						{
							JOptionPane.showMessageDialog(MainFrame.this,e.getMessage(),
							"Exception",JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			);
			this.saveManifestFileItem = new JMenuItem("Manifest File");
			this.saveManifestFileItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						try
						{
							JFileChooser fc = new JFileChooser();
							fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
							fc.setMultiSelectionEnabled(false);
							int result = fc.showSaveDialog(MainFrame.this);
							if (result == JFileChooser.APPROVE_OPTION)
							{
								ThingTalkConverter converter = 
								new ThingTalkConverter(MainFrame.this.creator.getDescription());
								converter.saveManifestFile(fc.getSelectedFile());
							}
						}

						catch(Exception e)
						{
							JOptionPane.showMessageDialog(MainFrame.this,e.getMessage(),
							"Exception",JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			);
			this.viewManifestItem = new JMenuItem("Manifest");
			this.viewManifestItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						try
						{
							ThingTalkConverter converter = new ThingTalkConverter(MainFrame.this.creator.getDescription());
							String code = converter.getManifestCode();
							int parentHeight = MainFrame.this.getHeight();
							int parentWidth = MainFrame.this.getWidth();
							int height = (int)(parentHeight * 0.5);
							int width = (int)(parentWidth * 0.8);
							JDialog dialog = new TextAreaDialog(MainFrame.this,"Manifest Code",code);
							dialog.setSize(width,height);
							dialog.setLocationRelativeTo(MainFrame.this);
							dialog.setVisible(true);
						}

						catch(Exception e)
						{
							JOptionPane.showMessageDialog(MainFrame.this,e.getMessage(),
							"Exception",JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			);
			this.checkItem = new JMenuItem("Check");
			this.checkItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						try
						{
							if (MainFrame.this.checkDescription())
								JOptionPane.showMessageDialog(MainFrame.this,"KB is consistent",
								"Consistent Check",JOptionPane.INFORMATION_MESSAGE);
						}

						catch(Exception e)
						{
							JOptionPane.showMessageDialog(MainFrame.this,e.getMessage(),
							"Exception",JOptionPane.ERROR_MESSAGE);
						}		
					}
				}
			);
			this.viewDescriptionItem = new JMenuItem("Description");
			this.viewDescriptionItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						int parentHeight = MainFrame.this.getHeight();
						int parentWidth = MainFrame.this.getWidth();
						int height = (int)(parentHeight * 0.5);
						int width = (int)(parentWidth * 0.5);
						JDialog dialog = new DescriptionViewDialog(MainFrame.this);
						dialog.setSize(width,height);
						dialog.setLocationRelativeTo(MainFrame.this);
						dialog.setVisible(true);
					}
				}
			);
			this.displayMenu.add(viewManifestItem);
			this.displayMenu.add(viewDescriptionItem);
			this.closeMenu.add(this.closeDescriptionItem);
			this.saveDescriptionMenu.add(this.rdfxmlFormatItem);
			this.saveDescriptionMenu.add(this.owlxmlFormatItem);
			this.saveDescriptionMenu.add(this.manchesterFormatItem);
			this.saveDescriptionMenu.add(this.turtleFormatItem);
			this.saveMenu.add(this.saveDescriptionMenu);
			this.saveMenu.add(this.saveManifestFileItem);
			this.fileMenu.add(this.displayMenu);
			this.fileMenu.add(this.saveMenu);
			this.fileMenu.add(this.checkItem);
			this.fileMenu.add(this.closeMenu);
			this.fileMenu.add(this.exitItem);
		}

		//reimposta i menù iniziali
		private void restoreMenu()
		{
			this.fileMenu.remove(this.displayMenu);
			this.fileMenu.remove(this.closeMenu);
			this.fileMenu.remove(this.saveMenu);
			this.fileMenu.remove(this.checkItem);
			this.fileMenu.remove(this.exitItem);
			this.fileMenu.add(this.importMenu);
			this.fileMenu.add(this.createMenu);
			this.fileMenu.add(this.exitItem);
			this.rightPanel = new JPanel();
			this.sp.setRightComponent(this.rightPanel);
		}

		private JPopupMenu getClassMenu()
		{
			JPopupMenu pm = new JPopupMenu();
			JMenu createMenu = new JMenu("New");
			JMenuItem newIndividualMenuItem = new JMenuItem("Individual");
			newIndividualMenuItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						OntologyClass c = MainFrame.this.getSelectedClass();
						if (c != null)
						{
							int parentHeight = MainFrame.this.getHeight();
							int parentWidth = MainFrame.this.getWidth();
							int height = (int)(parentHeight * 0.2);
							int width = (int)(parentWidth * 0.25);
							JDialog dialog = new NewIndividualDialog(MainFrame.this,c,MainFrame.this.creator);
							dialog.setSize(width,height);
							dialog.setLocationRelativeTo(MainFrame.this);
							dialog.setVisible(true);
						}
					}
				}
			);
			createMenu.add(newIndividualMenuItem);
			pm.add(createMenu);
			return pm;
		}

		private JPopupMenu getAttributeMenu()
		{
			JPopupMenu pm = new JPopupMenu();
			JMenu setMenu = new JMenu("Set");
			JMenuItem setAttributeItem = new JMenuItem("Attribute");
			setAttributeItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						try
						{
							DefaultMutableTreeNode node = (DefaultMutableTreeNode) 														MainFrame.this.tree.getLastSelectedPathComponent();
							TreeNodeOntologyClass tn = (TreeNodeOntologyClass) node.getParent().getParent();
							OntologyClass c = tn.getOntologyClass();
							OWLClass arr[] = MainFrame.this.manager.getSubClassList(c.getType());
							OWLClass tot[] = new OWLClass[arr.length + 1];
							tot[0] = c.getType();
							for(int i = 0 ; i < arr.length; i++)
								tot[i + 1] = arr[i];
							OntologyClass total[] = MainFrame.this.ontology.getAllClasses();
							ArrayList<OntologyClass> classList = new ArrayList<OntologyClass>();
							for(OntologyClass cls : total)
							{
								for(int i = 0; i < tot.length; i++)
								{
									if (cls.getType().compareTo(tot[i]) == 0)
									{
										classList.add(cls);
										break;
									}
								}
							}
							ArrayList<OntologyIndividual> list = new ArrayList<OntologyIndividual>();
							for(OntologyClass cls : classList)
							{
								OntologyIndividual inds[] = 
								MainFrame.this.creator.getDescription().getIndividualsOfClass(cls);
								for(OntologyIndividual individual : inds)
									list.add(individual);
							}
							OntologyIndividual sources[] = new OntologyIndividual[list.size()];
							sources = list.toArray(sources);
							Attribute attributes[] = c.getAttributes();
							Attribute attribute = null;
							for(Attribute a : attributes)
							{
								if (a.getName().equals(node.toString()))
								{
									attribute = a;
									break;
								}	
							}
							int parentHeight = MainFrame.this.getHeight();
							int parentWidth = MainFrame.this.getWidth();
							int height = (int)(parentHeight * 0.2);
							int width = (int)(parentWidth * 0.5);
							JDialog dialog = new SetAttributeDialog(MainFrame.this,sources,attribute);
							dialog.setSize(width,height);
							dialog.setLocationRelativeTo(MainFrame.this);
							dialog.setVisible(true);
						}

						catch(Exception e)
						{
							JOptionPane.showMessageDialog(MainFrame.this,e.getMessage(),
							"Exception",JOptionPane.ERROR_MESSAGE);	
						}
					}
				}
			);
			setMenu.add(setAttributeItem);
			pm.add(setMenu);
			return pm;
		}

		private JPopupMenu getRelationMenu()
		{
			JPopupMenu pm = new JPopupMenu();
			JMenu setMenu = new JMenu("Set");
			JMenuItem setRelationItem = new JMenuItem("Relationship");
			setRelationItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						try
						{
							DefaultMutableTreeNode node = (DefaultMutableTreeNode) 														MainFrame.this.tree.getLastSelectedPathComponent();
							TreeNodeOntologyClass tn = (TreeNodeOntologyClass) node.getParent().getParent();
							OntologyClass c = tn.getOntologyClass();
							OWLClass arr[] = MainFrame.this.manager.getSubClassList(c.getType());
							OWLClass tot[] = new OWLClass[arr.length + 1];
							tot[0] = c.getType();
							for(int i = 0 ; i < arr.length; i++)
								tot[i + 1] = arr[i];
							OntologyClass total[] = MainFrame.this.ontology.getAllClasses();
							ArrayList<OntologyClass> classList = new ArrayList<OntologyClass>();
							for(OntologyClass cls : total)
							{
								for(int i = 0; i < tot.length; i++)
								{
									if (cls.getType().compareTo(tot[i]) == 0)
									{
										classList.add(cls);
										break;
									}
								}
							}
							ArrayList<OntologyIndividual> list = new ArrayList<OntologyIndividual>();
							for(OntologyClass cls : classList)
							{
								OntologyIndividual inds[] = 
								MainFrame.this.creator.getDescription().getIndividualsOfClass(cls);
								for(OntologyIndividual individual : inds)
									list.add(individual);
							}
							OntologyIndividual sources[] = new OntologyIndividual[list.size()];
							sources = list.toArray(sources);
							Relationship relations[] = c.getRelations();
							Relationship relation = null;
							for(Relationship r : relations)
							{
								if (r.getName().equals(node.toString()))
								{
									relation = r;
									break;
								}	
							}
							OntologyClass tc = relation.getRange();
							arr = MainFrame.this.manager.getSubClassList(tc.getType());
							tot = new OWLClass[arr.length + 1];
							tot[0] = tc.getType();
							for(int i = 0 ; i < arr.length; i++)
								tot[i + 1] = arr[i];
							classList = new ArrayList<OntologyClass>();
							for(OntologyClass cls : total)
							{
								for(int i = 0; i < tot.length; i++)
								{
									if (cls.getType().compareTo(tot[i]) == 0)
									{
										classList.add(cls);
										break;
									}
								}
							}
							list = new ArrayList<OntologyIndividual>();
							for(OntologyClass cls : classList)
							{
								OntologyIndividual inds[] = 
								MainFrame.this.creator.getDescription().getIndividualsOfClass(cls);
								for(OntologyIndividual individual : inds)
									list.add(individual);
							}
							OntologyIndividual targets[] = new OntologyIndividual[list.size()];
							targets = list.toArray(targets);
							int parentHeight = MainFrame.this.getHeight();
							int parentWidth = MainFrame.this.getWidth();
							int height = (int)(parentHeight * 0.2);
							int width = (int)(parentWidth * 0.4);
							JDialog dialog = new SetRelationDialog(MainFrame.this,sources,relation,targets);
							dialog.setSize(width,height);
							dialog.setLocationRelativeTo(MainFrame.this);
							dialog.setVisible(true);
						}

						catch(Exception e)
						{
							JOptionPane.showMessageDialog(MainFrame.this,e.getMessage(),
							"Exception",JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			);
			setMenu.add(setRelationItem);
			pm.add(setMenu);
			return pm;
		}

		//restituisce la classe selezionata oppure null
		private OntologyClass getSelectedClass()
		{
			TreeNodeOntologyClass node = (TreeNodeOntologyClass) this.tree.getLastSelectedPathComponent();
			if (node == null)
				return null;
			OntologyClass c = node.getOntologyClass();
			return c;		
		}
	}