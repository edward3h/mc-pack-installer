package org.ethelred.mc.pack_installer

import net.java.truevfs.access.TPath

import java.util.regex.Pattern

abstract class ConfigScript extends Script {
    DefaultConfig appConfig

    def setAppConfig(v) {
        appConfig = v
    }

    def target(String path) {
        appConfig.target path
    }

    def source(String path) {
        appConfig.source path
    }

    def target(Closure block) {
        // block is run twice
        // first time to extract 'path'
        def pt = new PathTypeReader().tap(block)
        if (pt.path) {
            Class type = (pt.type && pt.type.equalsIgnoreCase("web")) ? WebTarget : Target
            Target t = appConfig.target pt.path, type
            // second run of block to apply other settings
            new TargetBuilder(t).tap(block)
        }
    }

    def source(Closure block) {
        // block is run twice
        // first time to extract 'path'
        def pt = new PathTypeReader().tap(block)
        if (pt.path) {
            Source s = appConfig.source pt.path
            // second run of block to apply other settings
            new SourceBuilder(s).tap(block)
        }
    }

    def skip(String v) {
        appConfig.skipPatterns << ~v
    }

    def skip(Pattern v) {
        appConfig.skipPatterns << v
    }
}

class PathTypeReader {
    String path
    String type

    def path(v) {
        this.path = v
    }

    def type(v) {
        this.type = v
    }

    def methodMissing(String name, def args) {
        // ignore - assume properties will be handled by builder
    }
}

class LocationBuilder {
    Location l

    LocationBuilder(l) {
        this.l = l
    }

    def path(v) { } //ignore

    def type(v) { } //ignore

    def include(Object... v) {
        l.include v
    }

    def exclude(Object... v) {
        l.exclude v
    }
}

class SourceBuilder extends LocationBuilder {

    SourceBuilder(Source s) {
        super(s)
    }

    def development(v) {
        l.development = v
    }
}

class TargetBuilder extends LocationBuilder {

    TargetBuilder(Target t) {
        super(t)
    }

    def remote(v) {
        l.remote = v
    }
}