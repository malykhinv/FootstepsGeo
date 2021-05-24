package com.malykhinv.footstepsgeo.mvp.model.fragments;

public class GlobeScreenModel {

    private Callback callback;

    public interface Callback {

    }

    public void registerCallback(Callback callback) {
        this.callback = callback;
    }
}
