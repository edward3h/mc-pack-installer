package org.ethelred.mc.pack

import java.nio.file.Files
import java.nio.file.Path

/**
 * TODO
 *
 * @author edward3h
 * @since 2020-10-18
 */
class Translations {
    def mapping = [:]

    Translations(Path packRoot) {
        def textDir = packRoot.resolve("texts")
        if (Files.exists(textDir)) {
            textDir.eachFileMatch(~/en_.*\.lang/) {path ->
                path.withReader {r ->
                    Properties p = new Properties()
                    p.load(r)
                    mapping.putAll(p)
                }
            }
        }
    }

    String translate(String text) {
        mapping.get(text, text)
    }
}
