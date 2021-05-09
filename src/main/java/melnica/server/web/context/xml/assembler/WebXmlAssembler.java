package melnica.server.web.context.xml.assembler;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import melnica.server.web.context.model.ServletContext;
import melnica.server.web.context.model.WebXml;

public class WebXmlAssembler {

	public WebXml process(Document document) {
		
        Map<String, ServletContext> servletContextMap = new HashMap<String, ServletContext>();
        String displayName = document.getElementsByTagName("display-name").item(0).getTextContent();

        NodeList servletNodeList = document.getElementsByTagName("servlet");
        int length = servletNodeList.getLength();
        for(int i = 0; i < length; i++) {
        	Node servletNode = servletNodeList.item(i);
        	if (servletNode.getNodeType() == Node.ELEMENT_NODE) {
                Element servletElem = (Element) servletNode;
                
                String name = servletElem.getElementsByTagName("servlet-name").item(0).getTextContent();
                String clazzAbsolutePath = servletElem.getElementsByTagName("servlet-class").item(0).getTextContent();
                
                String loadOnStartup = "0";
                NodeList startupNodeList = servletElem.getElementsByTagName("load-on-startup");
                if(startupNodeList.getLength() > 0) {
                	loadOnStartup = servletElem.getElementsByTagName("load-on-startup").item(0).getTextContent();
                }
                servletContextMap.put(name, new ServletContext(name, clazzAbsolutePath, Integer.parseInt(loadOnStartup)));
        	}
        }
        
        NodeList servletMappingNodeList = document.getElementsByTagName("servlet-mapping");
        length = servletMappingNodeList.getLength();
        for(int i = 0; i < length; i++) {
        	Node servletMappingNode = servletMappingNodeList.item(i);
        	if (servletMappingNode.getNodeType() == Node.ELEMENT_NODE) {
                Element servletMappingElem = (Element) servletMappingNode;
                
                String name = servletMappingElem.getElementsByTagName("servlet-name").item(0).getTextContent();
                if(!servletContextMap.containsKey(name)) {
                	servletContextMap.remove(name);
                	continue;
                }
                
                String urlPattern = servletMappingElem.getElementsByTagName("url-pattern").item(0).getTextContent();
                ServletContext servletContext = servletContextMap.get(name);
                servletContext.setUrlPattern(urlPattern);
                servletContextMap.put(name, servletContext);
        	}
        }
		
		return new WebXml(displayName, servletContextMap);
	}
}
