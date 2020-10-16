/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack

import groovy.transform.ToString

/**
 * define identity for pack so we can map to dependencies more easily
 */
@ToString
class PackId {
    def uuid
    Version version

    @Override
    int hashCode() {
        Objects.hash(getUuid(), getVersion()) // need to use getters explicitly to allow override
    }

    @Override
    boolean equals(Object obj) {
        if (is(obj)) return true
        // allow subclasses to match
        if (!obj instanceof PackId) return false
        PackId p = (PackId) obj
        getUuid() == p.getUuid() && getVersion() == p.getVersion() // need to use getters explicitly to allow override
    }
}
