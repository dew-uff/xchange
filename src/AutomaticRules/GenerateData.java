package AutomaticRules;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class GenerateData {

	static String separator = System.getProperty("file.separator");
	static String workingPath = System.getProperty("user.dir");
	static List<Integer> listaRegistros = new ArrayList<Integer>();
	static int tamBase = 20000;
	static int tamBaseInclusao = 2000;
	static int numInclusoes = 40;
	static int numExclusoes = 10;
	static int numVersoes = 9;
	static HashMap<String, List<String>> data = new HashMap<String, List<String>>();

	public static void main(String[] args) {	

		try {			
			dividirBaseInclusao(22000, 2000);			
			executar();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void executar() {
		try {
			System.out.println("Inicio: " + new Date());
			Random reg = new Random();

			data = dataCollector("vBase.xml");

			for (int i = 0 ; i < numInclusoes ; i++) {
				int num = reg.nextInt(tamBaseInclusao);
				listaRegistros.add(num);
			}

			writeXMLAfter("vBase.xml", "v1.xml", tamBase, numInclusoes, numExclusoes);

			for (int i = 1 ; i <= numVersoes ; i++) {
				writeXMLAfter("v"+i+".xml", "v"+(i+1)+".xml", tamBase, numInclusoes, numExclusoes);
				System.out.println("Versao " + i + " finalizada: " + new Date());
			}

			System.out.println("Fim: " + new Date());
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	//Escreve a segunda versao do XML
	private static void writeXMLAfter(String vAntes, String vDepois, int tamanhoBase, int qtdeInclusoes, int qtdeExclusoes) throws Exception {

		try {			
			String baseXMLAntes = workingPath + separator + "tempRandom" + separator + vAntes;
			String baseXMLDepois = workingPath + separator + "tempRandom" + separator + vDepois;

			XMLInputFactory factory = XMLInputFactory.newInstance();
			InputStream is = new FileInputStream(baseXMLAntes);
			XMLStreamReader vAntesReader = factory.createXMLStreamReader(is);

			XMLOutputFactory factoryOut = XMLOutputFactory.newInstance();
			OutputStream os = new FileOutputStream(baseXMLDepois);
			XMLStreamWriter vDepoisWriter = factoryOut.createXMLStreamWriter(os); 

			String root = null;
			String register = null;
			String attribute = null;
			String text = null;        

			HashMap<String, List<Integer>> modifications = aleatorio(tamanhoBase);
			List<Integer> listaExclusoes = listaExclusoes(qtdeExclusoes, tamanhoBase);

			int i = 0, event;

			while (vAntesReader.hasNext()) {
				event = vAntesReader.next();
				if (event == XMLStreamConstants.START_ELEMENT) {                    
					if (root == null) {
						root = vAntesReader.getLocalName();
						vDepoisWriter.writeStartElement(root);
					} else if (register == null) {
						register = vAntesReader.getLocalName();
						if (!listaExclusoes.contains(i)) {
							vDepoisWriter.writeStartElement(register);
						} else {
							register = "null";
							while (event != XMLStreamConstants.END_ELEMENT || register != "classificationpaper") {
								if (event != XMLStreamConstants.CHARACTERS) {
									register = vAntesReader.getLocalName();
								}                    			
								event = vAntesReader.next();
							}
						}
					} else if (attribute == null) {

						attribute = vAntesReader.getLocalName();

						if (attribute.equals("ssn")) {
							escreveAtributoSsn(modifications, vAntesReader, data, vDepoisWriter, i);
						} else if (attribute.equals("removed")) {
							escreveAtributosAfastamento(modifications, vAntesReader, data, vDepoisWriter, i);
						} else if (attribute.equals("university")) {
							escreveAtributosUniversidadesProgramas(modifications, vAntesReader, data, vDepoisWriter, i);
						} else if (attribute.equals("jobSchema")) {
							escreveAtributosSalarioProfessor(modifications, vAntesReader, data, vDepoisWriter, i);
						} else {
							vDepoisWriter.writeStartElement(attribute);
							if (modifications.containsKey(attribute)) {
								if (modifications.get(attribute).contains(i)) {                   			
									text = data.get(attribute).get(new Random().nextInt(data.get(attribute).size()));
									vAntesReader.getElementText();
								} else {
									text = vAntesReader.getElementText();
								}
							} else {
								text = vAntesReader.getElementText();
							}

							if (attribute.equals("email") && !text.startsWith("\"")) {
								text = "\"" + text + "\""; 
							}

							vDepoisWriter.writeCharacters(text.toLowerCase());
							vDepoisWriter.writeEndElement();
						}

						attribute = null;
					}
				} else if (event == XMLStreamConstants.END_ELEMENT) {

					if (!listaExclusoes.contains(i) && !vAntesReader.getLocalName().equals(root)) {
						vDepoisWriter.writeEndElement();
					}                	
					register = null;
					i++;
				} else if (event == XMLStreamConstants.END_DOCUMENT) {
					//vDepoisWriter.writeEndDocument();
				} else {                	
					if (!listaExclusoes.contains(i)) {
						vDepoisWriter.writeCharacters(vAntesReader.getText());
					}
				}
			}
			baseInclusao(vDepoisWriter, qtdeInclusoes);  

			vDepoisWriter.writeEndDocument();
			vDepoisWriter.close();
			is.close();
			os.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//Coletar todos os dados da primeira versao para usa-los na mudanca aleatoria 
	private static HashMap<String, List<String>> dataCollector(String fileName) throws Exception {

		HashMap<String, List<String>> data = new HashMap<String, List<String>>();

		try {
			String tempXMLData = workingPath + separator + "tempRandom" + separator + fileName;

			XMLInputFactory factory = XMLInputFactory.newInstance();
			InputStream is = new FileInputStream(tempXMLData);
			XMLStreamReader reader = factory.createXMLStreamReader(is);

			String root = null;
			String register = null;
			String attribute = null;
			String text = null; 

			while (reader.hasNext()) {
				int event = reader.next();
				if (event == XMLStreamConstants.START_ELEMENT) {
					if (root == null) {
						root = reader.getLocalName();
					} else if (register == null) {
						register = reader.getLocalName();                        
					} else if (attribute == null) {
						attribute = reader.getLocalName();
						text = reader.getElementText();

						if (data.get(attribute) == null) {
							data.put(attribute, new ArrayList<String>());
						}

						if (!data.get(attribute).contains(text)) {
							data.get(attribute).add(text);
						} else if (attribute.equals("siape")) {
							int temp = Integer.valueOf(text) + 1;
							text = String.valueOf(temp);
							data.get(attribute).add(text);
						}

						attribute = null;
					}
				} else if (event == XMLStreamConstants.END_ELEMENT) {
					register = null;
				}
			}
			is.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return data;
	}

	//Le um arquivo de registros para servir como base para nova inclusoes 
	private static void baseInclusao(XMLStreamWriter writer, int numInclusoes) throws Exception {

		try {			
			String xmlInclusoes = workingPath + separator + "tempRandom" + separator + "baseInclusao.xml";

			XMLInputFactory factory = XMLInputFactory.newInstance();
			InputStream is = new FileInputStream(xmlInclusoes);
			XMLStreamReader reader = factory.createXMLStreamReader(is);

			String root = null;
			String register = null;
			String attribute = null;
			String text = null;
			int i = 0;

			while (reader.hasNext() && i < numInclusoes) {
				int event = reader.next();
				if (event == XMLStreamConstants.START_ELEMENT) {
					if (root == null) {
						root = reader.getLocalName();
					} else if (register == null) {
						register = reader.getLocalName();
						if (!listaRegistros.contains(i)) {
							writer.writeStartElement(register);
						}
					} else if (attribute == null) {

						attribute = reader.getLocalName();                		
						text = reader.getElementText();

						if (!listaRegistros.contains(i)) {
							writer.writeStartElement(attribute);
							writer.writeCharacters(text.toLowerCase());
							writer.writeEndElement();
						}                    		
					}

					attribute = null;                
				} else if (event == XMLStreamConstants.END_ELEMENT) {
					if (!listaRegistros.contains(i)) {
						writer.writeEndElement();
					} else {
						listaRegistros.remove(new Integer(i));
					}

					register = null;
					i++;
				} else {                	
					if (!listaRegistros.contains(i)) {
						writer.writeCharacters(reader.getText());
					}
				}
			}
			reader.close();
			is.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}		

	//Gera lista aleatoria para exclusoes
	private static List<Integer> listaExclusoes(int qntExclusoes, int tamanhoBase) {

		Random r = new Random();
		List<Integer> lista = new ArrayList<Integer>();

		for (int i = 0 ; i < qntExclusoes ; i++) {
			lista.add(r.nextInt(tamanhoBase));
		}

		return lista;
	}

	//Tratamento SSN
	private static void escreveAtributoSsn(HashMap<String, List<Integer>> modifications,
			XMLStreamReader reader, HashMap<String, List<String>> data, XMLStreamWriter writer, int i) throws XMLStreamException {

		String ssn = "ssn";

		HashMap<String, String> valores = new HashMap<String, String>();
		HashMap<String, String> novosValores = new HashMap<String, String>();

		valores.put(ssn, null);
		novosValores.put(ssn, null);

		String aux = reader.getLocalName();			
		valores.put(aux, reader.getElementText());
		reader.getLocalName();

		if (modifications.get(ssn).contains(i)) {

			String inicio = valores.get(ssn).substring(0, 11);
			int fim = Integer.parseInt(valores.get(ssn).substring(11, 12));
			String completa = inicio + String.valueOf((fim==9 ? 0 : ++fim)) + "\"";
			novosValores.put(ssn, completa);
		} else {
			novosValores = valores;
		}

		for ( String chave : novosValores.keySet() ) {			
			writer.writeStartElement(chave);
			writer.writeCharacters(novosValores.get(chave.toLowerCase()));
			writer.writeEndElement();
		}
	}

	//Tratamento para universidades
	private static void escreveAtributosUniversidadesProgramas(HashMap<String, List<Integer>> modifications, XMLStreamReader reader, 
			HashMap<String, List<String>> data, XMLStreamWriter writer, int i) throws Exception {

		String university = "university";
		String graduateProgram = "graduateprogram";

		HashMap<String, String> valores = new HashMap<String, String>();
		HashMap<String, String> novosValores = new HashMap<String, String>();

		valores.put(university, null);
		valores.put(graduateProgram, null);

		novosValores.put(university, null);
		novosValores.put(graduateProgram, null);

		HashMap<String, List<String>> tableUniversidades = tableUniversidades();

		for (int a = 0 ; a < 2 ; a++) {

			String aux = reader.getLocalName();			
			valores.put(aux, reader.getElementText());

			if (a != 1) {
				int event = reader.next(); 
				if (event == XMLStreamConstants.START_ELEMENT) {
					event = reader.next();
				}
				if (event == XMLStreamConstants.END_ELEMENT) {
					event = reader.next();
				}            
				event = reader.next();
			}
		}

		if (!tableUniversidades.get(valores.get(university)).isEmpty()) {
			if (valores.get(graduateProgram) != null && !tableUniversidades.get(valores.get(university)).contains(valores.get(graduateProgram)) ) {
				valores.put(graduateProgram, tableUniversidades.get(valores.get(university)).get(new Random().nextInt(4)));
			}					
		} else {
			valores.put(graduateProgram, "null");
		}

		if (modifications.get(university).contains(i)) {
			String novoValor = data.get(university).get(new Random().nextInt(data.get(university).size()));

			while (novoValor.equals(valores.get(university))) {
				novoValor = data.get(university).get(new Random().nextInt(data.get(university).size()));
			}

			novosValores.put(university, novoValor);
		} else {
			novosValores.put(university, valores.get(university));
		}

		if (modifications.get(graduateProgram).contains(i)) {

			String novoValor = "null";

			if (!tableUniversidades.get(valores.get(university)).isEmpty()) {
				novoValor = tableUniversidades.get(valores.get(university)).get(new Random().nextInt(4));
				while (novoValor.equals(valores.get(graduateProgram))) {
					novoValor = data.get(university).get(new Random().nextInt(data.get(university).size()));
				}
			} else {
				novoValor = "null";
			}

			novosValores.put(graduateProgram, novoValor);
		} else {
			novosValores.put(graduateProgram, valores.get(graduateProgram));
		}

		for ( String chave : novosValores.keySet() ) {			
			writer.writeStartElement(chave);
			writer.writeCharacters((novosValores.get(chave)));
			writer.writeEndElement();
			if (!chave.equals(graduateProgram)) {
				writer.writeCharacters("\n\t\t");
			}
		}		
	}

	//Tratamento para afastamentos
	private static void escreveAtributosAfastamento(HashMap<String, List<Integer>> modifications, XMLStreamReader reader, 
			HashMap<String, List<String>> data, XMLStreamWriter writer, int i) throws Exception {

		String medicalRemoved = "removed";
		String datelastRemoved = "datelastremoved";

		HashMap<String, String> valores = new HashMap<String, String>();
		HashMap<String, String> novosValores = new HashMap<String, String>();

		valores.put(medicalRemoved, null);
		valores.put(datelastRemoved, null);

		novosValores.put(medicalRemoved, null);
		novosValores.put(datelastRemoved, null);

		for (int a = 0 ; a < 2 ; a++) {

			String aux = reader.getLocalName();			
			valores.put(aux, reader.getElementText());

			if (a != 1) {
				int event = reader.next(); 
				if (event == XMLStreamConstants.START_ELEMENT) {
					event = reader.next();
				}
				if (event == XMLStreamConstants.END_ELEMENT) {
					event = reader.next();
				}            
				event = reader.next();
			}
		}

		if (valores.get(medicalRemoved).equals("no")) {
			valores.put(datelastRemoved, "null");
		}

		if (modifications.get(medicalRemoved).contains(i)) {

			if (valores.get(medicalRemoved).equals("no")) {
				novosValores.put(medicalRemoved, "yes");
			}

			Date date = new Date();
			SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
			String str = "\"" + fmt.format(date) + "\"";				
			novosValores.put(datelastRemoved, str);
		} else {
			novosValores = valores;
		}

		writer.writeStartElement(medicalRemoved);
		if (novosValores.get(medicalRemoved) != null) {
			writer.writeCharacters(novosValores.get(medicalRemoved).toLowerCase());
		} else {
			writer.writeCharacters("");
		}
		writer.writeEndElement();
		writer.writeCharacters("\n\t\t");

		writer.writeStartElement(datelastRemoved);
		writer.writeCharacters(novosValores.get(datelastRemoved).toLowerCase());
		writer.writeEndElement();		
	}

	//Tratamento para cargos e salarios
	private static void escreveAtributosSalarioProfessor(HashMap<String, List<Integer>> modifications, XMLStreamReader reader, 
			HashMap<String, List<String>> data, XMLStreamWriter writer, int i) throws Exception {

		String jobSchema = "jobSchema";
		String classs = "class";
		String level = "level";
		String title = "title";
		String salary = "salary";
		boolean modifcou = false;

		HashMap<String, String> valores = returnHashMap();		
		HashMap<String, String> novoValores = returnHashMap();

		for (int a = 0 ; a < 5 ; a++) {

			String aux = reader.getLocalName();			
			valores.put(aux, reader.getElementText());

			if (a != 4) {
				int event = reader.next(); 
				if (event == XMLStreamConstants.START_ELEMENT) {
					event = reader.next();
				}
				if (event == XMLStreamConstants.END_ELEMENT) {
					event = reader.next();
				}            
				event = reader.next();
			}
		}

		if (valores.get(classs).equals(Class.TitularProfessor.getClasse()) && Integer.parseInt(valores.get(level)) > 1) {
			valores.put(level, "1");
		}

		if ((valores.get(classs).equals(Class.AuxiliaryProfessor.getClasse()) || valores.get(classs).equals(Class.AssistantProfessor.getClasse())) && 
				(Integer.parseInt(valores.get(level)) > 2)) {
			valores.put(level, "2");
		}

		if (modifications.get(jobSchema).contains(i)) {
			String novoValor = data.get(jobSchema).get(new Random().nextInt(data.get(jobSchema).size()));

			while (novoValor.equals(valores.get(jobSchema))) {
				novoValor = data.get(jobSchema).get(new Random().nextInt(data.get(jobSchema).size()));
			}

			novoValores.put(jobSchema, novoValor);
			modifcou = true;
		} else {
			novoValores.put(jobSchema, valores.get(jobSchema));
		}

		if (modifications.get(classs).contains(i)) {

			if (valores.get(classs).equals(Class.AuxiliaryProfessor.getClasse())) {
				novoValores.put(classs, Class.AssistantProfessor.getClasse());
			} else if (valores.get(classs).equals(Class.AssistantProfessor.getClasse())) {
				novoValores.put(classs, Class.AdjunctProfessor.getClasse());
			} else if (valores.get(classs).equals(Class.AdjunctProfessor.getClasse())) {
				novoValores.put(classs, Class.AssociatedProfessor.getClasse());				
			} else {
				novoValores.put(classs, Class.TitularProfessor.getClasse());
			}

			novoValores.put(level, "1");
			modifcou = true;

		} else if (modifications.get(level).contains(i)) {

			if (valores.get(level).equals(4)) {
				if (valores.get(classs).equals(Class.AuxiliaryProfessor.getClasse())) {
					novoValores.put(classs, Class.AssistantProfessor.getClasse());
				} else if (valores.get(classs).equals(Class.AssistantProfessor.getClasse())) {
					novoValores.put(classs, Class.AdjunctProfessor.getClasse());
				} else if (valores.get(classs).equals(Class.AdjunctProfessor.getClasse())) {
					novoValores.put(classs, Class.AssociatedProfessor.getClasse());				
				} else {
					novoValores.put(classs, Class.TitularProfessor.getClasse());
				}

				novoValores.put(level, "1");
				modifcou = true;

			} else if ((valores.get(level).equals("2")) && (valores.get(classs).equals(Class.AuxiliaryProfessor.getClasse()) 
					|| valores.get(classs).equals(Class.AssistantProfessor.getClasse()))) {

				if (valores.get(classs).equals(Class.AuxiliaryProfessor.getClasse())) {
					novoValores.put(classs, Class.AssistantProfessor.getClasse());
				} else if (valores.get(classs).equals(Class.AssistantProfessor.getClasse())) {
					novoValores.put(classs, Class.AdjunctProfessor.getClasse());
				}

				novoValores.put(level, "1");
				modifcou = true;

			} else if (valores.get(classs).equals(Class.TitularProfessor.getClasse())) {
				novoValores.put(classs, valores.get(classs));
				novoValores.put(level, valores.get(level));
			} else {
				Integer valorAnt = Integer.parseInt(valores.get(level));
				if (valorAnt < 4) {				
					valorAnt++;
				}
				novoValores.put(level, valorAnt.toString());
				novoValores.put(classs, valores.get(classs));
			}
		} else {			
			novoValores.put(classs, valores.get(classs));
			novoValores.put(level, valores.get(level));
		}

		if (modifications.get(title).contains(i)) {

			if (valores.get(title).equals("graduate")) {
				novoValores.put(title, "master");
			} else {
				novoValores.put(title, "phd");
			}

			modifcou = true;

		} else {
			novoValores.put(title, valores.get(title));
		}

		if (modifcou) {
			String jobSchemaN = "";
			String classN = "";
			Integer levelN = 0;
			String titleN = "";

			//jobSchema<class<level<title,salary>
			HashMap<String, HashMap<String, HashMap<Integer, HashMap<String,Integer>>>> table = table();
			Integer salario = 0;			
			try {
				jobSchemaN = novoValores.get(jobSchema);
				classN = novoValores.get(classs);
				levelN = Integer.parseInt(novoValores.get(level));
				titleN = novoValores.get(title);

				salario = (((table.get(jobSchemaN)).get(classN)).get(levelN)).get(titleN);
			} catch (Exception e) {
				throw e;
			}

			novoValores.put(salary, salario.toString());
		} else {
			novoValores.put(salary, valores.get(salary));
		}		

		for ( String chave : novoValores.keySet() ) {			
			writer.writeStartElement(chave);
			writer.writeCharacters(novoValores.get(chave).toLowerCase());
			writer.writeEndElement();
			if (!chave.equals(salary)) {
				writer.writeCharacters("\n\t\t");
			}
		}

		valores = null;
		novoValores = null;
	}

	//Geracao aleatoria a partir das porcentagens
	private static HashMap<String, List<Integer>> aleatorio(int tamanhoBase) {

		List<Percents> porcents = porcentagens();
		porcentagensMatriz(porcents);		
		Collections.sort(porcents);
		HashMap<String, List<Integer>> modificacoesFinais = new HashMap<String, List<Integer>>();
		
		for (Percents p : porcents) {
			List<Integer> regs = new ArrayList<Integer>();
			modificacoesFinais.put(p.getAtributo(), regs);
			
//			for (Percents pPar : p.getPercentPares()) {
//				if (modificacoesFinais.containsKey(pPar.getAtributo())) {
//					regs.addAll(modificacoesFinais.get(pPar.getAtributo()).subList(0, (int)pPar.getPercentIndividual()*modificacoesFinais.get(pPar.getAtributo()).size()/100));
//				}
//			}
			
			while (regs.size() < p.getPercentIndividual()*tamanhoBase/100) {                    
				regs.add(new Random().nextInt(tamanhoBase));
			}
			p.setRegistros(regs);
		}
		
		regras(modificacoesFinais);

		return modificacoesFinais;
	}	

	private static HashMap<String, List<Integer>> regras(HashMap<String, List<Integer>> modificacoesFinais) {
		HashMap<List<String>, Integer> lista = regras();
		List<Integer> novos = new ArrayList<Integer>();
		
		for (List<String> regra : lista.keySet()) {
			novos = new ArrayList<Integer>();
			List<Integer> regs = modificacoesFinais.get(regra.get(0));
			
			while (novos.size() < regs.size()*lista.get(regra)/100) {                    
				int index = new Random().nextInt(regs.size());
				novos.add(regs.get(index));
			}
						
			for (String atr : regra) {
				if (!atr.equals(regra.get(0))) {
					modificacoesFinais.get(atr).addAll(novos);
				}
			}
		}
		
		return modificacoesFinais;
	}

	private static List<Percents> porcentagensMatriz(List<Percents> porcentagensSimples) {

		float[] valores = {0,0,3,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,10,3,5,0,0,0,0,0,0,0,0,5,0,0,0,0,0,10,3,0.5F,0,0,0,0,0,0,0,5,70,0,0,0,0,3,3,3,1,0,0,2,5,5,5,5,5,10,0,0,0,0,0,5,0.5F,1,0,0,2,2,0,0,3,0,8,0,0,0,0,0,0,0,0,0,60,0.5F,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,60,0.5F,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0.5F,0.5F,80,15,15,5,3,10,0,0,85,85,0,0,0,5,2,0,0,80,30,45,5,5,10,3,60,85,85,0,0,0,5,0,0,0,15,30,70,100,2,10,1,5,5,10,0,0,0,5,0,0,0,15,45,70,100,3,6,2,3,3,11,0,0,0,5,3,0,0,5,5,100,100,1,20,0,0,0,0,0,0,5,5,0,0,0,3,5,2,3,1,80,40,10,10,8,3,5,70,10,8,0,0,10,10,10,6,20,80,30,10,10,5,0,0,0,0,0,0,0,0,3,1,2,0,40,30,2,2,5,0,0,0,0,0,0,0,0,60,5,3,0,10,10,2,80,80,0,0,0,0,0,0,0,85,85,5,3,0,10,10,2,80,80,0,0,0,0,0,0,0,85,85,10,11,0,8,5,5,80,80};
		int i = 0;		
		
		List<String> listaAtributos = listaAtributos(porcentagensSimples);        

		for (Percents p : porcentagensSimples) {
			List<Percents> pares = new ArrayList<Percents>();
			for (String atributo : listaAtributos) {
				if (!p.getAtributo().equals(atributo)) {
					pares.add(new Percents(atributo, valores[i]));
					i++;
				}
			}
			
			p.setPercentPares(pares);
		}
		return porcentagensSimples;
	}
	
	private static HashMap<List<String>, Integer> regras() {
		HashMap<List<String>, Integer> lista = new HashMap<List<String>, Integer>();
		
		List<String> regra = new ArrayList<String>();
		regra.add("numberadvisors");
		regra.add("classificationpaper");		
		regra.add("title");
		regra.add("jobSchema");				
		lista.put(regra, 40);
		
		regra = new ArrayList<String>();		
		regra.add("level");	
		regra.add("class");
		regra.add("title");	
		lista.put(regra, 35);
		
		regra = new ArrayList<String>();		
		regra.add("classificationpaper");
		regra.add("title");
		lista.put(regra, 15);
		
		regra = new ArrayList<String>();		
		regra.add("numberadvisors");	
		regra.add("classificationpaper");
		regra.add("numbercourse");	
		lista.put(regra, 20);
		
		regra = new ArrayList<String>();		
		regra.add("numberadvisors");
		regra.add("classificationpaper");
		regra.add("title");				
		lista.put(regra, 20);
		
		regra = new ArrayList<String>();		
		regra.add("departament");
		regra.add("university");			
		lista.put(regra, 25);
		
		regra = new ArrayList<String>();		
		regra.add("university");
		regra.add("maritalstatus");
		regra.add("phone");				
		lista.put(regra, 20);
		
		regra = new ArrayList<String>();		
		regra.add("university");
		regra.add("hiredate");
		regra.add("ssn");						
		lista.put(regra, 20);
		
//		regra = new ArrayList<String>();		
//		regra.add("level");
//		regra.add("salary");
//		regra.add("title");
//		regra.add("class");
		
		return lista;
	}

	private static List<Percents> porcentagens() {

		List<Percents> lista = new ArrayList<Percents>();
		
		lista.add(new Percents("ssn", 1));
		lista.add(new Percents("phone", 0.5F));
		lista.add(new Percents("email", 0.5F));
		lista.add(new Percents("hiredate", 1));
		lista.add(new Percents("maritalstatus", 1));
		lista.add(new Percents("removed", 1));
		lista.add(new Percents("datelastremoved", 1));
		lista.add(new Percents("jobSchema", 0.5F));
		lista.add(new Percents("title", 1));
		lista.add(new Percents("level", 1.5F));
		lista.add(new Percents("class", 0.8F));
		//lista.add(new Percents("salary", 1.5F));
		lista.add(new Percents("departament", 0.5F));
		lista.add(new Percents("university", 0.4F));
		lista.add(new Percents("graduateprogram", 0.2F));
		lista.add(new Percents("numbercourse", 2));
		lista.add(new Percents("numberadvisors", 4));
		lista.add(new Percents("classificationpaper", 3.5F));

		return lista;
	}
	
	private static List<String> listaAtributos(List<Percents> lista) {		
		List<String> listaAtributos = new ArrayList<String>();
		for (Percents p : lista) {
			listaAtributos.add(p.getAtributo());
		}		
		return listaAtributos;
	}
	
	private static HashMap<String, String> returnHashMap() {

		HashMap<String, String> hashMap = new HashMap<String, String>();

		hashMap.put("title", null);
		hashMap.put("class", null);
		hashMap.put("level", null);		
		hashMap.put("salary", null);
		hashMap.put("jobSchema", null);

		return hashMap;
	}

	private static void dividirBaseInclusao(int tamanhoBase, int tamBaseInclusao) throws Exception {
		System.out.println("Inicio Divisao: " + new Date());
		try {			
			String baseXMLAntes = workingPath + separator + "tempRandom" + separator + tamanhoBase + ".xml";
			String vBase = workingPath + separator + "tempRandom" + separator + "vBase.xml";
			String baseInclusao = workingPath + separator + "tempRandom" + separator + "baseInclusao.xml";

			XMLInputFactory factory = XMLInputFactory.newInstance();
			InputStream is = new FileInputStream(baseXMLAntes);
			XMLStreamReader vAntesReader = factory.createXMLStreamReader(is);

			XMLOutputFactory factoryOut = XMLOutputFactory.newInstance();
			OutputStream os = new FileOutputStream(baseInclusao);
			XMLStreamWriter vDepoisWriter = factoryOut.createXMLStreamWriter(os); 

			String root = null;
			String register = null;
			String attribute = null;

			int i = 0, event;

			while (vAntesReader.hasNext()) {

				event = vAntesReader.next();
				if (event == XMLStreamConstants.START_ELEMENT) {                    
					if (root == null) {
						root = vAntesReader.getLocalName();
						vDepoisWriter.writeStartElement(root);
					} else if (register == null) {
						register = vAntesReader.getLocalName();
						vDepoisWriter.writeStartElement(register);
					} else if (attribute == null) {
						attribute = vAntesReader.getLocalName();						
						vDepoisWriter.writeStartElement(attribute);
						vDepoisWriter.writeCharacters(vAntesReader.getElementText().toLowerCase());
						vDepoisWriter.writeEndElement();

						attribute = null;
					}
				} else if (event == XMLStreamConstants.END_ELEMENT) {
					if (!vAntesReader.getLocalName().equals(root)) {
						vDepoisWriter.writeEndElement();
					} else {
						break;
					}
					register = null;
					i++;
					if (i == tamBaseInclusao) {
						vDepoisWriter.writeEndDocument();
						vDepoisWriter.close();
						os.close();

						factoryOut = XMLOutputFactory.newInstance();
						os = new FileOutputStream(vBase);
						vDepoisWriter = factoryOut.createXMLStreamWriter(os);

						vDepoisWriter.writeStartElement(root);
					}
				} else {
					vDepoisWriter.writeCharacters(vAntesReader.getText());
				}
			}
			//System.out.println(i);
			vDepoisWriter.writeEndDocument();
			vDepoisWriter.close();
			is.close();
			os.close();
			System.out.println("Fim Divisao: " + new Date());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//Conjunto para programas de pos
	private static HashMap<String, List<String>> tableUniversidades() {

		HashMap<String, List<String>> table = new HashMap<String, List<String>>();  

		table.put("ufac", new ArrayList<String>());

		table.put("ufal", new ArrayList<String>());
		table.get("ufal").add("ppge");
		table.get("ufal").add("ppgec");
		table.get("ufal").add("ppgeq");
		table.get("ufal").add("ppgrhs");

		table.put("ufam", new ArrayList<String>());
		table.get("ufam").add("ppgas");
		table.get("ufam").add("ppg");
		table.get("ufam").add("ppgpsi");
		table.get("ufam").add("ppgccom");

		table.put("unifap", new ArrayList<String>());
		table.get("unifap").add("ppgbio");
		table.get("unifap").add("ppgcs");
		table.get("unifap").add("ppgmdr");
		table.get("unifap").add("ppgdapp");

		table.put("ufba", new ArrayList<String>());
		table.get("ufba").add("ppgci");
		table.get("ufba").add("pei");
		table.get("ufba").add("ppgenf");
		table.get("ufba").add("ppgac");

		table.put("ufrb", new ArrayList<String>());

		table.put("ufc", new ArrayList<String>());
		table.get("ufc").add("pgditm");
		table.get("ufc").add("ppgeti");
		table.get("ufc").add("pec");
		table.get("ufc").add("geslog");

		table.put("urca", new ArrayList<String>());

		table.put("unb", new ArrayList<String>());
		table.get("unb").add("psto");
		table.get("unb").add("ppgee");
		table.get("unb").add("ppgec");
		table.get("unb").add("ppca");

		table.put("ufes", new ArrayList<String>());
		table.get("ufes").add("ppgcc");
		table.get("ufes").add("ppgps");
		table.get("ufes").add("ppgef");
		table.get("ufes").add("poscom");

		table.put("ufg", new ArrayList<String>());
		table.get("ufg").add("ppgbrph");
		table.get("ufg").add("ppgcta");
		table.get("ufg").add("pgmp");
		table.get("ufg").add("ppggecon");

		table.put("fesurv", new ArrayList<String>());

		table.put("ufma", new ArrayList<String>());
		table.get("ufma").add("ppge");
		table.get("ufma").add("ppgpp");
		table.get("ufma").add("ppgquim");
		table.get("ufma").add("ppgee");

		table.put("ufmg", new ArrayList<String>());

		table.put("ufv", new ArrayList<String>());

		table.put("ufu", new ArrayList<String>());

		table.put("ufla", new ArrayList<String>());

		table.put("ufop", new ArrayList<String>());

		table.put("ufjf", new ArrayList<String>());
		table.get("ufjf").add("ppge");
		table.get("ufjf").add("ppee");
		table.get("ufjf").add("ppgcso");
		table.get("ufjf").add("pgcc");

		table.put("unifei", new ArrayList<String>());

		table.put("unifal-mg", new ArrayList<String>());

		table.put("uftm", new ArrayList<String>());
		table.get("uftm").add("profletras");
		table.get("uftm").add("profmat");
		table.get("uftm").add("pmpit");
		table.get("uftm").add("pgcs");

		table.put("ufsj", new ArrayList<String>());
		table.get("ufsj").add("ppgcf");
		table.get("ufsj").add("ppgcs");
		table.get("ufsj").add("ppgee");
		table.get("ufsj").add("ppgel");

		table.put("ufvjm", new ArrayList<String>());
		table.get("ufvjm").add("ppggied");
		table.get("ufvjm").add("ppgcf");
		table.get("ufvjm").add("pmpgcf");
		table.get("ufvjm").add("ppgpv");

		table.put("ufms", new ArrayList<String>());

		table.put("ufgd", new ArrayList<String>());

		table.put("ufmt", new ArrayList<String>());
		table.get("ufmt").add("ppgda");
		table.get("ufmt").add("ppgecb");
		table.get("ufmt").add("ppge");
		table.get("ufmt").add("ppgef");

		table.put("ufpa", new ArrayList<String>());

		table.put("ufra", new ArrayList<String>());

		table.put("ufopa", new ArrayList<String>());
		table.get("ufopa").add("ppg-rna");
		table.get("ufopa").add("ppg-racam");
		table.get("ufopa").add("ppg-bio");
		table.get("ufopa").add("profletras");

		table.put("ufpb", new ArrayList<String>());

		table.put("ufcg", new ArrayList<String>());

		table.put("ufpe", new ArrayList<String>());

		table.put("ufrpe", new ArrayList<String>());

		table.put("univasf", new ArrayList<String>());
		table.get("univasf").add("cpgcvs");
		table.get("univasf").add("pggrnsa");
		table.get("univasf").add("cpgcsb");
		table.get("univasf").add("cpgea");

		table.put("ufpi", new ArrayList<String>());

		table.put("ufpr", new ArrayList<String>());
		table.get("ufpr").add("ppgadm");
		table.get("ufpr").add("pgapv");
		table.get("ufpr").add("ppgas");
		table.get("ufpr").add("cpgcv");

		table.put("utfpr", new ArrayList<String>());
		table.get("utfpr").add("ppgtal");
		table.get("utfpr").add("ppgec");
		table.get("utfpr").add("ppgeb");
		table.get("utfpr").add("ppgea");

		table.put("unila", new ArrayList<String>());

		table.put("ufrj", new ArrayList<String>());
		table.get("ufrj").add("ppgcal");
		table.get("ufrj").add("pesc");
		table.get("ufrj").add("pee");
		table.get("ufrj").add("ppe");

		table.put("uff", new ArrayList<String>());
		table.get("uff").add("pgmec");
		table.get("uff").add("pgta");
		table.get("uff").add("pgcaps");
		table.get("uff").add("ppgo");

		table.put("ufrrj", new ArrayList<String>());
		table.get("ufrrj").add("ppgao");
		table.get("ufrrj").add("pgcaf");
		table.get("ufrrj").add("ppgcf");
		table.get("ufrrj").add("ppgcs");

		table.put("unirio", new ArrayList<String>());
		table.get("unirio").add("ppgan");
		table.get("unirio").add("ppgeac");
		table.get("unirio").add("ppghiv/hv");
		table.get("unirio").add("ppgneuro");

		table.put("ufrn", new ArrayList<String>());
		table.get("ufrn").add("mpee");
		table.get("ufrn").add("ddm");
		table.get("ufrn").add("mpgpi");
		table.get("ufrn").add("mpes");

		table.put("ufersa", new ArrayList<String>());
		table.get("ufersa").add("pgcc");
		table.get("ufersa").add("pgcs");
		table.get("ufersa").add("pgfito");
		table.get("ufersa").add("pgid");

		table.put("unir", new ArrayList<String>());
		table.get("unir").add("pgdra");
		table.get("unir").add("mel");
		table.get("unir").add("ppghisec");
		table.get("unir").add("mapsi");

		table.put("ufrr", new ArrayList<String>());
		table.get("ufrr").add("ppgsof");
		table.get("ufrr").add("profmat");
		table.get("ufrr").add("ppdra");
		table.get("ufrr").add("procisa");

		table.put("ufrgs", new ArrayList<String>());
		table.get("ufrgs").add("ppgepi");
		table.get("ufrgs").add("ppggeo");
		table.get("ufrgs").add("ppgas");
		table.get("ufrgs").add("ppgsr");

		table.put("ufsm", new ArrayList<String>());
		table.get("ufsm").add("ppgart");
		table.get("ufsm").add("ppgter");
		table.get("ufsm").add("ppgd");
		table.get("ufsm").add("ppgemef");

		table.put("ufpel", new ArrayList<String>());
		table.get("ufpel").add("ppgom");
		table.get("ufpel").add("pgcc");
		table.get("ufpel").add("ppgent");
		table.get("ufpel").add("ppfrechid");

		table.put("furg", new ArrayList<String>());
		table.get("furg").add("ppge");
		table.get("furg").add("ppmec");
		table.get("furg").add("ppgh");
		table.get("furg").add("ppgcomp");

		table.put("ufcspa", new ArrayList<String>());

		table.put("unipampa", new ArrayList<String>());

		table.put("ufsc", new ArrayList<String>());

		table.put("furb", new ArrayList<String>());
		table.get("furb").add("ppgcc");
		table.get("furb").add("ppgdr");
		table.get("furb").add("ppge");
		table.get("furb").add("ppgea");

		table.put("uffs", new ArrayList<String>());
		table.get("uffs").add("ppgel");
		table.get("uffs").add("ppgadr");
		table.get("uffs").add("ppgcta");
		table.get("uffs").add("ppge");

		table.put("ufs", new ArrayList<String>());
		table.get("ufs").add("ppgagri");
		table.get("ufs").add("ppgcr");
		table.get("ufs").add("prodir");
		table.get("ufs").add("ppgpi");

		table.put("usp", new ArrayList<String>());

		table.put("unifesp", new ArrayList<String>());

		table.put("ufscar", new ArrayList<String>());
		table.get("ufscar").add("ppgaa");
		table.get("ufscar").add("ppgadr");
		table.get("ufscar").add("ppgcam");
		table.get("ufscar").add("ppgcem");

		table.put("ufabc", new ArrayList<String>());

		table.put("unitau", new ArrayList<String>());

		table.put("uft", new ArrayList<String>());
		table.get("uft").add("ppgl ");
		table.get("uft").add("pgecotonos");
		table.get("uft").add("pgciamb");
		table.get("uft").add("ppgmcs");

		table.put("unitins", new ArrayList<String>());

		return table;
	}

	//Conjunto para salario dos professores
	private static HashMap<String, HashMap<String, HashMap<Integer, HashMap<String,Integer>>>> table() {

		//jobSchema<class<level<title,salary>
		HashMap<String, HashMap<String, HashMap<Integer, HashMap<String,Integer>>>> table = 
				new HashMap<String, HashMap<String,HashMap<Integer,HashMap<String,Integer>>>>();  

		table.put("\"20h\"", new HashMap<String, HashMap<Integer,HashMap<String,Integer>>>());
		table.put("\"40h\"", new HashMap<String, HashMap<Integer,HashMap<String,Integer>>>());
		table.put("\"ed\"", new HashMap<String, HashMap<Integer,HashMap<String,Integer>>>());	

		table.get("\"20h\"").put("titular professor", new HashMap<Integer, HashMap<String,Integer>>());
		table.get("\"20h\"").put("associated professor", new HashMap<Integer, HashMap<String,Integer>>());
		table.get("\"20h\"").put("adjunct professor", new HashMap<Integer, HashMap<String,Integer>>());
		table.get("\"20h\"").put("assistant professor", new HashMap<Integer, HashMap<String,Integer>>());
		table.get("\"20h\"").put("auxiliary professor", new HashMap<Integer, HashMap<String,Integer>>());

		table.get("\"20h\"").get("titular professor").put(1, new HashMap<String,Integer>());

		table.get("\"20h\"").get("titular professor").get(1).put("graduate", 2801);
		table.get("\"20h\"").get("titular professor").get(1).put("master", 3723);
		table.get("\"20h\"").get("titular professor").get(1).put("phd", 4334);

		table.get("\"20h\"").get("associated professor").put(4, new HashMap<String,Integer>());
		table.get("\"20h\"").get("associated professor").put(3, new HashMap<String,Integer>());
		table.get("\"20h\"").get("associated professor").put(2, new HashMap<String,Integer>());
		table.get("\"20h\"").get("associated professor").put(1, new HashMap<String,Integer>());

		table.get("\"20h\"").get("associated professor").get(4).put("graduate", 2708);
		table.get("\"20h\"").get("associated professor").get(4).put("master", 3520);
		table.get("\"20h\"").get("associated professor").get(4).put("phd", 4059);

		table.get("\"20h\"").get("associated professor").get(3).put("graduate", 2662);
		table.get("\"20h\"").get("associated professor").get(3).put("master", 3433);
		table.get("\"20h\"").get("associated professor").get(3).put("phd", 3889);	

		table.get("\"20h\"").get("associated professor").get(2).put("graduate", 2618);
		table.get("\"20h\"").get("associated professor").get(2).put("master", 3375);
		table.get("\"20h\"").get("associated professor").get(2).put("phd", 3776);

		table.get("\"20h\"").get("associated professor").get(1).put("graduate", 2588);
		table.get("\"20h\"").get("associated professor").get(1).put("master", 3335);
		table.get("\"20h\"").get("associated professor").get(1).put("phd", 3733);

		table.get("\"20h\"").get("adjunct professor").put(4, new HashMap<String,Integer>());
		table.get("\"20h\"").get("adjunct professor").put(3, new HashMap<String,Integer>());
		table.get("\"20h\"").get("adjunct professor").put(2, new HashMap<String,Integer>());
		table.get("\"20h\"").get("adjunct professor").put(1, new HashMap<String,Integer>());

		table.get("\"20h\"").get("adjunct professor").get(4).put("graduate", 2357);
		table.get("\"20h\"").get("adjunct professor").get(4).put("master", 2924);
		table.get("\"20h\"").get("adjunct professor").get(4).put("phd", 3388);

		table.get("\"20h\"").get("adjunct professor").get(3).put("graduate", 2326);
		table.get("\"20h\"").get("adjunct professor").get(3).put("master", 2856);
		table.get("\"20h\"").get("adjunct professor").get(3).put("phd", 3329);

		table.get("\"20h\"").get("adjunct professor").get(2).put("graduate", 2296);
		table.get("\"20h\"").get("adjunct professor").get(2).put("master", 2809);
		table.get("\"20h\"").get("adjunct professor").get(2).put("phd", 3264);

		table.get("\"20h\"").get("adjunct professor").get(1).put("graduate", 2193);
		table.get("\"20h\"").get("adjunct professor").get(1).put("master", 2691);
		table.get("\"20h\"").get("adjunct professor").get(1).put("phd", 3110);

		table.get("\"20h\"").get("assistant professor").put(2, new HashMap<String,Integer>());
		table.get("\"20h\"").get("assistant professor").put(1, new HashMap<String,Integer>());

		table.get("\"20h\"").get("assistant professor").get(2).put("graduate", 2093);
		table.get("\"20h\"").get("assistant professor").get(2).put("master", 2580);
		table.get("\"20h\"").get("assistant professor").get(2).put("phd", 2971);

		table.get("\"20h\"").get("assistant professor").get(1).put("graduate", 2069);
		table.get("\"20h\"").get("assistant professor").get(1).put("master", 2527);
		table.get("\"20h\"").get("assistant professor").get(1).put("phd", 2893);

		table.get("\"20h\"").get("auxiliary professor").put(2, new HashMap<String,Integer>());
		table.get("\"20h\"").get("auxiliary professor").put(1, new HashMap<String,Integer>());

		table.get("\"20h\"").get("auxiliary professor").get(2).put("graduate", 1999);
		table.get("\"20h\"").get("auxiliary professor").get(2).put("master", 2443);
		table.get("\"20h\"").get("auxiliary professor").get(2).put("phd", 2802);

		table.get("\"20h\"").get("auxiliary professor").get(1).put("graduate", 1966);
		table.get("\"20h\"").get("auxiliary professor").get(1).put("master", 2394);
		table.get("\"20h\"").get("auxiliary professor").get(1).put("phd", 2752);

		//////////////////////////////////////

		table.get("\"40h\"").put("titular professor", new HashMap<Integer, HashMap<String,Integer>>());
		table.get("\"40h\"").put("associated professor", new HashMap<Integer, HashMap<String,Integer>>());
		table.get("\"40h\"").put("adjunct professor", new HashMap<Integer, HashMap<String,Integer>>());
		table.get("\"40h\"").put("assistant professor", new HashMap<Integer, HashMap<String,Integer>>());
		table.get("\"40h\"").put("auxiliary professor", new HashMap<Integer, HashMap<String,Integer>>());

		table.get("\"40h\"").get("titular professor").put(1, new HashMap<String,Integer>());

		table.get("\"40h\"").get("titular professor").get(1).put("graduate", 4146);
		table.get("\"40h\"").get("titular professor").get(1).put("master", 5533);
		table.get("\"40h\"").get("titular professor").get(1).put("phd", 7052);

		table.get("\"40h\"").get("associated professor").put(4, new HashMap<String, Integer>());
		table.get("\"40h\"").get("associated professor").put(3, new HashMap<String, Integer>());
		table.get("\"40h\"").get("associated professor").put(2, new HashMap<String, Integer>());
		table.get("\"40h\"").get("associated professor").put(1, new HashMap<String, Integer>());

		table.get("\"40h\"").get("associated professor").get(4).put("graduate", 4004);
		table.get("\"40h\"").get("associated professor").get(4).put("master", 5225);
		table.get("\"40h\"").get("associated professor").get(4).put("phd", 6599);

		table.get("\"40h\"").get("associated professor").get(3).put("graduate", 3935);
		table.get("\"40h\"").get("associated professor").get(3).put("master", 5134);
		table.get("\"40h\"").get("associated professor").get(3).put("phd", 6471);	

		table.get("\"40h\"").get("associated professor").get(2).put("graduate", 3868);
		table.get("\"40h\"").get("associated professor").get(2).put("master", 5063);
		table.get("\"40h\"").get("associated professor").get(2).put("phd", 6389);

		table.get("\"40h\"").get("associated professor").get(1).put("graduate", 3861);
		table.get("\"40h\"").get("associated professor").get(1).put("master", 5053);
		table.get("\"40h\"").get("associated professor").get(1).put("phd", 6371);

		table.get("\"40h\"").get("adjunct professor").put(4, new HashMap<String, Integer>());
		table.get("\"40h\"").get("adjunct professor").put(3, new HashMap<String, Integer>());
		table.get("\"40h\"").get("adjunct professor").put(2, new HashMap<String, Integer>());
		table.get("\"40h\"").get("adjunct professor").put(1, new HashMap<String, Integer>());

		table.get("\"40h\"").get("adjunct professor").get(4).put("graduate", 3392);
		table.get("\"40h\"").get("adjunct professor").get(4).put("master", 4463);
		table.get("\"40h\"").get("adjunct professor").get(4).put("phd", 5843);

		table.get("\"40h\"").get("adjunct professor").get(3).put("graduate", 3343);
		table.get("\"40h\"").get("adjunct professor").get(3).put("master", 4340);
		table.get("\"40h\"").get("adjunct professor").get(3).put("phd", 5658);

		table.get("\"40h\"").get("adjunct professor").get(2).put("graduate", 3269);
		table.get("\"40h\"").get("adjunct professor").get(2).put("master", 4239);
		table.get("\"40h\"").get("adjunct professor").get(2).put("phd", 5555);

		table.get("\"40h\"").get("adjunct professor").get(1).put("graduate", 3118);
		table.get("\"40h\"").get("adjunct professor").get(1).put("master", 4060);
		table.get("\"40h\"").get("adjunct professor").get(1).put("phd", 5308);

		table.get("\"40h\"").get("assistant professor").put(2, new HashMap<String, Integer>());
		table.get("\"40h\"").get("assistant professor").put(1, new HashMap<String, Integer>());

		table.get("\"40h\"").get("assistant professor").get(2).put("graduate", 3010);
		table.get("\"40h\"").get("assistant professor").get(2).put("master", 3929);
		table.get("\"40h\"").get("assistant professor").get(2).put("phd", 5121);

		table.get("\"40h\"").get("assistant professor").get(1).put("graduate", 2938);
		table.get("\"40h\"").get("assistant professor").get(1).put("master", 3843);
		table.get("\"40h\"").get("assistant professor").get(1).put("phd", 4964);

		table.get("\"40h\"").get("auxiliary professor").put(2, new HashMap<String,Integer>());
		table.get("\"40h\"").get("auxiliary professor").put(1, new HashMap<String,Integer>());

		table.get("\"40h\"").get("auxiliary professor").get(2).put("graduate", 2834);
		table.get("\"40h\"").get("auxiliary professor").get(2).put("master", 3701);
		table.get("\"40h\"").get("auxiliary professor").get(2).put("phd", 4799);

		table.get("\"40h\"").get("auxiliary professor").get(1).put("graduate", 2764);
		table.get("\"40h\"").get("auxiliary professor").get(1).put("master", 3599);
		table.get("\"40h\"").get("auxiliary professor").get(1).put("phd", 4699);

		//////////////////////////////////////

		table.get("\"ed\"").put("titular professor", new HashMap<Integer, HashMap<String,Integer>>());
		table.get("\"ed\"").put("associated professor", new HashMap<Integer, HashMap<String,Integer>>());
		table.get("\"ed\"").put("adjunct professor", new HashMap<Integer, HashMap<String,Integer>>());
		table.get("\"ed\"").put("assistant professor", new HashMap<Integer, HashMap<String,Integer>>());
		table.get("\"ed\"").put("auxiliary professor", new HashMap<Integer, HashMap<String,Integer>>());

		table.get("\"ed\"").get("titular professor").put(1, new HashMap<String,Integer>());

		table.get("\"ed\"").get("titular professor").get(1).put("graduate", 6363);
		table.get("\"ed\"").get("titular professor").get(1).put("master", 9656);
		table.get("\"ed\"").get("titular professor").get(1).put("phd", 15956);

		table.get("\"ed\"").get("associated professor").put(4, new HashMap<String,Integer>());
		table.get("\"ed\"").get("associated professor").put(3, new HashMap<String,Integer>());
		table.get("\"ed\"").get("associated professor").put(2, new HashMap<String,Integer>());
		table.get("\"ed\"").get("associated professor").put(1, new HashMap<String,Integer>());

		table.get("\"ed\"").get("associated professor").get(4).put("graduate", 6144);
		table.get("\"ed\"").get("associated professor").get(4).put("master", 9299);
		table.get("\"ed\"").get("associated professor").get(4).put("phd", 15059);

		table.get("\"ed\"").get("associated professor").get(3).put("graduate", 6038);
		table.get("\"ed\"").get("associated professor").get(3).put("master", 9192);
		table.get("\"ed\"").get("associated professor").get(3).put("phd", 14537);	

		table.get("\"ed\"").get("associated professor").get(2).put("graduate", 5933);
		table.get("\"ed\"").get("associated professor").get(2).put("master", 9087);
		table.get("\"ed\"").get("associated professor").get(2).put("phd", 14010);

		table.get("\"ed\"").get("associated professor").get(1).put("graduate", 5923);
		table.get("\"ed\"").get("associated professor").get(1).put("master", 9075);
		table.get("\"ed\"").get("associated professor").get(1).put("phd", 13604);

		table.get("\"ed\"").get("adjunct professor").put(4, new HashMap<String,Integer>());
		table.get("\"ed\"").get("adjunct professor").put(3, new HashMap<String,Integer>());
		table.get("\"ed\"").get("adjunct professor").put(2, new HashMap<String,Integer>());
		table.get("\"ed\"").get("adjunct professor").put(1, new HashMap<String,Integer>());

		table.get("\"ed\"").get("adjunct professor").get(4).put("graduate", 4704);
		table.get("\"ed\"").get("adjunct professor").get(4).put("master", 7205);
		table.get("\"ed\"").get("adjunct professor").get(4).put("phd", 10373);

		table.get("\"ed\"").get("adjunct professor").get(3).put("graduate", 4629);
		table.get("\"ed\"").get("adjunct professor").get(3).put("master", 7033);
		table.get("\"ed\"").get("adjunct professor").get(3).put("phd", 10060);

		table.get("\"ed\"").get("adjunct professor").get(2).put("graduate", 4556);
		table.get("\"ed\"").get("adjunct professor").get(2).put("master", 6888);
		table.get("\"ed\"").get("adjunct professor").get(2).put("phd", 9760);

		table.get("\"ed\"").get("adjunct professor").get(1).put("graduate", 4484);
		table.get("\"ed\"").get("adjunct professor").get(1).put("master", 6746);
		table.get("\"ed\"").get("adjunct professor").get(1).put("phd", 9536);

		table.get("\"ed\"").get("assistant professor").put(2, new HashMap<String,Integer>());
		table.get("\"ed\"").get("assistant professor").put(1, new HashMap<String,Integer>());

		table.get("\"ed\"").get("assistant professor").get(2).put("graduate", 4176);
		table.get("\"ed\"").get("assistant professor").get(2).put("master", 6212);
		table.get("\"ed\"").get("assistant professor").get(2).put("phd", 8828);

		table.get("\"ed\"").get("assistant professor").get(1).put("graduate", 4111);
		table.get("\"ed\"").get("assistant professor").get(1).put("master", 6131);
		table.get("\"ed\"").get("assistant professor").get(1).put("phd", 8740);

		table.get("\"ed\"").get("auxiliary professor").put(2, new HashMap<String,Integer>());
		table.get("\"ed\"").get("auxiliary professor").put(1, new HashMap<String,Integer>());

		table.get("\"ed\"").get("auxiliary professor").get(2).put("graduate", 3865);
		table.get("\"ed\"").get("auxiliary professor").get(2).put("master", 5881);
		table.get("\"ed\"").get("auxiliary professor").get(2).put("phd", 8480);

		table.get("\"ed\"").get("auxiliary professor").get(1).put("graduate", 3804);
		table.get("\"ed\"").get("auxiliary professor").get(1).put("master", 5736);
		table.get("\"ed\"").get("auxiliary professor").get(1).put("phd", 8344);

		return table;
	}

	public enum Class{

		AuxiliaryProfessor("auxiliary professor", 1),
		AssistantProfessor("assistant professor", 2),
		AdjunctProfessor("adjunct professor", 3),
		AssociatedProfessor("associated professor", 4),		
		TitularProfessor("titular professor", 5);

		private int nivel;
		private String classe;

		Class(String classe, int nivel){
			this.classe = classe;
			this.nivel = nivel;
		}

		public int getNivel(){
			return this.nivel;
		}

		public String getClasse(){
			return this.classe;
		}
	}
}
