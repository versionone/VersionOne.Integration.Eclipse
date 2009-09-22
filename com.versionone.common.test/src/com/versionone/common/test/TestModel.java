package com.versionone.common.test;

import java.util.Arrays;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.versionone.apiclient.FileAPIConnector;
import com.versionone.apiclient.Localizer;
import com.versionone.apiclient.MetaModel;
import com.versionone.apiclient.Services;
import com.versionone.apiclient.IV1Configuration.TrackingLevel;
import com.versionone.common.preferences.PreferenceConstants;
import com.versionone.common.preferences.PreferencePage;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.EffortTrackingLevel;
import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.ValueId;
import com.versionone.common.sdk.Workitem;

public class TestModel {

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(TestModel.class);
    }

    /**
     * Configuration Parameters
     */
    private static final String SERVER_URL = "http://localhost/V1_71";
    private static final String USER_ID = "andre";
    private static final String USER_PASSWORD = "andre";
    private static final String USER_MEMBER_ID = "Member:1000";
    private static final String SCOPE_ID = "Scope:1012";
    private static final boolean VALIDATION_REQUIRED = false;
    private static final boolean TRACKING = false;
    private static ApiDataLayer datalayer;

    private static void loadTestData() throws Exception {
        FileAPIConnector metaConnector = new FileAPIConnector("testdata/TestMetaData.xml", "meta.v1/");
        FileAPIConnector dataConnector = new FileAPIConnector("testdata/TestData.xml", "rest-1.v1/");
        FileAPIConnector localizeConnector = new FileAPIConnector("testdata/TestLocalizeData.xml", "loc.v1/");
        MetaModel metaModel = new MetaModel(metaConnector);
        Services services = new Services(metaModel, dataConnector);
        Localizer localizer = new Localizer(localizeConnector);
        datalayer = ApiDataLayer.getInstance();

        final String[] all = {Workitem.STORY_PREFIX, Workitem.DEFECT_PREFIX, Workitem.TASK_PREFIX, Workitem.TEST_PREFIX};
        setDataLayerAttribute(Workitem.STATUS_PROPERTY, true, all);
        setDataLayerAttribute(Workitem.OWNERS_PROPERTY, true, all);
        datalayer.connectFotTesting(services, metaModel, localizer, TrackingLevel.On, TrackingLevel.Off);
    }

    private static void setDataLayerAttribute(String attribute, boolean isListType, String... typePrefixes) {
        for (String prefix : typePrefixes) {
            datalayer.addProperty(attribute, prefix, isListType);
        }
    }
    
        
    @BeforeClass
    public static void setUp() throws Exception {
        loadTestData();
        PreferencePage preference = new PreferencePage();
        if (!preference.getPreferenceStore().getBoolean(PreferenceConstants.P_ENABLED)) {
            enableView(preference.getPreferenceStore());
        }
    }

    private static void enableView(IPreferenceStore config) {
        config.setValue(PreferenceConstants.P_URL, SERVER_URL);
        config.setValue(PreferenceConstants.P_USER, USER_ID);
        config.setValue(PreferenceConstants.P_PASSWORD, USER_PASSWORD);
        config.setValue(PreferenceConstants.P_MEMBER_TOKEN, USER_MEMBER_ID);
        config.setValue(PreferenceConstants.P_REQUIRESVALIDATION, VALIDATION_REQUIRED);
        config.setValue(PreferenceConstants.P_PROJECT_TOKEN, SCOPE_ID);
        config.setValue(PreferenceConstants.P_ENABLED, true);
    }

    @Test
    public void testGetServerInstance() {
        Assert.assertNotNull(ApiDataLayer.getInstance());
    }
	
    @Test
    public void testGetProject() throws Exception {
        List<Workitem> projectNode = datalayer.getProjectTree();
        Assert.assertNotNull(projectNode);
        List<Workitem> children = projectNode.get(0).children;
        Assert.assertEquals(1, projectNode.size());
        Workitem companyNode = projectNode.get(0);
        Assert.assertEquals("Company", companyNode.getProperty(Workitem.NAME_PROPERTY));
        
        Assert.assertEquals(3, companyNode.children.size());
        companyNode = children.get(0);
        Assert.assertEquals("Call Center", companyNode.getProperty(Workitem.NAME_PROPERTY));
    }

    @Test
    public void testGetUsersTask() throws Exception {
        datalayer.showAllTasks = false;
        Workitem[] allWorkItem = datalayer.getWorkitemTree();
        Assert.assertEquals(7, allWorkItem.length);
        /*
         * Workitem task, String expectedId, String expectedName, String
         * expectedEstimate, String expectedDone, String expectedEffort, String
         * expectedTodo, String expectedStatus
         */
        validateTask(allWorkItem[0], "B-01190", "Story:2265", "FAST LAND 1", null, null, null, null, "Done",
                Workitem.STORY_PREFIX);
        validateTask(allWorkItem[2], "D-01093", "Defect:2248", "defect 1", "0,02", "-2,00", null, "0,01", "Done",
                Workitem.DEFECT_PREFIX);
        validateTask(allWorkItem[1].children.get(0), "AT-01010", "Test:2273", "AT4", "13,00", null, null, "18,00",
                "", Workitem.TEST_PREFIX);
        validateTask(allWorkItem[0].children.get(1), "TK-01031", "Task:2269", "task2", "9,30", "5,00", null, "9,30",
                "In Progress", Workitem.TASK_PREFIX);
    }
    
    @Test
    public void testGetAllTasks() throws Exception {
        datalayer.showAllTasks = true;
        Workitem[] allWorkItem = datalayer.getWorkitemTree();
        Assert.assertEquals(11, allWorkItem.length);
        /*
         * Workitem task, String expectedId, String expectedName, String
         * expectedEstimate, String expectedDone, String expectedEffort, String
         * expectedTodo, String expectedStatus
         */
        validateTask(allWorkItem[1].children.get(1), "TK-01030", "Task:2268", "task1", "10,00", "5,00", null, "0,00", "Completed",
                Workitem.TASK_PREFIX);

        validateTask(allWorkItem[6], "D-01093", "Defect:2248", "defect 1", "0,02", "-2,00", null, "0,01", "Done",
                Workitem.DEFECT_PREFIX);

        validateTask(allWorkItem[5].children.get(1), "AT-01008", "Test:2244", "test1", "0,00", "35,00", null, "0,00",
                "Passed", Workitem.TEST_PREFIX);
    }
	
