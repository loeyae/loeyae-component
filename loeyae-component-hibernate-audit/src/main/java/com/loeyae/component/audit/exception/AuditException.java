package com.loeyae.component.audit.exception;

import org.hibernate.HibernateException;

/**
 * AuditException
 *
 * @author ZhangYi<loeyae @ gmail.com>
 * @version 1.0
 * @date 2023/4/20
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
