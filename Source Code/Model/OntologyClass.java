//classe che implementa una classe di un'ontologia

	import org.semanticweb.owlapi.model.OWLClass;
	import org.semanticweb.owlapi.model.OWLDataProperty;
	import org.semanticweb.owlapi.model.OWLObjectProperty;
	import java.util.ArrayList;
	import org.semanticweb.owlapi.model.IRI;

	public class OntologyClass implements Comparable<OntologyClass>
	{
		private OWLClass type; //classe di appartenenza
		private ArrayList<Attribute> attributes; //lista degli attributi
		private ArrayList<Relationship> relations; //lista delle relazioni
		private OntologyClass parent; //classe padre
		private ArrayList<OntologyClass> sons; //lista sottoclassi
		private boolean root = false; //indicatore di route

		//costruttore
		public OntologyClass(OWLClass type)
		{
			this.type = type;
			this.attributes = new ArrayList<Attribute>();
			this.relations = new ArrayList<Relationship>();
			this.parent = null;
			this.sons = new ArrayList<OntologyClass>();
		}

		//metodi per manipolare gli attributi

		public void addAttribute(Attribute a)
		{
			this.attributes.add(a);	
		}

		public void removeAttribute(Attribute a)
		{
			this.attributes.remove(a);
		}

		public void setAttributes(Attribute arr[])
		{
			for(Attribute a : arr)
				this.attributes.add(a);
		}

		//metodi per manipolare le relazioni

		public void addRelation(Relationship r)
		{
			this.relations.add(r);	
		}

		public void removeRelation(Relationship r)
		{
			this.relations.remove(r);	
		}

		public void setRelations(Relationship arr[])
		{
			for(Relationship r : arr)
				this.relations.add(r);
		}

		//metodi per manipolare le sottoclassi		

		public void addSon(OntologyClass c)
		{
			this.sons.add(c);	
		}

		public void removeSon(OntologyClass c)
		{
			this.sons.remove(c);	
		}

		public void setSons(OntologyClass arr[])
		{
			for(OntologyClass c : arr)
				this.addSon(c);
		}
		
		//imposta la superclasse
		public void setParent(OntologyClass c)
		{
			this.parent = c;
		}

		//imposta la proprietà di root
		public void setAsRoot(boolean root)
		{
			this.root = root;
		}

		//restituisce la superclasse
		public OntologyClass getParent()
		{
			return this.parent;
		}

		//restituisce il tipo
		public OWLClass getType()
		{
			return this.type;
		}

		//restituisce la lista degli attributi
		public Attribute[] getAttributes()
		{
			Attribute arr[] = new Attribute[this.attributes.size()];
			return this.attributes.toArray(arr);
		}

		//restituisce la lista delle proprietà
		public Relationship[] getRelations()
		{
			Relationship arr[] = new Relationship[this.relations.size()];
			return this.relations.toArray(arr);
		}

		//restituisce la lista delle sottoclassi
		public OntologyClass[] getSons()
		{
			OntologyClass arr[] = new OntologyClass[this.sons.size()];
			return this.sons.toArray(arr);
		}

		//restituisce true se la classe è una root
		public boolean isRoot()
		{
			return this.root;
		}

		//restituisce l'IRI della classe
		public IRI getIRI()
		{
			return this.getType().getIRI();
		}

		//restituisce il nome della classe
		public String getName()
		{
			String str = this.getIRI().toString();
			String arr[] = str.split("#");
			return arr[arr.length - 1];	
		}

		//implementazione dell'interfaccia Comparable
		public int compareTo(OntologyClass c)
		{
			return this.getType().compareTo(c.getType());
		}

		//verifica se la classe contiene l'attributo specificato
		public boolean contains(Attribute a)
		{
			for(Attribute att : this.getAttributes())
			{
				if (att.equals(a))
					return true;
			}
			return false;
		}

		//verifica se la classe contiene la relazione specificata
		public boolean contains(Relationship r)
		{
			for(Relationship rel : this.getRelations())
			{
				if (rel.equals(r))
					return true;
			}
			return false;
		}

		//sovrascrive il metodo di Object
		public String toString()
		{
			return this.getName();
		}

		//sovrascrive il metodo di Object
		public boolean equals(Object o)
		{
			OntologyClass c = (OntologyClass) o;
			if (this.compareTo(c) == 0)
				return true;
			return false;
		}
	}