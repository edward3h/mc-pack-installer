/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

import org.ethelred.mc.pack.Pack

interface UI {

    List<Target> confirmTarget(List<Target> targets)

    List<Source> confirmSource(List<Source> sources)

    List<Pack> listPacks(List<Pack> packs)
}