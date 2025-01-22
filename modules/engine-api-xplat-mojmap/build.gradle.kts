plugins {
    id("com.kneelawk.versioning")
    id("com.kneelawk.submodule")
    id("com.kneelawk.kpublish")
}

submodule {
    applyXplatConnection(":engine-api-xplat")
    setupJavadoc()
}

kpublish {
    createPublication()
}
