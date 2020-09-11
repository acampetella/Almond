//classe che implementa un'ontologia
//oggetto composto da un insieme di OntologyClass

	import java.util.ArrayList;
	import org.semanticweb.owlapi.model.IRI;

	public class Ontology
	{
		private IRI iri; //IRI dell'ontologia		
		private ArrayList<OntologyClass> classes; //lista totale delle classi

		//costruttore
		public Ontology(IRI iri)
		{
			this.iri = iri;
			this.classes = new ArrayList<OntologyClass>();
		}

		//imposta la lista totale delle classi
		public void setClasses(OntologyClass arr[])
		{
			for(OntologyClass c : arr)
				this.classes.add(c);		
		}

		//aggiunge una classe
		public void addClass(OntologyClass c)
		{
			this.classes.add(c);	
		}

		//rimuove una classe
		public void removeClass(OntologyClass c)
		{
			this.classes.remove(c);	
		}

		//verifica se l'ontologia contiene la classe specificata
		public boolean contains(OntologyClass c)
		{
			for(OntologyClass cls : this.classes)
			{
				if (cls.equals(c))
					return true;
			}
			return false;
		}

		//restituisce la lista totale delle classi dell'ontologia
		public OntologyClass[] getAllClasses()
		{
			OntologyClass arr[] = new OntologyClass[this.classes.size()];
			return this.classes.toArray(arr);
		}

		//restituisce la lista delle root class
		public OntologyClass[] getRootClasses()
		{
			ArrayList<OntologyClass> list = new ArrayList<OntologyClass>();
			OntologyClass arr[] = this.getAllClasses();
			for(OntologyClass c : arr)
			{
				if (c.isRoot())
					list.add(c);
			}
			OntologyClass result[] = new OntologyClass[list.size()];
			result = list.toArray(result);
			return result;
		}			

		//restitisce l'IRI
		public IRI getIRI()
		{
			return this.iri;
		}

	}