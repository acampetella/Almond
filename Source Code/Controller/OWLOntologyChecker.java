//classe per verificare la concistenza di una ontologia

	import org.semanticweb.owlapi.model.OWLOntology;
	import org.semanticweb.owlapi.reasoner.OWLReasoner;
	import org.semanticweb.HermiT.ReasonerFactory;
	import org.semanticweb.owlapi.model.OWLAxiom;
	import org.semanticweb.owlapi.model.parameters.Imports;
	import java.util.Set;
	import org.semanticweb.owlapi.model.OWLOntologyManager;
	import java.util.ArrayList;

	public class OWLOntologyChecker
	{
		private OWLOntology ontology; //ontologia da verificare

		//costruttore
		public OWLOntologyChecker(OWLOntology o)
		{
			this.ontology = o;
		}

		//verifica se l'ontologia è consistente
		public boolean isConsistent()
		{
			ReasonerFactory rf = new ReasonerFactory();
			OWLReasoner r = rf.createNonBufferingReasoner(this.ontology);
			boolean result = r.isConsistent();
			r.dispose();
			return result;
		}

		//verifica la ABox e restituisce gli assiomi che generano eventuali inconsistenze
		public OWLAxiom[] checkABox()
		{
			Set<OWLAxiom> set = this.ontology.getABoxAxioms(Imports.INCLUDED);
			OWLOntologyManager m = this.ontology.getOWLOntologyManager();
			for(OWLAxiom a : set)
				m.removeAxiom(this.ontology,a);
			ArrayList<OWLAxiom> list = new ArrayList<OWLAxiom>();
			for(OWLAxiom a : set)
			{
				m.addAxiom(this.ontology,a);
				if (!this.isConsistent())
					list.add(a);
			}
			OWLAxiom result[] = new OWLAxiom[list.size()];
			return list.toArray(result);
		}
			
	}