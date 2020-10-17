/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack

import com.fasterxml.jackson.core.json.JsonReadFeature

import static org.ethelred.mc.text.MCText.fromString as t

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Charsets
import groovy.transform.Memoized
import groovy.transform.ToString

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
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
            json = _parseJson(path)
            json.with {
                assert format_version
                assert header
                assert header.name
                assert header.uuid
                assert modules
                assert !modules.empty // TODO is there ever more than one module?
                assert getType()
            }
            log.info "Valid manifest '$path' '${this.name}'"
        } catch(Throwable e) {
            log.warn("Invalid manifest '$path' \n        ${e.message}")
            throw new InvalidPackException("Invalid manifest", e)
        }
    }

    static def _parseJson(Path path) {
        [
            StandardCharsets.UTF_8,
            Charset.forName("Windows-1252"),
            Charsets.ISO_8859_1
        ].findResult { Charset cs ->
            try {
                return _parseJson(path, cs)
            } catch(Exception e) {
                log.warn("Could not read json $path : ${e.message}")
            }
        }
    }

    static def _parseJson(Path path, Charset charset) {
        ObjectMapper objectMapper = new ObjectMapper()
                .enable(
                        JsonReadFeature.ALLOW_JAVA_COMMENTS.mappedFeature(),
                        JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(),
                        JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature()
                )
        objectMapper.readValue(path.newReader(charset.toString()), Map)
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