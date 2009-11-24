package com.versionone.taskview.test;

import java.util.Arrays;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.versionone.common.preferences.PreferenceConstants;
import com.versionone.common.preferences.PreferencePage;
import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.PropertyValuesMock;
import com.versionone.common.sdk.TestDataLayer;
import com.versionone.common.sdk.Entity;
import com.versionone.common.sdk.WorkitemMock;
import com.versionone.taskview.views.TaskView;

import static com.versionone.common.sdk.WorkitemType.*;

public class TestTaskView {

    static final String VIEW_ID = "com.versionone.taskview.views.TaskView";

    /**
     * Configuration Parameters
     */
    static final String SERVER_URL = "http://localhost/V1_71";
    static final String USER_ID = "admin";
    static final String USER_PASSWORD = "admin";
    static final String USER_MEMBER_ID = "Member:1000";
    static final String SCOPE_ID = "Scope:0";
    static final boolean VALIDATION_REQUIRED = false;
    static final boolean TRACKING = false;

    // column indexes
    static final int ID_COLUMN_INDEX = 0;
    static final int NAME_COLUMN_INDEX = 1;
    static final int OWNER_COLUMN_INDEX = 2;
    static final int STATUS_COLUMN_INDEX = 3;
    static final int DETAIL_ESTIMATE_COLUMN_INDEX = 4;
    static final int DONE_COLUMN_INDEX = 5;
    // index changes when effort tracking is enabled
    static final int TODO_COLUMN_INDEX = 5;
    static final int EFFORT_COLUMN_INDEX = 6;
    static final int TRACKED_TODO_COLUMN_INDEX = 7;

    // test target
    private static TaskView testView = null;
    private static PreferencePage preference = null;

    private static WorkitemMock story;
    private static WorkitemMock storyTask;
    private static WorkitemMock storyTest;
    private static WorkitemMock defect;
    private static WorkitemMock defectTask;
    private static WorkitemMock defectTest;

