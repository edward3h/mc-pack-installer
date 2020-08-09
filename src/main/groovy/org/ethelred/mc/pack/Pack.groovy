/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack

import com.google.common.annotations.VisibleForTesting

import groovy.transform.ToString

import java.nio.file.Path

/**
 * a pack
 */
@ToString
class Pack extends PackId {
    Set<Path> path
    @Delegate Manifest manifest

    @VisibleForTesting
    Pack() {
        path = []
    }

    Pack(Path dir) {
        path = new HashSet().tap { add dir }
        manifest = new Manifest(dir.resolve(Manifest.NAME))
        log.info "new Pack $this"
    }

    def leftShift(Pack other) {
        tap {
            if (it == other) {
                path << other.path
            }
        }
    }

    @Override
    String toString() {
        return """\
Pack{
    path=${path.first()}, 
    name=$name, version=$version, uuid=$uuid
}"""
    }

    String toStringShort() {
        "${name.padRight(24)} $version ${type.shortCode} $uuid"
    }
}
