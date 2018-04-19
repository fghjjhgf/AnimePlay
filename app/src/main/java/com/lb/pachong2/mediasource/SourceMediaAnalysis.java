package com.lb.pachong2.mediasource;

/**
 * Created by Administrator on 2018/4/19.
 */

public class SourceMediaAnalysis extends BaseMediaAnalysis {
    private static String TAG = SourceMediaAnalysis.class.getSimpleName();

    @Override
    protected void setMediasource(String sourceurl) {
        super.setMediasource(sourceurl);
        mediaResponseCallback.sucessResponse(sourceurl);
    }
}
