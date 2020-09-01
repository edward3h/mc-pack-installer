/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack

import static org.ethelred.mc.MCText.fromString as t

import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import groovy.transform.Memoized
import groovy.transform.ToString

import java.nio.file.Path

/**
 * See https://bedrock.dev/docs/stable/Addons#manifest.json
 */
@ToString
class Manifest {
    static String NAME = "manifest.json"
    def json

    @Memoized
    def getName() {
        t(json.header.name)
    }

    @Memoized
    def getUuid() {
        json.header.uuid
    }

    @Memoized
    def getDescription() {
        t(json.header?.description)
    }

    @Memoized
    Version getVersion() {
        new Version(json.header.version)
    }

    @Memoized
    Metadata getMetadata() {
        new Metadata(metadata: json.metadata)
    }

    List<PackId> getDependencies() {
        json.dependencies.collect { new PackId(uuid: it.uuid, version: new Version(it.version)) }
    }

    def getType() {
        PackType.fromString(json.modules.first().type)
    }

    Manifest(Path path) {
        try {
            json = new JsonSlurper(type: JsonParserType.LAX).parse(path)
            json.with {
                assert format_version
                assert header
                assert header.name
                assert header.uuid
                assert modules
                assert !modules.empty // TODO is there ever more than one module?
                assert getType()
            }
        } catch(Throwable e) {
            throw new InvalidPackException("Invalid pack", e)
        }
    }
}

class Metadata {
    def metadata

    List getAuthors() {
        metadata?.authors?.collect { t(it) }
    }

    def getLicense() {
        t(metadata?.license)
    }

    def getUrl() {
        metadata?.url
    }
}