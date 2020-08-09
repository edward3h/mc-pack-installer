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
    static def DEFAULT_SEARCH_ROOTS = [
        new TPath(System.getProperty('user.home'), '/Downloads'),
        new TPath(System.getProperty('user.home'), '/src/minecraft')
    ]

    Path path

    static List<Source> findCandidates(def targets) {
        DEFAULT_SEARCH_ROOTS.findAll { Files.isDirectory(it) }
        .collect { new Source(path:it) } + targets.collect { new Source(path: it.path) }
    }

    void findPacks(consumer, Path from = path) {
        try {
            switch (from.fileName.toString()) {
                case Manifest.NAME:
                    consumer << new Pack(from.parent)
                    break
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
        } catch(AccessDeniedException e) {
            log.warning("Access denied ${from}")
        }
    }
}
