package com.versionone.common.test;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.versionone.apiclient.FileAPIConnector;
import com.versionone.apiclient.Localizer;
import com.versionone.apiclient.MetaModel;
import com.versionone.apiclient.Services;
import com.versionone.apiclient.IV1Configuration.TrackingLevel;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.EffortTrackingLevel;
import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.ValueId;
import com.versionone.common.sdk.Workitem;

public class TestModel {

    private static final ApiDataLayer datalayer = ApiDataLayer.getInstance();
    private static final NumberFormat format = Workitem.numberFormat;

    @BeforeClass
    public static void setUp() throws Exception {
        final String[] all = { Workitem.STORY_PREFIX, Workitem.DEFECT_PREFIX, Workitem.TASK_PREFIX,
                Workitem.TEST_PREFIX };
        setDataLayerAttribute(Workitem.STATUS_PROPERTY, true, all);
        setDataLayerAttribute(Workitem.OWNERS_PROPERTY, true, all);

        FileAPIConnector metaConnector = new FileAPIConnector("testdata/TestMetaData.xml", "meta.v1/");
        FileAPIConnector dataConnector = new FileAPIConnector("testdata/TestData.xml", "rest-1.v1/");
        FileAPIConnector localizeConnector = new FileAPIConnector("testdata/TestLocalizeData.xml", "loc.v1/");
        MetaModel metaModel = new MetaModel(metaConnector);
        Services services = new Services(metaModel, dataConnector);
        Localizer localizer = new Localizer(localizeConnector);
        datalayer.connectFotTesting(services, metaModel, localizer, TrackingLevel.Off, TrackingLevel.On);
    }

    private static void setDataLayerAttribute(String attribute, boolean isListType, String... typePrefixes) {
        for (String prefix : typePrefixes) {
            datalayer.addProperty(attribute, prefix, isListType);
        }
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
        validateTask(allWorkItem[0], "B-01190", "Story:2265", "FAST LAND 1", null, null, null, null, "Done", null,
                Workitem.STORY_PREFIX);
        validateTask(allWorkItem[2], "D-01093", "Defect:2248", "defect 1", "0,02", "-2,00", null, "0,01", "Done", null,
                Workitem.DEFECT_PREFIX);
        validateTask(allWorkItem[1].children.get(0), "AT-01010", "Test:2273", "AT4", "13,00", null, null, "18,00", "",
                "FAST LAND 2", Workitem.TEST_PREFIX);
        validateTask(allWorkItem[0].children.get(1), "TK-01031", "Task:2269", "task2", "9,30", "5,00", null, "9,30",
                "In Progress", "FAST LAND 1", Workitem.TASK_PREFIX);
    }

    @Test
    public void testGetAllTasks() throws Exception {
        datalayer.showAllTasks = true;
        Workitem[] allWorkItem = datalayer.getWorkitemTree();
        Assert.assertEquals(11, allWorkItem.length);
        validateTask(allWorkItem[1].children.get(1), "TK-01030", "Task:2268", "task1", "10,00", "5,00", null, "0,00",
                "Completed", "FAST LAND 1", Workitem.TASK_PREFIX);
        validateTask(allWorkItem[6], "D-01093", "Defect:2248", "defect 1", "0,02", "-2,00", null, "0,01", "Done", null,
                Workitem.DEFECT_PREFIX);
        validateTask(allWorkItem[5].children.get(1), "AT-01008", "Test:2244", "test1", "0,00", "35,00", null, "0,00",
                "Passed", "STORY33.db", Workitem.TEST_PREFIX);
    }

