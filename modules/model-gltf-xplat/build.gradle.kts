plugins {
    id("com.kneelawk.versioning")
    id("com.kneelawk.submodule")
    id("com.kneelawk.kpublish")
}

submodule {
    setRefmaps("krender_model_gltf")
    xplatProjectDependency(":model-loading", include = false)
    xplatProjectDependency(":model-guard", include = false)
    xplatProjectDependency(":engine-api", include = false)
    setupJavadoc()
}

kpublish {
    createPublication("intermediary")
}
