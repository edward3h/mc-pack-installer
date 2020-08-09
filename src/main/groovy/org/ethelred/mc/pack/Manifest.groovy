/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack

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

    def getName() {
        json.header.name
    }

    def getUuid() {
        json.header.uuid
    }

    def getDescription() {
        json.header?.description
    }

    @Memoized
    Version getVersion() {
        new Version(json.header.version)
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
