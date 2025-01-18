package com.kneelawk.krender.engine.backend.frapi.impl;

import java.util.function.BooleanSupplier;

import net.fabricmc.fabric.api.renderer.v1.Renderer;

public class FRAPIPredicate implements BooleanSupplier {
    @Override
    public boolean getAsBoolean() {
        if (rendererPresent()) {
            return true;
        } else {
            KRBFRLog.LOG.warn("Fabric Render API is present, but no FRAPI implementations are present");
            return false;
        }
    }

    private static boolean rendererPresent() {
        try {
            Renderer.get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
