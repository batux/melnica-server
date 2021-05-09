package melnica.server.web.context.xml.parser;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import melnica.server.web.context.model.WebXml;
import melnica.server.web.context.xml.assembler.WebXmlAssembler;

public class WebXmlParser {

	private WebXmlAssembler assembler;
	
	public WebXmlParser(WebXmlAssembler assembler) {
		this.assembler = assembler;
	}
	
	public WebXml parse(String path) throws ParserConfigurationException, SAXException, IOException {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(path));
		document.getDocumentElement().normalize();
	
		return this.assembler.process(document);
	}
}
