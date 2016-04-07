package org.keycloak.example.jpa;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Table(name="DATABASE_LOCK")
@Entity
public class DBLockEntity {

    @Id
    @Column(name = "ID", length = 36)
    @Access(AccessType.PROPERTY) // we do this because relationships often fetch id, but not entity.  This avoids an extra SQL
    private String id;

    @Column(name = "LOCKED", length = 36)
    private String locked;

    @Column(name = "LOCKEDBY", length = 36)
    private String lockedBy;

    @Column(name = "LOCKGRANTED", length = 36)
    private String lockGranted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public String getLockGranted() {
        return lockGranted;
    }

    public void setLockGranted(String lockGranted) {
        this.lockGranted = lockGranted;
    }
}

