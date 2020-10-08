/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack

import spock.lang.Specification
import groovy.json.JsonParserType
import groovy.json.JsonSlurper

import java.nio.file.Paths

/**
 * TODO
 *
 * @author eharman* @since 2020-08-03
 */
class ManifestTest extends Specification {
    def "load a valid manifest"() {
        given:
        def p = Paths.get(getClass().getResource('/ManifestTest/valid_manifest.json').toURI())

        when:
        def m = new Manifest(p)

        then:
        m.name.strip == "valid name"
        m.version == new Version([1, 0, 0])
    }

    def "load an invalid manifest"() {
        given:
        def p = Paths.get(getClass().getResource('/ManifestTest/invalid_manifest.json').toURI())

        when:
        def m = new Manifest(p)

        then:
        thrown(InvalidPackException)
    }

    def "load a manifest with a comment"() {
        given:
        def p = Paths.get(getClass().getResource('/ManifestTest/bridge_manifest.json').toURI())

        when:
        def m = new Manifest(p)

        then:
        m.name.strip == "valid name"
        m.version == new Version([1, 0, 0])
    }
}
