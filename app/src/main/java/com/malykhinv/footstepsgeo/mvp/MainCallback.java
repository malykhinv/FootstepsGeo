package com.malykhinv.footstepsgeo.mvp;

import javax.security.auth.callback.Callback;

public interface MainCallback extends Callback {
    void onError(Exception e);
}
