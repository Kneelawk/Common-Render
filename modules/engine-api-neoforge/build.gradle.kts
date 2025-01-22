plugins {
    id("com.kneelawk.versioning")
    id("com.kneelawk.submodule")
    id("com.kneelawk.kpublish")
}

submodule {
    setLibsDirectory()
    applyXplatConnection(":engine-api-xplat")
    setupJavadoc()
}

kpublish {
    createPublication()
}

dependencies {
    implementation(accessTransformers(project(":rendertype-moddev"))!!)
    accessTransformersElements(project(":rendertype-moddev"))
}
