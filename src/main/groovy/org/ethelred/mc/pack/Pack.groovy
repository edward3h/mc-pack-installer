/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack

import com.google.common.annotations.VisibleForTesting

import groovy.transform.ToString

import java.nio.file.Files
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
        path = new HashSet().tap { it.add(dir) }
        manifest = new Manifest(dir.resolve(Manifest.NAME))
        log.info "new Pack $this"
    }

    def leftShift(Pack other) {
        tap {
            if (it == other) {
                path.addAll(other.path)
            }
            false
        }
    }

    @Override
    String toString() {
        return """\
Pack{
    path=${path.empty ? "EMPTY" : path.first()}, 
    name=$name, version=$version, uuid=$uuid
}"""
    }

    String toStringShort() {
        "${name.padRight(24)} $version ${type.shortCode} $uuid"
    }

    boolean isIn(Path targetPath) {
        def r = path.any {Path test -> test != targetPath && test.toString().startsWith(targetPath.toString()) }
        log.debug("isIn $r $targetPath <- $path")
        r
    }

    void writeUnder(Path targetPath) {
        try {
            def zipName = "$name ${type.shortCode} $version".replaceAll(/\W+/, '_') + '.mcpack'
            def targetRoot = targetPath.resolve(zipName)
            if (!Files.exists(targetRoot)) Files.createDirectories(targetRoot)
            def sourceRoot = path.first()
            sourceRoot.traverse { sourcePath ->
                Path relative = sourceRoot.relativize(sourcePath)
                Path dest = targetRoot.resolve(relative)
                if (Files.exists(dest)) return
                    if (Files.isDirectory(sourcePath)) {
                        Files.createDirectories(dest)
                    } else {
                        Files.copy(sourcePath, dest)
                    }
                log.debug "copy $sourcePath to $dest"
            }
        } catch(e) {
            log.error("Failed to write pack ${toStringShort()}", e)
        }
    }
}
