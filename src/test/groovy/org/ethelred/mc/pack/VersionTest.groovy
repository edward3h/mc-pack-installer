package org.ethelred.mc.pack

import spock.lang.Specification
import spock.lang.Unroll

/**
 * version comparisons
 */
class VersionTest extends Specification {
    @Unroll
    def "test #a equals #b"() {
        given:
        Version av = new Version(a)
        Version bv = new Version(b)

        expect:
        av == bv

        where:
        a | b
        [] | []
        [1, 0, 0] | [1, 0, 0]
        [0, 1, 0] | [0, 1, 0]
        [1, 0, 0] | [1]
        [1, 0] | [1, 0, 0, 0, 0, 0]
    }

    @Unroll
    def "test #a greater than #b"() {
        given:
        Version av = new Version(a)
        Version bv = new Version(b)

        expect:
        av > bv
        bv < av

        where:
        a | b
        [1] | []
        [0, 0, 1] | []
        [0, 1, 2] | [0, 1, 1]
        [1, 0, 1] | [1, 0, 0]
        [1, 16, 100] | [1, 16, 2]
        [1, 1, 0] | [0, 12, 1]
    }

    @Unroll
    def "test toString #a"() {
        given:
        Version v = new Version(a)

        expect:
        "$v" == expected

        where:
        a | expected
        [] | "0"
        [1] | "1"
        [1, 16, 100] | "1.16.100"
    }
}
