package com.flyadventure.flyadventure.VoiceRecognition.VoiceRecorder;

import android.content.Context;
import android.util.Log;

import com.flyadventure.flyadventure.VoiceRecognition.utils.ConstantValues;
import com.flyadventure.flyadventure.VoiceRecognition.utils.MFCC.MFCC;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moziliang on 16/11/10.
 */
public class MFCCClassifier {
    static private MFCCClassifier mMFCCClassifier = null;
    static private Context mContext = null;

    static public MFCCClassifier getInstance(Context context) {
        if (mMFCCClassifier == null) {
            mContext = context;
            mMFCCClassifier = new MFCCClassifier();
        }
        return mMFCCClassifier;
    }

//    final static public ArrayList<String> outputs = new ArrayList<String>(
//            Arrays.asList("down", "jump", "left", "move", "right", "shoot", "speedup", "up"));
    final static public ArrayList<String> outputs = new ArrayList<String>(
            Arrays.asList("jump", "left", "move", "right"));
    static public ArrayList<MFCCData> allData = new ArrayList<>();

    static int K = 50;

    static public Calendar calendar = null;

    static private VoiceRecorder mVoiceRecorder = null;

    private MFCCClassifier() {
        getAllData();
        mVoiceRecorder = VoiceRecorder.getInstance(mContext);
        mVoiceRecorder.setmVoiceRecorder(new MyVoiceRecordListener());
    }

