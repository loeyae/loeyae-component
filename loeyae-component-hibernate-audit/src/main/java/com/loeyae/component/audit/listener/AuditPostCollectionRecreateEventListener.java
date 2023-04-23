package com.loeyae.component.audit.listener;

import com.loeyae.component.audit.boot.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PostCollectionRecreateEvent;
import org.hibernate.event.spi.PostCollectionRecreateEventListener;

/**
 * AuditPostCollectionRecreateEventListener
 *
 * @author ZhangYi<loeyae @ gmail.com>
 * @version 1.0
 * @date 2023/4/20
 */
@Slf4j
public class AuditPostCollectionRecreateEventListener extends BaseAuditEventListener implements PostCollectionRecreateEventListener {
    public AuditPostCollectionRecreateEventListener(AuditService auditService) {
        super(auditService);
    }

    @Override
    public void onPostRecreateCollection(PostCollectionRecreateEvent event) {

        log.debug("On Post Recreate Collection【{}】", event);

    }
}
