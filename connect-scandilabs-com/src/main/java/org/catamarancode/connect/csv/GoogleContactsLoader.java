package org.catamarancode.connect.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.catamarancode.connect.entity.Person;
import org.catamarancode.entity.support.NotUniqueResultException;
import org.catamarancode.entity.support.Persistable;
import org.catamarancode.entity.support.PersistableUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.context.internal.ThreadLocalSessionContext;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import com.csvreader.CsvReader;

public class GoogleContactsLoader {

	/**
	 * Run from command line
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Load the spring contexts
		GenericApplicationContext ctx = new GenericApplicationContext();
		XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
		xmlReader.loadBeanDefinitions(new ClassPathResource("csv-spring.xml"));
		// xmlReader.loadBeanDefinitions("file:/bh/autosite/conf/chrome-load-spring.xml");
		ctx.refresh();
		SessionFactory sessionFactory = (SessionFactory) ctx
				.getBean("sessionFactory");

		if ((args.length == 0) || (!StringUtils.hasText(args[0]))) {
			throw new IllegalArgumentException(
					"Missing parameter: path to data file");
		}
		String dataFileName = args[0];
		logger.info("Processing " + dataFileName);

		// Instantiate and run
		GoogleContactsLoader loader = new GoogleContactsLoader(sessionFactory);
		File dataFile = null;
		try {
			dataFile = new File(dataFileName);
			if (!dataFile.exists()) {
				throw new IllegalArgumentException("File does not exist: "
						+ dataFile.getAbsolutePath());
			}
			File trimmedDataFile = loader.trimEOLWithinStrings(dataFile);
			loader.loadDataFromFile(trimmedDataFile);
		} catch (Exception e) {
			String msg = "Error in file " + dataFile.getAbsolutePath() + ": "
					+ e.getMessage();
			// logger.error("Error in file " + dataFile.getAbsolutePath() + ": "
			// + e.getMessage(), e);
			throw new RuntimeException(msg, e);
		}

		logger.info("Successfully finished processing "
				+ dataFile.getAbsolutePath());
	}
	
	private boolean hasUnterminatedQuote(String s) {
		if (s == null) {
			return false;
		}
		int firstQuotePos = s.indexOf('"');
		if (firstQuotePos == -1) {
			return false;
		}
		String remains = s.substring(firstQuotePos + 1);		
		int secondQuote = remains.indexOf('"');
		if (secondQuote == -1) {
			
			// We have an unterminated quote
			return true;
		}
		
		// We know that the first quote was terminated but we don't know if there are more quotes in the string
		return hasUnterminatedQuote(remains.substring(secondQuote+1));		
	}

	/**
	 * Trims away EOL characters contained within text qualifiers. Used because
	 * the CsvReader utility does not handle entries that span multiple lines.
	 * 
	 * @param inFile
	 * @return outFile
	 * @throws IOException 
	 */
	private File trimEOLWithinStrings(File inFile) throws IOException {
        String property = "java.io.tmpdir";
        String tempDir = System.getProperty(property);
        File tempDirAsFile = new File(tempDir);        
        File outFile = new File(tempDirAsFile, RandomStringUtils.randomAlphabetic(6) + "_" + inFile.getName());
        logger.debug("Writing trimmed temporary file to " + outFile.getAbsolutePath());
        
        BufferedReader reader = new BufferedReader(new FileReader(inFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile)); 
        
        String line = null;
        boolean previousLineOpen = false;
        do {
        	line = reader.readLine();
        	if (line == null) {
        		continue;
        	}
        	//line = trimNullChars(line);
        	if (line.length() == 1 && line.charAt(0) == '\0') {
        		continue;
        	}
        	writer.append(line);
        	boolean currentLineOpen = this.hasUnterminatedQuote(line); 
        	if (!currentLineOpen && !previousLineOpen) {
        		
        		// Current line does not have unterminated quote and previous line(s) did not have unterminated quote
        		writer.newLine();
        	} else if (currentLineOpen && !previousLineOpen) {
        		
        		// Since a previous line was not open, then the current line must have opened up a new string        		
        		previousLineOpen = true;
        		writer.append(';');
        	} else if (!currentLineOpen && previousLineOpen) {
        		
        		// Since a previous line was open, then the current line must be a continuation of the previous string
        		writer.append(';');
        		
        	} else {
        		
        		// Since a previous line was not open, then the current line must have terminated the previous opened string
        		writer.newLine();
        		previousLineOpen = false;        		
        	}
        } while (line != null);
        reader.close();
        writer.close();
        return outFile;
	}

	private SessionFactory sessionFactory;

	private static Logger logger = LoggerFactory
			.getLogger(GoogleContactsLoader.class.getName());

