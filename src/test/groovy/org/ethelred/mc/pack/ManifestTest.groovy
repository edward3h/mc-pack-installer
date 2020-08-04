/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack

import spock.lang.Specification

import java.nio.file.Path

/**
 * TODO
 *
 * @author eharman* @since 2020-08-03
 */
class ManifestTest extends Specification {
    def "load a valid manifest"() {
        given:
        def p = Path.of(getClass().getResource('/ManifestTest/valid_manifest.json').toURI())

        when:
        def m = new Manifest(p)

        then:
        m.name == "valid name"
        m.version == new Version([1, 0, 0])
    }

    def "load an invalid manifest"() {
        given:
        def p = Path.of(getClass().getResource('/ManifestTest/invalid_manifest.json').toURI())

        when:
        def m = new Manifest(p)

        then:
        thrown(InvalidPackException)
    }
}
