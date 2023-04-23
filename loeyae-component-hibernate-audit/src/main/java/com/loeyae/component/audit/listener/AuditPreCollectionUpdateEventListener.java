package com.loeyae.component.audit.listener;

import com.loeyae.component.audit.boot.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PreCollectionUpdateEvent;
import org.hibernate.event.spi.PreCollectionUpdateEventListener;

/**
 * AuditPreCollectionUpdateEventListener
 *
 * @author ZhangYi<loeyae @ gmail.com>
 * @version 1.0
 * @date 2023/4/20
 */
@Slf4j
public class AuditPreCollectionUpdateEventListener extends BaseAuditEventListener implements PreCollectionUpdateEventListener {
    public AuditPreCollectionUpdateEventListener(AuditService auditService) {
        super(auditService);
    }

    @Override
    public void onPreUpdateCollection(PreCollectionUpdateEvent event) {

        log.debug("On Pre Update Collection【{}】", event);
    }
}
