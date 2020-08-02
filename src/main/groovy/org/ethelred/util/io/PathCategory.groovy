package org.ethelred.util.io

import groovy.util.logging.Log

import java.nio.file.FileSystems
import java.nio.file.Path

/**
 * extensions for Path
 */
@Category(Path)
@Log
class PathCategory {
    Path openZip() {
        def uri = "jar:file:${this}".replaceAll(/ /, "%20").toURI()
        def r = FileSystems.newFileSystem(uri, [:]).getPath('')
        log.info("opened zip ${this} -> ${r.getFileSystem()}")
        r
    }
}
