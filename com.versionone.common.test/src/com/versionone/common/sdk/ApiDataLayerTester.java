package com.versionone.common.sdk;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static com.versionone.common.sdk.WorkitemType.Defect;
import static com.versionone.common.sdk.WorkitemType.Scope;
import static com.versionone.common.sdk.WorkitemType.Story;
import static com.versionone.common.sdk.WorkitemType.Task;
import static com.versionone.common.sdk.WorkitemType.Test;

public class ApiDataLayerTester implements IntegrationalTest {

    @Ignore("This test is integrational. It works with V1 server.")
    @Test
    public void testCreateAndGetDefect() throws Exception {
        final ApiDataLayer data = ApiDataLayer.getInstance();
        data.addProperty("Name", Defect, false);
        data.addProperty("Owners", Defect, true);
        data.addProperty("Status", Defect, true);
        data.connect(V1_PATH, V1_USER, V1_PASSWORD, false);
        data.getWorkitemTree();
        final Entity defect = data.createNewWorkitem(Defect, null);
        assertEquals(null, defect.parent);
        assertEquals(0, defect.children.size());
        assertFalse(defect.canQuickClose());
        assertFalse(defect.canSignup());
        try {
            defect.close();
            fail();
        } catch (UnsupportedOperationException e) {
            // Do nothing
        }
        try {
            defect.quickClose();
            fail();
        } catch (UnsupportedOperationException e) {
            // Do nothing
        }
        try {
            defect.signup();
            fail();
        } catch (UnsupportedOperationException e) {
            // Do nothing
        }
        try {
            defect.revertChanges();
            fail();
        } catch (UnsupportedOperationException e) {
            // Do nothing
        }
        assertEquals("NULL", defect.getId());
        assertEquals(Defect, defect.getType());
        assertTrue(defect.hasChanges());
        assertFalse(defect.isMine());
        assertFalse(defect.isPropertyReadOnly(Entity.NAME_PROPERTY));
        assertEquals("", defect.getPropertyAsString(Entity.NAME_PROPERTY));
        defect.setProperty(Entity.NAME_PROPERTY, "NewName53765");
        assertEquals("NewName53765", defect.getPropertyAsString(Entity.NAME_PROPERTY));
        assertEquals("", defect.getPropertyAsString(Entity.STATUS_PROPERTY));
        defect.setProperty(Entity.STATUS_PROPERTY, "StoryStatus:133");
        assertEquals("Future", defect.getPropertyAsString(Entity.STATUS_PROPERTY));

        assertTrue(data.getWorkitemTree().contains(defect));
    }

    @Ignore("This test is integrational. It works with V1 server.")
    @Test
    public void testCreateAndGetTask() throws Exception {
        final ApiDataLayer data = ApiDataLayer.getInstance();
        data.addProperty("Name", Task, false);
        data.addProperty("Owners", Task, true);
        data.addProperty("Status", Task, true);
        data.connect(V1_PATH, V1_USER, V1_PASSWORD, false);
        final Entity story = data.getWorkitemTree().get(0);
        final Entity task = data.createNewWorkitem(Task, story);
        assertEquals(story, task.parent);
        assertEquals(0, task.children.size());
        assertFalse(task.canQuickClose());
        assertFalse(task.canSignup());
        try {
            task.close();
            fail();
        } catch (UnsupportedOperationException e) {
            // Do nothing
        }
        try {
            task.quickClose();
            fail();
        } catch (UnsupportedOperationException e) {
            // Do nothing
        }
        try {
            task.signup();
            fail();
        } catch (UnsupportedOperationException e) {
            // Do nothing
        }
        try {
            task.revertChanges();
            fail();
        } catch (UnsupportedOperationException e) {
            // Do nothing
        }
        assertEquals("NULL", task.getId());
        assertEquals(Task, task.getType());
        assertTrue(task.hasChanges());
        assertFalse(task.isMine());
        assertFalse(task.isPropertyReadOnly(Entity.NAME_PROPERTY));
        assertEquals("", task.getPropertyAsString(Entity.NAME_PROPERTY));
        task.setProperty(Entity.NAME_PROPERTY, "NewName53765");
        assertEquals("NewName53765", task.getPropertyAsString(Entity.NAME_PROPERTY));
        assertEquals("", task.getPropertyAsString(Entity.STATUS_PROPERTY));
        task.setProperty(Entity.STATUS_PROPERTY, "TaskStatus:123");
        assertEquals("In Progress", task.getPropertyAsString(Entity.STATUS_PROPERTY));

        Entity story2 = data.getWorkitemTree().get(0);
        assertTrue(story2.children.contains(task));
    }

    @Ignore("Intergational test")
    @Test
    public void testCreateChild() throws Exception {
        final ApiDataLayer data = ApiDataLayer.getInstance();
        data.addProperty("Name", Task, false);
        data.addProperty("Owners", Task, true);
        data.addProperty("Status", Task, true);
        data.connect(V1_PATH, V1_USER, V1_PASSWORD, false);
        Entity story = data.getWorkitemTree().get(0);
        final Entity test = story.createChild(Test);
        assertTrue(story.children.contains(test));
        assertEquals(story, test.parent);
        assertEquals(0, test.children.size());
        final Entity task = story.createChild(Task);
        assertTrue(story.children.contains(task));
        assertEquals(story, test.parent);
        assertEquals(0, test.children.size());

        // Retrieve new Workitem tree
        story = data.getWorkitemTree().get(0);
        assertTrue(story.children.contains(test));
        assertEquals(story, test.parent);
        assertEquals(0, test.children.size());
        assertTrue(story.children.contains(task));
        assertEquals(story, test.parent);
        assertEquals(0, test.children.size());

        try {
            story.createChild(Story);
            fail("Story allow to create child story");
        } catch (IllegalArgumentException e) {
            // Do nothing
        }
        try {
            story.createChild(Defect);
            fail("Story allow to create child defect");
        } catch (IllegalArgumentException e) {
            // Do nothing
        }
        try {
            story.createChild(Scope);
            fail("Story allow to create child project");
        } catch (IllegalArgumentException e) {
            // Do nothing
        }
    }
}
