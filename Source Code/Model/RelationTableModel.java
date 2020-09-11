//classe che implementa un modello di tabella per visualizzare oggetti Relationship
//il modello preve de due colonne per visualizzare il nome e l'individuo associato alla relazione

	import javax.swing.table.DefaultTableModel;

	public class RelationTableModel extends DefaultTableModel
	{
		private Relationship relations[]; //relazioni da visualizzare

		//costruttore
		public RelationTableModel(Relationship rel[])
		{
			super(rel.length,2);
			this.relations = rel;
		}

		//restituisce il nome della colonna
		public String getColumnName(int column)
		{
			String result = "";
			switch(column)
			{
				case 0 :
					result = "Name";
					break;
				case 1 :
					result = "Individual";
					break;
			}
			return result;
		}

		//restituisce il valore di ogni singola cella
		public Object getValueAt(int row, int column)
		{
			String result = "";
			switch(column)
			{
				case 0 :
					result = this.relations[row].getName();
					break;
				case 1 :
					result = this.relations[row].getIndividual().getName();
					break;
			}
			return result;
		}

		//restituisce true se la cella è modificabile
		public boolean isCellEditable(int row, int column)
		{
			return false;
		}
	}