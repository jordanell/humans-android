package com.humansapp.humans.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;

import java.io.IOException;

/**
  * Created by jordan on 24/09/14.
  */
 public class HumansMedia {

    public static void vibrate(Context context, int length) {
        AudioManager am = getAudioManager(context);
        if (am.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            return;
        }

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(length);
    }

    public static void playAlert(Context context, Uri alert) {
        AudioManager am = getAudioManager(context);
        if (am.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            return;
        }

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    public static AudioManager getAudioManager(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audio;
    }
 }
