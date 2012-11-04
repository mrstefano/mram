package org.mrstefano.mram.manager;

import static android.media.AudioManager.STREAM_ALARM;
import static android.media.AudioManager.STREAM_MUSIC;
import static android.media.AudioManager.STREAM_NOTIFICATION;
import static android.media.AudioManager.STREAM_RING;
import static android.media.AudioManager.STREAM_SYSTEM;
import static android.media.AudioManager.STREAM_VOICE_CALL;
import static android.media.AudioManager.VIBRATE_SETTING_OFF;
import static android.media.AudioManager.VIBRATE_SETTING_ON;
import static android.media.AudioManager.VIBRATE_TYPE_NOTIFICATION;
import static android.media.AudioManager.VIBRATE_TYPE_RINGER;

import org.mrstefano.mram.model.SoundProfile;
import org.mrstefano.mram.model.StreamSettings;
import org.mrstefano.mram.model.StreamSettings.Type;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;

public class AudioManager {

	private android.media.AudioManager audioManager;
	private Context context;

	public AudioManager(Context context) {
		super();
		this.context = context;
		audioManager = (android.media.AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	}

	public void applyProfile(SoundProfile profile) {
		int ringerMode = profile.getRingerMode();
		audioManager.setRingerMode(ringerMode);
		Type[] types = Type.values();
		for (Type streamType : types) {
			StreamSettings streamSettings = profile.getStreamSettings(streamType);
			if ( streamSettings != null ) {
				applyStreamSetting(streamType, streamSettings, 0);
			}
		}
    	applyHapticFeedback(profile.haptickFeedbackEnabled);
    }

	protected void applyHapticFeedback(boolean enabled) {
		System.putInt(context.getContentResolver(), System.HAPTIC_FEEDBACK_ENABLED, enabled ? 1: 0);
	}
	
	public SoundProfile extractProfileFromCurrentSystemSettings() {
		SoundProfile profile = new SoundProfile();
		Type[] types = StreamSettings.Type.values();
		for (Type streamType : types) {
			int audioManagerStreamType = getAudioManagerStreamType(streamType);
			int maxVol = audioManager.getStreamMaxVolume(audioManagerStreamType);
			int vol = audioManager.getStreamVolume(audioManagerStreamType);
			int volPercent = ( (Double) (Math.floor(vol * 100 / maxVol)) ).intValue();
			int vibrateSettings;
			Uri ringtoneUri;
			switch ( streamType ) {
			case RINGER:
				vibrateSettings = audioManager.getVibrateSetting(VIBRATE_TYPE_RINGER);
				ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
				break;
			case NOTIFICATION:
				vibrateSettings = audioManager.getVibrateSetting(VIBRATE_TYPE_NOTIFICATION);
				ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
				break;
			case ALARM:
				ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM);
				vibrateSettings = VIBRATE_SETTING_OFF;
				break;
			default:
				ringtoneUri = null;
				vibrateSettings = VIBRATE_SETTING_OFF;
			}
			boolean vibrate = vibrateSettings == VIBRATE_SETTING_ON;
			StreamSettings streamSettings = new StreamSettings(volPercent, vibrate, ringtoneUri);
			profile.putStreamSetting(streamType, streamSettings);
		}
		profile.haptickFeedbackEnabled = isHapticFeedbackEnabled();
		return profile;
	}
	
	private void applyStreamSetting(Type streamType, StreamSettings streamSettings, int flags) {
		int audioManagerStreamType = getAudioManagerStreamType(streamType);
		int maxVolume = audioManager.getStreamMaxVolume(audioManagerStreamType);
		double vol = Math.ceil(maxVolume * streamSettings.volume / 100);
		audioManager.setStreamVolume(audioManagerStreamType, Double.valueOf(vol).intValue(), flags);
		Uri ringtoneUri = parseUri(streamSettings.ringtoneUri);
		switch ( streamType ) {
		case RINGER:
			toggleVibrate(VIBRATE_TYPE_RINGER, streamSettings.vibrate);
			applyRingtone(RingtoneManager.TYPE_RINGTONE, ringtoneUri);
			break;
		case NOTIFICATION:
			toggleVibrate(VIBRATE_TYPE_NOTIFICATION, streamSettings.vibrate);
			applyRingtone(RingtoneManager.TYPE_NOTIFICATION, ringtoneUri);
			break;
		case ALARM:
			applyRingtone(RingtoneManager.TYPE_ALARM, ringtoneUri);
			break;
		default:
		}
	}
	
	protected int getAudioManagerStreamType(Type streamType) {
		switch(streamType) {
		case RINGER:
			return STREAM_RING;
		case NOTIFICATION:
			return STREAM_NOTIFICATION;
		case ALARM:
			return STREAM_ALARM;
		case MUSIC:
			return STREAM_MUSIC;
		case SYSTEM:
			return STREAM_SYSTEM;
		case VOICE_CALL:
			return STREAM_VOICE_CALL;
		default:
			return -1;
		}
	}
    
	protected boolean isHapticFeedbackEnabled() {
		int hapticFeedbackEnabledInt;
		try {
			hapticFeedbackEnabledInt = System.getInt(context.getContentResolver(), System.HAPTIC_FEEDBACK_ENABLED);
			return hapticFeedbackEnabledInt == 1;
		} catch (SettingNotFoundException e) {
			return false;
		}
	}
	
    private void applyRingtone(int type, Uri uri) {
		if ( uri != null ) {
			RingtoneManager.setActualDefaultRingtoneUri(context, type, uri);
		}
	}

	private void toggleVibrate(int vibrateType, boolean on) {
		audioManager.setVibrateSetting(vibrateType, on ? VIBRATE_SETTING_ON: VIBRATE_SETTING_OFF);
    }
    
    private Uri parseUri(String uri) {
		Uri parsedUri = uri == null ? null: Uri.parse(uri);
		return parsedUri;
    }
}