    private void getAllData() {
        try {
            calendar = Calendar.getInstance();
            Log.d(ConstantValues.debugTag, "begin getAllData" + calendar.get(Calendar.MINUTE) + " " + calendar.get(Calendar.SECOND));

//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(mContext.getAssets().open("_all_source.json")));
//            String line = null;
//            String jsonString = "";
//            while ((line = reader.readLine()) != null) {
//                jsonString += line;
//            }
//            parseJSON(jsonString);
            parseData();
            calendar = Calendar.getInstance();
            Log.d(ConstantValues.debugTag, "end getAllData" + calendar.get(Calendar.MINUTE) + " " + calendar.get(Calendar.SECOND));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJSON(String jsonString) {
        try {

            JSONObject mainObject = new JSONObject(jsonString);
            for (String outputString : outputs) {
                JSONObject main1Object = mainObject.getJSONObject(outputString);

                int fileNum = main1Object.getInt("fileNum");
                for (int fileIndex = 0; fileIndex < fileNum; fileIndex++) {
                    JSONObject fileObject = main1Object.getJSONObject("" + fileIndex);
                    int frameNum = fileObject.getInt("frameNum");

                    MFCCData currentMfccData = new MFCCData();
                    currentMfccData.result = outputs.indexOf(outputString);
                    currentMfccData.data = new double[frameNum][];
                    for (int frameIndex = 0; frameIndex < frameNum; frameIndex++) {
                        JSONObject frameObject = fileObject.getJSONObject("" + frameIndex);
                        JSONArray dataArray = frameObject.getJSONArray("data");
                        currentMfccData.data[frameIndex] = new double[dataArray.length()];
                        for (int k = 0; k < dataArray.length(); k++) {
                            currentMfccData.data[frameIndex][k] = dataArray.getDouble(k);
                        }
                    }
                    allData.add(currentMfccData);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseData() throws Exception{
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(mContext.getAssets().open("_all_source_old.txt")));
        String line = null;
        while ((line = reader.readLine()) != null) {
            int currentResult = outputs.indexOf(line);
            int dataNum = Integer.parseInt(reader.readLine());
            for (int dataIndex = 0; dataIndex < dataNum; dataIndex++) {
                MFCCData mfccData = new MFCCData();
                mfccData.result = currentResult;
                int frameNum = Integer.parseInt(reader.readLine());
                mfccData.data = new double[frameNum][];
                for (int frameIndex = 0; frameIndex < frameNum; frameIndex ++) {
                    int doubleNum = Integer.parseInt(reader.readLine());
                    mfccData.data[frameIndex] = new double[doubleNum];
                    for (int doubleIndex = 0; doubleIndex < doubleNum; doubleIndex ++) {
                        mfccData.data[frameIndex][doubleIndex] = Double.parseDouble(reader.readLine());
                    }
                }
                if (outputs.contains(line)) {
                    allData.add(mfccData);
                }
            }
        }
    }

    private String process(MFCCData mfccData) throws Exception{
        Map<Integer, Double> indexAndDiffernce = new HashMap<>();
        for (int j = 0; j < allData.size(); j++) {
            MFCCData mfccData2 = allData.get(j);
            double difference = mfccData.getDifference(mfccData2);
            indexAndDiffernce.put(j, difference);
        }
        List<Map.Entry<Integer, Double>> entryList = new ArrayList<>(indexAndDiffernce.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> entry1, Map.Entry<Integer, Double> entry2) {
                if (entry1.getValue() > entry2.getValue()) {
                    return 1;
                } else if (entry1.getValue() < entry2.getValue()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        double[] outputScore = new double [outputs.size()];
        for (int j = 0; j < outputs.size(); j++) {
            outputScore[j] = 0;
        }
        for (int j = 0; j < K; j++) {
            int entryIndex = entryList.get(j).getKey();
            int label = allData.get(entryIndex).result;
            if (outputs.get(label).equals("left")) {
                outputScore[label] += 4;
            } else if (outputs.get(label).equals("right")) {
                outputScore[label] += 5;
            } else if (outputs.get(label).equals("move")) {
                outputScore[label] += 1.5;
            } else if (outputs.get(label).equals("jump")) {
                outputScore[label] += 2;
            }
        }
        double MaxScore = outputScore[0];
        int MaxIndex = 0;
        for (int j = 1; j < outputScore.length; j++) {
            if (MaxScore < outputScore[j]) {
                MaxScore = outputScore[j];
                MaxIndex = j;
            }
        }
        return outputs.get(MaxIndex);
    }

    private class MyVoiceRecordListener implements VoiceRecorder.VoiceRecordListener {
        @Override
        public void onVoiceRecordListener(byte[] data) {
            try {
                int[]sample = byteToInt(data);
                sample = ZeroFilter(sample);
                myMFCC(sample);


//                Calendar calendar = Calendar.getInstance();
//                String timeString = "" + calendar.get(Calendar.YEAR) +
//                        "_" + calendar.get(Calendar.MONTH) +
//                        "_" + calendar.get(Calendar.DATE) +
//                        "_" + calendar.get(Calendar.HOUR) +
//                        "_" + calendar.get(Calendar.MINUTE) +
//                        "_" + calendar.get(Calendar.SECOND);
//
//                File file = Environment.getExternalStorageDirectory();
//                String mFileFolder = file.getPath() + "/VoiceRecognition";
//                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(mFileFolder + "/" + timeString + ".txt")));
//                for (int i = 0 ; i < sample.length;i ++) {
//                    bufferedWriter.write("" + sample[i] + "\n");
//                }
//                bufferedWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public int[] byteToInt(byte[] data){
            int[]sample = new int[data.length / 2];
            for (int i = 0 ; i < data.length; i += 2) {
                int currentSample =
                        (data[i + 1] << 8)  & 0x0000ff00 |
                                (data[i] << 0)  & 0x000000ff;

                if (currentSample >= 32768) {
                    currentSample = currentSample - 65536;
                }

                sample[i / 2] = currentSample;
            }
            return sample;
        }

        public int[] ZeroFilter(int[]sample) throws Exception {
            int scale = 0;
            for (int i = 0; i < sample.length; i++) {
                if (Math.abs(sample[i]) > scale) {
                    scale = Math.abs(sample[i]);
                }
            }
            double threshold = scale / 10.0;

            int[] temp = new int[sample.length];
            System.arraycopy(sample, 0, temp, 0, sample.length);
            int begin = 0;
            int end = sample.length - 1;
            while (Math.abs(temp[begin]) < threshold) {
                begin++;
            }
            while (Math.abs(temp[end]) < threshold) {
                end--;
            }
            if (begin > end) {
                throw new Exception("begin > end");
            }

            sample = new int[end - begin + 1];
            System.arraycopy(temp, begin, sample, 0, sample.length);
            return sample;
        }

        public void myMFCC(int sample[]) throws Exception{
            int window = 512;
            int factorNum = 20;
            int sampleLength = sample.length;
            if (sampleLength % (window / 2) != 0) {
                sampleLength = (sampleLength / (window / 2) + 1) * (window / 2);
            }

            double[] temp = new double[sampleLength];
            for (int i = 0; i < sample.length; i++) {
                temp[i] = (double) sample[i];
            }
            for (int i = sample.length; i < sampleLength; i++) {
                temp[i] = 0.0;
            }
            // MFCC mfcc = new MFCC(24000);
            // double [][]result = mfcc.process(temp);

            MFCC mfcc2 = new MFCC(24000, window, factorNum, true);
            double[][] result2 = mfcc2.process(temp);

            MFCCData mfccData = new MFCCData();
            mfccData.data = result2;

            String result = process(mfccData);
            if (mfccResultListener != null) {
                mfccResultListener.onReceivedMFCCResult(result);
            }
        }
    }

    public interface MFCCResultListener {
        public void onReceivedMFCCResult(String result);
    }
    private MFCCResultListener mfccResultListener = null;
    public void setmMFCCClassifier(MFCCResultListener mfccResultListener) {
        this.mfccResultListener = mfccResultListener;
    }
}
