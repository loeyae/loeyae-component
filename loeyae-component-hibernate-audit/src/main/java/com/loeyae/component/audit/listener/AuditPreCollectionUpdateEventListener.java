package com.loeyae.component.audit.listener;

import com.loeyae.component.audit.boot.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PreCollectionUpdateEvent;
import org.hibernate.event.spi.PreCollectionUpdateEventListener;

/**
 * AuditPreCollectionUpdateEventListener
 *
 * @author ZhangYi
 */
@Slf4j
public class AuditPreCollectionUpdateEventListener extends BaseAuditEventListener implements PreCollectionUpdateEventListener {
    private static final long serialVersionUID = 4144365916049655768L;

    public AuditPreCollectionUpdateEventListener(AuditService auditService) {
        super(auditService);
    }

    @Override
    public void onPreUpdateCollection(PreCollectionUpdateEvent event) {

        log.debug("On Pre Update Collection【{}】", event);
    }
}
