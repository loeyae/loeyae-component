package com.loeyae.component.audit.configuration;

import org.dom4j.Document;
import org.dom4j.DocumentException;

/**
 * MappingCollector
 *
 * @author ZhangYi
 */
public interface MappingCollector {
 void addDocument(Document document) throws DocumentException;
}
