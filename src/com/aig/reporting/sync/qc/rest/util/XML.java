package com.aig.reporting.sync.qc.rest.util;

import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;

/**
 * QC REST API's xml response parser
 *
 * @author 389747
 * @since JUN-2016 | Spritz 5.x
 * @version 0.1.1
 */
public class XML {

    /**
     * query to get id of a field
     */
    public static final String QUERY_ID = "/Field[@Name='id']/Value/text()";

    public static String getID(String doc, BY by, String key) {
        return getID(doc, String.format(by.xpath(), key) + QUERY_ID);
    }

    public static String getID(String doc, String query) {
        try {
            return XPathFactory.newInstance().newXPath().compile(query)
                    .evaluate(getDoc(doc), XPathConstants.STRING).toString();
        } catch (Exception ex) {
            Logger.getLogger(XML.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return null;
    }

    public static String getID(String doc, BY by) {
        String id = getID(doc, by.xpath() + QUERY_ID);
        System.out.println(id);
        return id;
    }

    public static String getID(String doc, BY by, String parentId, String key) {
        return getID(doc, String.format(by.xpath(), parentId, key) + QUERY_ID);
    }

    public static Document getDoc(String doc) throws Exception {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new ByteArrayInputStream(doc.getBytes("UTF-8")));
    }

    /**
     * XPATH library
     */
    public enum BY {
        ROOT("/"),
        /**
         * query a field from fields with field id
         */
        NAME(
                "(//Fields[Field[@Name='name']/Value[.='%s']])[1]"
        ),
        /**
         * query a field from fields with test id
         */
        TEST_ID(
                "(//Fields[Field[@Name='test-id']/Value[.='%s']])[1]"
        ),
        /**
         * query a field from fields with fields parent id and name
         */
        PARENT_ID_AND_NAME(
                "(//Fields[Field[contains(@Name,'parent')]/Value[.='%s']][Field[@Name='name']/Value[.='%s']])[1]");

        private final String xpath;

        private BY(String xpath) {
            this.xpath = xpath;
        }

        public String xpath() {
            return xpath;
        }

    }

}
