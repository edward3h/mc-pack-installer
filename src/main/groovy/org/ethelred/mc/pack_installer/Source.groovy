/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

import org.ethelred.mc.pack.Manifest
import org.ethelred.mc.pack.Pack

import groovy.transform.ToString
import groovy.util.logging.Log

import java.nio.file.Files
import java.nio.file.Path

/**
 * Source - places to search for packs
 */
@ToString
@Log
class Source {
    static def DEFAULT_SEARCH_ROOTS = [
        Path.of(System.getProperty('user.home'), '/Downloads'),
        Path.of(System.getProperty('user.home'), '/src/minecraft')
    ]

    Path path

    static List<Source> findCandidates(def targets) {
        DEFAULT_SEARCH_ROOTS.findAll { Files.isDirectory(it) }
        .collect { new Source(path:it) }
        + targets*.path
    }

    List<Pack> findPacks(Path from = path) {
        try {
            switch (from.fileName.toString()) {
                case "..":
                    return []
                case Manifest.NAME:
                    return [new Pack(from.parent)]
                case { Files.isDirectory(from) }:
                    return Files.list(from).withCloseable { stream ->
                        stream.toList().collectMany { findPacks(it) }
                    }
                case ~/.*\.zip$/:
                case ~/.*\.mcpack$/:
                case ~/.*\.mcaddon$/:
                    return use(PathCategory) {
                        findPacks(from.openZip())
                    }
                default:
                    return []
            }
        } catch(def ignored) {
            return []
        }
    }
}
