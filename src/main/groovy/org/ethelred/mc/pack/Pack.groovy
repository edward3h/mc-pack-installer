/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack

import com.google.common.annotations.VisibleForTesting

import groovy.transform.Memoized
import groovy.transform.ToString

import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

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

    @Memoized
    String getZipName() {
        "$name ${type.shortCode} $version".replaceAll(/\W+/, ' ').strip().replaceAll(/\W+/, '_')
    }

    Path getIconPath() {
        def p = path.first().resolve("pack_icon.png")
        if (Files.isRegularFile(p))
            p
        else
            null
    }

    void writeUnder(Path targetPath) {
        try {
            if (!Files.exists(targetPath)) Files.createDirectories(targetPath)
            if (Files.exists(targetPath.resolve(zipName + ".zip")) || Files.exists(targetPath.resolve(zipName + ".mcpack"))) return

                def targetRoot = targetPath.resolve(zipName + ".mcpack")
            def sourceRoot = path.first()
            sourceRoot.traverse { sourcePath ->
                Path relative = sourceRoot.relativize(sourcePath)
                Path dest = targetRoot.resolve(relative.toString())
                try {
                    if (Files.exists(dest)) return
                } catch(e) {
                    log.error("Exception checking file existence?", e)
                    return
                }
                try {
                    if (Files.isDirectory(sourcePath)) {
                        Files.createDirectories(dest)
                    } else {
                        Files.copy(sourcePath, dest, StandardCopyOption.REPLACE_EXISTING)
                    }
                } catch(e) {
                    log.error "Failed to copy ${sourcePath}", e
                }
                log.debug "copy $sourcePath to $dest"
            }
            //Files.move(targetPath.resolve(zipName + ".zip"), targetPath.resolve(zipName + ".mcpack"), StandardCopyOption.REPLACE_EXISTING)
        } catch(e) {
            log.error("Failed to write pack ${toStringShort()}", e)
        }
    }
}
