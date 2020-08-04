/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack

import groovy.transform.ToString

import java.nio.file.Path

/**
 * a pack
 */
@ToString
class Pack {
    Path path
    @Delegate Manifest manifest

    Pack(Path dir) {
        path = dir
        manifest = new Manifest(path.resolve(Manifest.NAME))
    }


    @Override
    public String toString() {
        return """\
Pack{
    path=$path, 
    manifest=$manifest
}"""
    }
}