	public GoogleContactsLoader(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private String valueAt(CsvReader csv, int pos) throws IOException {
		if (StringUtils.hasText(csv.getValues()[pos])) {
			return csv.getValues()[pos].trim();
		}
		return null;
	}

	public void loadDataFromFile(File dataFile) {

		StatelessSession statelessSession = sessionFactory
				.openStatelessSession();
		statelessSession.beginTransaction();

		BufferedReader in = null;
		CsvReader csv = null;
		try {

			// Read header line
			in = new BufferedReader(new FileReader(dataFile));
			csv = new CsvReader(in, ',');
			csv.setTextQualifier('"');
			csv.setUseTextQualifier(true);
			csv.readHeaders();

			// NOTE: There seems to be a bug in CsvReader because
			// csv.getIndex("Given Name") returns -1.
			// Therefore we'll use hard-coded file indexes for now

			while (csv.readRecord()) {
				// logger.info(String.format("Processing record %d in file %s",
				// csv.getCurrentRecord(), dataFile.getName()));

				boolean validLine = false;
				Person person = new Person();

				String firstName = fieldInColumn(csv, 1);
				if (StringUtils.hasText(firstName)) {
					validLine = true;
					person.setFirstName(firstName);
				}

				String lastName = fieldInColumn(csv, 3);
				if (StringUtils.hasText(lastName)) {
					validLine = true;
					person.setLastName(lastName);
				}

				String email1 = fieldInColumn(csv, 28);
				if (StringUtils.hasText(email1)) {
					validLine = true;
					person.setEmail1(email1);
				}
				person.setEmail2(fieldInColumn(csv, 30));
				person.setCompany(fieldInColumn(csv, 49));
				person.setPhone1(fieldInColumn(csv, 32));
				person.setPhone2(fieldInColumn(csv, 34));
				person.setPhone3(fieldInColumn(csv, 36));
				person.setPhone3(fieldInColumn(csv, 38));
				person.setWebsite(fieldInColumn(csv, 57));
				person.setMiddleName(fieldInColumn(csv, 2));

				// Skip empty lines with no name or primary email
				// TODO: Figure out how to refactor this into a JSR-303
				// compliant validation rule
				if (!validLine) {
					logger.debug(String.format(
							"Missing first, last or email on line %d: %s",
							csv.getCurrentRecord(), csv.getRawRecord()));
					continue;
				}

				// BEGIN duplicate person load logic (note we can't use PersistableBase b/c of stateless session)
				//Person existingPerson = (Person) person.locateEntity();
				Person existingPerson = null;								
				Criteria crit = statelessSession.createCriteria(Person.class);				
				for (String name : person.naturalKey().keySet()) {
					Object value = person.naturalKey().get(name);
					// TODO: Remove restriction to only string
					if (value != null && value instanceof String) {
						crit.add(Restrictions.eq(name, value));	
					}			
				}
				List<Person> list = crit.list();
				if (list.size() > 1) {
					throw new NotUniqueResultException(
							String.format(
									"Not unique result: Query on class %s using %d parameter arguments resulted in %d matches",
									this.getClass().getName(), person.naturalKey().size(), list.size()));
				}
				if (! list.isEmpty()) {
					existingPerson = list.iterator().next(); 
				}
				// END duplicate person load logic
				
				if (existingPerson != null) {
					person.setId(existingPerson.getId());
					statelessSession.update(person);
					logger.info(String.format(
							"Updated %s (%d) from line %d: %s",
							person.getFirstNameOrEmailLocal(), person.getId(),
							csv.getCurrentRecord(), csv.getRawRecord()));
				} else {
					statelessSession.insert(person);
					logger.info(String.format(
							"Inserted %s with id %d from line %d: %s",
							person.getFirstNameOrEmailLocal(), person.getId(),
							csv.getCurrentRecord(), csv.getRawRecord()));
				}
			}

		} catch (IOException e) {
			String msg = String
					.format("Line %d: Error", csv.getCurrentRecord());
			logger.error(msg, e);
		} catch (ArrayIndexOutOfBoundsException e) {
			String msg = String
					.format("Line %d: Error", csv.getCurrentRecord());
			logger.error(msg, e);
		} catch (Exception e) {
			String msg = String.format("Error parsing line %d: %s",
					csv.getCurrentRecord(), e.getMessage());
			logger.error(msg, e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				statelessSession.getTransaction().commit();
				statelessSession.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

/**
     * Removes '\0' characters and '<' and '>' 
     * @param in
     * @return
     */
	private static String trimInvalidChars(String in) {
		if (in == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < in.length(); i++) {
			char ch = in.charAt(i); 
			if (ch == 0) {
				continue;
			}
			if (ch == '<') {
				continue;
			}
			if (ch == '>') {
				continue;
			}
			if (ch == '"') {
				continue;
			}
			sb.append(ch);
		}
		String out = sb.toString().trim();
		if (out.length() == 0) {
			return null;
		}
		return out;
	}
	
	private static String trimNullChars(String in) {
		if (in == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < in.length(); i++) {
			if (in.charAt(i) == 0) {
				continue;
			}
			sb.append(in.charAt(i));
		}
		return sb.toString();
	}

	private static String fieldInColumn(CsvReader csv, int column)
			throws IOException {
		String raw = csv.get(column);
		if (raw == null) {
			return null;
		}
		return trimInvalidChars(raw.trim());
	}

}
