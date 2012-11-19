package eu.uniek.wwy.export;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.wwy.database.HerkenningPunt;

import eu.uniek.wwy.database.OnderzoekDatabase;

public class XMLExport {

	public String getXMLString(OnderzoekDatabase onderzoekDatabase) {
		Document document = new Document();
		Element rootElement = new Element("kml");
		rootElement.setNamespace(Namespace.getNamespace("http://www.opengis.net/kml/2.2"));
		document.setRootElement(rootElement);
		Element documentElement = new Element("Document");
		rootElement.addContent(documentElement);
		documentElement.addContent(createElementWithContent("name", "OnderzoekUitkomst"));
		documentElement.addContent(createElementWithContent("open", "1"));
		documentElement.addContent(createElementWithContent("description", "Hieronder staat wat de testpersoon allemaal heeft gedaan"));

		Element folderElement = new Element("Folder");
		for(Element element : createLandmarksElement(onderzoekDatabase.getHerkenningPunten())) {
			folderElement.addContent(element);
		}
		documentElement.addContent(folderElement);
		folderElement.addContent(createElementWithContent("name", "paths"));
		folderElement.addContent(createElementWithContent("visibility", "1"));
		folderElement.addContent(createElementWithContent("description", "Route"));
		Element placeMarkElement = new Element("Placemark");
		folderElement.addContent(placeMarkElement);
		placeMarkElement.addContent(createElementWithContent("name", "Gelopen route"));
		placeMarkElement.addContent(createElementWithContent("visibility", "1"));

		Element lineStringElement = new Element("LineString");
		placeMarkElement.addContent(lineStringElement);
		lineStringElement.addContent(createElementWithContent("route", "1"));

		lineStringElement.addContent(createCoordinatesElement(onderzoekDatabase));

		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		return outputter.outputString(document);
	}

	private Element createElementWithContent(String name, String content) {
		Element elem = new Element(name);
		elem.setText(content);
		return elem;
	}

	private Element createCoordinatesElement(OnderzoekDatabase onderzoekDatabase) {
		Element coordinatesElement = new Element("coordinates");
		StringBuilder stringBuilder = new StringBuilder();
		for(int index = 1; index < onderzoekDatabase.getBreadcrumbsRowCount(); index++) {
			stringBuilder.append(onderzoekDatabase.getBreadCrumb(index).getLongitudeE6() / 1e6);
			stringBuilder.append(",");
			stringBuilder.append(onderzoekDatabase.getBreadCrumb(index).getLatitudeE6() / 1e6);
			stringBuilder.append("\r\n");
		}
		coordinatesElement.setText(stringBuilder.toString());
		return coordinatesElement;
	}

	private List<Element> createLandmarksElement(List<HerkenningPunt> locations) {
		List<Element> elements = new ArrayList<Element>();
		int i = 0;
		for(HerkenningPunt location : locations) {
			i++;
			if(location != null) {
				Element placeMarkElement = new Element("Placemark");
				placeMarkElement.addContent(createElementWithContent("name", "landmark" + i));
				placeMarkElement.addContent(createElementWithContent("description", location.getmComment()));
				Element Point = new Element("Point");
				placeMarkElement.addContent(Point);
				Point.addContent(createElementWithContent("coordinates", (double) location.getLongitudeE6() / 1e6 + "," + (double) location.getLatitudeE6() / 1e6));
				elements.add(placeMarkElement);
			}
		}
		return elements;
	}
}
