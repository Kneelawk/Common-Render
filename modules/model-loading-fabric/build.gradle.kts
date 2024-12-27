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

loom {
    accessWidenerPath.set(file("src/main/resources/krender_model_loading.accesswidener"))
}
