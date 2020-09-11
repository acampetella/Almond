//classe che converte una descrizione nel codice ThingTalk
//genera il file manifest di Almond

	import java.io.File;
	import java.io.FileWriter;
	import java.io.BufferedWriter;

	public class ThingTalkConverter
	{
		private OntologyDescription description; //descrizione da convertire

		//costruttore
		public ThingTalkConverter(OntologyDescription description)
		{
			this.description = description;
		}

		//restitisce il codice del file manifest corrispondente alla descrizione
		public String getManifestCode()
		{
			String result = "";
			OntologyIndividual individuals[] = this.description.getIndividuals();
			for(OntologyIndividual ind : individuals)
			{
				OntologyClass c = ind.getType();
				String name = c.getName();
				if (name.equals("DeviceClass"))
				{
					result = getDeviceClassCode(ind);
					break;
				}
			}
			return result;
		}

		//restituisce il codice ThingTalk relativo all'individuo specificato
		public static String getTTCodeOf(OntologyIndividual ind)
		{
			return getCode(ind);
		}

		//salva il codice del manifest in un file
		public void saveManifestFile(File output) throws Exception
		{
			String out = this.getManifestCode();
			FileWriter fw = new FileWriter(output);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(out);
			bw.flush();
			bw.close();
		}

		//metodo privato che ricava il codice relativo all'individuo specificato
		private static String getCode(OntologyIndividual ind)
		{
			String result = "";
			OntologyClass c = ind.getType();
			final String name = c.getName();
			switch(name)
			{
				case "CustomLoader" :
					result = getCustomLoaderCode();
					break;
				case "RSSLoader" :
					result = getRSSLoaderCode();
					break;
				case "RESTLoader" :
					result = getRESTLoaderCode();
					break;
				case "BasicConfiguration" :
					result = getBasicConfigurationCode();
					break;
				case "APIKeyConfiguration" :
					result = getAPIKeyConfigurationCode(ind);
					break;
				case "FormConfiguration" :
					result = getFormConfigurationCode(ind);
					break;
				case "ComplexConfiguration" :
					result = getComplexConfigurationCode(ind);
					break;
				case "NaturalLanguageAnnotation" :
					result = getNLAnnotationCode(ind);
					break;
				case "ImplementationAnnotation" :
					result = getImplAnnotationCode(ind);
					break;
				case "Action" :
					result = getActionFunctionCode(ind);
					break;
				case "Query" :
					result = getQueryFunctionCode(ind);
					break;
				case "DeviceClass" :
					result = getDeviceClassCode(ind);
					break;
			}
			return result;
		}

		//restituisce il codice corrispondente ad un individuo di tipo DeviceClass
		private static String getDeviceClassCode(OntologyIndividual ind)
		{
			String result = "";
			String id = "";
			String loader = "";
			String config = "";
			String functions = "";
			String annotations = "";
			Attribute att[] = ind.getAttributes();
			id = att[0].getValue();
			Relationship relations[] = ind.getRelations();
			for(Relationship rel : relations)
			{
				OntologyIndividual target = rel.getIndividual();
				if (rel.getName().equals("hasLoader"))
					loader = getLoaderCode(target);
				else
				{
					if (rel.getName().equals("hasConfig"))
						config = getConfigCode(target);
					else
					{
						if (rel.getName().equals("hasFunction"))
							functions += getFunctionCode(target) + "\r\n\r\n";	
						else
						{
							if (rel.getName().equals("hasAnnotation"))
								annotations += getAnnotationCode(ind) + "\r\n";	
						}		
					}
				}
			}
			result = "class @" + id + " {\r\n\t" + loader + "\r\n\t" + config + "\r\n\r\n\t" + functions + "\r\n\t" + annotations + "\r\n}";
			return result;	
		}

		//restituisce il codice relativo ad un individuo di tipo Loader
		private static String getLoaderCode(OntologyIndividual ind)
		{
			String result = "";
			if (ind.getType().getName().equals("CustomLoader"))
				result = getCustomLoaderCode();
			if (ind.getType().getName().equals("RSSLoader"))
				result = getRSSLoaderCode();
			if (ind.getType().getName().equals("RESTLoader"))
				result = getRESTLoaderCode();
			return result;
		}

		//restituisce il codice relativo ad un individuo di tipo Configuration
		private static String getConfigCode(OntologyIndividual ind)
		{
			String result = "";
			if (ind.getType().getName().equals("BasicConfiguration"))
				result = getBasicConfigurationCode();
			if (ind.getType().getName().equals("APIKeyConfiguration"))
				result = getAPIKeyConfigurationCode(ind);
			if (ind.getType().getName().equals("FormConfiguration"))
				result = getFormConfigurationCode(ind);
			if (ind.getType().getName().equals("ComplexConfiguration"))
				result = getComplexConfigurationCode(ind);
			return result;
		}

		//restituisce il codice relativo ad un individuo di tipo CustomLoader
		private static String getCustomLoaderCode()
		{
			String mixin = "@org.thingpedia.v2()";
			String result = "import loader from " + mixin + ";";
			return result;
		}

		//restituisce il codice relativo ad un individuo di tipo RSSLoader
		private static String getRSSLoaderCode()
		{
			String mixin = "@org.thingpedia.rss";
			String result = "import loader from " + mixin + ";";
			return result;
		}

		//restituisce il codice relativo ad un individuo di tipo RESTLoader
		private static String getRESTLoaderCode()
		{
			String mixin = "@org.thingpedia.generic_rest.v1()";
			String result = "import loader from " + mixin + ";";
			return result;
		}

		//restituisce il codice relativo ad un individuo di tipo BasicConfiguration
		private static String getBasicConfigurationCode()
		{
			String mixin = "@org.thingpedia.config.none()";
			String result = "import config from " + mixin + ";";
			return result;
		}

		//restituisce il codice relativo ad un individuo di tipo APIKeyConfiguration
		private static String getAPIKeyConfigurationCode(OntologyIndividual ind)
		{
			Attribute attributes[] = ind.getAttributes();
			String value = attributes[0].getValue();
			String mixin = "@org.thingpedia.config.none(api_key=" + value + ")";
			String result = "import config from " + mixin + ";";
			return result;	
		}

		//restituisce il codice relativo ad un individuo di tipo FormConfiguration
		private static String getFormConfigurationCode(OntologyIndividual ind)
		{
			Relationship relations[] = ind.getRelations();
			String param = "";
			for(int i = 0; i < relations.length; i++)
			{
				OntologyIndividual  p = relations[i].getIndividual();
				Attribute att[] = p.getAttributes();
				param += att[0].getValue() + ":";
				Relationship rel[] = p.getRelations();
				for(Relationship r : rel)
				{
					if (r.getName().equals("hasDataType"))
					{
						OntologyIndividual dt = r.getIndividual();
						param += dt.getType().getName();	
					}
				}
				if (i < relations.length - 1)
					param += ",";
			}
			String mixin = "@org.thingpedia.config.form(params=makeArgMap(" + param + "))";
			String result = "import config from " + mixin + ";";
			return result;
		}

		//restituisce il codice relativo ad un individuo di tipo ComplexConfiguration
		private static String getComplexConfigurationCode(OntologyIndividual ind)
		{
			String result = "";
			Relationship relations[] = ind.getRelations();
			OntologyIndividual  at = relations[0].getIndividual();
			String name = at.getType().getName();
			if (name.equals("BasicAuthentication"))
				result = getBasicAuthenticationCode(at);
			if (name.equals("DiscoveryAuthentication"))
				result = getDiscoveryAuthenticationCode(at);
			if (name.equals("OpenAuthentication"))
				result = getOpenAuthenticationCode(at);
			return result;
		}

		//restituisce il codice relativo ad un individuo di tipo BasicAuthentication
		private static String getBasicAuthenticationCode(OntologyIndividual ind)
		{
			String mixin = "@org.thingpedia.config.basic_auth";
			String result = "";
			String param = "";
			Relationship relations[] = ind.getRelations();
			for(int i = 0; i < relations.length; i++)
			{
				OntologyIndividual  p = relations[i].getIndividual();
				Attribute att[] = p.getAttributes();
				param += att[0].getValue() + ":";
				Relationship rel[] = p.getRelations();
				for(Relationship r : rel)
				{
					if (r.getName().equals("hasDataType"))
					{
						OntologyIndividual dt = r.getIndividual();
						param += dt.getType().getName();	
					}
				}
				if (i < relations.length - 1)
					param += ",";
			}
			if (!param.equals(""))
				mixin += "(extra_params=makeArgMap(" + param + "))";
			result = "import config from " + mixin + ";";
			return result;	
		}

		//restituisce il codice relativo ad un individuo di tipo DiscoveryAuthentication
		private static String getDiscoveryAuthenticationCode(OntologyIndividual ind)
		{
			String mixin = "@org.thingpedia.config.discovery.";
			Attribute attributes[] = ind.getAttributes();
			mixin += attributes[0].getValue();
			String result = "import config from " + mixin + ";";
			return result;	
		}

		//restituisce il codice relativo ad un individuo di tipo OpenAuthentication
		private static String getOpenAuthenticationCode(OntologyIndividual ind)
		{
			String mixin = "@org.thingpedia.config.oauth2(client_Id=";
			String clientId = "";
			String clientSecret = "";
			Attribute attributes[] = ind.getAttributes();
			for(int i = 0; i < attributes.length; i++)
			{
				if (attributes[i].getName().equals("clientId"))
					clientId = attributes[i].getValue();
				else
					clientSecret = attributes[i].getValue();
			}
			mixin += clientId + ",client_secret=" + clientSecret + ")";
			String result = "import config from " + mixin + ";";
			return result;
		}

		//restituisce il codice relativo ad un individuo di tipo Function
		private static String getFunctionCode(OntologyIndividual ind)
		{
			String result = "";
			if (ind.getType().getName().equals("Action"))
				result = getActionFunctionCode(ind);
			else
				result = getQueryFunctionCode(ind);
			return result;
		}

		//restituisce il codice relativo ad un individuo di tipo Action
		private static String getActionFunctionCode(OntologyIndividual ind)
		{
			String result = "action " + getMainFunctionCode(ind);
			return result;
		}

		//restituisce il codice relativo ad un individuo di tipo Query
		private static String getQueryFunctionCode(OntologyIndividual ind)
		{
			String result = "";
			String qualifiers = "";
			Relationship relations[] = ind.getRelations();
			for(Relationship rel : relations)
			{
				if (rel.getName().equals("hasQualifier"))
				{
					OntologyIndividual target = rel.getIndividual();
					Attribute att[] = target.getAttributes();
					qualifiers += att[0].getValue() + " ";	
				}
			}
			result = qualifiers + "query " + getMainFunctionCode(ind);
			return result;	
		}

		//restituisce il codice principale di un individuo di tipo Function
		private static String getMainFunctionCode(OntologyIndividual ind)
		{
			String result = "";
			String funcId = "";
			String params = "";
			String annotations = "";
			Attribute attributes[] = ind.getAttributes();
			funcId = attributes[0].getValue();
			Relationship relations[] = ind.getRelations();
			for(Relationship rel : relations)
			{
				if (rel.getName().equals("hasInputParam"))
				{
					params += "in ";
					OntologyIndividual target = rel.getIndividual();
					Attribute att[] = target.getAttributes();
					if (att[0].getValue().equals("true"))
						params += "req ";
					else
						params += "opt ";
					Relationship rels[] = target.getRelations();
					target = rels[0].getIndividual();
					params += getParameterCode(target) + ",\r\n\t\t";
				}
			}
			for(Relationship rel : relations)
			{
				if (rel.getName().equals("hasOutputParam"))
				{
					params += "out ";
					OntologyIndividual target = rel.getIndividual();
					params += getParameterCode(target) + ",\r\n\t\t";
				}
			}
			for(Relationship rel : relations)
			{
				if (rel.getName().equals("hasAnnotation"))
				{
					OntologyIndividual target = rel.getIndividual();
					annotations += getAnnotationCode(target) + "\r\n";
				}
			}
			params = params.substring(0,params.length() - 5);
			result = funcId + "(" + params + ")\r\n\r\n" + annotations + ";";
			return result;
		}

		//restituisce il codice relativo ad un individuo di tipo Annotation
		private static String getAnnotationCode(OntologyIndividual ind)
		{
			String result = "";
			if (ind.getType().getName().equals("NaturalLanguageAnnotation"))
				result = getNLAnnotationCode(ind);
			else
				result = getImplAnnotationCode(ind);
			return result;
		}

		//restituisce il codice relativo ad un individuo di tipo NaturalLanguageAnnotation
		private static String getNLAnnotationCode(OntologyIndividual ind)
		{
			String result = "#_" + getMainAnnotationCode(ind);
			return result;
		}

		//restituisce il codice relativo ad un individuo di tipo ImplementationAnnotation
		private static String getImplAnnotationCode(OntologyIndividual ind)
		{
			String result = "#" + getMainAnnotationCode(ind);
			return result;
		}

		//restituisce il codice principale relativo ad un individuo di tipo Annotation
		private static String getMainAnnotationCode(OntologyIndividual ind)
		{
			String key = "";
			String value = "";
			Attribute attributes[] = ind.getAttributes();
			for(Attribute a : attributes)
			{
				if (a.getName().equals("annotationKey"))
					key = a.getValue();
			}
			Relationship relations[] = ind.getRelations();
			OntologyIndividual target = relations[0].getIndividual();
			Attribute att[] = target.getAttributes();
			value = att[0].getValue();
			String result = "[" + key + "=\"" + value + "\"]";
			return result;
		}

		//restituisce il codice relativo ad un individuo di tipo Parameter
		private static String getParameterCode(OntologyIndividual ind)
		{
			String paramName = "";
			String paramType = "";
			String annotations = "";
			String result = "";
			Attribute attributes[] = ind.getAttributes();
			paramName = attributes[0].getValue();
			Relationship relations[] = ind.getRelations();
			for(Relationship rel : relations)
			{
				OntologyIndividual target = rel.getIndividual();
				if (rel.getName().equals("hasDataType"))
				{
					if (target.getType().getName().equals("Entity"))
					{
						Attribute att[] = target.getAttributes();
						for(Attribute a : att)
						{
							if (a.getName().equals("entityId"))
							{
								paramType = target.getType().getName() + "(" + a.getValue() + ")";
								break;
							}
						}
					}
					else
					{
						if (target.getType().getName().equals("Measure"))
						{
							Attribute attr[] = target.getAttributes();
							paramType = target.getType().getName() + "(" + attr[0].getValue() + ")";
						}
						else
							paramType = target.getType().getName();	
					}
					result = paramName + " : " + paramType;
				}
				else
					annotations += getAnnotationCode(target) + " ";	
			}
			if (!annotations.equals(""))
				result += " " + annotations;
			return result;
		}
	}