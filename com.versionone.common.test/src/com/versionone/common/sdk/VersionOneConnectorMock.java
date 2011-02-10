package com.versionone.common.sdk;

import com.versionone.apiclient.FileAPIConnector;
import com.versionone.apiclient.Localizer;
import com.versionone.apiclient.MetaModel;
import com.versionone.apiclient.Services;

public class VersionOneConnectorMock extends VersionOneConnector {
    
    @Override
    public void connect(ConnectionSettings settings) {
        FileAPIConnector metaConnector = new FileAPIConnector("testdata/TestMetaData.xml", "meta.v1/");
        FileAPIConnector dataConnector = new FileAPIConnector("testdata/TestData.xml", "rest-1.v1/");
        FileAPIConnector localizeConnector = new FileAPIConnector("testdata/TestLocalizeData.xml", "loc.v1/");
        
        metaModel = new MetaModel(metaConnector);
        services = new Services(metaModel, dataConnector);
        localizer = new Localizer(localizeConnector);
        requiredFieldsValidator = new RequiredFieldsValidator(metaModel, services);
    }

}
