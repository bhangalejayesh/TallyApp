package com.example.RestApi.utility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.softage.commons.util.Utility;

public class XMLFile implements ErrorListener {
	private static final Logger logger = LogManager.getLogger();
	
	private int level = 0; 
	private String xslFileName; 
	private String encoding;
	private Element[] nodes; 
	private Document doc; 
	
	private final DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
	private DocumentBuilder docBuilder;
	
	public XMLFile() {this(null);}
	public XMLFile(InputStream is) {
		try {
	        docBuilder = dbfac.newDocumentBuilder();
	        
	        if (is!=null)
	        	doc = docBuilder.parse(new InputSource(is));
	        
	        else {
	        	doc = docBuilder.newDocument();
	        	//doc.setTextContent("<!DOCTYPE xml>");
	        }
	        
	        nodes = new Element[10];
	        level = -1; 
        }
		
		catch(Exception e){
			logger.error("Error initializing XMLFile" + (is==null ? "" : " from stream"), e);
		}
 	}
	
	public Document getDocument() {
		return doc;
	}
	
	public boolean writeXMLFile(String path) {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING,(encoding != null ? encoding : "UTF-8"));
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
//			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "xml");
			
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path),
				encoding != null? Charset.forName(encoding): Charset.forName("UTF-8"));
			Result result = new StreamResult(writer);
			Source source = new DOMSource(doc);
			transformer.transform(source, result);
			writer.close();
			return true;
		}
		
		catch (Exception e)
		{
			logger.error("Error writing XMLFile", e);
			return false;
		}
	}
	
	/**
	 * Note: this should NOT be used to write files, as encoding is not considered.
	 * Use writeXMLFile() instead.
	 */
	public String toString() {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING,(encoding != null ? encoding : "UTF-8"));
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			
			StringWriter writer = new StringWriter();
			Result result = new StreamResult(writer);
			Source source = new DOMSource(doc);
			transformer.transform(source, result);
			writer.close();
			return writer.toString();
		}
		
		catch (Exception e) {
			logger.error("Error stringifying XMLFile", e);
			return null;
		}
	}
	
	public void writeHeader(String xslFileName, Locale locale) {
//		if(logger.isTraceEnabled()) logger.trace("Writing XSL Header: " + xslFileName);
//		this.xslFileName = Utility.getXslRoot(locale) + xslFileName + ".xsl";
	}
	
	public void writeXMLOpen(String tagName) {
		writeXMLOpen(tagName, null);
	}
	
	public void writeXMLOpen(String tagName, String textContent) {
		if(logger.isTraceEnabled()) logger.trace("writeXMLOpen: " + tagName);
		
		appendChild(tagName);
		
		if (textContent!=null)
			nodes[level].setTextContent(textContent);
	}
	
	private void appendChild(String tagName) {
		nodes[++level] = doc.createElement(tagName);
		
		if(level == 0)
			doc.appendChild(nodes[level]);
			
		else
			nodes[level-1].appendChild(nodes[level]);
	}

	public void writeXMLClose(String tagName) {
		if(logger.isTraceEnabled()) logger.trace("writeXMLClose: " + tagName);
		level--;
	}
	
	public void writeComment(String comment) {
		if(comment == null) return;
		
		if (comment.trim().length() == 0)
			comment = "&#160;";
		
		else {
			comment = Utility.replace(comment, "\r\n", "<br/>", true);
			comment = Utility.replace(comment, "\n", "<br/>", true);
			comment = Utility.replace(comment, "\r", "<br/>", true);
		}
		
		Comment text = doc.createComment(comment);
		nodes[level].appendChild(text);
	}
	
	public void writeNewLine() {
//		Text text = doc.createTextNode("\n");
//		nodes[level].appendChild(text);
	}
	
	public void writeXML (String tagName, String tagValue) {
		writeXML(tagName, tagValue, true);
	}
	
	public void appendXML(String tagName, String valueString) throws SAXException, IOException {
		Element node = doc.createElement(tagName);
		
		Document valueDoc = docBuilder.parse(
		    new InputSource(new StringReader(valueString.replace("<p><br></p>", "<br/>")
		    		.replaceAll("&nbsp;", " "))));
		
		Node valueElement = doc.importNode(valueDoc.getDocumentElement(), true);
		node.appendChild(valueElement);
		
//		appendChild(tagName);
		nodes[level].appendChild(node);
		
//		writeXMLClose(tagName);
	}
	
	public void writeXML(String tagName, String tagValue, boolean forFOP) { 
		if(logger.isTraceEnabled()) logger.trace("writeXML: tagName=" + tagName + ", tagValue=" + (tagValue != null ? tagValue : "null"));
		
		if (tagValue == null) {
			if (!forFOP) return;
			tagValue="";
		}
		
		//tagValue = Utility.escapeStringForHtml(tagValue);
		tagValue = Utility.replace(tagValue, "\r\n", "<br/>", true);
		tagValue = Utility.replace(tagValue, "\n", "<br/>", true);
		tagValue = Utility.replace(tagValue, "\r", "<br/>", true);
		
		// replace word char's
		tagValue= Utility.replaceCharactersFromWord(tagValue);
		
		if (tagValue.length() == 0 && !forFOP)
			tagValue = "&#160;";
		
		writeXMLOpen(tagName);
		
		Text text = doc.createTextNode(tagValue);
		nodes[level].appendChild(text);
 		 
		writeXMLClose(tagName);
	}
	
	public void writeXML(String tagName, boolean value) {
		writeXML(tagName, (value ? "1" : "0"));
	}
	
	public void writeXMLAttr(String attrName, String attrValue) {
		nodes[level].setAttribute(attrName, attrValue);
	}
	
	private static HashMap<String, Transformer> xslTransforms = new HashMap<String, Transformer>();  
	
	private static Transformer getXSLTransform(String xslFileName) throws Exception {
		if(logger.isTraceEnabled()) logger.trace("Getting XSLTransform for " + xslFileName);
	
		if(xslTransforms.containsKey(xslFileName)) {
			if(logger.isTraceEnabled()) logger.trace("Getting cached transform for " + xslFileName);
			return xslTransforms.get(xslFileName);
		}
		
		if(logger.isTraceEnabled()) logger.trace("Creating new transform");
        //set up a transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer(new StreamSource(xslFileName));
        
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        
        //Cache it
        xslTransforms.put(xslFileName, trans);
        return trans;
	}
	
		 
	public String transformXMLandXSL() {	
		try{
			if(logger.isTraceEnabled()) logger.trace("Transforming doc");
			if(logger.isTraceEnabled()) logger.trace("Doc=" + this.toString());
			
	        Transformer trans = getXSLTransform(xslFileName);
	        
	        if(logger.isTraceEnabled()) logger.trace("Transformer trans=" + trans);
	        
			//create string from xml tree
	        StringWriter sw = new StringWriter();
	        StreamResult result = new StreamResult(sw);
	        
	        if(logger.isTraceEnabled()) logger.trace("StreamResult result=" + result);
	        
	        DOMSource source = new DOMSource(doc);
	        
	        if(logger.isTraceEnabled()) logger.trace("DOMSource source=" + source);
	        
	        trans.setErrorListener(this);
	        trans.transform(source, result);
	        String xmlString = sw.toString();
	        
	        if(logger.isTraceEnabled()) logger.trace("Output=\r\n" + xmlString);
	        
	        return xmlString;
	        
		}catch(Exception e){
			logger.error("Error during XSL transform", e);
			return ""; 
		}
	}

	@Override
	public void error(TransformerException arg0) throws TransformerException
	{
		throw arg0;
	}

	@Override
	public void fatalError(TransformerException arg0)
			throws TransformerException {
		throw arg0;
	}

	@Override
	public void warning(TransformerException arg0) throws TransformerException
	{
		throw arg0;
		
	}
	
	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}
	
	public String getEncoding()
	{
		return this.encoding;
	}
}