    @BeforeClass
    public static void loadTestData() throws Exception {
        story = new WorkitemMock("1", Story);
        storyTask = new WorkitemMock(Task, "12", story);
        storyTest = new WorkitemMock(Test, "11", story);
        defect = new WorkitemMock("2", Defect);
        defectTask = new WorkitemMock(Task, "22", defect);
        defectTest = new WorkitemMock(Test, "21", defect);
        TestDataLayer.getInstance().workitemTree = Arrays.asList((Entity)story, defect );
        setupWorkitem(story, "Bil, Tom, Administrator", "FAST LAND 1", "B-01190", "5,00", "6,00", "7,00", "8,00",
        "Done");
        setupWorkitem(defect, "Cat", "New Defect1", "D-01094", "1,00", "2,00", "3,00", "4,00", "Accepted");
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
    public void testValid() throws Exception {
        reCreateTable();
        Assert.assertNotNull(testView);
    }

    /**
     * Test columns when effort tracking is enabled
     * 
     * @throws Exception
     */

    @Test
    public void testEffortEnabled() throws Exception {
        TestDataLayer.getInstance().isEffortTracking = true;
        reCreateTable();
        Tree table = testView.getViewer().getTree();
        Assert.assertNotNull(table);
        Assert.assertEquals(8, table.getColumnCount());
        TreeColumn[] columns = table.getColumns();
        Assert.assertEquals("Title", columns[NAME_COLUMN_INDEX].getText());
        Assert.assertEquals("ID", columns[ID_COLUMN_INDEX].getText());
        Assert.assertEquals("Owner", columns[OWNER_COLUMN_INDEX].getText());
        Assert.assertEquals("Detail Estimate", columns[DETAIL_ESTIMATE_COLUMN_INDEX].getText());
        Assert.assertEquals("Done", columns[DONE_COLUMN_INDEX].getText());
        Assert.assertEquals("Effort", columns[EFFORT_COLUMN_INDEX].getText());
        Assert.assertEquals("To Do", columns[TRACKED_TODO_COLUMN_INDEX].getText());
        Assert.assertEquals("Status", columns[STATUS_COLUMN_INDEX].getText());
    }

    private void reCreateTable() throws Exception {
        waitForJobs();
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(testView);

        waitForJobs();
        preference = new PreferencePage();
        if (!preference.getPreferenceStore().getBoolean(PreferenceConstants.P_ENABLED)) {
            enableView(preference.getPreferenceStore());
        }
        testView = (TaskView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(VIEW_ID);
        testView.setupEffortColumns();
        waitForJobs();
    }

    /**
     * Test columns when effort tracking is disabled
     * 
     * @throws Exception
     */
    @Test
    public void testEffortDisabled() throws Exception {
        TestDataLayer.getInstance().isEffortTracking = false;
        reCreateTable();
        Tree table = testView.getViewer().getTree();
        Assert.assertNotNull(table);
        Assert.assertEquals(6, table.getColumnCount());
        TreeColumn[] columns = table.getColumns();
        Assert.assertEquals("Title", columns[NAME_COLUMN_INDEX].getText());
        Assert.assertEquals("ID", columns[ID_COLUMN_INDEX].getText());
        Assert.assertEquals("Owner", columns[OWNER_COLUMN_INDEX].getText());
        Assert.assertEquals("Detail Estimate", columns[DETAIL_ESTIMATE_COLUMN_INDEX].getText());
        Assert.assertEquals("To Do", columns[TODO_COLUMN_INDEX].getText());
        Assert.assertEquals("Status", columns[STATUS_COLUMN_INDEX].getText());
    }

    /**
     * Test row when all projects are selected
     * 
     * @throws Exception
     */
    @Test
    public void testViewDataNoEffort() throws Exception {
        TestDataLayer.getInstance().isEffortTracking = false;
        reCreateTable();
        Tree table = testView.getViewer().getTree();
        Assert.assertNotNull(table);
        TreeItem[] rows = table.getItems();
        Assert.assertEquals(2, rows.length);
        validateRow(rows[0], story, false);
        validateRow(rows[1], defect, false);
    }

    /**
     * Setup workitemMock so it can be validated by validateRow()
     */
    private static void setupWorkitem(WorkitemMock item, String owners, String name, String number, String estimate,
            String done, String effort, String todo, String status) {
        final PropertyValues ownersList = createListValue(owners);
        TestDataLayer.getInstance().setListProperty(Entity.OWNERS_PROPERTY, item.getType(), ownersList);
        item.properties.put(Entity.OWNERS_PROPERTY, ownersList);
        item.properties.put(Entity.NAME_PROPERTY, name);
        item.properties.put(Entity.ID_PROPERTY, number);
        item.properties.put(Entity.DETAIL_ESTIMATE_PROPERTY, estimate);
        item.properties.put(Entity.DONE_PROPERTY, done);
        item.properties.put(Entity.EFFORT_PROPERTY, effort);
        item.properties.put(Entity.TODO_PROPERTY, todo);
        item.properties.put(Entity.STATUS_PROPERTY, createListValue(status));
    }

    private static PropertyValues createListValue(String ownersString) {
        PropertyValues res = new PropertyValuesMock(ownersString.split(", "));
        return res;
    }

    /**
     * Validate one row in the table
     */
    private void validateRow(TreeItem row, WorkitemMock item, boolean checkEffort) {
        Assert.assertEquals(item.getPropertyAsString(Entity.OWNERS_PROPERTY), row.getText(OWNER_COLUMN_INDEX));
        Assert.assertEquals(item.getPropertyAsString(Entity.NAME_PROPERTY), row.getText(NAME_COLUMN_INDEX));
        Assert.assertEquals(item.getPropertyAsString(Entity.ID_PROPERTY), row.getText(ID_COLUMN_INDEX));
        Assert.assertEquals(item.getPropertyAsString(Entity.STATUS_PROPERTY), row.getText(STATUS_COLUMN_INDEX));
        Assert.assertEquals(item.getPropertyAsString(Entity.DETAIL_ESTIMATE_PROPERTY), row
                .getText(DETAIL_ESTIMATE_COLUMN_INDEX));
        if (checkEffort) {
            Assert.assertEquals(item.getPropertyAsString(Entity.EFFORT_PROPERTY), row.getText(EFFORT_COLUMN_INDEX));
            Assert.assertEquals(item.getPropertyAsString(Entity.DONE_PROPERTY), row.getText(DONE_COLUMN_INDEX));
            Assert.assertEquals(item.getPropertyAsString(Entity.TODO_PROPERTY), row
                    .getText(TRACKED_TODO_COLUMN_INDEX));
        } else {
            Assert.assertEquals(item.getPropertyAsString(Entity.TODO_PROPERTY), row.getText(TODO_COLUMN_INDEX));
        }
    }

    /**
     * Test row when all projects are selected
     * 
     * @throws Exception
     */
    @Test
    public void testViewDataEffort() throws Exception {
        TestDataLayer.getInstance().isEffortTracking = true;
        reCreateTable();
        Tree table = testView.getViewer().getTree();
        Assert.assertNotNull(table);
        TreeItem[] rows = table.getItems();
        Assert.assertEquals(2, rows.length);

        validateRow(rows[0], story, true);
        validateRow(rows[1], defect, true);
    }

    /**
     * Process UI input but do not return for the specified time interval
     * 
     * @param waitTimeMillis
     *            number of milliseconds to wait
     */
    private static void delay(int waitTimeMillis) {
        Display display = Display.getCurrent();

        // if this is the UI thread, then process input
        if (null != display) {
            long endTime = System.currentTimeMillis() + waitTimeMillis;
            while (System.currentTimeMillis() < endTime) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        } else { // just sleep
            try {
                Thread.sleep(waitTimeMillis);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Wait for background tasks to complete
     */
    private static void waitForJobs() {
        while (null != Job.getJobManager().currentJob()) {
            delay(1000);
        }
    }
}
