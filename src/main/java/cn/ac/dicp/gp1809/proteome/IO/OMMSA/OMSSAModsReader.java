/* 
 ******************************************************************************
 * File: OMSSAModsReader.java * * * Created on 09-03-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.OMMSA;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;

/**
 * Reader for XML OMSSA mods file
 * 
 * @author Xinning
 * @version 0.1, 09-03-2008, 16:52:13
 */
public class OMSSAModsReader {

	private XMLEventReader reader;

	OMSSAModsReader(String modsfile) throws ModsReadingException {
		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			reader = factory
			        .createXMLEventReader(new FileInputStream(modsfile));
		} catch (FileNotFoundException e) {
			throw new ModsReadingException("The file is unreachable.", e);
		} catch (XMLStreamException e) {
			throw new ModsReadingException(
			        "Exception while generating the reader.", e);
		}
	}

	/**
	 * Get next modification
	 * 
	 * @return
	 * @throws ModsReadingException
	 */
	OMSSAMod getMod() throws ModsReadingException {

		try {
			int index = -1;
			String name = null;
			String description = null;
			double addedMonoMass = 0d;
			double addedAvgMass = 0d;
			double addedN15Mass = 0d;
			String modifiedAt = "";
			boolean isNeutralloss = false;
			double lossMonoMass = 0d;
			double lossAvgMass = 0d;
			double lossN15Mass = 0d;

			int modtypeidx = -1;
			String modtypename = null;

			XMLEvent event;
			while (!(event = reader.nextEvent()).isEndDocument()) {
				if (event.isStartElement()) {
					StartElement se = ((StartElement) event);
					String nodename = se.getName().getLocalPart();

					if (nodename.equals("MSMod")) {
						name = ((Attribute) se.getAttributes().next())
						        .getValue();
						index = Integer.parseInt(((Characters) reader
						        .nextEvent()).getData());
						continue;
					}

					if (nodename.equals("MSModSpec_name")) {
						description = ((Characters) reader.nextEvent())
						        .getData();
						continue;
					}

					if (nodename.equals("MSModSpec_monomass")) {
						addedMonoMass = Double.parseDouble(((Characters) reader
						        .nextEvent()).getData());
						continue;
					}

					if (nodename.equals("MSModSpec_averagemass")) {
						addedAvgMass = Double.parseDouble(((Characters) reader
						        .nextEvent()).getData());
						continue;
					}

					//merge all modified aminoacids together
					if (nodename.equals("MSModSpec_residues_E")) {
						modifiedAt += ((Characters) reader.nextEvent())
						        .getData();
						continue;
					}

					if (nodename.equals("MSModSpec_n15mass")) {
						addedN15Mass = Double.parseDouble(((Characters) reader
						        .nextEvent()).getData());
						continue;
					}

					if (nodename.equals("MSModType")) {
						modtypename = ((Attribute) se.getAttributes().next())
						        .getValue().toLowerCase();
						modtypeidx = Integer.parseInt(((Characters) reader
						        .nextEvent()).getData());
						continue;
					}

					if (nodename.equals("MSMassSet_monomass")) {
						lossMonoMass = Double.parseDouble(((Characters) reader
						        .nextEvent()).getData());
						isNeutralloss = true;
						continue;
					}

					if (nodename.equals("MSMassSet_averagemass")) {
						lossAvgMass = Double.parseDouble(((Characters) reader
						        .nextEvent()).getData());
						continue;
					}

					if (nodename.equals("MSMassSet_n15mass")) {
						lossN15Mass = Double.parseDouble(((Characters) reader
						        .nextEvent()).getData());
						continue;
					}

				} else if (event.isEndElement()) {
					String nodename = ((EndElement) event).getName()
					        .getLocalPart();

					if (nodename.equals("MSModSpec")) {

						// A unsigned modification
						if (addedMonoMass == 0d && addedAvgMass == 0d) {
							continue;
						}

						// If only one mass is set, set another the same value.
						if (addedAvgMass == 0) {
							addedAvgMass = addedMonoMass;
						} else if (addedMonoMass == 0) {
							addedMonoMass = addedAvgMass;
						}

						if (isNeutralloss) {

							// If only one mass is set, set another the same
							// value.
							if (lossMonoMass == 0) {
								lossMonoMass = lossAvgMass;
							} else if (lossAvgMass == 0) {
								lossAvgMass = lossMonoMass;
							}

							return new OMSSAMod(index, name, description,
							        addedMonoMass, addedAvgMass, addedN15Mass, modifiedAt,
							        isNeutralloss, lossMonoMass, lossAvgMass, lossN15Mass,
							        modtypename, modtypeidx);

						} else {
							return new OMSSAMod(index, name, description,
							        addedMonoMass, addedAvgMass, addedN15Mass,modifiedAt,
							        modtypename, modtypeidx);
						}
					}
				}
			}

			return null;
		} catch (Exception e) {
			this.close();
			throw new ModsReadingException("Reading exception.", e);
		}

	}

	/**
	 * Close the reader
	 */
	void close() {
		try {
			this.reader.close();
		} catch (XMLStreamException e) {
			System.err
			        .println("Error while closing the file, but it doesn't matter");
			;
		}
	}
}