    private void validateTask(Workitem task, String number, String id, String name, String estimate, String done,
            String effort, String todo, String status, String parent, String typePrefix) throws Exception {
        Assert.assertEquals(id, task.getId());
        Assert.assertEquals(name, task.getProperty(Workitem.NAME_PROPERTY));
        Assert.assertEquals(number, task.getProperty(Workitem.ID_PROPERTY));

        Assert.assertEquals(estimate, task.getProperty(Workitem.DETAIL_ESTIMATE_PROPERTY));
        Assert.assertEquals(done, task.getProperty(Workitem.DONE_PROPERTY));
        Assert.assertEquals(effort, task.getProperty(Workitem.EFFORT_PROPERTY));
        Assert.assertEquals(todo, task.getProperty(Workitem.TODO_PROPERTY));
        Assert.assertEquals(status, task.getProperty(Workitem.STATUS_PROPERTY).toString());
        Assert.assertEquals(parent, task.getProperty(Workitem.PARENT_NAME_PROPERTY));
        Assert.assertEquals(typePrefix, task.getTypePrefix());
    }

    // @Test
    // public void testGetStatusCodes() throws Exception {
    // IStatusCodes statusCodes = V1Server.getInstance().getTaskStatusValues();
    // Assert.assertNotNull(statusCodes);
    // validateGetDisplayValues(statusCodes);
    // validateGetID(statusCodes);
    // validateGetDisplayValue(statusCodes);
    // validateGetDisplayFromOid(statusCodes);
    // validateGetOidIndex(statusCodes);
    // }
    //

    @Test
    public void testDescription() throws Exception {
        final Workitem defect0 = datalayer.getWorkitemTree()[0];

        defect0.setProperty(Workitem.DESCRIPTION_PROPERTY, "<br>test <b>new test</b>");
        Assert.assertEquals("<br>test <b>new test</b>", defect0.getProperty(Workitem.DESCRIPTION_PROPERTY));

        defect0.setProperty(Workitem.DESCRIPTION_PROPERTY, "--");
        Assert.assertEquals("--", defect0.getProperty(Workitem.DESCRIPTION_PROPERTY));

        defect0.setProperty(Workitem.DESCRIPTION_PROPERTY, "");
        Assert.assertEquals(null, defect0.getProperty(Workitem.DESCRIPTION_PROPERTY));
    }

    /**
     * ToDo cannot be negative.
     */
    @Test
    public void testSetToDo() throws Exception {
        validateNumberProperty(datalayer.getWorkitemTree()[0], Workitem.TODO_PROPERTY);
    }

    /**
     * Estimate cannot be negative.
     */
    @Test
    public void testSetEstimate() throws Exception {
        validateNumberProperty(datalayer.getWorkitemTree()[1], Workitem.ESTIMATE_PROPERTY);
    }

    /**
     * Estimate cannot be negative.
     */
    @Test
    public void testSetDetailEstimate() throws Exception {
        validateNumberProperty(datalayer.getWorkitemTree()[0], Workitem.DETAIL_ESTIMATE_PROPERTY);
    }

    @Test
    public void testSetEffort() throws Exception {
        Workitem workitem = datalayer.getWorkitemTree()[0];
        String property = Workitem.EFFORT_PROPERTY;
        workitem.setProperty(property, "0");
        Assert.assertEquals(null, workitem.getProperty(property));

        workitem.setProperty(property, 10.125);
        Assert.assertEquals(format.format(10.125), workitem.getProperty(property));

        workitem.setProperty(property, 10.01);
        Assert.assertEquals(format.format(10.01), workitem.getProperty(property));

        workitem.setProperty(property, "-1");
        Assert.assertEquals(format.format(-1), workitem.getProperty(property));
    }

    private void validateNumberProperty(Workitem workitem, String property) {
        workitem.setProperty(property, "0");
        Assert.assertEquals(format.format(0), workitem.getProperty(property));

        workitem.setProperty(property, 10.125);
        Assert.assertEquals(format.format(10.125), workitem.getProperty(property));

        workitem.setProperty(property, 10.01);
        Assert.assertEquals(format.format(10.01), workitem.getProperty(property));

        try {
            workitem.setProperty(property, "-1");
        } catch (Exception e) {
            // Do nothing
        }
        Assert.assertEquals(format.format(10.01), workitem.getProperty(property));
    }

