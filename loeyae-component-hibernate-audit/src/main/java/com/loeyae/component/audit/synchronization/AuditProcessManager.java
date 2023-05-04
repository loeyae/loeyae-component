package com.loeyae.component.audit.synchronization;

import org.hibernate.Transaction;
import org.hibernate.action.spi.AfterTransactionCompletionProcess;
import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.EventSource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AuditProcessManager
 *
 * @author ZhangYi
 */
public class AuditProcessManager {
    private final Map<Transaction, AuditProcess> auditProcesses;

    public AuditProcessManager() {
        auditProcesses = new ConcurrentHashMap<>();
    }

    public AuditProcess get(EventSource session) {
        final Transaction transaction = session.accessTransaction();

        AuditProcess auditProcess = auditProcesses.get( transaction );
        if ( auditProcess == null ) {
            auditProcess = new AuditProcess(session );
            auditProcesses.put( transaction, auditProcess );

            session.getActionQueue().registerProcess(
                    session1 -> {
                        final AuditProcess process = auditProcesses.get( transaction );
                        if ( process != null ) {
                            process.doBeforeTransactionCompletion(session1);
                        }
                    }
            );

            session.getActionQueue().registerProcess(
                    (success, session12) -> auditProcesses.remove( transaction )
            );
        }

        return auditProcess;
    }
}
