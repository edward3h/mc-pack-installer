/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack

import spock.lang.Shared
import spock.lang.Specification
import groovy.json.JsonGenerator
import groovy.json.JsonSlurper
import groovy.json.StreamingJsonBuilder

import java.nio.file.Files
import java.nio.file.Path

/**
 * test Pack and PackId
 */
class PackTest extends Specification {
    @Shared def tempPaths = []

    def generateTestManifestAndDir(values, d = null) {
        Files.createTempDirectory("PackTest").tap {dir ->
            tempPaths << dir
            dir.resolve(Manifest.NAME).withWriter {writer ->
                def json = new StreamingJsonBuilder(writer, new JsonGenerator.Options().excludeNulls().build())
                json {
                    format_version 2, 0, 0
                    header {
                        name "PackTest"
                        uuid values.uuid
                        version values.version
                    }
                    modules( [[type: 'resources']])
                    dependencies d
                }
            }
        }
    }

    def "test my own test utility"() {
        given:
        def testValues = [uuid: "whatever", version: [1, 2, 3]]
        def testDependencies = [
            [uuid: "dep", version: [0, 0, 1]]
        ]

        when:
        Path packDir = generateTestManifestAndDir testValues, testDependencies

        then:
        Files.isRegularFile(packDir.resolve(Manifest.NAME))

        when:
        def json = new JsonSlurper().parse(packDir.resolve(Manifest.NAME))

        then:
        json.format_version == [2, 0, 0]
        json.header.name == "PackTest"
        json.header.uuid == testValues.uuid
        json.modules.size() == 1
        json.modules.first().type == 'resources'
        json.dependencies.first().version == [0, 0, 1]
    }

    def "Pack and PackId are equal"() {
        given:
        def uuid = "testUuid"
        def version = [0, 1, 2]

        when:
        def pack = new Pack(generateTestManifestAndDir(uuid: uuid, version: version))
        def packId = new PackId(uuid: uuid, version: new Version(version))

        then:
        pack == packId
        pack.hashCode() == packId.hashCode()
    }
}
