/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

import org.ethelred.mc.pack.Pack
import org.ethelred.mc.pack.PackId
import org.ethelred.mc.pack.Version

import spock.lang.Specification

/**
 * test Library
 */
class LibraryTest extends Specification{
    def defaultVersion = new Version([1])
    class MockPack extends Pack {
        def uuid
        def dependencies

        @Override
        def getUuid() {
            uuid
        }

        @Override
        Version getVersion() {
            defaultVersion
        }

        @Override
        List<PackId> getDependencies() {
            dependencies.collect { new PackId(uuid: it, version: defaultVersion) }
        }

        @Override
        def getName() {
            uuid
        }
    }

    def mockPack(id, def... dependencies) {
        new MockPack(uuid: id, dependencies: dependencies)
    }

    def "single pack is returned"() {
        setup:
        def library = new Library()
        def p = mockPack("a")

        when:
        library << p
        def output = library.dependencyGroups

        then:
        output.size() == 1
        output.first().size() == 1
        output.first().first().uuid == "a"
    }

    def "dependency sets"() {
        setup:
        def library = new Library()
        def ps = [
            mockPack("a"),
            mockPack("b", "a"),
            mockPack("c", "b"),
            mockPack("d", "b"),
            mockPack("e")
        ]

        when:
        ps.each {library << it }
        def output = library.dependencyGroups

        then:
        output.size() == 2
        output.first().size() == 4
        output.first().first().uuid == "a"
        output.last().first().uuid == "e"
    }
}
