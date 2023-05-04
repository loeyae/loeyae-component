package com.loeyae.component.audit.listener;

import com.loeyae.component.audit.boot.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PreCollectionRemoveEvent;
import org.hibernate.event.spi.PreCollectionRemoveEventListener;

/**
 * AuditPreCollectionRemoveEventListener
 *
 * @author ZhangYi
 */
@Slf4j
public class AuditPreCollectionRemoveEventListener extends BaseAuditEventListener implements PreCollectionRemoveEventListener {
    public AuditPreCollectionRemoveEventListener(AuditService auditService) {
        super(auditService);
    }

    @Override
    public void onPreRemoveCollection(PreCollectionRemoveEvent event) {
        log.debug("On Post Remove Collection【{}】", event);
    }
}
