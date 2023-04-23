package com.loeyae.component.audit.boot;

import com.loeyae.component.audit.configuration.MappingCollector;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.hibernate.HibernateException;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.SourceType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmHibernateMapping;
import org.hibernate.boot.jaxb.internal.MappingBinder;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.spi.AdditionalJaxbMappingProducer;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.service.ServiceRegistry;
import org.jboss.jandex.IndexView;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * AuditAdditionalJaxbMappingProducer
 *
 * @author ZhangYi<loeyae @ gmail.com>
 * @version 1.0
 * @date 2023/4/20
 */
@Slf4j
public class AuditAdditionalJaxbMappingProducer implements AdditionalJaxbMappingProducer {
    @Override
    public Collection<MappingDocument> produceAdditionalMappings(MetadataImplementor metadata, IndexView jandexIndex, MappingBinder mappingBinder, MetadataBuildingContext buildingContext) {
        final ServiceRegistry serviceRegistry = metadata.getMetadataBuildingOptions().getServiceRegistry();
        final AuditService auditService = serviceRegistry.getService( AuditService.class );

        if ( !auditService.isEnabled() ) {
            return Collections.emptyList();
        }

        final ArrayList<MappingDocument> additionalMappingDocuments = new ArrayList<>();

        final Origin origin = new Origin( SourceType.OTHER, "audit" );

        final MappingCollector mappingCollector = document -> {
            dump( document );
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                final Writer w = new BufferedWriter( new OutputStreamWriter( baos, "UTF-8" ) );
                final XMLWriter xw = new XMLWriter( w, new OutputFormat( " ", true ) );
                xw.write( document );
                w.flush();
            }
            catch (IOException e) {
                throw new HibernateException( "Unable to bind Envers-generated XML", e );
            }

            ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
            BufferedInputStream bis = new BufferedInputStream( bais );
            final Binding jaxbBinding = mappingBinder.bind( bis, origin );

            final JaxbHbmHibernateMapping jaxbRoot = (JaxbHbmHibernateMapping) jaxbBinding.getRoot();
            additionalMappingDocuments.add( new MappingDocument( jaxbRoot, origin, buildingContext ) );
        };

        auditService.initialize( metadata, mappingCollector );

        return additionalMappingDocuments;
    }

    private static void dump(Document document) {
        if ( !log.isTraceEnabled() ) {
            return;
        }

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Writer w = new PrintWriter( baos );

        try {
            final XMLWriter xw = new XMLWriter( w, new OutputFormat( " ", true ) );
            xw.write( document );
            w.flush();
        }
        catch (IOException e1) {
            throw new RuntimeException( "Error dumping enhanced class", e1 );
        }

        log.trace( "Envers-generate entity mapping -----------------------------\n{}", baos.toString() );
        log.trace( "------------------------------------------------------------" );
    }
}
