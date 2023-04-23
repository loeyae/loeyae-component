package com.loeyae.component.audit.configuration;

import org.dom4j.Document;
import org.dom4j.DocumentException;

/**
 * MappingCollector
 *
 * @author ZhangYi<loeyae @ gmail.com>
 * @version 1.0
 * @date 2023/4/20
 */
public interface MappingCollector {
 void addDocument(Document document) throws DocumentException;
}
