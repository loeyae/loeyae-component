package com.loeyae.component.audit.configuration.metadata;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * EntityXmlMappingData
 *
 * @author ZhangYi
 */
public class EntityXmlMappingData {
    private Document mainXmlMapping;
    private List<Document> additionalXmlMappings;
    /**
     * The xml element that maps the class. The root can be one of the folowing elements:
     * class, subclass, union-subclass, joined-subclass
     */
    private Element classMapping;

    public EntityXmlMappingData() {
        mainXmlMapping = DocumentHelper.createDocument();
        additionalXmlMappings = new ArrayList<>();
    }

    public Document getMainXmlMapping() {
        return mainXmlMapping;
    }

    public List<Document> getAdditionalXmlMappings() {
        return additionalXmlMappings;
    }

    public Document newAdditionalMapping() {
        Document additionalMapping = DocumentHelper.createDocument();
        additionalXmlMappings.add( additionalMapping );

        return additionalMapping;
    }

    public Element getClassMapping() {
        return classMapping;
    }

    public void setClassMapping(Element classMapping) {
        this.classMapping = classMapping;
    }
}
