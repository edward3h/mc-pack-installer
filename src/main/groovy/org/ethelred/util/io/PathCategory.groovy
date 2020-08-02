package org.ethelred.util.io

import java.nio.file.FileSystems
import java.nio.file.Path

/**
 * extensions for Path
 */
@Category(Path)
class PathCategory {
    Path openZip() {
        def uri = "jar:file:${this}".replaceAll(/ /, "%20").toURI()
        FileSystems.newFileSystem(uri, [:]).getPath('')
    }
}
