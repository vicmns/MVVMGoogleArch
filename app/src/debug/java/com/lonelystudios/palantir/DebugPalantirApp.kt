package com.lonelystudios.palantir

import com.facebook.stetho.Stetho

/**
 * Created by vicmns on 11/22/17.
 */
class DebugPalantirApp: PalantirApp() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build())
    }
}