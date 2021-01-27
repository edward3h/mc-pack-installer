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


}
