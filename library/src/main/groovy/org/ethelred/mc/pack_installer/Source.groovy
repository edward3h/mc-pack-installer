/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

import org.ethelred.mc.pack.InvalidPackException
import org.ethelred.mc.pack.Manifest
import org.ethelred.mc.pack.Pack

import groovy.transform.ToString

import java.nio.file.AccessDeniedException
import java.nio.file.Files
import java.nio.file.Path

import net.java.truevfs.access.TPath

/**
 * Source - places to search for packs
 */
@ToString
class Source {
    Path path

    static List<Source> findCandidates(def sources, def targets) {
        sources.findAll { Files.isDirectory(it.path) } + targets.collect { new Source(path: it.path) }
    }

    void findPacks(consumer, Path from = path) {
        try {
            //noinspection GroovyFallthrough
            switch (from.fileName.toString()) {
                case Manifest.NAME:
                    consumer << new Pack(from.parent)
                    break
                case "cache":
                case "plugins":
                    if (from.parent?.fileName?.toString() == "bridge") {
                        return // don't recurse into bridge embeds
                    }
                case { Files.isDirectory(from) }:
                    Files.list(from).withCloseable { stream ->
                        stream.toList().each { findPacks(consumer, it) }
                    }
                    break
                default:
                    return
            }
        } catch(InvalidPackException ignored) {
            //ignore
        } catch(e) {
            log.error("Error reading ${from}", e)
        }
    }
}
