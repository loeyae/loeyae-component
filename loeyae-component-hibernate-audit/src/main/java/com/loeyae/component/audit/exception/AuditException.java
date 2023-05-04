package com.loeyae.component.audit.exception;

import org.hibernate.HibernateException;

/**
 * AuditException
 *
 * @author ZhangYi
 */
public class AuditException extends HibernateException {

    public AuditException(String message) {
        super(message);
    }

    public AuditException(Throwable cause) {
        super(cause);
    }

    public AuditException(String message, Throwable cause) {
        super(message, cause);
    }
}
