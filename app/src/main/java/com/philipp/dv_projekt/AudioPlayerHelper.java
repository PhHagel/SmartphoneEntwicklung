package com.philipp.dv_projekt;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.widget.Toast;

public class AudioPlayerHelper {

    private static MediaPlayer player;


    public static void playAudio(Context context, int audioResId, Runnable onCompletion) {
        release();

        player = MediaPlayer.create(context, audioResId);
        if (player == null) return;

        player.setOnCompletionListener(mp -> {
            if (onCompletion != null) onCompletion.run();
            release();
        });

        player.start();
    }


    public static void playAudioFile(Context context, String filePath, Runnable onCompletion) {
        release();

        player = new MediaPlayer();
        try {
            player.setDataSource(filePath);
            player.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());

            player.setOnPreparedListener(MediaPlayer::start);
            player.setOnCompletionListener(mp -> {
                if (onCompletion != null) onCompletion.run();
                release();
            });

            player.prepareAsync();
        } catch (Exception e) {
            Toast.makeText(context, "Fehler beim Abspielen: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    public static boolean isPlaying() {
        return player != null && player.isPlaying();
    }


    public static void release() {
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
            player = null;
        }
    }


    public static void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        if (player != null) {
            player.setOnCompletionListener(listener);
        }
    }

}