//	@Test
//	public void testGetStatusCodes() throws Exception {
//		IStatusCodes statusCodes = V1Server.getInstance().getTaskStatusValues();
//		Assert.assertNotNull(statusCodes);
//		validateGetDisplayValues(statusCodes);
//		validateGetID(statusCodes);
//		validateGetDisplayValue(statusCodes);
//		validateGetDisplayFromOid(statusCodes);
//		validateGetOidIndex(statusCodes);
//	}
//
    @Test
    public void testSetTaskValues() throws Exception {
        Workitem[] allWorkItem = datalayer.getWorkitemTree();
        // Assert.assertEquals(2, allWorkItem.length);
        Workitem testMe = allWorkItem[0];
        validateSetEstimate(testMe);
        // validateSetName(testMe);
        // validatSetStatus(testMe);
        validateSetToDo(testMe);
        validateDescription(testMe);
        validateSetOwner(testMe);
        validateSetOwner1(allWorkItem[1]);
    }	

    private void validateDescription(Workitem testMe) {
        testMe.setProperty(Workitem.DESCRIPTION_PROPERTY, "<br>test <b>new test</b>");
        Assert.assertEquals("<br>test <b>new test</b>", testMe.getProperty(Workitem.DESCRIPTION_PROPERTY));

        testMe.setProperty(Workitem.DESCRIPTION_PROPERTY, "--");
        Assert.assertEquals("--", testMe.getProperty(Workitem.DESCRIPTION_PROPERTY));

        //testMe.setProperty(Workitem.DescriptionProperty, "");
        //Assert.assertEquals("", testMe.getProperty(Workitem.DescriptionProperty));
    }

    /**
     * ToDo cannot be negative
     * 
     * @param testMe
     */
    private void validateSetToDo(Workitem testMe) throws Exception {

        testMe.setProperty(Workitem.TODO_PROPERTY, "0");
        Assert.assertEquals("0,00", testMe.getProperty(Workitem.TODO_PROPERTY));

        testMe.setProperty(Workitem.TODO_PROPERTY, "10,125");
        Assert.assertEquals("10,125", testMe.getProperty(Workitem.TODO_PROPERTY));

        testMe.setProperty(Workitem.TODO_PROPERTY, "10,01");
        Assert.assertEquals("10,01", testMe.getProperty(Workitem.TODO_PROPERTY));

        testMe.setProperty(Workitem.TODO_PROPERTY, "-1");
        Assert.assertEquals("10,01", testMe.getProperty(Workitem.TODO_PROPERTY));
    }

