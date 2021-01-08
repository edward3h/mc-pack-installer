/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack

import com.google.common.annotations.VisibleForTesting
import groovy.transform.Memoized
import groovy.transform.ToString
import net.java.truevfs.access.TFile
import net.java.truevfs.access.TPath
import net.java.truevfs.access.TVFS

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
/**
 * a pack
 */
@ToString
class Pack extends PackId {
    TPath path
    @Delegate Manifest manifest
    Translations translations

    @VisibleForTesting
    Pack() {
    }

    Pack(TPath dir) {
        path = dir
        manifest = new Manifest(dir.resolve(Manifest.NAME), new Translations(dir))
        log.debug "new Pack $this"
    }

    @Override
    String toString() {
        return """\
Pack{
    path=${(!path || path.empty) ? "EMPTY" : path.first()},
    name=${manifest == null ? "null": name}, version=${manifest == null ? "null": version}, uuid=${manifest == null ? "null" : uuid}
}"""
    }

    String toStringShort() {
        "${name.ansi.padRight(24)} $version ${type.shortCode} $uuid"
    }

    @Memoized
    String getZipName() {
        "${name.strip} ${type.shortCode} $version".replaceAll(/\W+/, ' ').trim().replaceAll(/\W+/, '_')
    }

    Path getIconPath() {
        def p = path.resolve("pack_icon.png")
        if (Files.isRegularFile(p))
            p
        else
            null
    }

    void writeUnder(TPath targetPath) {
        try {
            if (!Files.exists(targetPath)) Files.createDirectories(targetPath)

            _tryDelete(targetPath.resolve(zipName + ".zip"))
            _tryDelete(targetPath.resolve(zipName + ".mcpack"))

            TFile targetRoot = targetPath.resolve(zipName + ".mcpack").toFile()
            TFile sourceRoot = path.toFile()
            sourceRoot.cp_r(targetRoot)
//            sourceRoot.traverse { sourcePath ->
//                Path relative = sourceRoot.relativize(sourcePath)
//                Path dest = targetRoot.resolve(relative.toString())
//                try {
//                    if (Files.exists(dest)) return
//                } catch(e) {
//                    log.error("Exception checking file existence?", e)
//                    return
//                }
//                try {
//                    if (Files.isDirectory(sourcePath)) {
//                        Files.createDirectories(dest)
//                    } else {
//                        Files.copy(sourcePath, dest, StandardCopyOption.REPLACE_EXISTING)
//                    }
//                } catch(e) {
//                    log.error "Failed to copy ${sourcePath}", e
//                }
//                log.debug "copy $sourcePath to $dest"
//            }
            TVFS.sync(targetRoot)
            log.info "Added ${toStringShort()} under $targetPath"
        } catch(e) {
            log.error($/Failed to write pack ${toStringShort()}:
source: $path
target: $targetPath
message: ${e.message}/$)
//            System.exit 1
        }
    }

    private void _tryDelete(TPath tPath)  {
        try {
            if (Files.exists(tPath)) {
                TFile tFile = tPath.toFile()
                tFile.rm_r()
                TVFS.sync(tFile)
            }
        } catch(e) {
            log.error("Failed to delete path $tPath", e)
            System.exit 1
        }
    }
}
