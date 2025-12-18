package IO;

import abstractClasses.abstractStorage;
import Bicycle.Bicycle;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XMLHandler {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

	public void saveToXml(abstractStorage<Bicycle> storage, String filename) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			Element root = doc.createElement("bicycles");
			doc.appendChild(root);

			Element metadata = doc.createElement("metadata");
			root.appendChild(metadata);

			Element generationDate = doc.createElement("generationDate");
			generationDate.setTextContent(new Date().toString());
			metadata.appendChild(generationDate);

			Element count = doc.createElement("count");
			count.setTextContent(String.valueOf(storage.size()));
			metadata.appendChild(count);

			Element bikesElement = doc.createElement("bicycleList");
			root.appendChild(bikesElement);

			for (Bicycle bike : storage.get_All()) {
				Element bikeElement = createBicycleElement(doc, bike);
				bikesElement.appendChild(bikeElement);
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filename));
			transformer.transform(source, result);

			System.out.println("✓ Data saved to XML file: " + filename);

		} catch (Exception e) {
			System.err.println("✗ Error saving to XML: " + e.getMessage());
		}
	}

	public int loadFromXml(abstractStorage<Bicycle> storage, String filename) {
		int loadedCount = 0;

		try {
			File file = new File(filename);
			if (!file.exists()) {
				System.out.println("XML file not found: " + filename);
				return 0;
			}

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);

			doc.getDocumentElement().normalize();

			NodeList bikeNodes = doc.getElementsByTagName("bicycle");

			for (int i = 0; i < bikeNodes.getLength(); i++) {
				Node node = bikeNodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element bikeElement = (Element) node;

					try {
						Bicycle bike = parseBicycleElement(bikeElement);
						if (bike != null) {
							storage.add(bike);
							loadedCount++;
						}
					} catch (Exception e) {
						System.err.println("Error parsing bicycle element: " + e.getMessage());
					}
				}
			}

			System.out.println("✓ Loaded " + loadedCount + " bicycles from XML file: " + filename);

		} catch (Exception e) {
			System.err.println("✗ Error loading from XML: " + e.getMessage());
		}

		return loadedCount;
	}

	private Element createBicycleElement(Document doc, Bicycle bike) {
		Element bikeElement = doc.createElement("bicycle");

		bikeElement.setAttribute("id", String.valueOf(bike.getId()));

		Element type = doc.createElement("type");
		type.setTextContent(bike.getType());
		bikeElement.appendChild(type);

		Element model = doc.createElement("model");
		model.setTextContent(bike.getModel());
		bikeElement.appendChild(model);

		Element frameMaterial = doc.createElement("frameMaterial");
		frameMaterial.setTextContent(bike.getFrameMaterial());
		bikeElement.appendChild(frameMaterial);

		Element wheelSize = doc.createElement("wheelSize");
		wheelSize.setTextContent(String.valueOf(bike.getWheelSize()));
		bikeElement.appendChild(wheelSize);

		Element gears = doc.createElement("gears");
		gears.setTextContent(String.valueOf(bike.getGears()));
		bikeElement.appendChild(gears);

		if (bike.getManufactureDate() != null) {
			Element manufactureDate = doc.createElement("manufactureDate");
			manufactureDate.setTextContent(DATE_FORMAT.format(bike.getManufactureDate()));
			bikeElement.appendChild(manufactureDate);
		}

		Element price = doc.createElement("price");
		price.setTextContent(String.format("%.2f", bike.getPrice()));
		bikeElement.appendChild(price);

		if (!bike.getAccessories().isEmpty()) {
			Element accessories = doc.createElement("accessories");
			for (String accessory : bike.getAccessories()) {
				Element accessoryElement = doc.createElement("accessory");
				accessoryElement.setTextContent(accessory);
				accessories.appendChild(accessoryElement);
			}
			bikeElement.appendChild(accessories);
		}

		return bikeElement;
	}

	private Bicycle parseBicycleElement(Element bikeElement) throws Exception {
		int id = Integer.parseInt(bikeElement.getAttribute("id"));
		String type = getElementText(bikeElement, "type");
		String model = getElementText(bikeElement, "model");
		String frameMaterial = getElementText(bikeElement, "frameMaterial");
		int wheelSize = Integer.parseInt(getElementText(bikeElement, "wheelSize"));
		int gears = Integer.parseInt(getElementText(bikeElement, "gears"));

		Date manufactureDate = null;
		String dateStr = getElementText(bikeElement, "manufactureDate");
		if (!dateStr.isEmpty()) {
			manufactureDate = DATE_FORMAT.parse(dateStr);
		}

		double price = Double.parseDouble(getElementText(bikeElement, "price"));

		Bicycle bike = new Bicycle(id, type, model, frameMaterial, wheelSize, gears, manufactureDate, price);

		NodeList accessories = bikeElement.getElementsByTagName("accessory");
		for (int i = 0; i < accessories.getLength(); i++) {
			Element accessoryElement = (Element) accessories.item(i);
			bike.addAccessory(accessoryElement.getTextContent());
		}

		return bike;
	}

	private String getElementText(Element parent, String tagName) {
		NodeList nodes = parent.getElementsByTagName(tagName);
		if (nodes.getLength() > 0) {
			return nodes.item(0).getTextContent();
		}
		return "";
	}

	public void updateXmlFile(abstractStorage<Bicycle> storage, String filename) {
		try {
			File file = new File(filename);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc;

			if (file.exists()) {
				doc = builder.parse(file);
			} else {
				doc = builder.newDocument();
				Element root = doc.createElement("bicycles");
				doc.appendChild(root);
			}

			doc.getDocumentElement().normalize();

			NodeList oldBikes = doc.getElementsByTagName("bicycle");
			while (oldBikes.getLength() > 0) {
				oldBikes.item(0).getParentNode().removeChild(oldBikes.item(0));
			}

			Element root = doc.getDocumentElement();
			if (root == null) {
				root = doc.createElement("bicycles");
				doc.appendChild(root);
			}

			for (Bicycle bike : storage.get_All()) {
				Element bikeElement = createBicycleElement(doc, bike);
				root.appendChild(bikeElement);
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filename));
			transformer.transform(source, result);

			System.out.println("✓ XML file updated: " + filename);

		} catch (Exception e) {
			System.err.println("✗ Error updating XML: " + e.getMessage());
		}
	}
}