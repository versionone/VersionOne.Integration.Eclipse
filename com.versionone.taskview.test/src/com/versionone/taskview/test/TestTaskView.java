package com.versionone.taskview.test;

import org.eclipse.swt.widgets.TableColumn;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.versionone.apiclient.FileAPIConnector;
import com.versionone.apiclient.MetaModel;
import com.versionone.apiclient.Services;
import com.versionone.common.preferences.PreferenceConstants;
import com.versionone.common.preferences.PreferencePage;
import com.versionone.common.sdk.V1Server;
import com.versionone.taskview.views.TaskView;

public class TestTaskView {
	// View ID
	static final String VIEW_ID = "com.versionone.taskview.views.TaskView";

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
		
	// column indexes
	static final int ID_COLUMN_INDEX = 0;
	static final int STORY_COLUMN_INDEX = 1;
	static final int NAME_COLUMN_INDEX = 2;
	static final int ESTIMATE_COLUMN_INDEX = 3;
	static final int TODO_COLUMN_INDEX = 4;
	static final int STATUS_COLUMN_INDEX = 5;
	// index changes when effort tracking is enabled
	static final int DONE_COLUMN_INDEX = 4;
	static final int EFFORT_COLUMN_INDEX = 5;
	static final int TRACKED_TODO_COLUMN_INDEX = 6;
	static final int TRACKED_STATUS_COLUMN_INDEX = 7;
	
	// test target
	private TaskView testView = null;
	private PreferencePage preference = null;
	
	@BeforeClass
	public static void loadTestData() {
		FileAPIConnector metaConnector = new FileAPIConnector("testdata/TestMetaData.xml", "meta.v1/");
		FileAPIConnector dataConnector = new FileAPIConnector("testdata/TestData.xml", "rest-1.v1/");
		MetaModel metaModel = new MetaModel(metaConnector);
		Services services = new Services(metaModel, dataConnector);		
		V1Server.initialize(services, metaModel);
	}
	
