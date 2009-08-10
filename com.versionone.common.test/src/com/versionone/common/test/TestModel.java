package com.versionone.common.test;

import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.versionone.apiclient.APIException;
import com.versionone.apiclient.FileAPIConnector;
import com.versionone.apiclient.Localizer;
import com.versionone.apiclient.MetaModel;
import com.versionone.apiclient.Services;
import com.versionone.common.preferences.PreferenceConstants;
import com.versionone.common.preferences.PreferencePage;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.Workitem;


public class TestModel {

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestModel.class);
		}

	/**
	 * Configuration Parameters
	 */
	static final String SERVER_URL = "http://localhost/V1_71";
	static final String USER_ID = "andre";
	static final String USER_PASSWORD = "andre";
	static final String USER_MEMBER_ID = "Member:1000";
	static final String SCOPE_ID = "Scope:1012";
	static final boolean VALIDATION_REQUIRED = false;
	static final boolean TRACKING = false;
	private static final double EPSILON = 0.005;
	private static ApiDataLayer datalayer = null;
	
	
	@BeforeClass
	public static void loadTestData() {
		FileAPIConnector metaConnector = new FileAPIConnector("testdata/TestMetaData.xml", "meta.v1/");
		FileAPIConnector dataConnector = new FileAPIConnector("testdata/TestData.xml", "rest-1.v1/");
		FileAPIConnector localizeConnector = new FileAPIConnector("testdata/TestLocalizeData.xml", "loc.v1/");
		MetaModel metaModel = new MetaModel(metaConnector);
		Services services = new Services(metaModel, dataConnector);	
		Localizer localizer = new Localizer(localizeConnector);
		datalayer = ApiDataLayer.getInitializedInstance(services, metaModel, localizer);
	}

	@Before
	public void setUp() throws Exception {
		PreferencePage preference = new PreferencePage();
		if(!preference.getPreferenceStore().getBoolean(PreferenceConstants.P_ENABLED)) {
			enableView(preference.getPreferenceStore());
		}
	}
	
	private void enableView(IPreferenceStore config) {
		config.setValue(PreferenceConstants.P_URL, SERVER_URL);
		config.setValue(PreferenceConstants.P_USER, USER_ID);
		config.setValue(PreferenceConstants.P_PASSWORD, USER_PASSWORD);
		config.setValue(PreferenceConstants.P_MEMBER_TOKEN, USER_MEMBER_ID);
		config.setValue(PreferenceConstants.P_REQUIRESVALIDATION, VALIDATION_REQUIRED);
		//config.setValue(PreferenceConstants.P_TRACK_EFFORT, TRACKING);
		config.setValue(PreferenceConstants.P_PROJECT_TOKEN, SCOPE_ID);
		config.setValue(PreferenceConstants.P_ENABLED, true);	
	}
	
	@Test
	public void testGetServerInstance() {
		Assert.assertNotNull(datalayer.getInstance());
	}
	
	@Test
	public void testGetProject() throws Exception {
		List<Workitem> projectNode = datalayer.getProjectTree();
		Assert.assertNotNull(projectNode);
		List<Workitem> children = projectNode.get(0).children;
		Assert.assertEquals(1, children.size());
		Workitem companyNode = children.get(0);
		Assert.assertEquals("Company", companyNode.getProperty(Workitem.NameProperty));
		//Assert.assertEquals("Scope:1011", companyNode.getToken());
		Assert.assertEquals(3, companyNode.children.size());
	}
//
//	@Test
//	public void testGetTask() throws Exception {
//		Task[] allTask = V1Server.getInstance().getTasks();
//		Assert.assertEquals(2, allTask.length);
//		validateTask(allTask[0], "TK-01061", "Add Shipping Notes", "Service Changes", 24, "30.0", 0, 10, "TaskStatus:123");
//		validateTask(allTask[1], "TK-01068", "View Daily Call Count", "Service Changes", 24, "", 0, 24, "TaskStatus:123");
//	}
//	
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
//	@Test
//	public void testSetTaskValues() throws Exception {
//		Task[] allTask = V1Server.getInstance().getTasks();
//		Assert.assertEquals(2, allTask.length);
//		Task testMe = allTask[0];
//		validateSetEffort(testMe);
//		validateSetEstimate(testMe);
//		validateSetName(testMe);
//		validatSetStatus(testMe);
//		validateSetToDo(testMe);		
//	}
//		
//	/**
//	 * ToDo cannot be negative
//	 * @param testMe
//	 */
//	private void validateSetToDo(Task testMe) throws Exception {
//		
//		testMe.setToDo(0);
//		Assert.assertEquals(0, testMe.getToDo(), EPSILON);
//		
//		testMe.setToDo(10);
//		Assert.assertEquals(10, testMe.getToDo(), EPSILON);
//		
//		try {
//			testMe.setToDo(-1);
//			Assert.fail("ToDo cannot be negative");
//		}
//		catch(IllegalArgumentException e) {}
//		Assert.assertEquals(10, testMe.getToDo(), EPSILON);
//	}
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
//	/**
//	 * Estimate must be a positive value
//	 * @param testMe
//	 */
//	private void validateSetEstimate(Task testMe) throws Exception {
//	
//		testMe.setEstimate(0);
//		Assert.assertEquals(0, testMe.getEstimate(), EPSILON);
//		
//		testMe.setEstimate(10);
//		Assert.assertEquals(10, testMe.getEstimate(), EPSILON);
//		
//		try {
//			testMe.setEstimate(-1);
//			Assert.fail("Estimate cannot be negative");
//		}
//		catch(IllegalArgumentException e) {}
//		
//		Assert.assertEquals(10, testMe.getEstimate(), EPSILON);
//	}
//
//	/**
//	 * Effort is allowed to accept any float value
//	 * @param testMe
//	 * @throws Exception
//	 */
//	private void validateSetEffort(Task testMe) throws Exception {
//		testMe.setEffort(0);
//		Assert.assertEquals(0, testMe.getEffort(), EPSILON);
//		
//		testMe.setEffort(10);
//		Assert.assertEquals(10, testMe.getEffort(), EPSILON);
//
//		testMe.setEffort(-1);
//		Assert.assertEquals(-1, testMe.getEffort(), EPSILON);
//	}
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
//	private void validateTask(Task task, 
//			String expectedId, 
//			String expectedStory, 
//			String expectedName, 
//			float expectedEstimate, 
//			String expectedDone, 
//			float expectedEffort, 
//			float expectedTodo,
//			String expectedStatus) throws Exception {
//		Assert.assertEquals(expectedName, task.getName());
//		Assert.assertEquals(expectedId, task.getID());
//		Assert.assertEquals(expectedStory, task.getStoryName());
//		Assert.assertEquals(expectedEstimate, task.getEstimate(), EPSILON);
//		Assert.assertEquals(expectedDone, task.getDone());
//		Assert.assertEquals(expectedEffort, task.getEffort(), EPSILON);
//		Assert.assertEquals(expectedTodo, task.getToDo(), EPSILON);
//		Assert.assertEquals(expectedStatus, task.getStatus());
//	}

}
