package cn.com.tzy.springbootface.proxy;

import cn.com.tzy.springbootface.pool.QualityOfIntegrityPool;
import cn.com.tzy.springbootface.pool.SeetaConfSetting;
import com.seeta.sdk.QualityOfIntegrity;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaPointF;
import com.seeta.sdk.SeetaRect;

public class QualityOfIntegrityProxy {

    private QualityOfIntegrityPool pool;

    private QualityOfIntegrityProxy() {
    }

    public QualityOfIntegrityProxy(SeetaConfSetting setting) {

        pool = new QualityOfIntegrityPool(setting);
    }

    public IntegrityItem check(SeetaImageData imageData, SeetaRect face, SeetaPointF[] landmarks) {
        float[] score = new float[1];
        QualityOfIntegrity.QualityLevel qualityLevel = null;

        QualityOfIntegrity qualityOfIntegrity = null;

        try {
            qualityOfIntegrity = pool.borrowObject();
            qualityLevel = qualityOfIntegrity.check(imageData, face, landmarks, score);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (qualityOfIntegrity != null) {
                pool.returnObject(qualityOfIntegrity);
            }
        }

        return new IntegrityItem(qualityLevel, score[0]);

    }

    public class IntegrityItem {
        private QualityOfIntegrity.QualityLevel qualityLevel;

        private float score;

        public IntegrityItem(QualityOfIntegrity.QualityLevel qualityLevel, float score) {
            this.qualityLevel = qualityLevel;
            this.score = score;
        }

        public QualityOfIntegrity.QualityLevel getQualityLevel() {
            return qualityLevel;
        }

        public void setQualityLevel(QualityOfIntegrity.QualityLevel qualityLevel) {
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
