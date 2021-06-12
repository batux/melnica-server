package melnica.server.web.context.xml.parser;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import melnica.server.web.context.model.FilterContext;
import melnica.server.web.context.model.ServletContext;
import melnica.server.web.context.model.WebXml;

public class WebXmlAssembler {

	public WebXml apply(Document document) {
		
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
                	continue;
                }
                
                String urlPattern = servletMappingElem.getElementsByTagName("url-pattern").item(0).getTextContent();
                ServletContext servletContext = servletContextMap.get(name);
                servletContext.setUrlPattern(urlPattern);
                servletContextMap.put(name, servletContext);
        	}
        }
        
        Map<String, FilterContext> filterContextMap = new LinkedHashMap<String, FilterContext>();

        NodeList filterNodeList = document.getElementsByTagName("filter");
        length = filterNodeList.getLength();
        for(int i = 0; i < length; i++) {
        	Node filterNode = filterNodeList.item(i);
        	if (filterNode.getNodeType() == Node.ELEMENT_NODE) {
                Element filterElem = (Element) filterNode;
                
                String name = filterElem.getElementsByTagName("filter-name").item(0).getTextContent();
                String clazzAbsolutePath = filterElem.getElementsByTagName("filter-class").item(0).getTextContent();
                filterContextMap.put(name, new FilterContext(name, clazzAbsolutePath));
        	}
        }
        
        
        NodeList filterMappingNodeList = document.getElementsByTagName("filter-mapping");
        length = filterMappingNodeList.getLength();
        for(int i = 0; i < length; i++) {
        	Node filterMappingNode = filterMappingNodeList.item(i);
        	if (filterMappingNode.getNodeType() == Node.ELEMENT_NODE) {
                Element filterMappingElem = (Element) filterMappingNode;
                
                String filterName = filterMappingElem.getElementsByTagName("filter-name").item(0).getTextContent();
                if(!filterContextMap.containsKey(filterName)) {
                	continue;
                }
                
                String urlPattern = filterMappingElem.getElementsByTagName("url-pattern").item(0).getTextContent();
                FilterContext filterContext = filterContextMap.get(filterName);
                filterContext.setUrlPattern(urlPattern);
                
                String servletName = filterMappingElem.getElementsByTagName("servlet-name").item(0).getTextContent();
                ServletContext servletContext = servletContextMap.get(servletName);
                if(servletContext != null) {
                	servletContext.add(filterName);
                }
        	}
        }
		
		return new WebXml(displayName, servletContextMap, filterContextMap);
	}
}
