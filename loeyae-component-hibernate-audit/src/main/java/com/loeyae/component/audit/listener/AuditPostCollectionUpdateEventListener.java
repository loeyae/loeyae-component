package com.loeyae.component.audit.listener;

import com.loeyae.component.audit.boot.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PostCollectionUpdateEvent;
import org.hibernate.event.spi.PostCollectionUpdateEventListener;

/**
 * AuditPostCollectionUpdateEventListener
 *
 * @author ZhangYi<loeyae @ gmail.com>
 * @version 1.0
 * @date 2023/4/20
 */
@Slf4j
public class AuditPostCollectionUpdateEventListener extends BaseAuditEventListener implements PostCollectionUpdateEventListener {
    public AuditPostCollectionUpdateEventListener(AuditService auditService) {
        super(auditService);
    }

    @Override
    public void onPostUpdateCollection(PostCollectionUpdateEvent event) {

        log.debug("On Post Update Collection【{}】", event);
    }
}
