package org.ethelred.mc.pack_installer

import org.ethelred.mc.pack.PackId
import org.ethelred.mc.pack.Version

/**
 * TODO
 *
 * @author edward3h
 * @since 2020-10-16
 */
class PackInstances extends PackId {
    List<LocationPack> packs = []

    def leftShift(LocationPack pack) {
        packs << pack
        packs.sort(true) { lp ->
            switch (lp.location) {
                case {  it.isDevelopment() }: return 0
                case Source: return 1
                default: return 2
            }
        }
        this
    }

    @Delegate
    private LocationPack _first() {
        if (packs.isEmpty()) {
            throw new IllegalStateException()
        }
        return packs.first()
    }

    @Override
    Object getUuid() {
        _first().uuid
    }

    @Override
    Version getVersion() {
        _first().version
    }

    boolean hasSource() {
        packs.any {it.location instanceof Source }
    }

    boolean isDevelopment() {
        packs.any { it.location.isDevelopment() }
    }

    boolean isInstalled(Target t) {
        packs.any { it.location.is(t) }
    }
}
