//classe per modificare il Font di default dell'applicazione

	import java.util.Enumeration;
	import javax.swing.plaf.FontUIResource;
	import javax.swing.UIManager;
	import java.awt.Font;
	

	public class ChangeFontSize
	{

		private int size; //dimensione da impostare

		//costruttore
		public ChangeFontSize(int size)
		{
			this.size = size;
			this.setUIFont();
		}		

		//esegue la modifica del font size
		private void setUIFont()
		{
    			Enumeration keys = UIManager.getLookAndFeelDefaults().keys();
    			while (keys.hasMoreElements()) 
			{
      				Object key = keys.nextElement();
      				Object value = UIManager.get(key);
      				if (value instanceof FontUIResource)
				{
					FontUIResource r = (FontUIResource) value;
					String fam = r.getFamily();
					int style = r.getStyle();
        				UIManager.put(key,new Font(fam,style,this.size));
					
				}
      			}
    		} 
	}