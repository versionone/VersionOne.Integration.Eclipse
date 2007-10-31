package com.versionone.common.test;

import org.junit.BeforeClass;
import org.junit.Test;

import com.versionone.apiclient.FileAPIConnector;
import com.versionone.apiclient.Localizer;
import com.versionone.apiclient.MetaModel;
import com.versionone.apiclient.Services;
import com.versionone.common.sdk.V1Server;


public class TestModel {

	@BeforeClass
	public static void loadTestData() {
		FileAPIConnector metaConnector = new FileAPIConnector("testdata/TestMetaData.xml", "meta.v1/");
		FileAPIConnector dataConnector = new FileAPIConnector("testdata/TestData.xml", "rest-1.v1/");
		FileAPIConnector localizeConnector = new FileAPIConnector("testdata/TestLocalizeData.xml", "loc.v1/");
		MetaModel metaModel = new MetaModel(metaConnector);
		Services services = new Services(metaModel, dataConnector);	
		Localizer localizer = new Localizer(localizeConnector);
		V1Server.initialize(services, metaModel, localizer);
	}
	
	
	@Test
	public void testGetServerInstance() {
		
	}
}
