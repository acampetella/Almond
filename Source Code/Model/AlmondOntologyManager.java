//classe per la gestione dell'ontologia Almond

	//import delle classi necessarie
	import java.io.File;
	import org.semanticweb.owlapi.model.OWLOntologyManager;
	import org.semanticweb.owlapi.model.IRI;
	import org.semanticweb.owlapi.apibinding.OWLManager;
	import org.semanticweb.owlapi.model.OWLOntology;
	import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
	import org.semanticweb.owlapi.model.OWLClass;
	import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
	import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
	import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
	import org.semanticweb.owlapi.model.OWLObjectProperty;
	import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
	import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
	import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
	import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
	import org.semanticweb.owlapi.model.OWLDataProperty;
	import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
	import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
	import org.semanticweb.owlapi.vocab.OWL2Datatype;
	import uk.ac.manchester.cs.owl.owlapi.OWL2DatatypeImpl;
	import org.semanticweb.owlapi.io.FileDocumentTarget;
	import org.semanticweb.owlapi.model.OWLOntologyStorageException;
	import org.semanticweb.owlapi.formats.ManchesterSyntaxDocumentFormatFactory;
	import org.semanticweb.owlapi.formats.RDFXMLDocumentFormatFactory;
	import org.semanticweb.owlapi.formats.OWLXMLDocumentFormatFactory;
	import org.semanticweb.owlapi.formats.TurtleDocumentFormatFactory;
	import org.semanticweb.owlapi.model.OWLDocumentFormat;
	import org.semanticweb.owlapi.model.OWLOntologyCreationException;
	import org.semanticweb.owlapi.model.parameters.Imports;
	import java.util.Set;
	import org.semanticweb.owlapi.model.OWLAxiom;
	import org.semanticweb.owlapi.model.OWLEntity;
	import java.util.ArrayList;
	import org.semanticweb.owlapi.model.OWLDatatype;
	import org.semanticweb.owlapi.model.AxiomType;
	import org.semanticweb.owlapi.model.OWLClassExpression;
	import org.semanticweb.owlapi.model.ClassExpressionType;
	import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
	import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
	import org.semanticweb.owlapi.reasoner.OWLReasoner;
	import org.semanticweb.owlapi.reasoner.NodeSet;
	import javax.swing.tree.TreeNode;
	import javax.swing.tree.DefaultMutableTreeNode;
	import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
	import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
	import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
	import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
	import org.semanticweb.owlapi.model.OWLNamedIndividual;
	import java.util.TreeSet;
	import java.util.HashSet;
	import org.semanticweb.owlapi.model.OWLLiteral;

	public class AlmondOntologyManager
	{
		private OWLOntologyManager manager = null;
		private OWLOntology ontology = null;

		//costanti statiche che indicano un tipo di formato
		public static final int MANCHESTER_FORMAT = 0;
		public static final int RDF_XML_FORMAT = 1;
		public static final int OWL_XML_FORMAT = 2;
		public static final int TURTLE_FORMAT = 3;
		
		//costruttore di default
		public AlmondOntologyManager() throws Exception
		{
			//creo l'oggetto OWLOntologyManager per la gestione delle ontologie
			//utilizzo il metoto statico createOWLOntologyManager della classe OWLManager
			this.manager = OWLManager.createOWLOntologyManager();
			//creo l'ontologia di Almond
			this.ontology = createOntology();
		}

		//altro costruttore: recepisce l'ontologia da un file
		public AlmondOntologyManager(File f) throws Exception
		{
			//creo l'oggetto OWLOntologyManager per la gestione delle ontologie
			//utilizzo il metoto statico createOWLOntologyManager della classe OWLManager
			this.manager = OWLManager.createOWLOntologyManager();
			//leggo l'ontologia dal file di input
			this.ontology = this.manager.loadOntologyFromOntologyDocument(f);
		}

		//altro costruttore: ontologia specificata in input
		public AlmondOntologyManager(OWLOntology o) throws Exception
		{
			this.ontology = o;
			this.manager = this.ontology.getOWLOntologyManager();
		}

		//restituisce l'ontologia di Almond come oggetto OWLOntology
		public OWLOntology getOWLAlmondOntology()
		{
			return this.ontology;
		}

		//salva l'ontologia nel formato specificato
		public void saveOntology(File output, int format) throws Exception
		{
			switch(format)
			{
				case AlmondOntologyManager.MANCHESTER_FORMAT :
					this.saveManchesterFormat(this.ontology,output);
					break;
				case AlmondOntologyManager.RDF_XML_FORMAT :
					this.saveRDFXMLFormat(this.ontology,output);
					break;
				case AlmondOntologyManager.OWL_XML_FORMAT :
					this.saveOWLXMLFormat(this.ontology,output);
					break;
				case AlmondOntologyManager.TURTLE_FORMAT :
					this.saveTurtleFormat(this.ontology,output);
					break;	
			}	
		}

		//salva la ABox nel formato specificato
		public void saveABox(File output, int format) throws Exception
		{
			OWLAxiom tbox[] = this.getTBoxAxioms();
			OWLAxiom rbox[] = this.getRBoxAxioms();
			for(OWLAxiom a : tbox)
				this.manager.removeAxiom(this.ontology,a);
			for(OWLAxiom a : rbox)
				this.manager.removeAxiom(this.ontology,a);
			this.saveOntology(output,format);
			for(OWLAxiom a : tbox)
				this.manager.addAxiom(this.ontology,a);
			for(OWLAxiom a : rbox)
				this.manager.addAxiom(this.ontology,a);
		}

		//salva la TBox nel formato specificato
		public void saveTBox(File output, int format) throws Exception
		{
			OWLAxiom abox[] = this.removeABox();
			this.saveOntology(output,format);
			for(OWLAxiom a : abox)
				this.manager.addAxiom(this.ontology,a);
		}

		//rimuove la ABox dall'ontologia e restituisce le asserzioni eliminate
		public OWLAxiom[] removeABox() throws Exception
		{
			OWLAxiom abox[] = this.getABoxAxioms();
			for(OWLAxiom a : abox)
				this.manager.removeAxiom(this.ontology,a);
			return abox;
		}

		//aggiunge un array di assiomi all'ontologia
		public void addAxioms(OWLAxiom arr[])
		{
			for(OWLAxiom a : arr)
				this.manager.addAxiom(this.ontology,a);
		}

		//aggiunge un assioma all'ontologia
		public void addAxioms(OWLAxiom axiom)
		{
			this.manager.addAxiom(this.ontology,axiom);
		}

		//restituisce la lista degli assiomi terminologici
		public OWLAxiom[] getTBoxAxioms() throws Exception
		{
			Set<OWLAxiom> set = this.ontology.getTBoxAxioms(Imports.INCLUDED);
			OWLAxiom list[] = new OWLAxiom[set.size()];
			list = set.toArray(list);
			return list;	
		}

		//restituisce la lista degli assiomi relativi alle proprietà
		public OWLAxiom[] getRBoxAxioms() throws Exception
		{
			Set<OWLAxiom> set = this.ontology.getRBoxAxioms(Imports.INCLUDED);
			OWLAxiom list[] = new OWLAxiom[set.size()];
			list = set.toArray(list);
			return list;	
		}

		//restituisce la lista delle asserzioni individuali
		public OWLAxiom[] getABoxAxioms() throws Exception
		{
			Set<OWLAxiom> set = this.ontology.getABoxAxioms(Imports.INCLUDED);
			OWLAxiom list[] = new OWLAxiom[set.size()];
			list = set.toArray(list);
			return list;	
		}

		//restituisce la lista delle entità
		public OWLEntity[] getEntityList() throws Exception
		{
			Set<OWLEntity> set = this.ontology.getSignature(Imports.INCLUDED);
			OWLEntity list[] = new OWLEntity[set.size()];
			list = set.toArray(list);
			return list;	
		}

		//restituisce la lista delle classi presenti nell'ontologia
		public OWLClass[] getClassList() throws Exception
		{
			Set<OWLClass> list = this.ontology.getClassesInSignature();
			OWLClass result[] = new OWLClass[list.size()];
			result = list.toArray(result);
			return result;	
		}

		//restituisce la lista degli Object Property presenti nell'ontologia
		public OWLObjectProperty[] getObjectPropertyList() throws Exception
		{
			Set<OWLObjectProperty> list = this.ontology.getObjectPropertiesInSignature();
			OWLObjectProperty result[] = new OWLObjectProperty[list.size()];
			result = list.toArray(result);
			return result;
		}

		//restituisce la lista delle Data Property presenti nell'ontologia
		public OWLDataProperty[] getDataPropertyList() throws Exception
		{
			Set<OWLDataProperty> list = this.ontology.getDataPropertiesInSignature();
			OWLDataProperty result[] = new OWLDataProperty[list.size()];
			result = list.toArray(result);
			return result;
		}

		//restituisce la lista dei Data Type presenti nell'ontologia
		public OWLDatatype[] getDatatypeList() throws Exception
		{
			Set<OWLDatatype> list = this.ontology.getDatatypesInSignature();
			OWLDatatype result[] = new OWLDatatype[list.size()];
			result = list.toArray(result);
			return result;
		}

		//restituisce il dominio di un Object Property
		public OWLClass[] getObjectPropertyDomain(OWLObjectProperty property) throws Exception
		{
			OWLReasonerFactory rf = new StructuralReasonerFactory();
			OWLReasoner r = rf.createReasoner(this.ontology);
			NodeSet<OWLClass> ns = r.getObjectPropertyDomains(property,true);
			Set<OWLClass> list = ns.getFlattened();
			r.dispose();
			OWLClass result[] = new OWLClass[list.size()];
			result = list.toArray(result);
			return result;
		}

		//restituisce il codominio di un Object Property
		public OWLClass getObjectPropertyRange(OWLObjectProperty property) throws Exception
		{
			OWLReasonerFactory rf = new StructuralReasonerFactory();
			OWLReasoner r = rf.createReasoner(this.ontology);
			NodeSet<OWLClass> ns = r.getObjectPropertyRanges(property,true);
			Set<OWLClass> list = ns.getFlattened();
			r.dispose();
			OWLClass result[] = new OWLClass[list.size()];
			result = list.toArray(result);
			return result[0];
		}

		//restituisce il dominio di un Data Property
		public OWLClass[] getDataPropertyDomain(OWLDataProperty property) throws Exception
		{
			OWLReasonerFactory rf = new StructuralReasonerFactory();
			OWLReasoner r = rf.createReasoner(this.ontology);
			NodeSet<OWLClass> ns = r.getDataPropertyDomains(property,true);
			Set<OWLClass> list = ns.getFlattened();
			r.dispose();
			OWLClass result[] = new OWLClass[list.size()];
			result = list.toArray(result);
			return result;	
		}

		//restituisce il codominio di un Data Property
		public OWLDatatype getDataPropertyRange(OWLDataProperty property) throws Exception
		{
			OWLAxiom axioms[] = this.getTBoxAxioms();
			OWLDatatype range = null;
			for(OWLAxiom a : axioms)
			{
				if (a.isOfType(AxiomType.DATA_PROPERTY_RANGE))
				{
					OWLDataPropertyRangeAxiom axiom = (OWLDataPropertyRangeAxiom) a;
					Set<OWLDataProperty> setProperty = axiom.getDataPropertiesInSignature();
					for(OWLDataProperty p : setProperty)
					{
						if (p.compareTo(property) == 0)
							 range = (OWLDatatype) axiom.getRange();
					}
				}
			}
			return range;
		}

		//restituisce la lista degli Object Property relativi ad una classe
		public OWLObjectProperty[] getObjectPropertiesOf(OWLClass c) throws Exception
		{
			OWLObjectProperty total[] = this.getObjectPropertyList();
			ArrayList<OWLObjectProperty> list = new ArrayList<OWLObjectProperty>(); 
			for(OWLObjectProperty p : total)
			{
				OWLClass domain[] = this.getObjectPropertyDomain(p);
				for(OWLClass cls : domain)
				{
					if (cls.compareTo(c) == 0)
					{
						list.add(p);
						break;
					}
				}	
			}
			OWLObjectProperty result[] = new OWLObjectProperty[list.size()];
			result = list.toArray(result);
			return result;
		}

		//restituisce la lista totale degli Object Property relativi ad una classe
		public OWLObjectProperty[] getAllObjectPropertiesOf(OWLClass c) throws Exception
		{
			OWLObjectProperty total[] = this.getObjectPropertiesOf(c);
			ArrayList<OWLObjectProperty> list = new ArrayList<OWLObjectProperty>();
			for(OWLObjectProperty p : total)
				list.add(p);
			boolean cont = true;
			OWLClass current = c;
			while(cont)
			{
				OWLClass superclass = this.getSuperClassOf(current);
				if (superclass != null)
				{
					total = this.getObjectPropertiesOf(superclass);
					for(OWLObjectProperty p : total)
						list.add(p);
					current = superclass;		
				}
				else
					cont = false;
			}
			OWLObjectProperty result[] = new OWLObjectProperty[list.size()];
			result = list.toArray(result);
			return result;
		}

		//restituisce la lista dei Data Property relativi ad una classe
		public OWLDataProperty[] getDataPropertiesOf(OWLClass c) throws Exception
		{
			OWLDataProperty total[] = this.getDataPropertyList();
			ArrayList<OWLDataProperty> list = new ArrayList<OWLDataProperty>();
			for(OWLDataProperty p : total)
			{
				OWLClass domain[] = this.getDataPropertyDomain(p);
				for(OWLClass cls : domain)
				{
					if (cls.compareTo(c) == 0)
					{
						list.add(p);
						break;
					}
				}	
			}
			OWLDataProperty result[] = new OWLDataProperty[list.size()];
			result = list.toArray(result);
			return result;
		}

		//restituisce la lista totale dei Data Property relativi ad una classe
		public OWLDataProperty[] getAllDataPropertiesOf(OWLClass c) throws Exception
		{
			OWLDataProperty total[] = this.getDataPropertiesOf(c);
			ArrayList<OWLDataProperty> list = new ArrayList<OWLDataProperty>();
			for(OWLDataProperty p : total)
				list.add(p);
			boolean cont = true;
			OWLClass current = c;
			while(cont)
			{
				OWLClass superclass = this.getSuperClassOf(current);
				if (superclass != null)
				{
					total = this.getDataPropertiesOf(superclass);
					for(OWLDataProperty p : total)
						list.add(p);
					current = superclass;		
				}
				else
					cont = false;
			}
			OWLDataProperty result[] = new OWLDataProperty[list.size()];
			result = list.toArray(result);
			return result;
		}

		//restituisce la superclassa della classe di input
		public OWLClass getSuperClassOf(OWLClass c) throws Exception
		{
			OWLReasonerFactory rf = new StructuralReasonerFactory();
			OWLReasoner r = rf.createReasoner(this.ontology);
			NodeSet<OWLClass> ns = r.getSuperClasses(c,true);
			Set<OWLClass> list = ns.getFlattened();
			r.dispose();
			OWLClass result[] = new OWLClass[list.size()];
			result = list.toArray(result);
			if (result.length == 0)
				return null;
			return result[0];
		}

		//restituisce la lista delle superclassi della classe data
		public OWLClass[] getSuperClassList(OWLClass c) throws Exception
		{
			OWLReasonerFactory rf = new StructuralReasonerFactory();
			OWLReasoner r = rf.createReasoner(this.ontology);
			NodeSet<OWLClass> ns = r.getSuperClasses(c,false);
			Set<OWLClass> list = ns.getFlattened();
			r.dispose();
			OWLClass result[] = new OWLClass[list.size()];
			result = list.toArray(result);
			return result;	
		}

		//restituisce la lista delle sottoclassi dirette di una determinata classe
		public OWLClass[] getDirectSubClassList(OWLClass c) throws Exception
		{
			OWLReasonerFactory rf = new StructuralReasonerFactory();
			OWLReasoner r = rf.createReasoner(this.ontology);
			NodeSet<OWLClass> ns = r.getSubClasses(c,true);
			Set<OWLClass> list = ns.getFlattened();
			r.dispose();
			OWLClass result[] = new OWLClass[list.size()];
			result = list.toArray(result);
			return result;		
		}

		//restituisce la lista di tutte le sottoclassi dirette e indirette di una determinata classe
		public OWLClass[] getSubClassList(OWLClass c) throws Exception
		{
			OWLReasonerFactory rf = new StructuralReasonerFactory();
			OWLReasoner r = rf.createReasoner(this.ontology);
			NodeSet<OWLClass> ns = r.getSubClasses(c,false);
			Set<OWLClass> list = ns.getFlattened();
			r.dispose();
			OWLClass result[] = new OWLClass[list.size()];
			result = list.toArray(result);
			return result;	
		}

		//restituisce le superclassi dell'ontologia
		public OWLClass[] getRootClasses() throws Exception
		{
			//istanzio la lista che dovrà contenere il risultato
			ArrayList<OWLClass> list = new ArrayList<OWLClass>();
			//ricavo la lista totale delle classi
			OWLClass total[] = this.getClassList();
			//estraggo solo le superclassi
			for(OWLClass c : total)
			{
				if (this.getSuperClassOf(c).getIRI().toString().contains("Thing"))
					list.add(c);
			}
			OWLClass result[] = new OWLClass[list.size()];
			result = list.toArray(result);
			return result;
		}

		//restituisce le Top class dell'ontologia
		//si tratta di superclassi che non sono il codominio di nessuna proprietà
		public OWLClass[] getTopClasses() throws Exception
		{
			OWLClass roots[] = this.getRootClasses();
			ArrayList<OWLClass> list = new ArrayList<OWLClass>();
			for(OWLClass c : roots)
				list.add(c);
			//ricavo tutte le proprietà definite nell'ontologia
			OWLObjectProperty properties[] = this.getObjectPropertyList();
			for(OWLObjectProperty p : properties)
			{
				//ricavo il codominio della proprietà corrente
				OWLClass range = this.getObjectPropertyRange(p);
				//se trovo nella lista una classe corrispondente a range la elimino
				for(OWLClass c : list)
				{
					if (c.compareTo(range) == 0)
					{
						list.remove(c);
						break;
					}
				}	
			}
			//a questo punto la lista contiene solo le top class
			OWLClass result[] = new OWLClass[list.size()];
			result = list.toArray(result);
			return result;
		}

		//restituisce gli individui di una specifica classe
		public OWLNamedIndividual[] getIstancesOfClass(OWLClass c)
		{
			OWLReasonerFactory rf = new StructuralReasonerFactory();
			OWLReasoner r = rf.createReasoner(this.ontology);
			Set<OWLNamedIndividual> set = r.getInstances(c,true).getFlattened();
			r.dispose();
			OWLNamedIndividual result[] = new OWLNamedIndividual[set.size()];
			result = set.toArray(result);
			return result;
		}

		//restituisce gli individui di un detrminato tipo
		//vengono compresi anche gli individui di eventuali sottoclassi
		public OWLNamedIndividual[] getIstancesOfType(OWLClass c)
		{
			OWLReasonerFactory rf = new StructuralReasonerFactory();
			OWLReasoner r = rf.createReasoner(this.ontology);
			Set<OWLNamedIndividual> set = r.getInstances(c,false).getFlattened();
			r.dispose();
			OWLNamedIndividual result[] = new OWLNamedIndividual[set.size()];
			result = set.toArray(result);
			return result;
		}

		//restituisce la classe di appartenenza di un individuo
		public OWLClass getType(OWLNamedIndividual ind)
		{
			OWLReasonerFactory rf = new StructuralReasonerFactory();
			OWLReasoner r = rf.createReasoner(this.ontology);
			Set<OWLClass> set = r.getTypes(ind,true).getFlattened();
			r.dispose();
			OWLClass arr[] = new OWLClass[set.size()];
			arr = set.toArray(arr);
			OWLClass result = arr[0];
			return result;
		}

		//restituisce l'oggetto OWLOntology nel formato Ontology
		public Ontology getAlmondOntology() throws Exception
		{
			Ontology o = new Ontology(this.manager.getOntologyDocumentIRI(this.ontology));
			OWLClass owlClasses[] = this.getClassList();
			OntologyClass classes[] = new OntologyClass[owlClasses.length];
			for(int i = 0; i < classes.length; i++)
			{
				classes[i] = new OntologyClass(owlClasses[i]);
				OWLDataProperty dataProperties[] = this.getAllDataPropertiesOf(owlClasses[i]);
				Attribute attributes[] = new Attribute[dataProperties.length];
				for(int j = 0; j < dataProperties.length; j++)
				{
					OWLDatatype dt = this.getDataPropertyRange(dataProperties[j]);
					attributes[j] = new Attribute(dataProperties[j],dt);
				}
				classes[i].setAttributes(attributes);
				OWLObjectProperty properties[] = this.getAllObjectPropertiesOf(owlClasses[i]);
				Relationship relations[] = new Relationship[properties.length];
				for(int j = 0; j < properties.length; j++)
					relations[j] = new Relationship(properties[j]);
				classes[i].setRelations(relations);
			}
			//imposto la superclasse
			for(int i = 0; i < classes.length; i++)
			{
				OWLClass sc = this.getSuperClassOf(owlClasses[i]);
				if (!sc.getIRI().toString().contains("Thing"))
				{
					for(OntologyClass c : classes)
					{
						if (c.getType().compareTo(sc) == 0)
						{
							classes[i].setParent(c);
							break;
						}
					}
				}
			}
			//imposto le sottoclassi
			for(int i = 0; i < classes.length; i++)
			{
				OWLClass arr[] = this.getDirectSubClassList(owlClasses[i]);
				for(OWLClass cls : arr)
				{
					if (!cls.getIRI().toString().contains("Nothing"))
					{
						for(OntologyClass c : classes)
						{
							if (c.getType().compareTo(cls) == 0)
							{
								classes[i].addSon(c);
								break;
							}
						}
					}
				}
			}
			//imposto il codominio delle proprietà di ogni classe
			for(int i = 0; i < classes.length; i++)
			{
				Relationship relations[] = classes[i].getRelations();
				for(Relationship r : relations)
				{
					OWLClass range = this.getObjectPropertyRange(r.getObjectProperty());
					for(OntologyClass c : classes)
					{
						if (c.getType().compareTo(range) == 0)
						{
							r.setRange(c);
							break;
						}
					}
				}
			}
			//ricavo le root class dell'ontologia
			OWLClass roots[] = this.getRootClasses();
			//imposto la proprietà di root per le classi
			//aggiungo solo quelle al risultato
			for(OntologyClass c : classes)
			{
				for(OWLClass cls : roots)
				{
					if (cls.compareTo(c.getType()) == 0)
						c.setAsRoot(true);
				}
			}
			o.setClasses(classes);
			return o;
		}

		//restituisce l'ontologia di Almond in formato TreeNode
		public TreeNode getAlmondOntologyAsTree() throws Exception
		{
			Ontology o = this.getAlmondOntology();
			OntologyClass classes[] = o.getRootClasses();
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("Almond");
			TreeNodeOntologyClass tn[] = new TreeNodeOntologyClass[classes.length];
			for(int i = 0; i < classes.length; i++)
			{
				tn[i] = new TreeNodeOntologyClass(classes[i]);
				root.add(tn[i]);
			}
			return root;
			
		}

		//aggiunge una descrizione all'ontologia
		public void addDescription(OntologyDescription description)
		{
			//recupera tutti gli individui della descrizione
			OntologyIndividual individuals[] = description.getIndividuals();
			for(OntologyIndividual ind : individuals)
				//scrive l'individuo nella ABox
				this.writeIndividual(ind);
			this.writeDifferentIndividualAssertions(individuals);
		}

		//verifica la consistenza dell'ontologia
		public boolean isConsistent()
		{
			OWLOntologyChecker checker = new OWLOntologyChecker(this.getOWLAlmondOntology());
			if (checker.isConsistent())
				return true;
			return false;
		}

		//restituisce le incongruenze rilevate nell'ontologia
		public OWLAxiom[] getInconsistencies()
		{
			OWLOntologyChecker checker = new OWLOntologyChecker(this.getOWLAlmondOntology());
			OWLAxiom errors[] = checker.checkABox();
			return errors;
		}

		//restituisce le descrizione presente nella ABox
		public OntologyDescription getDescription() throws Exception
		{
			//ricavo tutte le classi OWL dell'ontologia
			OWLClass owlClasses[] = this.getClassList();
			//ricavo tutti gli individui presenti
			ArrayList<OWLNamedIndividual> allNamedInds = new ArrayList<OWLNamedIndividual>();
			for(OWLClass c : owlClasses)
			{
				OWLNamedIndividual inds[] = this.getIstancesOfClass(c);
				for(OWLNamedIndividual ind : inds)
					allNamedInds.add(ind);
			}
			//converto gli oggetti OWLNamedIndividual in OntologyIndividual
			OntologyIndividual allOntologyInds[] = new OntologyIndividual[allNamedInds.size()];
			for(int i = 0; i < allOntologyInds.length; i++)
				allOntologyInds[i] = this.convert(allNamedInds.get(i));
			//imposto le proprietà degli individui
			OWLReasonerFactory rf = new StructuralReasonerFactory();
			OWLReasoner reasoner = rf.createReasoner(this.ontology);
			for(int i = 0; i < allOntologyInds.length; i++)
			{
				Attribute attributes[] = allOntologyInds[i].getType().getAttributes();
				for(Attribute a : attributes)
				{
					Set<OWLLiteral> literals = reasoner.getDataPropertyValues(allNamedInds.get(i),a.getDataProperty());
					for(OWLLiteral l : literals)
						allOntologyInds[i].setAttributeValue(a,l.getLiteral());
				}
			}
			//imposto le relazioni
			for(int i = 0; i < allOntologyInds.length; i++)
			{
				Relationship relations[] = allOntologyInds[i].getType().getRelations();
				for(Relationship r : relations)
				{
					Set<OWLNamedIndividual> set = reasoner.getObjectPropertyValues(allNamedInds.get(i),
					r.getObjectProperty()).getFlattened();
					for(OWLNamedIndividual individual : set)
					{
						for(OntologyIndividual ind : allOntologyInds)
						{
							if (ind.getIRI().compareTo(individual.getIRI()) == 0)
								allOntologyInds[i].setRelationValue(r,ind);	
						}
					}
				}
			}
			reasoner.dispose();
			OntologyDescription d = new OntologyDescription();
			for(OntologyIndividual ind : allOntologyInds)
				d.addIndividual(ind);
			return d;
		}

		//converte un oggetto OWLNamedIndividual in un oggetto OntologyIndividual
		private OntologyIndividual convert(OWLNamedIndividual ind) throws Exception
		{
			OWLClass c = this.getType(ind);
			Ontology o = this.getAlmondOntology();
			OntologyClass total[] = o.getAllClasses();
			OntologyClass target = null;
			for(OntologyClass cls : total)
			{
				if (cls.getType().compareTo(c) == 0)
				{
					target = cls;
					break;
				}
			}
			OntologyIndividual result = new OntologyIndividual(target,ind.getIRI());
			return result;	
		}

		//salva l'ontologia in un file nel formato di Manchester
		private void saveManchesterFormat(OWLOntology o, File output) throws Exception
		{
			if (o != null)
				this.manager.saveOntology(o,new ManchesterSyntaxDocumentFormatFactory().createFormat(),new FileDocumentTarget(output));
		}

		//salva l'ontologia in un file nel formato RDF/XML
		private void saveRDFXMLFormat(OWLOntology o, File output) throws Exception
		{
			if (o != null)
				manager.saveOntology(o,new RDFXMLDocumentFormatFactory().createFormat(),new FileDocumentTarget(output));
		}

		//salva l'ontologia in un file nel formato di OWL/XML
		private void saveOWLXMLFormat(OWLOntology o, File output) throws Exception
		{
			if (o != null)
				manager.saveOntology(o,new OWLXMLDocumentFormatFactory().createFormat(),new FileDocumentTarget(output));
		}

		//salva l'ontologia in un file nel formato Turtle
		private void saveTurtleFormat(OWLOntology o, File output) throws Exception
		{
			if (o != null)
				manager.saveOntology(o,new TurtleDocumentFormatFactory().createFormat(),new FileDocumentTarget(output));
		}

		//metodo privato che scrive un individuo nella ABox
		private void writeIndividual(OntologyIndividual ind)
		{
			OWLDataFactoryImpl df = new OWLDataFactoryImpl();
			OWLNamedIndividual ni = df.getOWLNamedIndividual(ind.getIRI());
			OWLClassAssertionAxiom axiom1 = df.getOWLClassAssertionAxiom(ind.getType().getType(),ni);
			this.manager.addAxiom(this.ontology,axiom1);
			Attribute attributes[] = ind.getAttributes();
			Relationship relations[] = ind.getRelations();
			if (attributes.length > 0)
			{
				for(Attribute a : attributes)
				{
					if (a.getValue() != null)
					{
						OWLDataPropertyAssertionAxiom axiom2 = 
						df.getOWLDataPropertyAssertionAxiom(a.getDataProperty(),ni,a.getValueAsLiteral());
						this.manager.addAxiom(this.ontology,axiom2);
					}
				}
			}
			if (relations.length > 0)
			{
				for(Relationship r : relations)
				{
					if (r.getIndividual() != null)
					{
						OntologyIndividual ind1 = r.getIndividual();
						OWLNamedIndividual ni1 = df.getOWLNamedIndividual(ind1.getIRI());
						OWLObjectPropertyAssertionAxiom axiom3 = 
						df.getOWLObjectPropertyAssertionAxiom(r.getObjectProperty(),ni,ni1);
						this.manager.addAxiom(this.ontology,axiom3);
					}
				}
			}
		}

		//scrive le asserzioni di differenza tra gli individui
		private void writeDifferentIndividualAssertions(OntologyIndividual arr[])
		{
			//recupero i tipi coinvolti
			TreeSet<OntologyClass> types = new TreeSet<OntologyClass>();
			for(OntologyIndividual ind : arr)
				types.add(ind.getType());
			//types contiene le OntologyClass coinvolte
			for(int i = 1; i <= types.size(); i++)
			{
				OntologyClass cls = types.pollFirst();
				//recupero gli individui appartenenti alla classe cls
				ArrayList<OntologyIndividual> list = new ArrayList<OntologyIndividual>();
				for(OntologyIndividual ind : arr)
				{
					if (ind.getType().equals(cls))
						list.add(ind);
				}
				//list contiene gli individui appartenenti a cls
				//creo gli assiomi di differenza tra gli individui e li inserisco nella ABox
				for(int j = 0; j < list.size() - 1; j++)
				{
					for(int k = 1; k < list.size(); k++)
					{
						OWLDataFactoryImpl df = new OWLDataFactoryImpl();
						OWLNamedIndividual ni1 = df.getOWLNamedIndividual(list.get(j).getIRI());
						OWLNamedIndividual ni2 = df.getOWLNamedIndividual(list.get(k).getIRI());
						OWLDifferentIndividualsAxiom axiom = df.getOWLDifferentIndividualsAxiom(ni1,ni2);
						this.manager.addAxiom(this.ontology,axiom);	
					}
				}
			}
		}

		//metodo privato che crea l'ontologia Almond
		private OWLOntology createOntology() throws Exception
		{
			//creo un oggetto IRI per ogni prefisso definito
			IRI almondIri = IRI.create("https://almond.stanford.edu/Ontology/almond");
			IRI ssnIri = IRI.create("http://www.w3.org/2005/Incubator/ssn/ssnx/ssn");
			IRI iotLiteIri = IRI.create("http://purl.oclc.org/NET/UNIS/fiware/iot-lite");
			//creo l'oggetto OWLOntology con l'IRI specificato sopra
			//questa istruzione può generare un'eccezione di tipo OWLOntologyCreationException
			OWLOntology ontology = this.manager.createOntology(almondIri);
			//Istanzio l'oggetto OWLDataFactory per la creazione delle varie entità dell'ontologia
			OWLDataFactoryImpl df = new OWLDataFactoryImpl();
			//Definizio della classe ssn:Device
			OWLClass ssnDevice = df.getOWLClass(IRI.create(ssnIri.toString() + "#Device"));
			//costruisco l'assioma corrispondente alla dichiarazione della classe Device
			OWLDeclarationAxiom da = df.getOWLDeclarationAxiom(ssnDevice);
			//aggiungo l'assioma all'ontologia
			this.manager.addAxiom(ontology,da);
			//ripeto lo stesso procedimento per la classe Service
			OWLClass iotLiteService = df.getOWLClass(IRI.create(iotLiteIri.toString() + "#Service"));
			da = df.getOWLDeclarationAxiom(iotLiteService);
			this.manager.addAxiom(ontology,da);
			//ripeto lo stesso procedimento per la classe DeviceClass
			OWLClass deviceClass = df.getOWLClass(IRI.create(almondIri.toString() + "#DeviceClass"));
			da = df.getOWLDeclarationAxiom(deviceClass);
			this.manager.addAxiom(ontology,da);

			//istanzio la classe Loader
			OWLClass loader = df.getOWLClass(IRI.create(almondIri.toString() + "#Loader"));
			//istanzio la classe RESTLoader
			OWLClass restLoader = df.getOWLClass(IRI.create(almondIri.toString() + "#RESTLoader"));
			//istanzio la classe RSSLoader
			OWLClass rssLoader = df.getOWLClass(IRI.create(almondIri.toString() + "#RSSLoader"));
			//istanzio la classe CustomLoader
			OWLClass customLoader = df.getOWLClass(IRI.create(almondIri.toString() + "#CustomLoader"));
			//creo l'assima RESTLoader subClassOf Loader
			OWLSubClassOfAxiom subClassOfAxiom = df.getOWLSubClassOfAxiom(restLoader,loader);
			//aggiungo l'assioma all'ontologia
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assima RSSLoader subClassOf Loader
			subClassOfAxiom = df.getOWLSubClassOfAxiom(rssLoader,loader);
			//aggiungo l'assioma all'ontologia
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assima CustomLoader subClassOf Loader
			subClassOfAxiom = df.getOWLSubClassOfAxiom(customLoader,loader);
			//aggiungo l'assioma all'ontologia
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma RESTLoader DisjointWith RSSLoader e lo aggiungo all'ontologia
			OWLDisjointClassesAxiom disjointClassesAxiom = df.getOWLDisjointClassesAxiom(restLoader,rssLoader);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma RESTLoader DisjointWith CustomLoader e lo aggiungo all'ontologia
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(restLoader,customLoader);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'asioma RSSLoader DisjointWith CustomLoader e lo aggiungo all'ontologia
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(rssLoader,customLoader);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//istanzio le classi Configuration, BasicConfiguration e ComplexConfiguration
			OWLClass configuration = df.getOWLClass(IRI.create(almondIri.toString() + "#Configuration"));
			OWLClass basicConfiguration = df.getOWLClass(IRI.create(almondIri.toString() + "#BasicConfiguration"));
			OWLClass complexConfiguration = df.getOWLClass(IRI.create(almondIri.toString() + "#ComplexConfiguration"));
			//creo l'assima BasicConfiguration subClassOf Configuration e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(basicConfiguration,configuration);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assima ComplexConfiguration subClassOf Configuration e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(complexConfiguration,configuration);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'asioma BasicConfiguration DisjointWith ComplexConfiguration e lo aggiungo all'ontologia
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(basicConfiguration,complexConfiguration);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//istanzio le classi APIKeyConfiguration e FormConfiguration
			OWLClass apiKeyConfiguration = df.getOWLClass(IRI.create(almondIri.toString() + "#APIKeyConfiguration"));
			OWLClass formConfiguration = df.getOWLClass(IRI.create(almondIri.toString() + "#FormConfiguration"));
			//creo l'assioma APIKeyConfiguration subClassOf BasicConfiguration e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(apiKeyConfiguration,basicConfiguration);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma FormConfiguration subClassOf BasicConfiguration e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(formConfiguration,basicConfiguration);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma APIKeyConfiguration DisjointWith FormConfiguration e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(apiKeyConfiguration,formConfiguration);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//istanzio le classi AuthenticationType, BasicAuthentication, OpenAuthentication e DiscoveryAuthentication
			OWLClass authenticationType = df.getOWLClass(IRI.create(almondIri.toString() + "#AuthenticationType"));
			OWLClass basicAuthentication = df.getOWLClass(IRI.create(almondIri.toString() + "#BasicAuthentication"));
			OWLClass openAuthentication = df.getOWLClass(IRI.create(almondIri.toString() + "#OpenAuthentication"));
			OWLClass discoveryAuthentication = df.getOWLClass(IRI.create(almondIri.toString() + "#DiscoveryAuthentication"));
			//creo l'assioma BasicAuthentication subClassOf AuthenticationType e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(basicAuthentication,authenticationType);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma OpenAuthentication subClassOf AuthenticationType e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(openAuthentication,authenticationType);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma DiscoveryAuthentication subClassOf AuthenticationType e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(discoveryAuthentication,authenticationType);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma BasicAuthentication DisjointWith OpenAuthentication e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(basicAuthentication,openAuthentication);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma BasicAuthentication DisjointWith DiscoveryAuthentication e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(basicAuthentication,discoveryAuthentication);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma OpenAuthentication DisjointWith DiscoveryAuthentication e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(openAuthentication,discoveryAuthentication);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//istanzio le classi Function, Query ed Action
			OWLClass function = df.getOWLClass(IRI.create(almondIri.toString() + "#Function"));
			OWLClass query = df.getOWLClass(IRI.create(almondIri.toString() + "#Query"));
			OWLClass action = df.getOWLClass(IRI.create(almondIri.toString() + "#Action"));
			//creo l'assioma Function subClassOf iot-lite:Service e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(function,iotLiteService);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma Query subClassOf Function e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(query,function);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma Action subClassOf Function e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(action,function);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma Query DisjointWith Action e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(query,action);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//istanzio la classe Qualifier, costruisco l'assioma corrispondente e lo aggiungo
			OWLClass qualifier = df.getOWLClass(IRI.create(almondIri.toString() + "#Qualifier"));
			da = df.getOWLDeclarationAxiom(qualifier);
			this.manager.addAxiom(ontology,da);
			//istanzio la classe Parameter, costruisco l'assioma corrispondente e lo aggiungo
			OWLClass parameter = df.getOWLClass(IRI.create(almondIri.toString() + "#Parameter"));
			da = df.getOWLDeclarationAxiom(parameter);
			this.manager.addAxiom(ontology,da);
			//istanzio le classi DataType, PrimitiveType ed ComplexType
			OWLClass dataType = df.getOWLClass(IRI.create(almondIri.toString() + "#DataType"));
			OWLClass primitiveType = df.getOWLClass(IRI.create(almondIri.toString() + "#PrimitiveType"));
			OWLClass complexType = df.getOWLClass(IRI.create(almondIri.toString() + "#ComplexType"));
			//creo l'assioma PrimitiveType subClassOf DataType e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(primitiveType,dataType);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma ComplexType subClassOf DataType e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(complexType,dataType);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma PrimitiveType DisjointWith ComplexType e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(primitiveType,complexType);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//istanzio le classi Boolean, String e Number
			OWLClass booleanClass = df.getOWLClass(IRI.create(almondIri.toString() + "#Boolean"));
			OWLClass stringClass = df.getOWLClass(IRI.create(almondIri.toString() + "#String"));
			OWLClass number = df.getOWLClass(IRI.create(almondIri.toString() + "#Number"));
			//creo l'assioma Booelan subClassOf PrimitiveType e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(booleanClass,primitiveType);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma String subClassOf PrimitiveType e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(stringClass,primitiveType);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma Number subClassOf PrimitiveType e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(number,primitiveType);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma Boolean DisjointWith String e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(booleanClass,stringClass);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Boolean DisjointWith Number e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(booleanClass,number);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma String DisjointWith Number e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(stringClass,number);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//istanzio le classi Enumeration, Entity, Measure, Currency, Date, Time, Location, Array e Tuple
			OWLClass enumClass = df.getOWLClass(IRI.create(almondIri.toString() + "#Enumeration"));
			OWLClass entity = df.getOWLClass(IRI.create(almondIri.toString() + "#Entity"));
			OWLClass measure = df.getOWLClass(IRI.create(almondIri.toString() + "#Measure"));
			OWLClass currency = df.getOWLClass(IRI.create(almondIri.toString() + "#Currency"));
			OWLClass date = df.getOWLClass(IRI.create(almondIri.toString() + "#Date"));
			OWLClass time = df.getOWLClass(IRI.create(almondIri.toString() + "#Time"));
			OWLClass location = df.getOWLClass(IRI.create(almondIri.toString() + "#Location"));
			OWLClass array = df.getOWLClass(IRI.create(almondIri.toString() + "#Array"));
			OWLClass tuple = df.getOWLClass(IRI.create(almondIri.toString() + "#Tuple"));
			//creo l'assioma Enumeration subClassOf ComplexType e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(enumClass,complexType);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma Entity subClassOf ComplexType e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(entity,complexType);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma Measure subClassOf ComplexType e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(measure,complexType);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma Currency subClassOf ComplexType e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(currency,complexType);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma Date subClassOf ComplexType e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(date,complexType);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma Time subClassOf ComplexType e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(time,complexType);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma Location subClassOf ComplexType e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(location,complexType);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma Array subClassOf ComplexType e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(array,complexType);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma Tuple subClassOf ComplexType e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(tuple,complexType);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma Enumeration DisjointWith Entity e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(enumClass,entity);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Enumeration DisjointWith Measure e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(enumClass,measure);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Enumeration DisjointWith Currency e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(enumClass,currency);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Enumeration DisjointWith Date e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(enumClass,date);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Enumeration DisjointWith Time e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(enumClass,time);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Enumeration DisjointWith Location e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(enumClass,location);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Enumeration DisjointWith Array e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(enumClass,array);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Enumeration DisjointWith Tuple e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(enumClass,tuple);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Entity DisjointWith Measure e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(entity,measure);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Entity DisjointWith Currency e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(entity,currency);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Entity DisjointWith Date e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(entity,date);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Entity DisjointWith Time e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(entity,time);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Entity DisjointWith Location e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(entity,location);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Entity DisjointWith Array e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(entity,array);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Entity DisjointWith Tuple e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(entity,tuple);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Measure DisjointWith Currency e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(measure,currency);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Measure DisjointWith Date e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(measure,date);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Measure DisjointWith Time e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(measure,time);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Measure DisjointWith Location e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(measure,location);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Measure DisjointWith Array e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(measure,array);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Measure DisjointWith Tuple e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(measure,tuple);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Currency DisjointWith Date e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(currency,date);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Currency DisjointWith Time e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(currency,time);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Currency DisjointWith Location e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(currency,location);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Currency DisjointWith Array e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(currency,array);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Currency DisjointWith Tuple e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(currency,tuple);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Date DisjointWith Time e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(date,time);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Date DisjointWith Location e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(date,location);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Date DisjointWith Array e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(date,array);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Date DisjointWith Tuple e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(date,tuple);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Time DisjointWith Location e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(time,location);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Time DisjointWith Array e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(time,array);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Time DisjointWith Tuple e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(time,tuple);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Location DisjointWith Array e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(location,array);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Location DisjointWith Tuple e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(location,tuple);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//creo l'assioma Array DisjointWith Tuple e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(array,tuple);
			this.manager.addAxiom(ontology,disjointClassesAxiom);
			//istanzio la classe Element, costruisco l'assioma corrispondente e lo aggiungo
			OWLClass element = df.getOWLClass(IRI.create(almondIri.toString() + "#Element"));
			da = df.getOWLDeclarationAxiom(element);
			this.manager.addAxiom(ontology,da);
			//istanzio le classi Annotation, NaturalLanguageAnnotation e ImplementationAnnotation
			OWLClass annotation = df.getOWLClass(IRI.create(almondIri.toString() + "#Annotation"));
			OWLClass nlAnnotation = df.getOWLClass(IRI.create(almondIri.toString() + "#NaturalLanguageAnnotation"));
			OWLClass implAnnotation = df.getOWLClass(IRI.create(almondIri.toString() + "#ImplementationAnnotation"));
			//creo l'assioma NaturalLanguageAnnotation subClassOf Annotation e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(nlAnnotation,annotation);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma ImplementationAnnotation subClassOf Annotation e lo aggiungo
			subClassOfAxiom = df.getOWLSubClassOfAxiom(implAnnotation,annotation);
			this.manager.addAxiom(ontology,subClassOfAxiom);
			//creo l'assioma NaturalLanguageAnnotation DisjointWith ImplementationAnnotation e lo aggiungo
			disjointClassesAxiom = df.getOWLDisjointClassesAxiom(nlAnnotation,implAnnotation);
			this.manager.addAxiom(ontology,disjointClassesAxiom);			
			//istanzio la classe InputParameter, costruisco l'assioma corrispondente e lo aggiungo
			OWLClass inputParameter = df.getOWLClass(IRI.create(almondIri.toString() + "#InputParameter"));
			da = df.getOWLDeclarationAxiom(inputParameter);
			this.manager.addAxiom(ontology,da);
			//istanzio la classe Failover, costruisco l'assioma corrispondente e lo aggiungo
			OWLClass failoverClass = df.getOWLClass(IRI.create(almondIri.toString() + "#Failover"));
			da = df.getOWLDeclarationAxiom(failoverClass);
			this.manager.addAxiom(ontology,da);

			//assiomi sulle proprietà

			//creo la proprietà hasLoader
			OWLObjectProperty hasLoader = df.getOWLObjectProperty(IRI.create(almondIri.toString() + "#hasLoader"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			OWLObjectPropertyDomainAxiom  domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(hasLoader,deviceClass);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			OWLObjectPropertyRangeAxiom rangeObjPropAxiom = df.getOWLObjectPropertyRangeAxiom(hasLoader,loader);
			this.manager.addAxiom(ontology,rangeObjPropAxiom);
			//creo gli assiomi relativi alla caratteristiche Functional e InverseFunctional, poi li aggiungo
			OWLFunctionalObjectPropertyAxiom functObjPropAxiom = df.getOWLFunctionalObjectPropertyAxiom(hasLoader);
			this.manager.addAxiom(ontology,functObjPropAxiom);
			OWLInverseFunctionalObjectPropertyAxiom invFunctObjPropAxiom = df.getOWLInverseFunctionalObjectPropertyAxiom(hasLoader);
			this.manager.addAxiom(ontology,invFunctObjPropAxiom);

			//creo la proprietà hasConfig
			OWLObjectProperty hasConfig = df.getOWLObjectProperty(IRI.create(almondIri.toString() + "#hasConfig"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(hasConfig,deviceClass);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			rangeObjPropAxiom = df.getOWLObjectPropertyRangeAxiom(hasConfig,configuration);
			this.manager.addAxiom(ontology,rangeObjPropAxiom);
			//creo gli assiomi relativi alla caratteristiche Functional e InverseFunctional, poi li aggiungo
			functObjPropAxiom = df.getOWLFunctionalObjectPropertyAxiom(hasConfig);
			this.manager.addAxiom(ontology,functObjPropAxiom);
			invFunctObjPropAxiom = df.getOWLInverseFunctionalObjectPropertyAxiom(hasConfig);
			this.manager.addAxiom(ontology,invFunctObjPropAxiom);

			//creo la proprietà hasFunction
			OWLObjectProperty hasFunction = df.getOWLObjectProperty(IRI.create(almondIri.toString() + "#hasFunction"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(hasFunction,deviceClass);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			rangeObjPropAxiom = df.getOWLObjectPropertyRangeAxiom(hasFunction,function);
			this.manager.addAxiom(ontology,rangeObjPropAxiom);
			//creo l'assioma relativo alla caratteristiche InverseFunctional, poi lo aggiungo
			invFunctObjPropAxiom = df.getOWLInverseFunctionalObjectPropertyAxiom(hasFunction);
			this.manager.addAxiom(ontology,invFunctObjPropAxiom);

			//creo la proprietà hasAnnotation
			OWLObjectProperty hasAnnotation = df.getOWLObjectProperty(IRI.create(almondIri.toString() + "#hasAnnotation"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(hasAnnotation,deviceClass);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(hasAnnotation,function);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(hasAnnotation,parameter);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			rangeObjPropAxiom = df.getOWLObjectPropertyRangeAxiom(hasAnnotation,annotation);
			this.manager.addAxiom(ontology,rangeObjPropAxiom);
			//creo l'assioma relativo alla caratteristiche InverseFunctional, poi lo aggiungo
			invFunctObjPropAxiom = df.getOWLInverseFunctionalObjectPropertyAxiom(hasAnnotation);
			this.manager.addAxiom(ontology,invFunctObjPropAxiom);

			//creo la proprietà failover
			OWLObjectProperty failover = df.getOWLObjectProperty(IRI.create(almondIri.toString() + "#failover"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(failover,deviceClass);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			rangeObjPropAxiom = df.getOWLObjectPropertyRangeAxiom(failover,failoverClass);
			this.manager.addAxiom(ontology,rangeObjPropAxiom);
			//creo l'assioma relativo alla caratteristiche InverseFunctional, poi lo aggiungo
			invFunctObjPropAxiom = df.getOWLInverseFunctionalObjectPropertyAxiom(failover);
			this.manager.addAxiom(ontology,invFunctObjPropAxiom);

			//creo la proprietà run
			OWLObjectProperty run = df.getOWLObjectProperty(IRI.create(almondIri.toString() + "#run"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(run,failoverClass);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			rangeObjPropAxiom = df.getOWLObjectPropertyRangeAxiom(run,deviceClass);
			this.manager.addAxiom(ontology,rangeObjPropAxiom);
			//creo l'assioma relativo alla caratteristica Functional poi lo aggiungo
			functObjPropAxiom = df.getOWLFunctionalObjectPropertyAxiom(run);
			this.manager.addAxiom(ontology,functObjPropAxiom);

			//creo la proprietà hasAuthType
			OWLObjectProperty hasAuthType = df.getOWLObjectProperty(IRI.create(almondIri.toString() + "#hasAuthType"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(hasAuthType,complexConfiguration);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			rangeObjPropAxiom = df.getOWLObjectPropertyRangeAxiom(hasAuthType,authenticationType);
			this.manager.addAxiom(ontology,rangeObjPropAxiom);
			//creo gli assiomi relativi alla caratteristiche Functional e InverseFunctional, poi li aggiungo
			functObjPropAxiom = df.getOWLFunctionalObjectPropertyAxiom(hasAuthType);
			this.manager.addAxiom(ontology,functObjPropAxiom);
			invFunctObjPropAxiom = df.getOWLInverseFunctionalObjectPropertyAxiom(hasAuthType);
			this.manager.addAxiom(ontology,invFunctObjPropAxiom);

			//creo la proprietà hasParameter
			OWLObjectProperty hasParameter = df.getOWLObjectProperty(IRI.create(almondIri.toString() + "#hasParameter"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(hasParameter,basicConfiguration);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(hasParameter,authenticationType);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			rangeObjPropAxiom = df.getOWLObjectPropertyRangeAxiom(hasParameter,parameter);
			this.manager.addAxiom(ontology,rangeObjPropAxiom);
			//creo l'assioma relativo alla caratteristiche InverseFunctional, poi lo aggiungo
			invFunctObjPropAxiom = df.getOWLInverseFunctionalObjectPropertyAxiom(hasParameter);
			this.manager.addAxiom(ontology,invFunctObjPropAxiom);

			//creo la proprietà hasInputParam
			OWLObjectProperty hasInputParam = df.getOWLObjectProperty(IRI.create(almondIri.toString() + "#hasInputParam"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(hasInputParam,function);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			rangeObjPropAxiom = df.getOWLObjectPropertyRangeAxiom(hasInputParam,inputParameter);
			this.manager.addAxiom(ontology,rangeObjPropAxiom);
			//creo l'assioma relativo alla caratteristiche InverseFunctional, poi lo aggiungo
			invFunctObjPropAxiom = df.getOWLInverseFunctionalObjectPropertyAxiom(hasInputParam);
			this.manager.addAxiom(ontology,invFunctObjPropAxiom);

			//creo la proprietà connectTo
			OWLObjectProperty connectTo = df.getOWLObjectProperty(IRI.create(almondIri.toString() + "#connectTo"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(connectTo,inputParameter);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			rangeObjPropAxiom = df.getOWLObjectPropertyRangeAxiom(connectTo,parameter);
			this.manager.addAxiom(ontology,rangeObjPropAxiom);
			//creo gli assiomi relativi alla caratteristiche Functional e InverseFunctional, poi li aggiungo
			functObjPropAxiom = df.getOWLFunctionalObjectPropertyAxiom(connectTo);
			this.manager.addAxiom(ontology,functObjPropAxiom);
			invFunctObjPropAxiom = df.getOWLInverseFunctionalObjectPropertyAxiom(connectTo);
			this.manager.addAxiom(ontology,invFunctObjPropAxiom);

			//creo la proprietà hasQualifier
			OWLObjectProperty hasQualifier = df.getOWLObjectProperty(IRI.create(almondIri.toString() + "#hasQualifier"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(hasQualifier,query);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			rangeObjPropAxiom = df.getOWLObjectPropertyRangeAxiom(hasQualifier,qualifier);
			this.manager.addAxiom(ontology,rangeObjPropAxiom);
			//creo l'assioma relativo alla caratteristiche InverseFunctional, poi lo aggiungo
			invFunctObjPropAxiom = df.getOWLInverseFunctionalObjectPropertyAxiom(hasQualifier);
			this.manager.addAxiom(ontology,invFunctObjPropAxiom);

			//creo la proprietà hasOutputParam
			OWLObjectProperty hasOutputParam = df.getOWLObjectProperty(IRI.create(almondIri.toString() + "#hasOutputParam"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(hasOutputParam,query);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			rangeObjPropAxiom = df.getOWLObjectPropertyRangeAxiom(hasOutputParam,parameter);
			this.manager.addAxiom(ontology,rangeObjPropAxiom);
			//creo l'assioma relativo alla caratteristiche InverseFunctional, poi lo aggiungo
			invFunctObjPropAxiom = df.getOWLInverseFunctionalObjectPropertyAxiom(hasOutputParam);
			this.manager.addAxiom(ontology,invFunctObjPropAxiom);

			//creo la proprietà hasDataType
			OWLObjectProperty hasDataType = df.getOWLObjectProperty(IRI.create(almondIri.toString() + "#hasDataType"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(hasDataType,parameter);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			rangeObjPropAxiom = df.getOWLObjectPropertyRangeAxiom(hasDataType,dataType);
			this.manager.addAxiom(ontology,rangeObjPropAxiom);
			//creo l'assioma relativo alla caratteristica Functional, poi la aggiungo
			functObjPropAxiom = df.getOWLFunctionalObjectPropertyAxiom(hasDataType);
			this.manager.addAxiom(ontology,functObjPropAxiom);

			//creo la proprietà isTypeOf
			OWLObjectProperty isTypeOf = df.getOWLObjectProperty(IRI.create(almondIri.toString() + "#isTypeOf"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(isTypeOf,element);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			rangeObjPropAxiom = df.getOWLObjectPropertyRangeAxiom(isTypeOf,dataType);
			this.manager.addAxiom(ontology,rangeObjPropAxiom);
			//creo l'assioma relativo alla caratteristica Functional, poi la aggiungo
			functObjPropAxiom = df.getOWLFunctionalObjectPropertyAxiom(isTypeOf);
			this.manager.addAxiom(ontology,functObjPropAxiom);

			//creo la proprietà isElement
			OWLObjectProperty isElement = df.getOWLObjectProperty(IRI.create(almondIri.toString() + "#isElement"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(isElement,annotation);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			rangeObjPropAxiom = df.getOWLObjectPropertyRangeAxiom(isElement,element);
			this.manager.addAxiom(ontology,rangeObjPropAxiom);
			//creo gli assiomi relativi alla caratteristiche Functional e InverseFunctional, poi li aggiungo
			functObjPropAxiom = df.getOWLFunctionalObjectPropertyAxiom(isElement);
			this.manager.addAxiom(ontology,functObjPropAxiom);
			invFunctObjPropAxiom = df.getOWLInverseFunctionalObjectPropertyAxiom(isElement);
			this.manager.addAxiom(ontology,invFunctObjPropAxiom);

			//creo la proprietà hasDeviceClass
			OWLObjectProperty hasDeviceClass = df.getOWLObjectProperty(IRI.create(almondIri.toString() + "#hasDeviceClass"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			domainObjPropAxiom = df.getOWLObjectPropertyDomainAxiom(hasDeviceClass,ssnDevice);
			this.manager.addAxiom(ontology,domainObjPropAxiom);
			rangeObjPropAxiom = df.getOWLObjectPropertyRangeAxiom(hasDeviceClass,deviceClass);
			this.manager.addAxiom(ontology,rangeObjPropAxiom);
			//creo gli assiomi relativi alla caratteristiche Functional e InverseFunctional, poi li aggiungo
			functObjPropAxiom = df.getOWLFunctionalObjectPropertyAxiom(hasDeviceClass);
			this.manager.addAxiom(ontology,functObjPropAxiom);
			invFunctObjPropAxiom = df.getOWLInverseFunctionalObjectPropertyAxiom(hasDeviceClass);
			this.manager.addAxiom(ontology,invFunctObjPropAxiom);

			//assiomi sugli attributi

			//creo l'attributo priority
			OWLDataProperty priority = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#priority"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			OWLDataPropertyDomainAxiom dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(priority,failoverClass);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			OWLDataPropertyRangeAxiom dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(priority,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_NON_NEGATIVE_INTEGER));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo deviceClassId
			OWLDataProperty deviceClassId = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#deviceClassId"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(deviceClassId,deviceClass);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(deviceClassId,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_STRING));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo apiKey
			OWLDataProperty apiKey = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#apiKey"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(apiKey,apiKeyConfiguration);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(apiKey,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_STRING));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo clientId
			OWLDataProperty clientId = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#clientId"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(clientId,openAuthentication);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(clientId,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_STRING));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo clientSecret
			OWLDataProperty clientSecret = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#clientSecret"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(clientSecret,openAuthentication);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(clientSecret,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_STRING));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo protocol
			OWLDataProperty protocol = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#protocol"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(protocol,discoveryAuthentication);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(protocol,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_STRING));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo isRequested
			OWLDataProperty isParamRequested = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#isParamRequested"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(isParamRequested,inputParameter);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(isParamRequested,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_BOOLEAN));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo functionId
			OWLDataProperty functionId = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#functionId"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(functionId,function);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(functionId,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_STRING));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo qualifierName
			OWLDataProperty qualifierName = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#qualifierName"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(qualifierName,qualifier);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(qualifierName,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_STRING));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo parameterName
			OWLDataProperty parameterName = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#parameterName"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(parameterName,parameter);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(parameterName,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_STRING));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo entityName
			OWLDataProperty entityName = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#entityName"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(entityName,entity);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(entityName,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_STRING));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo entityId
			OWLDataProperty entityId = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#entityId"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(entityId,entity);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(entityId,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_STRING));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo unitValue
			OWLDataProperty unitValue = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#unitValue"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(unitValue,measure);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(unitValue,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_STRING));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo elementValue
			OWLDataProperty elementValue = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#elementValue"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(elementValue,element);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(elementValue,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_STRING));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo annotationKey
			OWLDataProperty annotationKey = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#annotationKey"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(annotationKey,annotation);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(annotationKey,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_STRING));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo isAnnRequested
			OWLDataProperty isAnnRequested = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#isAnnRequested"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(isAnnRequested,annotation);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(isAnnRequested,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_BOOLEAN));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo deviceDescription
			OWLDataProperty deviceDescription = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#deviceDescription"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(deviceDescription,ssnDevice);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(deviceDescription,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_STRING));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo deviceCategory
			OWLDataProperty deviceCategory = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#deviceCategory"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(deviceCategory,ssnDevice);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(deviceCategory,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_STRING));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo deviceWebSite
			OWLDataProperty deviceWebSite = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#deviceWebSite"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(deviceWebSite,ssnDevice);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(deviceWebSite,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_ANY_URI));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo deviceSourceCode
			OWLDataProperty deviceSourceCode = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#deviceSourceCode"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(deviceSourceCode,ssnDevice);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(deviceSourceCode,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_ANY_URI));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);

			//creo l'attributo deviceFaults
			OWLDataProperty deviceFaults = df.getOWLDataProperty(IRI.create(almondIri.toString() + "#deviceFaults"));
			//specifico dominio e codominio, poi aggiungo gli assiomi all'ontologia
			dataPropDomainAxiom = df.getOWLDataPropertyDomainAxiom(deviceFaults,ssnDevice);
			this.manager.addAxiom(ontology,dataPropDomainAxiom);
			dataPropRangeAxiom = df.getOWLDataPropertyRangeAxiom(deviceFaults,
			new OWL2DatatypeImpl(OWL2Datatype.XSD_ANY_URI));
			this.manager.addAxiom(ontology,dataPropRangeAxiom);						

			return ontology;
		}

	}