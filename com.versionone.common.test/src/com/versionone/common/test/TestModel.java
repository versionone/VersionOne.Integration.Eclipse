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
import com.versionone.common.sdk.AttributeInfo;
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
        datalayer.setShowAllTasks(false);
        List<Workitem> allWorkItem = datalayer.getWorkitemTree();
        Assert.assertEquals(7, allWorkItem.size());
        validateTask(allWorkItem.get(0), "B-01190", "Story:2265", "FAST LAND 1", null, null, null, null, "Done", null,
                Workitem.STORY_PREFIX);
        validateTask(allWorkItem.get(2), "D-01093", "Defect:2248", "defect 1", "0,02", "-2,00", null, "0,01", "Done", null,
                Workitem.DEFECT_PREFIX);
        validateTask(allWorkItem.get(1).children.get(0), "AT-01010", "Test:2273", "AT4", "13,00", null, null, "18,00", "",
                "FAST LAND 2", Workitem.TEST_PREFIX);
        validateTask(allWorkItem.get(0).children.get(1), "TK-01031", "Task:2269", "task2", "9,30", "5,00", null, "9,30",
                "In Progress", "FAST LAND 1", Workitem.TASK_PREFIX);
    }

    @Test
    public void testGetAllTasks() throws Exception {
        datalayer.setShowAllTasks(true);
        List<Workitem> allWorkItem = datalayer.getWorkitemTree();
        Assert.assertEquals(11, allWorkItem.size());
        validateTask(allWorkItem.get(1).children.get(1), "TK-01030", "Task:2268", "task1", "10,00", "5,00", null, "0,00",
                "Completed", "FAST LAND 1", Workitem.TASK_PREFIX);
        validateTask(allWorkItem.get(6), "D-01093", "Defect:2248", "defect 1", "0,02", "-2,00", null, "0,01", "Done", null,
                Workitem.DEFECT_PREFIX);
        validateTask(allWorkItem.get(5).children.get(1), "AT-01008", "Test:2244", "test1", "0,00", "35,00", null, "0,00",
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

    @Test
    public void testSetDescription() throws Exception {
        final Workitem defect0 = datalayer.getWorkitemTree().get(0);

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
        validateNumberProperty(datalayer.getWorkitemTree().get(0), Workitem.TODO_PROPERTY);
    }

    /**
     * Estimate cannot be negative.
     */
    @Test
    public void testSetEstimate() throws Exception {
        validateNumberProperty(datalayer.getWorkitemTree().get(1), Workitem.ESTIMATE_PROPERTY);
    }

    /**
     * Estimate cannot be negative.
     */
    @Test
    public void testSetDetailEstimate() throws Exception {
        validateNumberProperty(datalayer.getWorkitemTree().get(0), Workitem.DETAIL_ESTIMATE_PROPERTY);
    }

    /**
     * Effort is allowed to accept doubles and strings.
     */
    @Test
    public void testSetEffort() throws Exception {
        Workitem workitem = datalayer.getWorkitemTree().get(0);
        String property = Workitem.EFFORT_PROPERTY;
        workitem.setProperty(property, "0");
        Assert.assertEquals(null, workitem.getProperty(property));

        workitem.setProperty(property, 10.125);
        Assert.assertEquals(format.format(10.125), workitem.getProperty(property));

        workitem.setProperty(property, 10.01);
        Assert.assertEquals(format.format(10.01), workitem.getProperty(property));

        workitem.setProperty(property, "-1");
        Assert.assertEquals(format.format(-1), workitem.getProperty(property));

        workitem.resetProperty(property);
        Assert.assertEquals(null, workitem.getProperty(property));
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
        final Workitem defect0 = datalayer.getWorkitemTree().get(0);
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

        Assert.assertTrue(defect0.isPropertyChanged(Workitem.STATUS_PROPERTY));
        defect0.resetProperty(Workitem.STATUS_PROPERTY);
        Assert.assertFalse(defect0.isPropertyChanged(Workitem.STATUS_PROPERTY));
        Assert.assertEquals("Accepted", defect0.getPropertyAsString(Workitem.STATUS_PROPERTY));

        defect0.setProperty(Workitem.STATUS_PROPERTY, statuses.getValueIdByIndex(0));
        Assert.assertTrue(defect0.hasChanges());
        defect0.revertChanges();
        Assert.assertFalse(defect0.hasChanges());
}

    @Test
    public void testSetName() throws Exception {
        final Workitem defect0 = datalayer.getWorkitemTree().get(0);
        defect0.setProperty(Workitem.NAME_PROPERTY, "New Name");
        Assert.assertEquals("New Name", defect0.getProperty(Workitem.NAME_PROPERTY));
        Assert.assertTrue(defect0.isPropertyChanged(Workitem.NAME_PROPERTY));
        defect0.resetProperty(Workitem.NAME_PROPERTY);
        Assert.assertFalse(defect0.isPropertyChanged(Workitem.NAME_PROPERTY));
        Assert.assertEquals("New Defect1", defect0.getProperty(Workitem.NAME_PROPERTY));

        defect0.setProperty(Workitem.NAME_PROPERTY, "New Name");
        Assert.assertTrue(defect0.hasChanges());
        defect0.revertChanges();
        Assert.assertFalse(defect0.hasChanges());
        Assert.assertEquals("New Defect1", defect0.getProperty(Workitem.NAME_PROPERTY));
    }

    @Test
    public void testSetStoryOwner() throws Exception {
        final Workitem defect0 = datalayer.getWorkitemTree().get(0);

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
        final Workitem story1 = datalayer.getWorkitemTree().get(1);

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

    @Test
    public void testReadonlyProperties() throws Exception {
        final List<Workitem> workitems = datalayer.getWorkitemTree();
        Workitem story1 = workitems.get(1);
        Workitem s1Test0 = story1.children.get(0);
        Workitem s1Task1 = story1.children.get(1);
        Workitem defect9 = workitems.get(9);
        Workitem d9Task0 = defect9.children.get(0);
        Workitem d9Test2 = defect9.children.get(2);

        Assert.assertTrue(story1.isPropertyReadOnly(Workitem.ID_PROPERTY));
        Assert.assertFalse(story1.isPropertyReadOnly(Workitem.NAME_PROPERTY));
        Assert.assertFalse(story1.isPropertyReadOnly(Workitem.OWNERS_PROPERTY));

        Assert.assertTrue(story1.isPropertyReadOnly(Workitem.EFFORT_PROPERTY));
        Assert.assertFalse(s1Task1.isPropertyReadOnly(Workitem.EFFORT_PROPERTY));
        Assert.assertFalse(s1Test0.isPropertyReadOnly(Workitem.EFFORT_PROPERTY));
        Assert.assertFalse(defect9.isPropertyReadOnly(Workitem.EFFORT_PROPERTY));
        Assert.assertTrue(d9Task0.isPropertyReadOnly(Workitem.EFFORT_PROPERTY));
        Assert.assertTrue(d9Test2.isPropertyReadOnly(Workitem.EFFORT_PROPERTY));
    }

    @Test
    public void testTrackingLevel() throws Exception {
        final EffortTrackingLevel tracking = datalayer.trackingLevel;
        final List<Workitem> workitems = datalayer.getWorkitemTree();
        Workitem story1 = workitems.get(1);
        Workitem s1Test0 = story1.children.get(0);
        Workitem s1Task1 = story1.children.get(1);
        Workitem defect9 = workitems.get(9);
        Workitem d9Task0 = defect9.children.get(0);
        Workitem d9Test2 = defect9.children.get(2);

        Assert.assertTrue(tracking.isTracking(defect9));
        Assert.assertFalse(tracking.isTracking(story1));
        Assert.assertTrue(tracking.isTracking(s1Test0));
        Assert.assertTrue(tracking.isTracking(s1Task1));
        Assert.assertFalse(tracking.isTracking(d9Task0));
        Assert.assertFalse(tracking.isTracking(d9Test2));
    }

    @Test
    public void testWorkitemIsMine() throws Exception {
        final List<Workitem> workitems = datalayer.getWorkitemTree();
        Workitem defect0 = workitems.get(0);
        Workitem story1 = workitems.get(1);
        Workitem s1Test0 = story1.children.get(0);
        Workitem s1Task1 = story1.children.get(1);
        Workitem defect9 = workitems.get(9);
        Workitem d9Task0 = defect9.children.get(0);
        Workitem d9Test2 = defect9.children.get(2);

        Assert.assertTrue(defect0.isMine());
        Assert.assertFalse(story1.isMine());
        Assert.assertFalse(s1Task1.isMine());
        Assert.assertTrue(s1Test0.isMine());
        Assert.assertTrue(defect9.isMine());
        Assert.assertTrue(d9Task0.isMine());
        Assert.assertTrue(d9Test2.isMine());
    }

    @Test
    public void testWorkitemCanSignup() throws Exception {
        final List<Workitem> workitems = datalayer.getWorkitemTree();
        Workitem defect0 = workitems.get(0);
        Workitem story1 = workitems.get(1);
        Workitem s1Test0 = story1.children.get(0);
        Workitem s1Task1 = story1.children.get(1);
        Workitem defect9 = workitems.get(9);
        Workitem d9Task0 = defect9.children.get(0);
        Workitem d9Test2 = defect9.children.get(2);

        Assert.assertTrue(defect0.canSignup());
        Assert.assertTrue(story1.canSignup());
        Assert.assertTrue(s1Task1.canSignup());
        Assert.assertTrue(s1Test0.canSignup());
        Assert.assertTrue(defect9.canSignup());
        Assert.assertTrue(d9Task0.canSignup());
        Assert.assertTrue(d9Test2.canSignup());
    }

    @Test
    public void testGetTestStatuses() throws Exception {
        PropertyValues statuses = datalayer.getListPropertyValues(Workitem.TEST_PREFIX, Workitem.STATUS_PROPERTY);
        Assert.assertNotNull(statuses);
        validatePropertyValues(statuses, "", "Failed", "Passed");
    }

    @Test
    public void testGetStoryStatuses() throws Exception {
        PropertyValues statuses = datalayer.getListPropertyValues(Workitem.STORY_PREFIX, Workitem.STATUS_PROPERTY);
        Assert.assertNotNull(statuses);
        validatePropertyValues(statuses, "", "Future", "In Progress", "Done", "Accepted");
    }

    private void validatePropertyValues(PropertyValues statuses, String... expecteds) {
        Assert.assertArrayEquals(expecteds, statuses.toStringArray());
    }

    @Test
    public void testAttributeInfo() {
        Assert.assertEquals("Story.Status(List:true)", new AttributeInfo("Status", "Story", true).toString());
        Assert.assertEquals("Test.Description(List:false)", new AttributeInfo("Description", "Test", false).toString());
    }
}
