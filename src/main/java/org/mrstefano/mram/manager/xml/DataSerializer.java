package org.mrstefano.mram.manager.xml;

import java.io.IOException;

import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import static org.mrstefano.mram.model.Constants.*;

import org.mrstefano.mram.model.SoundProfile;
import org.mrstefano.mram.model.SoundProfilesData;
import org.mrstefano.mram.model.StreamSettings;
import org.mrstefano.mram.model.StreamSettings.Type;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class DataSerializer {

	public void serialize(OutputStream os, SoundProfilesData data) throws IOException {
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(os, UTF_8);
		serializer.startDocument(UTF_8, true);
		serializer.startTag(null, DATA_EL);
		serializer.attribute(null, VERSION_ATTR, VERSION);
		valueToElement(serializer, SELECTED_PROFILE_EL, data.getSelectedProfileIndex());
		serializer.startTag(null, PROFILES_EL);
		List<SoundProfile> profiles = data.getProfiles();
		if ( profiles != null ) {
			for (SoundProfile sp : profiles) {
				serialize(serializer, sp);
			}
		}
		serializer.endTag(null, PROFILES_EL);
		serializer.endTag(null, DATA_EL);
		serializer.endDocument();
		serializer.flush();
	}

	private void serialize(XmlSerializer serializer, SoundProfile sp)
			throws IOException {
		serializer.startTag(null, PROFILE_EL);
		valueToElement(serializer, NAME_EL, sp.name);
		valueToElement(serializer, ICON_EL, sp.icon);
		valueToElement(serializer, HAPTIC_FEEDBACK_ENABLED_EL, sp.haptickFeedbackEnabled);
		Set<Type> streamTypes = sp.getStreamTypes();
		for (Type streamType : streamTypes) {
			StreamSettings streamSettings = sp.getStreamSettings(streamType);
			serialize(serializer, streamType, streamSettings);
		}
		serializer.endTag(null, PROFILE_EL);
	}

	private void serialize(XmlSerializer serializer, Type streamType,
			StreamSettings streamSettings) throws IOException {
		serializer.startTag(null, STREAM_SETTINGS_EL);
		valueToElement(serializer, STREAM_TYPE_EL, streamType.getCode());
		valueToElement(serializer, VOLUME_EL, streamSettings.volume);
		valueToElement(serializer, VIBRATE_EL, streamSettings.vibrate);
		serializer.endTag(null, STREAM_SETTINGS_EL);
	}
	
	private void valueToElement(XmlSerializer serializer, String tagName, Object value) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag(null, tagName);
		if ( value != null ) {
			serializer.text(value.toString());
		}
		serializer.endTag(null, tagName);
	}
	
}