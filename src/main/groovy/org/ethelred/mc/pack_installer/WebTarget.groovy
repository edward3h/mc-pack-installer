/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

import org.ethelred.mc.pack.Pack

import groovy.transform.ToString
import groovy.xml.MarkupBuilder

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

import net.java.truevfs.access.TPath

/**
 * instead of installing packs into a game directory, this creates a web listing of them
 */
@ToString
class WebTarget extends Target {

    @Override
    Path getPackRoot(type) {
        return path.resolve("packs")
    }

    Path getOutput() {
        Path.of(path.toString())
    }

    def extractIcon(pack) {
        def p = pack.iconPath
        if (p) {
            def iconRoot = output.resolve("icons")
            Files.createDirectories(iconRoot)
            def iconName = "${pack.uuid} ${pack.version}".replaceAll(/\W+/, '_') + '.png'
            def iconPath = iconRoot.resolve(iconName)
            Files.copy(p, iconPath, StandardCopyOption.REPLACE_EXISTING)
            return [src: "icons/$iconName", alt: pack.name]
        }
        return null
    }

    @Override
    void writePacks(List<List<Pack>> lists) {
        super.writePacks(lists)
        output.resolve("index.html").withPrintWriter { writer ->
            def index = new MarkupBuilder(writer)
            index.html {
                head {
                    title("MC Pack Listing")
                    style('''img { max-width: 256px; }
''')
                }
                body {
                    h1("MC Pack Listing")
                    table {
                        thead {
                            tr {
                                "Icon Name Type Description".split().each {
                                    th(it)
                                }
                            }
                        }
                        tbody {
                            lists.each { group ->
                                group.each { pack ->
                                    tr {
                                        def icon = extractIcon(pack)
                                        if (icon) {
                                            td {
                                                img(icon)
                                            }
                                        } else {
                                            td {
                                                mkp.yieldUnescaped("&nbsp;")
                                            }
                                        }
                                        td {
                                            a(href:"packs/${pack.zipName}.mcpack", "${pack.name} ${pack.version}")
                                        }
                                        td(pack.type)
                                        td(pack.description)
                                    }
                                }
                                tr {
                                    td(colspan:4) {
                                        mkp.yieldUnescaped("&nbsp;")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
