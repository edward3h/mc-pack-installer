package org.ethelred.mc.pack_installer

import groovy.transform.EqualsAndHashCode
import net.java.truevfs.access.TPath
import org.ethelred.mc.pack.InvalidPackException
import org.ethelred.mc.pack.Manifest
import org.ethelred.mc.pack.Pack
import org.ethelred.mc.pack.PackId
import org.ethelred.mc.pack.Version

import java.nio.file.Files
import java.nio.file.Path

/**
 * TODO
 *
 * @author eharman* @since 2020-10-16
 */
@EqualsAndHashCode()
class Location {
    TPath path

    def setPath(String v) {
        this.path = new TPath(v)
    }

    void findPacks(consumer, skipPatterns = Collections.emptySet(), TPath from = path) {
        try {
            //noinspection GroovyFallthrough
            switch (from.fileName.toString()) {
                case { fn -> skipPatterns.any { p -> fn.matches(p) }} :
                    log.info "Skipping $from"
                    break
                case Manifest.NAME:
                    consumer << new LocationPack(pack: new Pack(from.parent), location: this)
                    break
                case "cache":
                case "plugins":
                    if (from.parent?.fileName?.toString() == "bridge") {
                        break // don't recurse into bridge embeds
                    }
                case { Files.isDirectory(from) }:
                    Files.list(from).withCloseable { stream ->
                        stream.toList().each { findPacks(consumer, skipPatterns, it) }
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

    boolean isDevelopment() {
        false
    }


    @Override
    public String toString() {
        return """\
${getClass().simpleName}($path)
"""
    }
}

class LocationPack extends PackId {
    Location location
    @Delegate
    Pack pack

    @Override
    Object getUuid() {
        return pack.uuid
    }

    @Override
    Version getVersion() {
        return pack.version
    }
}