//
//	/**
//	 * SetStatus must accept an TaskStatus OID
//	 * @param testMe
//	 * @throws Exception
//	 */
//	private void validatSetStatus(Task testMe) throws Exception {
//		
//		testMe.setStatus("TaskStatus:126");
//		Assert.assertEquals("TaskStatus:126", testMe.getStatus());
//
//		try {
//			testMe.setStatus("On Hold");
//			Assert.fail("Expected APIException");
//		}
//		catch(APIException e) {
//			Assert.assertEquals("Error converting data", e.getMessage());
//		}
//		Assert.assertEquals("TaskStatus:126", testMe.getStatus());
//		
//		try {
//			testMe.setStatus("Task:123");
//			Assert.fail("Expected APIException");
//		}
//		catch(APIException e) {
//			Assert.assertEquals("Error converting data", e.getMessage());
//		}
//		Assert.assertEquals("TaskStatus:126", testMe.getStatus());
//		
//		
//	}
//
//	private void validateSetName(Task testMe) throws Exception {
//		testMe.setName("New Name");
//		Assert.assertEquals("New Name", testMe.getName());
//	}
//
    /**
     * Estimate must be a positive value
     * 
     * @param testMe
     */
    private void validateSetEstimate(Workitem testMe) throws Exception {

        testMe.setProperty(Workitem.DETAIL_ESTIMATE_PROPERTY, "0");
        Assert.assertEquals("0,00", testMe.getPropertyAsString(Workitem.DETAIL_ESTIMATE_PROPERTY));

        testMe.setProperty(Workitem.DETAIL_ESTIMATE_PROPERTY, "10");
        Assert.assertEquals("10,00", testMe.getPropertyAsString(Workitem.DETAIL_ESTIMATE_PROPERTY));

        testMe.setProperty(Workitem.DETAIL_ESTIMATE_PROPERTY, "-1");
        Assert.assertEquals("10,00", testMe.getPropertyAsString(Workitem.DETAIL_ESTIMATE_PROPERTY));
    }

    /**
     * Estimate must be a positive value
     * 
     * @param testMe
     */
    private void validateSetOwner(Workitem testMe) throws Exception {
        //TODO 
        PropertyValues owners = (PropertyValues) testMe.getProperty(Workitem.OWNERS_PROPERTY);
        Assert.assertEquals("Cat", owners.toString());
        Assert.assertEquals(1, owners.size());
        Assert.assertEquals("Cat", testMe.getPropertyAsString(Workitem.OWNERS_PROPERTY));
        final ValueId cat = owners.getValueIdByIndex(0);
        Assert.assertEquals("Cat", cat.toString());
        PropertyValues users = datalayer.getListPropertyValues(testMe.getTypePrefix(), Workitem.OWNERS_PROPERTY);
        ValueId admin = users.getValueIdByIndex(1);
        Assert.assertEquals("Administrator", admin.toString());
        owners = new PropertyValues(Arrays.asList(admin));
        testMe.setProperty(Workitem.OWNERS_PROPERTY, owners);
        Assert.assertEquals("Administrator", testMe.getPropertyAsString(Workitem.OWNERS_PROPERTY));
        owners = new PropertyValues();
        testMe.setProperty(Workitem.OWNERS_PROPERTY, owners);
        Assert.assertEquals("", testMe.getPropertyAsString(Workitem.OWNERS_PROPERTY));
        owners = new PropertyValues(Arrays.asList(admin, cat));
        testMe.setProperty(Workitem.OWNERS_PROPERTY, owners);
        Assert.assertEquals("Administrator, Cat", testMe.getPropertyAsString(Workitem.OWNERS_PROPERTY));
    }
    

    private void validateSetOwner1(Workitem testMe) {
        PropertyValues owners = (PropertyValues) testMe.getProperty(Workitem.OWNERS_PROPERTY);
        final ValueId adminForRemove = owners.getValueIdByIndex(0);
        owners.remove(adminForRemove);        
        PropertyValues users = datalayer.getListPropertyValues(testMe.getTypePrefix(), Workitem.OWNERS_PROPERTY);
        ValueId petja = users.getValueIdByIndex(8);
        Assert.assertEquals("Petja", petja.toString());
        owners.add(petja);
        Assert.assertEquals(3, owners.size());
        testMe.setProperty(Workitem.OWNERS_PROPERTY, owners);
        Assert.assertEquals("Petja, Bil, Tom", testMe.getPropertyAsString(Workitem.OWNERS_PROPERTY));        
    }


    /**
     * Effort is allowed to accept doubles and strings default locale encoded.
     */
    @Test
    public void testTrackingLevel() throws Exception {
        EffortTrackingLevel tracking = datalayer.trackingLevel;
        Workitem defect0 = datalayer.getWorkitemTree()[0];
        Workitem story1 = datalayer.getWorkitemTree()[1];
        Workitem s1Test0 = story1.children.get(0);
        Workitem s1Task1 = story1.children.get(1);
        Workitem defect9 = datalayer.getWorkitemTree()[9];
        Workitem d9Task0 = defect9.children.get(0);
        Workitem d9Test2 = defect9.children.get(2);
        
        Assert.assertFalse(tracking.isTracking(defect0));
        Assert.assertTrue(tracking.isTracking(story1));
        Assert.assertFalse(tracking.isTracking(s1Test0));
        Assert.assertFalse(tracking.isTracking(s1Task1));
        Assert.assertTrue(tracking.isTracking(d9Task0));
        Assert.assertTrue(tracking.isTracking(d9Test2));
    }

    @Test
    public void testSetEffort() throws Exception {
        Workitem story0 = datalayer.getWorkitemTree()[1];
        
        story0.setProperty(Workitem.EFFORT_PROPERTY, 0);
        Assert.assertEquals(null, story0.getProperty(Workitem.EFFORT_PROPERTY));

        story0.setProperty(Workitem.EFFORT_PROPERTY, 10.125);
        Assert.assertEquals("10,125", story0.getProperty(Workitem.EFFORT_PROPERTY));

        story0.setProperty(Workitem.EFFORT_PROPERTY, "20");
        Assert.assertEquals("20,00", story0.getProperty(Workitem.EFFORT_PROPERTY));

        story0.setProperty(Workitem.EFFORT_PROPERTY, "-1");
        Assert.assertEquals("-1,00", story0.getProperty(Workitem.EFFORT_PROPERTY));
    }
