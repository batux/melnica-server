package melnica.server.configuration.xml.assembler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import melnica.server.application.configuration.BosphorusConfiguration;
import melnica.server.application.configuration.HostConfiguration;
import melnica.server.application.configuration.ServerConfiguration;
import melnica.server.application.configuration.ServiceConfiguration;

public class ServerConfigurationAssembler {

	public ServerConfiguration process(Document document) {
		
		Map<String, ServiceConfiguration> services = new HashMap<String, ServiceConfiguration>();
		
		String shutdownCommand = document.getDocumentElement().getAttribute("shutdown");
        NodeList serviceNodeList = document.getElementsByTagName("Service");
        int length = serviceNodeList.getLength();
        
        for(int i = 0; i < length; i++) {
        	Node serviceNode = serviceNodeList.item(i);
        	if (serviceNode.getNodeType() == Node.ELEMENT_NODE) {
                Element serviceElem = (Element) serviceNode;
                ServiceConfiguration service = createService(serviceElem);
                services.put(service.getName(), service);
        	}
        }

        return new ServerConfiguration(services, shutdownCommand);
	}
	
	private ServiceConfiguration createService(Element element) {
		
		String name = element.getAttribute("name");
        NodeList connectorNodeList = element.getElementsByTagName("Bosphorus");
        NodeList hostNodeList = element.getElementsByTagName("Host");
        
        return new ServiceConfiguration(name,
        		createHostConfList(hostNodeList),
        		createConnectorConfList(connectorNodeList), 
        		createActiveWebPlatformConf(element));
	}
	
	private List<BosphorusConfiguration> createConnectorConfList(NodeList connectorNodeList) {
		
		int length = connectorNodeList.getLength();
        List<BosphorusConfiguration> connectorConf = new ArrayList<BosphorusConfiguration>();
		
        for(int i = 0; i < length; i++) {
        	Node connectorNode = connectorNodeList.item(i);
        	if (connectorNode.getNodeType() == Node.ELEMENT_NODE) {
        		Element connectorElement = (Element) connectorNode;
                connectorConf.add(createConnectorConf(connectorElement));
        	}
        }
        return connectorConf;
	}
	
	private BosphorusConfiguration createConnectorConf(Element element) {
		
		String portAsText = element.getAttribute("port");
		String protocolAsText = element.getAttribute("protocol");
		String timeoutAsText = element.getAttribute("timeout");
		return new BosphorusConfiguration(Integer.parseInt(portAsText), protocolAsText, Integer.parseInt(timeoutAsText));
	}
	
	
	private List<HostConfiguration> createHostConfList(NodeList hostNodeList) {
		
        int length = hostNodeList.getLength();
        List<HostConfiguration> hostConf = new ArrayList<HostConfiguration>();
		
        for(int i = 0; i < length; i++) {
        	Node hostNode = hostNodeList.item(i);
        	if (hostNode.getNodeType() == Node.ELEMENT_NODE) {
                Element hostElement = (Element) hostNode;
                hostConf.add(createHostConf(hostElement));
        	}
        }
        return hostConf;
	}
	
	private HostConfiguration createHostConf(Element element) {
		
		String domainAsText = element.getAttribute("domain");
		String name = element.getAttribute("name");
		String unpackWarsAsText = element.getAttribute("unpackWars");
		String appRootFolderNameAsText = element.getAttribute("appRootFolderName");
		return new HostConfiguration(name, domainAsText, appRootFolderNameAsText, Boolean.parseBoolean(unpackWarsAsText));
	}
	
	private List<String> createActiveWebPlatformConf(Element element) {
		
		String[] activeWebPlatforms = new String[1];
		String activeWebPlatformsAsText = element.getAttribute("activeWebPlatforms");
		activeWebPlatforms[0] = activeWebPlatformsAsText;
        if(activeWebPlatformsAsText.contains(",")) {
        	activeWebPlatforms = activeWebPlatformsAsText.split(",");
        }
        return Arrays.asList(activeWebPlatforms);
	}
}
