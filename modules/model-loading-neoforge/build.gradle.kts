plugins {
    id("com.kneelawk.versioning")
    id("com.kneelawk.submodule")
    id("com.kneelawk.kpublish")
}

submodule {
    setLibsDirectory()
    applyXplatConnection(":model-loading-xplat")
    setupJavadoc()
}

kpublish {
    createPublication()
}

dependencies {
    testRuntimeOnly("io.github.llamalad7:mixinextras-neoforge:0.4.1")
}
