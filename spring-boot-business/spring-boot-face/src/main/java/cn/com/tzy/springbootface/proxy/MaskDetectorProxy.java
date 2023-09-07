package cn.com.tzy.springbootface.proxy;

import cn.com.tzy.springbootface.pool.MaskDetectorPool;
import cn.com.tzy.springbootface.pool.SeetaConfSetting;
import com.seeta.sdk.MaskDetector;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaRect;

public class MaskDetectorProxy {

    private MaskDetectorPool pool;

    private MaskDetectorProxy() {
    }


    public MaskDetectorProxy(SeetaConfSetting confSetting) {

        pool = new MaskDetectorPool(confSetting);
    }


    public MaskItem detect(SeetaImageData imageData, SeetaRect face) {

        float[] score = new float[1];
        boolean detect = false;
        MaskDetector maskDetector = null;
        try {
            maskDetector = pool.borrowObject();
            detect = maskDetector.detect(imageData, face, score);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (maskDetector != null) {
                pool.returnObject(maskDetector);
            }
        }

        return new MaskItem(detect, score[0]);
    }


    public class MaskItem {
        private float score;

        private boolean mask;

        public MaskItem(boolean mask, float score) {
            this.mask = mask;
            this.score = score;
        }

        public float getScore() {
            return score;
        }

        public void setScore(float score) {
            this.score = score;
        }


        public boolean getMask() {
            return mask;
        }

        public void setMask(boolean mask) {
            this.mask = mask;
        }
    }

}
