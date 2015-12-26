package com.sharkozp.galleryapplication.system;

import java.util.List;

/**
 * Created by oleksandr on 12/26/15.
 */
public interface AsyncResponse {
    void processFinish(List<String> images);
}
