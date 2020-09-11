//classe che implementa una descrizione basata su un'ontologia

	import java.util.ArrayList;
	import org.semanticweb.owlapi.model.IRI;

	public class OntologyDescription
	{
		private ArrayList<OntologyIndividual> individuals; //lista degli individui della descrizione

		//costruttore
		public OntologyDescription()
		{
			this.individuals = new ArrayList<OntologyIndividual>();
		}

		//aggiunge un individuo alla descrizione
		public void addIndividual(OntologyIndividual ind)
		{
			this.individuals.add(ind);	
		}

		//rimuove un individuo dalla descrizione
		public void removeIndividual(OntologyIndividual ind)
		{
			//rimuovo prima le relazioni che hanno ind come oggetto connesso
			for(OntologyIndividual individual : this.individuals)
			{
				Relationship rels[] = individual.getRelations();
				for(Relationship r : rels)
				{
					if (r.getIndividual().equals(ind))
						individual.removeRelation(r);
				}
			}
			this.individuals.remove(ind);
		}

		//restituisce la lista degli individui
		public OntologyIndividual[] getIndividuals()
		{
			OntologyIndividual arr[] = new OntologyIndividual[this.individuals.size()];
			arr = this.individuals.toArray(arr);
			return arr;
		}

		//restituisce la lista degli individui di una specifica classe
		public OntologyIndividual[] getIndividualsOfClass(OntologyClass c)
		{
			ArrayList<OntologyIndividual> list = new ArrayList<OntologyIndividual>();
			for(OntologyIndividual ind : this.individuals)
			{
				if (ind.getType().equals(c))
					list.add(ind);
			}
			OntologyIndividual result[] = new OntologyIndividual[list.size()];
			result = list.toArray(result);
			return result;
		}

		//restituisce un individuo con l'IRI specificato
		public OntologyIndividual getIndividual(IRI iri)
		{
			OntologyIndividual arr[] = this.getIndividuals();
			OntologyIndividual result = null;
			for(OntologyIndividual ind : arr)
			{
				if (ind.getIRI().compareTo(iri) == 0)
				{
					result = ind;
					break;
				}
			}
			return result;	
		}
	}