//classe che implementa un attributo di un individuo dell'ontologia

	import org.semanticweb.owlapi.model.OWLDataProperty;
	import org.semanticweb.owlapi.model.OWLDatatype;
	import org.semanticweb.owlapi.model.OWLLiteral;
	import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
	import org.semanticweb.owlapi.model.IRI;

	public class Attribute implements Comparable<Attribute>
	{
		private OWLDataProperty property; //proprietà che rappresenta l'attributo
		private OWLDatatype datatype; //definisce il tipo di dato
		private String value; //valore dell'attributo

		//costruttore
		public Attribute(OWLDataProperty p, OWLDatatype dt)
		{
			this.property = p;
			this.datatype = dt;
		}

		//imposta il valore
		public void setValue(String v)
		{
			this.value = v;
		}

		//metodi get

		public OWLDataProperty getDataProperty()
		{
			return this.property;
		}

		public OWLDatatype getDatatype()
		{
			return this.datatype;
		}

		public String getValue()
		{
			return this.value;
		}

		public OWLLiteral getValueAsLiteral()
		{
			OWLDataFactoryImpl df = new OWLDataFactoryImpl();
			return df.getOWLLiteral(this.getValue(),this.getDatatype());
		}

		public String getName()
		{
			String iri = this.getIRI().toString();
			String arr[] = iri.split("#");
			return arr[arr.length - 1];
		}

		//restituisce l'IRI dell'attributo
		public IRI getIRI()
		{
			return this.getDataProperty().getIRI();
		}

		//implementazione dell'interfaccia Comparable
		public int compareTo(Attribute a)
		{
			return this.getDataProperty().compareTo(a.getDataProperty());
		}

		//sovrascrive il metodo equals di Object
		public boolean equals(Object o)
		{
			Attribute a = (Attribute) o;
			int diff = this.compareTo(a);
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