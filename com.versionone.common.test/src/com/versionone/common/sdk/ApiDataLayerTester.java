package com.versionone.common.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

public class ApiDataLayerTester {
    
    private static final String V1_PATH = "http://integsrv01/VersionOne/";
    private static final String V1_USER = "admin";
    private static final String V1_PASSWORD = "admin";

    @Ignore("This test is integrational. It works with V1 server.")
    @Test
    public void ApiDataLayerTest() throws DataLayerException {
        final ApiDataLayer data = ApiDataLayer.getInstance();
        data.addProperty("Name", "Test", false);
        data.addProperty("Owners", "Test", true);
        data.connect(V1_PATH, V1_USER, V1_PASSWORD, false);
        final Workitem test = data.createWorkitem(Workitem.TEST_PREFIX, null);
        assertEquals(null, test.parent);
        assertEquals(0, test.children.size());
        assertFalse(test.canQuickClose());
        assertFalse(test.canSignup());
        try {
            test.close();
            fail();
        } catch (UnsupportedOperationException e) {
            // Do nothing
        }
        try {
            test.quickClose();
            fail();
        } catch (UnsupportedOperationException e) {
            // Do nothing
        }
        try {
            test.signup();
            fail();
        } catch (UnsupportedOperationException e) {
            // Do nothing
        }
        try {
            test.revertChanges();
            fail();
        } catch (UnsupportedOperationException e) {
            // Do nothing
        }
        assertEquals("NULL", test.getId());
        assertEquals(Workitem.TEST_PREFIX, test.getTypePrefix());
        assertTrue(test.hasChanges());
        assertFalse(test.isMine());
        assertEquals("", test.getPropertyAsString(Workitem.NAME_PROPERTY));
        test.setProperty(Workitem.NAME_PROPERTY, "NewName53765");
        assertEquals("NewName53765", test.getPropertyAsString(Workitem.NAME_PROPERTY));
    }
}
