plugins {
    id("com.kneelawk.versioning")
    id("com.kneelawk.submodule")
    id("com.kneelawk.kpublish")
}

submodule {
    setRefmaps("krender_model_loading")
    setupJavadoc()
}

kpublish {
    createPublication("intermediary")
}