//	
//	
//	
//
//	private void validateGetOidIndex(IStatusCodes statusCodes) {
//		Assert.assertEquals(0, statusCodes.getOidIndex(""));
//		Assert.assertEquals(1, statusCodes.getOidIndex("TaskStatus:123"));
//		Assert.assertEquals(2, statusCodes.getOidIndex("TaskStatus:125"));
//		Assert.assertEquals(3, statusCodes.getOidIndex("TaskStatus:126"));		
//	}
//
//	private void validateGetDisplayFromOid(IStatusCodes statusCodes) {
//		Assert.assertEquals("*** Invalid OID ***", statusCodes.getDisplayFromOid(""));
//		Assert.assertEquals("", statusCodes.getDisplayFromOid("NULL"));
//		Assert.assertEquals("In Progress", statusCodes.getDisplayFromOid("TaskStatus:123"));
//		Assert.assertEquals("Completed", statusCodes.getDisplayFromOid("TaskStatus:125"));
//		Assert.assertEquals("On Hold", statusCodes.getDisplayFromOid("TaskStatus:126"));		
//	}
//
//	private void validateGetDisplayValue(IStatusCodes statusCodes) {
//		Assert.assertEquals("", statusCodes.getDisplayValue(0));
//		Assert.assertEquals("In Progress", statusCodes.getDisplayValue(1));
//		Assert.assertEquals("Completed", statusCodes.getDisplayValue(2));
//		Assert.assertEquals("On Hold", statusCodes.getDisplayValue(3));
//	}
//
//	private void validateGetID(IStatusCodes statusCodes) {
//		Assert.assertEquals("NULL", statusCodes.getID(0));
//		Assert.assertEquals("TaskStatus:123", statusCodes.getID(1));
//		Assert.assertEquals("TaskStatus:125", statusCodes.getID(2));
//		Assert.assertEquals("TaskStatus:126", statusCodes.getID(3));
//		try {
//			statusCodes.getID(4);
//			Assert.fail("Expected IndexOutOfBoundsException");
//		}
//		catch(IndexOutOfBoundsException e){}
//	}
//
//	private void validateGetDisplayValues(IStatusCodes statusCodes) {
//		String[] displayNames = statusCodes.getDisplayValues();
//		Assert.assertEquals(4, displayNames.length);
//		Assert.assertEquals("", displayNames[0]);
//		Assert.assertEquals("In Progress", displayNames[1]);
//		Assert.assertEquals("Completed", displayNames[2]);
//		Assert.assertEquals("On Hold", displayNames[3]);
//	}
//
	private void validateTask(Workitem task, 
			String expectedNumber,
			String expectedId,
			String expectedName, 
			String expectedEstimate, 
			String expectedDone, 
			String expectedEffort, 
			String expectedTodo,
			String expectedStatus,
			String expectedType) throws Exception {
		Assert.assertEquals(expectedName, task.getProperty(Workitem.NAME_PROPERTY));
		Assert.assertEquals(expectedId, task.getId());
		Assert.assertEquals(expectedNumber, task.getProperty(Workitem.ID_PROPERTY));
		
		//Assert.assertEquals(expectedStory, task.getStoryName());
		Assert.assertEquals(expectedEstimate, task.getProperty(Workitem.DETAIL_ESTIMATE_PROPERTY));
		Assert.assertEquals(expectedDone, task.getProperty(Workitem.DONE_PROPERTY));
		Assert.assertEquals(expectedEffort, task.getProperty(Workitem.EFFORT_PROPERTY));
		Assert.assertEquals(expectedTodo, task.getProperty(Workitem.TODO_PROPERTY));
		Assert.assertEquals(expectedStatus, ((ValueId)task.getProperty(Workitem.STATUS_PROPERTY)).toString());
		Assert.assertEquals(expectedType, task.getTypePrefix());
	}

}
