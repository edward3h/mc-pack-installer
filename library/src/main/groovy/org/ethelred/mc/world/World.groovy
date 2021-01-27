package org.ethelred.mc.world

import groovy.transform.Canonical
import groovy.transform.Memoized
import net.java.truevfs.access.TPath
import org.ethelred.mc.text.MCText

import java.nio.file.Files
import java.nio.file.Path

import static org.ethelred.mc.text.MCText.fromString

/**
 * TODO
 *
 * @author eharman* @since 2021-01-25
 */
@Canonical(includes = ["name"])
class World {
    static String LEVELNAME = "levelname.txt"
    TPath path
    MCText name

    World(TPath dir) {
        path = dir
        name = fromString(dir.resolve(LEVELNAME).text.trim())
    }

    Path getIconPath() {
        def p = path.resolve("world_icon.jpeg")
        if (Files.isRegularFile(p))
            p
        else
            null
    }

    String toStringShort() {
        "${name.ansi.padRight(24)}"
    }

    @Memoized
    String getZipName() {
        "${name.strip}".replaceAll(/\W+/, ' ').trim().replaceAll(/\W+/, '_')
    }
}
