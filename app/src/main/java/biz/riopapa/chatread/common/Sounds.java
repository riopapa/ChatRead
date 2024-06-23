package biz.riopapa.chatread.common;

import static biz.riopapa.chatread.MainActivity.beepRawIds;
import static biz.riopapa.chatread.MainActivity.isPhoneBusy;
import static biz.riopapa.chatread.MainActivity.mAudioManager;
import static biz.riopapa.chatread.MainActivity.mContext;
import static biz.riopapa.chatread.MainActivity.mFocusGain;
import static biz.riopapa.chatread.MainActivity.notificationBar;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import biz.riopapa.chatread.MainActivity;
import biz.riopapa.chatread.MainActivity.soundType;

public class Sounds {
    public static boolean isTalking = false;
    static TextToSpeech mTTS = null;
    static String TTSId = "";

    public Sounds() {

        stopTTS();

        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mFocusGain = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .build();
        mTTS = null;
        mTTS = new TextToSpeech(mContext, status -> {
            if (status == TextToSpeech.SUCCESS) {
                initializeTTS();
            }
        });
    }

    private void initializeTTS() {

        mTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                TTSId = utteranceId;
            }

            @Override
            // this method will always called from a background thread.
            public void onDone(String utteranceId) {
                if (mTTS.isSpeaking())
                    return;
                notificationBar.hideStop();
                isTalking = false;
                new Timer().schedule(new TimerTask() {
                    public void run () {
                        beepOnce(soundType.POST.ordinal());
                        mAudioManager.abandonAudioFocusRequest(mFocusGain);
                    }
                }, 100);
            }

            @Override
            public void onError(String utteranceId) { }
        });

        int result = mTTS.setLanguage(Locale.getDefault());
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            beepOnce(soundType.ERR.ordinal());
        } else {
            mTTS.setPitch(1.2f);
            mTTS.setSpeechRate(1.3f);
        }
    }

    public void stopTTS() {
        if (mTTS != null)
            mTTS.stop();
    }

    public void speakAfterBeep(String text) {

        if (isSilent())
            return;
        if (!isTalking) {
            if (isPhoneBusy) {
                beepOnce(MainActivity.soundType.INFO.ordinal());
                return;
            } else {
                beepOnce(soundType.PRE.ordinal());
            }
        }

        delayedSay(text);
    }

    private void delayedSay(String text2Speak) {
        long delay = 100;
//        if (mTTS == null) {
//            init();
//            delay = 30;
//        }

        if (isActive()) {
            isTalking = true;
            mAudioManager.requestAudioFocus(mFocusGain);
            new Timer().schedule(new TimerTask() {
                public void run() {
                    try {
                        mTTS.speak(onlySpeakable(text2Speak), TextToSpeech.QUEUE_ADD, null, TTSId);
                    } catch (Exception e) {
                        new Utils().logE("Sound", "TTS Error:" + e);
                    }
                }
            }, delay);
        }
    }

    public void speakKakao(String text) {

        if (!isActive())
            return;
        if (!isTalking)
            beepOnce(soundType.KAKAO.ordinal());
        delayedSay(text);
    }

    public void speakBuyStock(String text) {

        if (!isActive())
            return;
        beepOnce(soundType.STOCK.ordinal());
        if (isActive()) {
            mAudioManager.requestAudioFocus(mFocusGain);
            new Timer().schedule(new TimerTask() {
                public void run() {
                    try {
                        isTalking = true;
                        mTTS.speak(text, TextToSpeech.QUEUE_ADD, null, TTSId);
                    } catch (Exception e) {
                        new Utils().logE("Sound", "TTS Error:" + e);
                    }
                }
            }, 100);
        }
    }

    String onlySpeakable(String text) {
        // 한글, 영문, 숫자만 OK
        int idx = text.indexOf("http");
        if (idx > 0)
            text = text.substring(0, idx) + " 링크 있음";
        final String match = "[^가-힣\\da-zA-Z.,\\-]";
        return text.replaceAll(match, " ");
    }

    public void beepOnce(int soundNbr) {

        final MediaPlayer mPlayer = MediaPlayer.create(mContext, beepRawIds[soundNbr]);
//        mPlayer.setVolume(1f, 1f);
        mPlayer.start();
        mPlayer.setOnCompletionListener(mp -> {
            if (mPlayer.isPlaying())
                mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
        });
    }

    public boolean isActive() {
        if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            return true;
        }

        AudioDeviceInfo[] audioDevices = mAudioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
        for(AudioDeviceInfo deviceInfo : audioDevices){
            if (deviceInfo.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO     // 007
                    || deviceInfo.getType()==AudioDeviceInfo.TYPE_BLUETOOTH_A2DP    // 008
                    || deviceInfo.getType()==AudioDeviceInfo.TYPE_WIRED_HEADSET
                    || deviceInfo.getType()==AudioDeviceInfo.TYPE_WIRED_HEADPHONES
            )
                return true;
        }
        return false;
    }

    public boolean isSilent() {

        return (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT ||
                mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) < 5);
    }

}
// final String match = "[^\uAC00-\uD7A3\u3131-\u314E\\da-zA-Z.,\\-]";     // 가~힣 ㄱ~ㅎ
