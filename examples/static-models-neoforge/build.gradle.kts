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
    implementation(project(":engine-backend-neoforge"))
    jarJar(project(":engine-backend-neoforge"))
}

neoForge {
    runs {
        configureEach {
//            logLevel = org.slf4j.event.Level.DEBUG
        }
    }
}
