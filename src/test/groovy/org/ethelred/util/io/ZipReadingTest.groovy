package org.ethelred.util.io

import net.java.truevfs.access.TPath
import net.java.truevfs.access.TVFS
import spock.lang.Specification

/**
 * TODO
 *
 * @author eharman* @since 2020-08-03
 */
class ZipReadingTest extends Specification {
    def "test read zip contents"() {
        given:
        def p = new TPath(getClass().getResource('/ZipReadingTest/valid.zip').toURI())

        when:
        String r = p.resolve("README").text

        then:
        r.trim() == "Hello, world!"
    }

    def "test read nested zip contents"() {
        given:
        def p = new TPath(getClass().getResource('/ZipReadingTest/nested.zip').toURI())

        when:
        p = p.resolve("valid.zip")
        String r = p.resolve("README").text

        then:
        r.trim() == "Hello, world!"
    }

    def cleanupSpec() {
        TVFS.umount()
    }
}
