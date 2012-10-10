///*
// * Copyright (C) 2012 Brockmann Consult GmbH (info@brockmann-consult.de)
// *
// * This program is free software; you can redistribute it and/or modify it
// * under the terms of the GNU General Public License as published by the Free
// * Software Foundation; either version 3 of the License, or (at your option)
// * any later version.
// * This program is distributed in the hope that it will be useful, but WITHOUT
// * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
// * more details.
// *
// * You should have received a copy of the GNU General Public License along
// * with this program; if not, see http://www.gnu.org/licenses/
// */
//
//package com.bc.ceres.metadata;
//
//import com.bc.ceres.resource.ReaderResource;
//import net.sf.saxon.Configuration;
//import net.sf.saxon.om.DocumentInfo;
//import net.sf.saxon.query.DynamicQueryContext;
//import net.sf.saxon.query.StaticQueryContext;
//import net.sf.saxon.query.XQueryExpression;
//import net.sf.saxon.trans.XPathException;
//import org.w3c.dom.Element;
//
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//import javax.xml.transform.stream.StreamSource;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.util.Properties;
//
///**
// * Handles XPath expressions in velocity template files. The expressions parse an input xml file.
// *
// * @author Bettina
// * @since Ceres 0.13.2
// */
//public class XPathHandler {
//
//    /**
//     * Run a XPath query using Saxon.
//     * Call with $xpath.run("XPath expression", $source-XML) in the velocity template.
//     * e.g. $xpath.run("//creationDate", $metadata)
//     *
//     * @param xpath    The XPath expression
//     * @param document Either an instance of {@link ReaderResource}, {@link Element} or a raw xml {@link String}.
//     * @return The demanded information from the XML document.
//     */
//    public String run(String xpath, Object document) {
//        try {
//            Configuration config = new Configuration();
//            StaticQueryContext staticContext = new StaticQueryContext(config);
//            DynamicQueryContext dynamicContext = new DynamicQueryContext(config);
//            String query = "xs:string(" + xpath + ")";
//            XQueryExpression xQueryExpression = staticContext.compileQuery(query);
//
//            String docString;
//            if (document instanceof ReaderResource) {
//                docString = ((ReaderResource) document).getContent();
//            } else if (document instanceof String) {
//                docString = (String) document;
//            } else if (document instanceof Element) { //used?
//                DOMSource domSource = new DOMSource((Element) document);
//                StringWriter writer = new StringWriter();
//                StreamResult result = new StreamResult(writer);
//                TransformerFactory tf = TransformerFactory.newInstance();
//                Transformer transformer = tf.newTransformer();
//                Properties properties = new Properties();
//                properties.setProperty(OutputKeys.METHOD, "xml");
//                properties.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//                transformer.setOutputProperties(properties);
//                transformer.transform(domSource, result);
//                docString = writer.toString();
//            } else {
//                return null;
//            }
//            prepareContextNodeInDynamicContext(docString, dynamicContext, staticContext);
//
//            Properties properties = new Properties();
//            properties.setProperty(OutputKeys.METHOD, "text");
//            properties.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//            properties.setProperty(OutputKeys.STANDALONE, "yes");
//            StringWriter stringWriter = new StringWriter();
//            xQueryExpression.run(dynamicContext, new StreamResult(stringWriter), properties);
//            return stringWriter.getBuffer().toString();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void prepareContextNodeInDynamicContext(String docString, DynamicQueryContext dynamicContext,
//                                                    StaticQueryContext staticContext) throws XPathException {
//        StreamSource streamSource = new StreamSource(new StringReader(docString));
//        DocumentInfo documentInfo = staticContext.buildDocument(streamSource).getDocumentRoot();
//        dynamicContext.setContextItem(documentInfo);
//    }
//}