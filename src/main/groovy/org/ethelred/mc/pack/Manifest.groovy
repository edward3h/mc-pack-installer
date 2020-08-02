package org.ethelred.mc.pack

import groovy.json.JsonSlurper
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

    Manifest(Path path) {
        json = new JsonSlurper().parse(path)
        _validate()
    }

    void _validate() {
        log.info("_validate ${json}")
        json.with {
            assert format_version
            assert header
            assert modules
        }
    }
}
