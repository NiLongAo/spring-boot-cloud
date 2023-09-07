package cn.com.tzy.springbootface.proxy;

import cn.com.tzy.springbootface.pool.QualityOfClarityPool;
import cn.com.tzy.springbootface.pool.SeetaConfSetting;
import com.seeta.sdk.QualityOfClarity;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaPointF;
import com.seeta.sdk.SeetaRect;

public class QualityOfClarityProxy {

    private QualityOfClarityPool pool;


    private QualityOfClarityProxy() {
    }

    public QualityOfClarityProxy(SeetaConfSetting setting) {

        pool = new QualityOfClarityPool(setting);
    }


    public ClarityItem check(SeetaImageData imageData, SeetaRect face, SeetaPointF[] landmarks) {

        float[] score = new float[1];

        QualityOfClarity qualityOfClarity = null;
        QualityOfClarity.QualityLevel check = null;
        try {

            qualityOfClarity = pool.borrowObject();
            check = qualityOfClarity.check(imageData, face, landmarks, score);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (qualityOfClarity != null) {
                pool.returnObject(qualityOfClarity);
            }
        }

        return new ClarityItem(check, score[0]);
    }

    public class ClarityItem {
        private QualityOfClarity.QualityLevel qualityLevel;
        private float score;

        public ClarityItem(QualityOfClarity.QualityLevel qualityLevel, float score) {
            this.qualityLevel = qualityLevel;
            this.score = score;
        }

        public QualityOfClarity.QualityLevel getQualityLevel() {
            return qualityLevel;
        }

        public void setQualityLevel(QualityOfClarity.QualityLevel qualityLevel) {
            this.qualityLevel = qualityLevel;
        }

        public float getScore() {
            return score;
        }

        public void setScore(float score) {
            this.score = score;
        }
    }

}
