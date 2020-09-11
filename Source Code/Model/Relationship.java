//classe che implementa una proprietà di un individuo appartenente ad una ontologia

	import org.semanticweb.owlapi.model.OWLObjectProperty;
	import org.semanticweb.owlapi.model.IRI;

	public class Relationship implements Comparable<Relationship>
	{
		private OWLObjectProperty property; //proprietà che rappresenta la relazione
		private OntologyClass range; //codominio della relazione
		private OntologyIndividual individual; //individuo a cui fa riferimento la relazione		

		//costruttore
		public Relationship(OWLObjectProperty p)
		{
			this.property = p;
		}

		//altro costruttore
		public Relationship(OWLObjectProperty p, OntologyClass range)
		{
			this(p);
			this.setRange(range);
		}

		public void setRange(OntologyClass range)
		{
			this.range = range;
		}

		public void setIndividual(OntologyIndividual ind)
		{
			this.individual = ind;
		}

		//metodi get

		public OWLObjectProperty getObjectProperty()
		{
			return this.property;
		}

		public OntologyIndividual getIndividual()
		{
			return this.individual;
		}

		public OntologyClass getRange()
		{
			return this.range;
		}

		public String getName()
		{
			String iri = this.getIRI().toString();
			String arr[] = iri.split("#");
			return arr[arr.length - 1];
		}

		//restituisce l'IRI della relazione
		public IRI getIRI()
		{
			return this.getObjectProperty().getIRI();
		}

		//implementazione dell'interfaccia Comparable
		public int compareTo(Relationship r)
		{
			return this.getObjectProperty().compareTo(r.getObjectProperty());
		}

		//sovrascrive il metodo equals di Object
		public boolean equals(Object o)
		{
			Relationship r = (Relationship) o;
			int diff = this.compareTo(r);
			if (diff == 0)
				return true;
			return false;
		}

		//sovrascrive il metodo di Object
		public String toString()
		{
			return this.getName();
		}
	}