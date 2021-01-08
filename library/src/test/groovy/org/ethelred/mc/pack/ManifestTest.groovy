/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack

import spock.lang.Specification
import groovy.json.JsonParserType
import groovy.json.JsonSlurper

import java.nio.file.Paths

/**
 * TODO
 *
 * @author edward3h
 * @since 2020-08-03
 */
class ManifestTest extends Specification {
    def root = Paths.get(getClass().getResource('/ManifestTest').toURI())

    def "load a valid manifest"() {
        given:
        def p = root.resolve 'valid_manifest.json'

        when:
        def m = new Manifest(p)

        then:
        m.name.strip == "valid name"
        m.version == new Version([1, 0, 0])
    }

    def "load an invalid manifest"() {
        given:
        def p = root.resolve'invalid_manifest.json'

        when:
        def m = new Manifest(p)

        then:
        thrown(InvalidPackException)
    }

    def "load a manifest with a comment"() {
        given:
        def p = root.resolve'bridge_manifest.json'

        when:
        def m = new Manifest(p)

        then:
        m.name.strip == "valid name"
        m.version == new Version([1, 0, 0])
    }
}