    /**
     * SetStatus must accept a ValueID.
     */
    @Test
    public void testSetStatus() throws Exception {
        final Workitem defect0 = datalayer.getWorkitemTree()[0];
        PropertyValues statuses = datalayer.getListPropertyValues(defect0.getTypePrefix(), Workitem.STATUS_PROPERTY);
        PropertyValues types = datalayer.getListPropertyValues(Workitem.STORY_PREFIX, Workitem.TYPE_PROPERTY);

        ValueId status = statuses.getValueIdByIndex(0);
        defect0.setProperty(Workitem.STATUS_PROPERTY, status);
        Assert.assertEquals(status, defect0.getProperty(Workitem.STATUS_PROPERTY));

        status = statuses.getValueIdByIndex(1);
        defect0.setProperty(Workitem.STATUS_PROPERTY, status);
        Assert.assertEquals(status, defect0.getProperty(Workitem.STATUS_PROPERTY));

        try {
            defect0.setProperty(Workitem.STATUS_PROPERTY, "On Hold4");
        } catch (Exception e) {
            // Do nothing
        }
        Assert.assertEquals(status, defect0.getProperty(Workitem.STATUS_PROPERTY));

        try {
            defect0.setProperty(Workitem.STATUS_PROPERTY, "Task:123");
        } catch (Exception e) {
            // Do nothing
        }
        Assert.assertEquals(status, defect0.getProperty(Workitem.STATUS_PROPERTY));

        try {
            defect0.setProperty(Workitem.STATUS_PROPERTY, types.getValueIdByIndex(1));
        } catch (Exception e) {
            // Do nothing
        }
        Assert.assertEquals(status, defect0.getProperty(Workitem.STATUS_PROPERTY));
    }

    @Test
    public void testSetName() throws Exception {
        final Workitem defect0 = datalayer.getWorkitemTree()[0];
        defect0.setProperty(Workitem.NAME_PROPERTY, "New Name");
        Assert.assertEquals("New Name", defect0.getProperty(Workitem.NAME_PROPERTY));
    }

    @Test
    public void testSetStoryOwner() throws Exception {
        final Workitem defect0 = datalayer.getWorkitemTree()[0];

        PropertyValues owners = (PropertyValues) defect0.getProperty(Workitem.OWNERS_PROPERTY);
        final ValueId cat = owners.getValueIdByIndex(0);
        Assert.assertEquals("Cat", cat.toString());
        Assert.assertEquals(cat.toString(), defect0.getPropertyAsString(Workitem.OWNERS_PROPERTY));
        Assert.assertEquals(cat.toString(), owners.toString());
        Assert.assertEquals(1, owners.size());
        Assert.assertEquals(new PropertyValues(Arrays.asList(cat)), owners);
        PropertyValues users = datalayer.getListPropertyValues(defect0.getTypePrefix(), Workitem.OWNERS_PROPERTY);
        ValueId admin = users.getValueIdByIndex(1);
        Assert.assertEquals("Administrator", admin.toString());
        owners = new PropertyValues(Arrays.asList(admin));
        defect0.setProperty(Workitem.OWNERS_PROPERTY, owners);
        Assert.assertEquals(admin.toString(), defect0.getPropertyAsString(Workitem.OWNERS_PROPERTY));
        owners = new PropertyValues();
        defect0.setProperty(Workitem.OWNERS_PROPERTY, owners);
        Assert.assertEquals("", defect0.getPropertyAsString(Workitem.OWNERS_PROPERTY));
        owners = new PropertyValues(Arrays.asList(admin, cat));
        defect0.setProperty(Workitem.OWNERS_PROPERTY, owners);
        Assert.assertEquals("Administrator, Cat", defect0.getPropertyAsString(Workitem.OWNERS_PROPERTY));
    }

