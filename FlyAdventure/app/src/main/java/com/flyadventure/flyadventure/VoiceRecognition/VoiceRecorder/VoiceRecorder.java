package com.flyadventure.flyadventure.VoiceRecognition.VoiceRecorder;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Toast;

import com.flyadventure.flyadventure.VoiceRecognition.utils.ConstantValues;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by moziliang on 16/11/10.
 */
public class VoiceRecorder {
    static private VoiceRecorder mVoiceRecorder = null;
    static private Context mContext = null;

    static public VoiceRecorder getInstance(Context context) {
        if (mVoiceRecorder == null) {
            mContext = context;
            mVoiceRecorder = new VoiceRecorder();
        }
        return mVoiceRecorder;
    }

    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private String mFileFolder = null;
    int[] bufferData;
    int bytesRecorded;
    private static final int RECORDER_BPP = 16;     //bits per sample
    private static final int RECORDER_SAMPLERATE = 24000;//sample rate
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    short[] audioData;
    private boolean isRecording = false;

    private VoiceRecorder() {
        File file = Environment.getExternalStorageDirectory();
        mFileFolder = file.getPath() + "/VoiceRecognition";
        File fileFolder = new File(mFileFolder);
        if (!fileFolder.exists() || !fileFolder.isDirectory()) {
            fileFolder.mkdir();
        }

        bufferSize = AudioRecord.getMinBufferSize
                (RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING) * 3;
        audioData = new short[bufferSize];
    }

    public void start() {
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING,
                bufferSize);

        int state = recorder.getState();
        if (state == 1) {
            recorder.startRecording();
        } else {
            Toast.makeText(mContext, ConstantValues.VoiceRecordError + ": start state wrong",
                    Toast.LENGTH_SHORT).show();
        }

        isRecording = true;
        (new RecordThread()).start();
    }

    public void stop() {
        if (null != recorder) {
            isRecording = false;

            int state = recorder.getState();
            if (state == 1) {
                recorder.stop();
            } else {
                Toast.makeText(mContext, ConstantValues.VoiceRecordError + ": stop state wrong",
                        Toast.LENGTH_SHORT).show();
            }
            recorder.release();

            recorder = null;
        }
    }

    private class RecordThread extends Thread {
        public void run() {
            try {
                Calendar calendar = Calendar.getInstance();
                String timeString = "" + calendar.get(Calendar.YEAR) +
                        "_" + calendar.get(Calendar.MONTH) +
                        "_" + calendar.get(Calendar.DATE) +
                        "_" + calendar.get(Calendar.HOUR) +
                        "_" + calendar.get(Calendar.MINUTE) +
                        "_" + calendar.get(Calendar.SECOND);
                String rawFilename = mFileFolder + "/" + timeString + ".raw";
                String wavFilename = mFileFolder + "/" + timeString + ".wav";

                byte data[] = new byte[bufferSize];
                FileOutputStream os = null;

//            try {
//                os = new FileOutputStream(rawFilename);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            int readState = 0;
//            if (null != os) {
//                while (isRecording) {
//                    readState = recorder.read(data, 0, bufferSize);
//
//                    if (readState != AudioRecord.ERROR_INVALID_OPERATION) {
//                        try {
//                            os.write(data);
//                        } catch (Exception e) {
//                            Toast.makeText(mContext, ConstantValues.VoiceRecordError + ": ERROR_INVALID_OPERATION",
//                                    Toast.LENGTH_SHORT).show();
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                try {
//                    os.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                copyWaveFile(rawFilename, wavFilename);
//            }

                int readState = 0;
                ArrayList<byte[]> allBytes = new ArrayList<>();
                while (isRecording) {
                    readState = recorder.read(data, 0, bufferSize);

                    if (readState != AudioRecord.ERROR_INVALID_OPERATION) {
                        allBytes.add(data.clone());
                    }
                }
                data = new byte[allBytes.size() * bufferSize];
                os = new FileOutputStream(rawFilename);
                for (int i = 0; i < allBytes.size(); i++) {
                    System.arraycopy(allBytes.get(i), 0, data, i * bufferSize, bufferSize);
                    os.write(allBytes.get(i));
                }
                os.close();
                if (mVoiceRecordListener != null) {
                    mVoiceRecordListener.onVoiceRecordListener(data);
                }
                copyWaveFile(rawFilename, wavFilename);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void copyWaveFile(String inFilename, String outFilename) {
            FileInputStream in = null;
            FileOutputStream out = null;
            long totalAudioLen = 0;
            long totalDataLen = totalAudioLen + 36;
            long longSampleRate = RECORDER_SAMPLERATE;
            int channels = 1;
            long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;

            byte[] data = new byte[bufferSize];

            try {
                in = new FileInputStream(inFilename);
                out = new FileOutputStream(outFilename);
                totalAudioLen = in.getChannel().size();
                totalDataLen = totalAudioLen + 36;

                WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                        longSampleRate, channels, byteRate);

                while (in.read(data) != -1) {
                    out.write(data);
                }

                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                         long totalDataLen, long longSampleRate, int channels,
                                         long byteRate) throws IOException {
            byte[] header = new byte[44];

            header[0] = 'R';  // RIFF/WAVE header
            header[1] = 'I';
            header[2] = 'F';
            header[3] = 'F';
            header[4] = (byte) (totalDataLen & 0xff);
            header[5] = (byte) ((totalDataLen >> 8) & 0xff);
            header[6] = (byte) ((totalDataLen >> 16) & 0xff);
            header[7] = (byte) ((totalDataLen >> 24) & 0xff);
            header[8] = 'W';
            header[9] = 'A';
            header[10] = 'V';
            header[11] = 'E';
            header[12] = 'f';  // 'fmt ' chunk
            header[13] = 'm';
            header[14] = 't';
            header[15] = ' ';
            header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
            header[17] = 0;
            header[18] = 0;
            header[19] = 0;
            header[20] = 1;  // format = 1
            header[21] = 0;
            header[22] = (byte) channels;
            header[23] = 0;
            header[24] = (byte) (longSampleRate & 0xff);
            header[25] = (byte) ((longSampleRate >> 8) & 0xff);
            header[26] = (byte) ((longSampleRate >> 16) & 0xff);
            header[27] = (byte) ((longSampleRate >> 24) & 0xff);
            header[28] = (byte) (byteRate & 0xff);
            header[29] = (byte) ((byteRate >> 8) & 0xff);
            header[30] = (byte) ((byteRate >> 16) & 0xff);
            header[31] = (byte) ((byteRate >> 24) & 0xff);
            header[32] = (byte) (2 * 16 / 8);  // block align
            header[33] = 0;
            header[34] = RECORDER_BPP;  // bits per sample
            header[35] = 0;
            header[36] = 'd';
            header[37] = 'a';
            header[38] = 't';
            header[39] = 'a';
            header[40] = (byte) (totalAudioLen & 0xff);
            header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
            header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
            header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

            out.write(header, 0, 44);
        }
    }

    public interface VoiceRecordListener {
        public void onVoiceRecordListener(byte[] data);
    }
    private VoiceRecordListener mVoiceRecordListener = null;
    public void setmVoiceRecorder(VoiceRecordListener voiceRecorder) {
        mVoiceRecordListener = voiceRecorder;
    }
}
