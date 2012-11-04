package org.mrstefano.mram.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoundProfilesData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<SoundProfile> profiles;

	private Integer selectedProfileIndex;

	public SoundProfilesData() {
		profiles = new ArrayList<SoundProfile>();
	}
	
	public int addProfile(SoundProfile profile) {
		int position = profiles.size();
		profiles.add(profile);
		return position;
	}
	
	public String[] getProfileNames() {
		int size = profiles.size();
		String[] profileNames = new String[size];
		for ( int i = 0; i < size; i ++ ) {
			SoundProfile profile = profiles.get(i);
			profileNames[i] = profile.name;
		}
		return profileNames;
	}

	public SoundProfile getSelectedProfile() {
		if (selectedProfileIndex != null ) {
			return profiles.get(selectedProfileIndex);
		} else {
			return null;
		}
	}

	public SoundProfile getProfile(int index) {
		return profiles.get(index);
	}

	public Integer getSelectedProfileIndex() {
		return selectedProfileIndex;
	}

	public void setSelectedProfileIndex(Integer selectedProfileIndex) {
		this.selectedProfileIndex = selectedProfileIndex;
	}

	public List<SoundProfile> getProfiles() {
		return Collections.unmodifiableList(profiles);
	}

	public void setProfiles(List<SoundProfile> profiles) {
		this.profiles = profiles;
	}

	public void setProfile(Integer index, SoundProfile profile) {
		this.profiles.set(index, profile);
	}

	public void removeProfile(int index) {
		this.profiles.remove(index);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((profiles == null) ? 0 : profiles.hashCode());
		result = prime
				* result
				+ ((selectedProfileIndex == null) ? 0 : selectedProfileIndex
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SoundProfilesData other = (SoundProfilesData) obj;
		if (profiles == null) {
			if (other.profiles != null)
				return false;
		} else if (!profiles.equals(other.profiles))
			return false;
		if (selectedProfileIndex == null) {
			if (other.selectedProfileIndex != null)
				return false;
		} else if (!selectedProfileIndex.equals(other.selectedProfileIndex))
			return false;
		return true;
	}
	
	

}
