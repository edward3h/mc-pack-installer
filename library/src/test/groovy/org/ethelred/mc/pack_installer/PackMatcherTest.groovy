package org.ethelred.mc.pack_installer

import org.ethelred.mc.pack.Pack
import spock.lang.Specification

/**
 * TODO
 *
 * @author edward3h
 * @since 2021-01-05
 */
class PackMatcherTest extends Specification {
    class MockPack extends Pack {
        def name
    }

    def "test for matches"(name, includes, excludes, expectMatch) {
        given:
            def pm = new PackMatcher()
            pm.include includes
            pm.exclude excludes
            def pack = new MockPack(name: name)

        expect:
            pm.matches(pack) == expectMatch

        where:
        name | includes | excludes | expectMatch
        "Lucky Chicken" | null | null | true
        "Lucky Chicken" | [] | [] | true
        "Lucky Chicken" | ["Luck.*"] | [] | true
        "Lucky Chicken" | [".*(ck).*\\1.*"] | [".*"] | true
        "Lucky Chicken" | [".*Poop.*"] | [] | false
        "Lucky Chicken" | ["Luck.*", ".*Poop.*"] | [] | true
        "Lucky Chicken" | [] | [".*"] | false
        "Lucky Chicken" | [] | ["Luck.*"] | false
        "Lucky Chicken" | [] | [".*Poop.*"] | true
        "Lucky Chicken" | [] | ["Luck.*", ".*Poop.*"] | false
        "Lucky Chicken" | ["Luck.*"] | ["Luck.*"] | true
        "Lucky Chicken" | ["Luck.*"] | [".*"] | true
        "Lucky Chicken" | [".*Poop.*"] | [".*"] | false
    }

    def "test exceptional conditions"() {
        given:
            def pm = new PackMatcher()

        when:
            pm.include 1

        then:
            thrown(IllegalArgumentException)

        when:
            pm.include([1, 2])

        then:
            thrown(IllegalArgumentException)

        when:
            pm.matches(new MockPack())

        then:
            thrown(IllegalArgumentException)

    }

}
