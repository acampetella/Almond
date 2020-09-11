//classe che implementa un modello di tabella per visualizzare oggetti Attribute
//il modello preve de tre colonne per visualizzare il nome, il tipo di dato e il valore dell'attributo

	import javax.swing.table.DefaultTableModel;

	public class AttributeTableModel extends DefaultTableModel
	{
		private Attribute attributes[]; //attributi da visualizzare

		//costruttore
		public AttributeTableModel(Attribute att[])
		{
			super(att.length,3);
			this.attributes = att;
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
					result = "Datatype";
					break;
				case 2 :
					result = "Value";
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
					result = this.attributes[row].getName();
					break;
				case 1 :
					result = this.attributes[row].getDatatype().toString();
					break;
				case 2 :
					result = this.attributes[row].getValue();
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