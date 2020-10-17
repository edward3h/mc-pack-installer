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
class Source extends Location {
    boolean development

    static List<Source> findCandidates(def sources) {
        sources.findAll { Files.isDirectory(it.path) }
    }

    boolean isDevelopment() {
        development
    }
}
