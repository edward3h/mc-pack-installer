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
 * @author edward3h
 * @since 2020-10-16
 */
@EqualsAndHashCode(includes = ["path"])
class Location {
    TPath path
    @Delegate
    PackMatcher matcher = new PackMatcher()

    def setPath(String v) {
        this.path = normalizePath(v)
    }

    def setPath(Path p) {
        this.path = normalizePath(p)
    }

    static TPath normalizePath(Path p) {
        new TPath(p).toRealPath()
    }

    static TPath normalizePath(String s) {
        new TPath(s).toRealPath()
    }

    void findPacks(consumer, skipPatterns = Collections.emptySet(), TPath from = path) {
        try {
            //noinspection GroovyFallthrough
            switch (from.fileName.toString()) {
                case { fn -> skipPatterns.any { p -> fn.matches(p) }} :
                    log.info "Skipping $from"
                    break
                case Manifest.NAME:
                    def p = new LocationPack(pack: new Pack(from.parent), location: this)
                    if (matches(p)) {
                        consumer << p
                    }
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
        def s = """\
${getClass().simpleName}($path)
"""
        if (includes) {
            s += "includes ${includes.join(' ')}\n"
        }
        if (excludes) {
            s += "excludes ${excludes.join(' ')}\n"
        }
        s
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
