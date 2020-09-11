//classe che implementa un individuo di un'ontologia

	import java.util.ArrayList;
	import org.semanticweb.owlapi.model.OWLDatatype;
	import org.semanticweb.owlapi.model.IRI;
	import org.semanticweb.owlapi.model.OWLLiteral;

	public class OntologyIndividual implements Comparable<OntologyIndividual>
	{
		private IRI iri; //identificativo dell'individuo
		private OntologyClass type; //classe di appartenenza
		private ArrayList<Attribute> attributes; //lista degli attributi
		private ArrayList<Relationship> relations; //lista delle relazioni

		//costruttore
		public OntologyIndividual(OntologyClass type)
		{
			this.type = type;
			this.attributes = new ArrayList<Attribute>();
			this.relations = new ArrayList<Relationship>();
		}

		//altro costruttore
		public OntologyIndividual(OntologyClass type, IRI iri)
		{
			this(type);
			this.setIRI(iri);	
		}

		//imposta l'iri
		public void setIRI(IRI iri)
		{
			this.iri = iri;
		}

		//imposta il valore per un attributo
		public void setAttributeValue(Attribute a, String value)
		{
			Attribute att[] = this.type.getAttributes();
			for(Attribute attribute : att)
			{
				if (attribute.equals(a))
				{
					Attribute newAtt = new Attribute(a.getDataProperty(),a.getDatatype());
					newAtt.setValue(value);
					this.attributes.add(newAtt);
					break;
				}
			}
		}

		//imposta una relazione con un altro individuo
		public void setRelationValue(Relationship r, OntologyIndividual ind)
		{
			Relationship rel[] = this.type.getRelations();
			for(Relationship relation : rel)
			{
				if (relation.equals(r))
				{
					Relationship newRel = new Relationship(r.getObjectProperty());
					if (r.getRange() != null)
						newRel.setRange(r.getRange());
					newRel.setIndividual(ind);
					this.relations.add(newRel);
					break;
				}
			}
		}

		//rimuove un attributo
		public void removeAttribute(Attribute a)
		{
			for(Attribute att : this.attributes)
			{
				if (att.equals(a) && att.getValue().equals(a.getValue()))
				{
					this.attributes.remove(att);
					break;	
				}
			}
		}

		//rimuove una relazione
		public void removeRelation(Relationship r)
		{
			for(Relationship rel : this.relations)
			{
				if (rel.equals(r) && rel.getIndividual().equals(r.getIndividual()))
				{
					this.relations.remove(rel);
					break;	
				}
			}
		}

		//restituisce il parametro iri
		public IRI getIRI()
		{
			return this.iri;
		}

		//restituisce la classe di appartenenza
		public OntologyClass getType()
		{
			return this.type;
		}

		//restituisce la lista degli attributi
		public Attribute[] getAttributes()
		{
			Attribute arr[] = new Attribute[this.attributes.size()];
			return this.attributes.toArray(arr);
		}

		//restituisce la lista delle relazioni
		public Relationship[] getRelations()
		{
			Relationship arr[] = new Relationship[this.relations.size()];
			return this.relations.toArray(arr);
		}

		//restituisce il valore di un attributo come oggetto OWLLiteral
		public OWLLiteral getAttributeValue(Attribute a)
		{
			return a.getValueAsLiteral();
		}

		//restituisce il valore di una relazione, ossia l'individuo connesso a quello corrente secondo la relazione indicata
		public OntologyIndividual getRelationValue(Relationship r)
		{
			return r.getIndividual();
		}

		//implementazione dell'interfaccia Comparable
		public int compareTo(OntologyIndividual ind)
		{
			return this.getIRI().compareTo(ind.getIRI());	
		}

		//sovrascrive il metodo equals di Object
		public boolean equals(Object o)
		{
			OntologyIndividual ind = (OntologyIndividual) o;
			if (this.compareTo(ind) == 0)
				return true;
			return false;
		}

		//sovrascrive il metodo toString di Object
		public String toString()
		{
			return this.getName();
		}

		//restituisce il nome dell'individuo
		public String getName()
		{
			String str = this.getIRI().toString();
			String arr[] = str.split("#");
			return arr[arr.length - 1];	
		}
	}