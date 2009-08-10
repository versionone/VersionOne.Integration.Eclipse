package com.versionone.taskview.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.versionone.common.preferences.PreferenceConstants;
import com.versionone.common.preferences.PreferencePage;

/**
 * Test the preference store
 * Note: I'd like to make this test the actual preference control.  Any Ideas?
 * @author Jerry D. Odenwelder Jr.
 *
 */
public class TestTaskViewPreferences {

	private PreferencePage preference = new PreferencePage();
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testPreferencesExist() {	
		Assert.assertTrue(preference.getPreferenceStore().contains(PreferenceConstants.P_ENABLED));
		Assert.assertTrue(preference.getPreferenceStore().contains(PreferenceConstants.P_URL));
		Assert.assertTrue(preference.getPreferenceStore().contains(PreferenceConstants.P_USER));
		Assert.assertTrue(preference.getPreferenceStore().contains(PreferenceConstants.P_PASSWORD));
		Assert.assertTrue(preference.getPreferenceStore().contains(PreferenceConstants.P_INTEGRATED_AUTH));
		Assert.assertTrue(preference.getPreferenceStore().contains(PreferenceConstants.P_REQUIRESVALIDATION));
		Assert.assertTrue(preference.getPreferenceStore().contains(PreferenceConstants.P_MEMBER_TOKEN));
		Assert.assertTrue(preference.getPreferenceStore().contains(PreferenceConstants.P_PROJECT_TOKEN));
	}	
}