    @Test
    public void testSetDefectOwner() throws Exception {
        final Workitem story1 = datalayer.getWorkitemTree()[1];

        PropertyValues owners = (PropertyValues) story1.getProperty(Workitem.OWNERS_PROPERTY);
        final ValueId adminForRemove = owners.getValueIdByIndex(0);
        owners.remove(adminForRemove);
        PropertyValues users = datalayer.getListPropertyValues(story1.getTypePrefix(), Workitem.OWNERS_PROPERTY);
        ValueId petja = users.getValueIdByIndex(8);
        Assert.assertEquals("Petja", petja.toString());
        owners.add(petja);
        Assert.assertEquals(3, owners.size());
        story1.setProperty(Workitem.OWNERS_PROPERTY, owners);
        Assert.assertEquals("Petja, Bil, Tom", story1.getPropertyAsString(Workitem.OWNERS_PROPERTY));
    }

    /**
     * Effort is allowed to accept doubles and strings default locale encoded.
     */
    @Test
    public void testTrackingLevel() throws Exception {
        EffortTrackingLevel tracking = datalayer.trackingLevel;
        Workitem story1 = datalayer.getWorkitemTree()[1];
        Workitem s1Test0 = story1.children.get(0);
        Workitem s1Task1 = story1.children.get(1);
        Workitem defect9 = datalayer.getWorkitemTree()[9];
        Workitem d9Task0 = defect9.children.get(0);
        Workitem d9Test2 = defect9.children.get(2);

        Assert.assertTrue(tracking.isTracking(defect9));
        Assert.assertFalse(tracking.isTracking(story1));
        Assert.assertTrue(tracking.isTracking(s1Test0));
        Assert.assertTrue(tracking.isTracking(s1Task1));
        Assert.assertFalse(tracking.isTracking(d9Task0));
        Assert.assertFalse(tracking.isTracking(d9Test2));
    }

    // private void validateGetOidIndex(IStatusCodes statusCodes) {
    // Assert.assertEquals(0, statusCodes.getOidIndex(""));
    // Assert.assertEquals(1, statusCodes.getOidIndex("TaskStatus:123"));
    // Assert.assertEquals(2, statusCodes.getOidIndex("TaskStatus:125"));
    // Assert.assertEquals(3, statusCodes.getOidIndex("TaskStatus:126"));
    // }
    //
    // private void validateGetDisplayFromOid(IStatusCodes statusCodes) {
    // Assert.assertEquals("*** Invalid OID ***",
    // statusCodes.getDisplayFromOid(""));
    // Assert.assertEquals("", statusCodes.getDisplayFromOid("NULL"));
    // Assert.assertEquals("In Progress",
    // statusCodes.getDisplayFromOid("TaskStatus:123"));
    // Assert.assertEquals("Completed",
    // statusCodes.getDisplayFromOid("TaskStatus:125"));
    // Assert.assertEquals("On Hold",
    // statusCodes.getDisplayFromOid("TaskStatus:126"));
    // }
    //
    // private void validateGetDisplayValue(IStatusCodes statusCodes) {
    // Assert.assertEquals("", statusCodes.getDisplayValue(0));
    // Assert.assertEquals("In Progress", statusCodes.getDisplayValue(1));
    // Assert.assertEquals("Completed", statusCodes.getDisplayValue(2));
    // Assert.assertEquals("On Hold", statusCodes.getDisplayValue(3));
    // }
    //
    // private void validateGetID(IStatusCodes statusCodes) {
    // Assert.assertEquals("NULL", statusCodes.getID(0));
    // Assert.assertEquals("TaskStatus:123", statusCodes.getID(1));
    // Assert.assertEquals("TaskStatus:125", statusCodes.getID(2));
    // Assert.assertEquals("TaskStatus:126", statusCodes.getID(3));
    // try {
    // statusCodes.getID(4);
    // Assert.fail("Expected IndexOutOfBoundsException");
    // }
    // catch(IndexOutOfBoundsException e){}
    // }
    //
    // private void validateGetDisplayValues(IStatusCodes statusCodes) {
    // String[] displayNames = statusCodes.getDisplayValues();
    // Assert.assertEquals(4, displayNames.length);
    // Assert.assertEquals("", displayNames[0]);
    // Assert.assertEquals("In Progress", displayNames[1]);
    // Assert.assertEquals("Completed", displayNames[2]);
    // Assert.assertEquals("On Hold", displayNames[3]);
    // }
    //
}
