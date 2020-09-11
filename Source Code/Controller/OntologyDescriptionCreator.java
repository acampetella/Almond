//classe che consente di creare una descrizione basata su un'ontologia

	import org.semanticweb.owlapi.model.IRI;
	import org.semanticweb.owlapi.model.OWLDatatype;

	public class OntologyDescriptionCreator
	{
		private Ontology ontology; //ontlogia di base
		private String prefix; //prefisso utilizzato per creare le IRI degli individui
		private OntologyDescription description; //descrizione

		//costruttore
		public OntologyDescriptionCreator(Ontology o)
		{
			this.ontology = o;
			this.description = new OntologyDescription();
		}

		//altro costruttore
		public OntologyDescriptionCreator(Ontology o, String prefix)
		{
			this(o);
			this.setPrefix(prefix);
		}

		//imposta il prefisso
		public void setPrefix(String s)
		{
			this.prefix = s;
		}

		//crea un individuo e lo restituisce
		//restituisce null se la classe dell'individuo non è contenuta nell'ontologia
		public OntologyIndividual createIndividual(String name, OntologyClass c)
		{
			if (!this.ontology.contains(c))
				return null;
			IRI iri = IRI.create(this.getPrefix() + "#" + name);
			return new OntologyIndividual(c,iri);	
		}

		//crea un individuo e lo restituisce
		//restituisce null se la classe dell'individuo non è contenuta nell'ontologia
		public OntologyIndividual createIndividual(String name, String className)
		{
			OntologyClass total[] = this.ontology.getAllClasses();
			OntologyClass selected = null;
			for(OntologyClass c : total)
			{
				if (c.getName().equals(className))
				{
					selected = c;
					break;
				}
			}
			if (selected == null)
				return null;
			return this.createIndividual(name,selected);	
		}

		//imposta il valore di un attributo di un individuo
		public void setAttributeValue(OntologyIndividual ind, Attribute a, String value)
		{
			ind.setAttributeValue(a,value);
		}

		//imposta il valore di un attributo di un individuo
		public void setAttributeValue(OntologyIndividual ind, String attName, String value)
		{
			OntologyClass c = ind.getType();
			Attribute attributes[] = c.getAttributes();
			for(Attribute a : attributes)
			{
				if (a.getName().equals(attName))
				{
					ind.setAttributeValue(a,value);
					break;
				}
			}
		}
		
		//rimuove un attributo di un individuo
		public void removeAttribute(OntologyIndividual ind, Attribute a)
		{
			ind.removeAttribute(a);
		}

		//imposta il valore di una relazione di un individuo
		public void setRelationValue(Relationship r, OntologyIndividual ind1, OntologyIndividual ind2)
		{
			ind1.setRelationValue(r,ind2);	
		}

		//imposta il valore di una relazione di un individuo
		public void setRelationValue(String relName, OntologyIndividual ind1, OntologyIndividual ind2)
		{
			OntologyClass c = ind1.getType();
			Relationship relations[] = c.getRelations();
			for(Relationship r : relations)
			{
				if (r.getName().equals(relName))
				{
					ind1.setRelationValue(r,ind2);
					break;	
				}	
			}	
		}

		//rimuove una relazione di un individuo
		public void removeRelation(OntologyIndividual ind, Relationship r)
		{
			ind.removeRelation(r);
		}

		//aggiunge un individuo alla descrizione
		public void addToDescription(OntologyIndividual ind)
		{
			if (this.ontology.contains(ind.getType()))
				this.description.addIndividual(ind);	
		}

		//rimuove un individuo dalla descrizione
		public void removeFromDescription(OntologyIndividual ind)
		{
			if (this.ontology.contains(ind.getType()))
				this.description.removeIndividual(ind);
		}

		//restituisce la descrizione
		public OntologyDescription getDescription()
		{
			return this.description;
		}

		//restituisce il prefisso
		public String getPrefix()
		{
			return this.prefix;
		}
	}