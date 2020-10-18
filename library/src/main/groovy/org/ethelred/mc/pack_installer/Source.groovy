/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

import groovy.transform.ToString

import java.nio.file.Files
/**
 * Source - places to search for packs
 */
@ToString
class Source extends Location {
    boolean development

    static Set<Source> findCandidates(def sources) {
        sources.findAll { Files.isDirectory(it.path) }
    }

    boolean isDevelopment() {
        development
    }
}
