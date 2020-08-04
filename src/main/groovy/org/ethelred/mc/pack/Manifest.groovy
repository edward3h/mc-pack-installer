/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack

import groovy.json.JsonSlurper
import groovy.transform.Memoized
import groovy.transform.ToString
import groovy.util.logging.Log

import java.nio.file.Path

/**
 * See https://bedrock.dev/docs/stable/Addons#manifest.json
 */
@ToString
@Log
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
    def getVersion() {
        new Version(json.header.version)
    }

    Manifest(Path path) {
        json = new JsonSlurper().parse(path)
        _validate()
    }

    void _validate() {
        log.info("_validate ${json}")
        try {
            json.with {
                assert format_version
                assert header
                assert header.name
                assert header.uuid
                assert modules
            }
        } catch(Throwable e) {
            throw new InvalidPackException("Invalid pack", e)
        }
    }
}