	@Before
	public void setUp() throws Exception {
		waitForJobs();
		preference = new PreferencePage();
		if(!preference.getPreferenceStore().getBoolean(PreferenceConstants.P_ENABLED)) {
			enableView(preference.getPreferenceStore());
		}
		testView = (TaskView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(VIEW_ID);
		waitForJobs();
		delay(1000);		
	}
	
	private void enableView(IPreferenceStore config) {
		config.setValue(PreferenceConstants.P_URL, SERVER_URL);
		config.setValue(PreferenceConstants.P_USER, USER_ID);
		config.setValue(PreferenceConstants.P_PASSWORD, USER_PASSWORD);
		config.setValue(PreferenceConstants.P_MEMBER_TOKEN, USER_MEMBER_ID);
		config.setValue(PreferenceConstants.P_REQUIRESVALIDATION, VALIDATION_REQUIRED);
		config.setValue(PreferenceConstants.P_TRACK_EFFORT, TRACKING);
		config.setValue(PreferenceConstants.P_PROJECT_TOKEN, SCOPE_ID);
		config.setValue(PreferenceConstants.P_ENABLED, true);	
	}

	@After
	public void teardown() throws Exception {
		waitForJobs();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(testView);
	}

	@Test
	public void testValid() {
		Assert.assertNotNull(testView);
	}
	
	/**
	 * Test columns when effort tracking is enabled
	 */
	@Test
	public void testEffortEnabled() {
		enableEffortTracking();
		Table table = testView.getViewer().getTable();
		Assert.assertNotNull(table);
		Assert.assertEquals(8, table.getColumnCount());
		TableColumn[] columns = table.getColumns();
		Assert.assertEquals("Task Name", columns[NAME_COLUMN_INDEX].getText());
		Assert.assertEquals("ID", columns[ID_COLUMN_INDEX].getText());
		Assert.assertEquals("Story", columns[STORY_COLUMN_INDEX].getText());
		Assert.assertEquals("Detail Estimate", columns[ESTIMATE_COLUMN_INDEX].getText());
		Assert.assertEquals("Done", columns[DONE_COLUMN_INDEX].getText());
		Assert.assertEquals("Effort", columns[EFFORT_COLUMN_INDEX].getText());
		Assert.assertEquals("To Do", columns[TRACKED_TODO_COLUMN_INDEX].getText());
		Assert.assertEquals("Status", columns[TRACKED_STATUS_COLUMN_INDEX].getText());
	}
	
	private void enableEffortTracking() {
		if(!preference.getPreferenceStore().getBoolean(PreferenceConstants.P_TRACK_EFFORT)) {
			preference.getPreferenceStore().setValue(PreferenceConstants.P_TRACK_EFFORT, true);
			waitForJobs();
		}
	}
	
	/**
	 * Test columns when effort tracking is disabled
	 */	
	@Test
	public void testEffortDisabled() {
		disableEffortTracking();
		Table table = testView.getViewer().getTable();
		Assert.assertNotNull(table);
		Assert.assertEquals(6, table.getColumnCount());
		TableColumn[] columns = table.getColumns();
		Assert.assertEquals("Task Name", columns[NAME_COLUMN_INDEX].getText());
		Assert.assertEquals("ID", columns[ID_COLUMN_INDEX].getText());
		Assert.assertEquals("Story", columns[STORY_COLUMN_INDEX].getText());
		Assert.assertEquals("Detail Estimate", columns[ESTIMATE_COLUMN_INDEX].getText());
		Assert.assertEquals("To Do", columns[TODO_COLUMN_INDEX].getText());
		Assert.assertEquals("Status", columns[STATUS_COLUMN_INDEX].getText());
	}

	private void disableEffortTracking() {
		if(preference.getPreferenceStore().getBoolean(PreferenceConstants.P_TRACK_EFFORT)) {
			preference.getPreferenceStore().setValue(PreferenceConstants.P_TRACK_EFFORT, false);
			waitForJobs();
		}
	}

	/**
	 * Test row when all projects are selected
	 */	
	@Test
	public void testViewDataNoEffort() {
		disableEffortTracking();
		Table table = testView.getViewer().getTable();
		Assert.assertNotNull(table);
		TableItem[] rows = table.getItems();
		Assert.assertEquals(2, rows.length);

		validateRow(rows[0], "Add Shipping Notes", "Service Changes", "TK-01061", "24.0", "10.0", "In Progress");
		validateRow(rows[1], "View Daily Call Count", "Service Changes", "TK-01068", "24.0", "24.0", "In Progress");
	}

	/**
	 * Validate one row in the table
	 */	
	private void validateRow(TableItem row, String story, String name, String number, String estimate, String todo, String status) {		
		Assert.assertEquals(story, row.getText(STORY_COLUMN_INDEX));
		Assert.assertEquals(name, row.getText(NAME_COLUMN_INDEX));
		Assert.assertEquals(number, row.getText(ID_COLUMN_INDEX));
		Assert.assertEquals(estimate, row.getText(ESTIMATE_COLUMN_INDEX));
		Assert.assertEquals(todo, row.getText(TODO_COLUMN_INDEX));
		Assert.assertEquals(status, row.getText(STATUS_COLUMN_INDEX));
	}
	
	/**
	 * Test row when all projects are selected
	 */	
	@Test
	public void testViewDataEffort() {
		enableEffortTracking();
		Table table = testView.getViewer().getTable();
		Assert.assertNotNull(table);
		TableItem[] rows = table.getItems();
		Assert.assertEquals(2, rows.length);

		validateRow(rows[0], "Add Shipping Notes", "Service Changes", "TK-01061", "24.0", "30.0", "", "10.0", "In Progress");
		validateRow(rows[1], "View Daily Call Count", "Service Changes", "TK-01068", "24.0", "", "", "24.0", "In Progress");
	}

	/**
	 * Validate one row in the table
	 */	
	private void validateRow(TableItem row, String story, String name, String number, String estimate, String done, String effort, String todo, String status) {		
		Assert.assertEquals(story, row.getText(STORY_COLUMN_INDEX));
		Assert.assertEquals(name, row.getText(NAME_COLUMN_INDEX));
		Assert.assertEquals(number, row.getText(ID_COLUMN_INDEX));
		Assert.assertEquals(estimate, row.getText(ESTIMATE_COLUMN_INDEX));		
		Assert.assertEquals(effort, row.getText(EFFORT_COLUMN_INDEX));
		Assert.assertEquals(done, row.getText(DONE_COLUMN_INDEX));
		Assert.assertEquals(todo, row.getText(TRACKED_TODO_COLUMN_INDEX));
		Assert.assertEquals(status, row.getText(TRACKED_STATUS_COLUMN_INDEX));
	}
	
	/**
	 * Process UI input but do not return for the specified time interval
	 * 
	 * @param waitTimeMillis number of milliseconds to wait
	 */
	private void delay(int waitTimeMillis) {
		Display display = Display.getCurrent();
		
		// if this is the UI thread, then process input
		if(null != display) {
			long endTime = System.currentTimeMillis() + waitTimeMillis;
			while(System.currentTimeMillis() < endTime) {
				if(!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} else { // just sleep
			try {Thread.sleep(waitTimeMillis);} catch (InterruptedException e) {}
		}
	}

	/**
	 * Wait for background tasks to complete
	 */
	private void waitForJobs() {
		
		while(null != Job.getJobManager().currentJob()) {
			delay(1000);
		}
	}	
}
