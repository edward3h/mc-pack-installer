/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack

/**
 * types of pack
 */
enum PackType {
    RESOURCE('R', 'resources'),
    BEHAVIOR('B', 'data'),
    SKIN('S', 'skin');

    def manifestCode
    def shortCode

    PackType(shortCode, manifestCode) {
        this.shortCode = shortCode
        this.manifestCode = manifestCode
    }

    static PackType fromString(s) {
        values().find {
            s in [
                it.name(),
                it.manifestCode,
                it.shortCode
            ]
        }
    }
}