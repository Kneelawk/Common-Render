plugins {
    id("com.kneelawk.versioning")
    id("com.kneelawk.submodule")
}

submodule {
    setLibsDirectory()
    applyXplatConnection(":static-models-xplat")
    generateRuns()
}

dependencies {
    implementation(project(":engine-backend-frapi", configuration = "namedElements"))
    include(project(":engine-backend-frapi"))

    val mod_menu_version: String by project
    modLocalRuntime("com.terraformersmc:modmenu:$mod_menu_version") {
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
    }
}

loom {
    runs {
        configureEach {
            vmArg("-Dmixin.debug.export=true")
        }
    }
}
