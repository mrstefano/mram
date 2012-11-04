package org.mrstefano.mram.manager.xml;

import java.util.ArrayList;
import java.util.List;

import static org.mrstefano.mram.model.Constants.*;

import org.mrstefano.mram.model.SoundProfile;
import org.mrstefano.mram.model.SoundProfilesData;
import org.mrstefano.mram.model.StreamSettings;
import org.mrstefano.mram.model.StreamSettings.Type;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DataHandler extends DefaultHandler {

	private StringBuffer buffer = new StringBuffer();
	private SoundProfilesData data;
	private List<SoundProfile> profiles;
	private SoundProfile profile;
	private StreamSettings streamSettings;
	private Type streamType;

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		buffer.setLength(0);
		if (localName.equals(DATA_EL)) {
			data = new SoundProfilesData();
			//String version = atts.getValue(VERSION_ATTR);
			//todo version manage
		} else if (localName.equals(PROFILES_EL)) {
			profiles = new ArrayList<SoundProfile>();
		} else if (localName.equals(PROFILE_EL)) {
			profile = new SoundProfile();
		} else if (localName.equals(STREAM_SETTINGS_EL)) {
			streamSettings = new StreamSettings();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException {
		if (localName.equals(PROFILES_EL)) {
			data.setProfiles(profiles);
		} else if (localName.equals(SELECTED_PROFILE_EL)) {
			data.setSelectedProfileIndex(parseIntegerValue());
		} else if (localName.equals(PROFILE_EL)) {
			profiles.add(profile);
		} else if (localName.equals(NAME_EL)) {
			profile.name = buffer.toString();
		} else if (localName.equals(ICON_EL)) {
			profile.icon = buffer.toString();
		} else if (localName.equals(HAPTIC_FEEDBACK_ENABLED_EL)) {
			profile.haptickFeedbackEnabled = parseBooleanValue();
		} else if (localName.equals(STREAM_SETTINGS_EL)) {
			profile.putStreamSetting(streamType, streamSettings);
		} else if (localName.equals(STREAM_TYPE_EL)) {
			Integer streamTypeCode = parseIntegerValue();
			streamType = Type.valueOf(streamTypeCode);
		} else if (localName.equals(VOLUME_EL)) {
			streamSettings.volume = parseIntegerValue();
		} else if (localName.equals(VIBRATE_EL)) {
			streamSettings.vibrate = parseBooleanValue();
		}
	}

	protected boolean parseBooleanValue() {
		return Boolean.parseBoolean(buffer.toString());
	}

	private Integer parseIntegerValue() {
		String val = buffer.toString();
		return val != null && ! val.equals("") ? Integer.parseInt(val): null;
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		buffer.append(ch, start, length);
	}

	public SoundProfilesData retrieveData() {
		return data;
	}

}
