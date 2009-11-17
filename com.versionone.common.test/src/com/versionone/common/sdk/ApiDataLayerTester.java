package com.versionone.common.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

public class ApiDataLayerTester implements IntegrationalTest {
    
//    @Ignore("This test is integrational. It works with V1 server.")
    @Test
    public void testCreateTwoWorkitem() throws Exception {
        final ApiDataLayer data = ApiDataLayer.getInstance();
        data.addProperty("Name", "Task", false);
        data.addProperty("Owners", "Task", true);
        data.addProperty("Status", "Task", true);
        data.connect(V1_PATH, V1_USER, V1_PASSWORD, false);
        data.setCurrentProjectId("Scope:1002");
        Workitem story = data.getWorkitemTree().get(0);
        Workitem story1 = data.getWorkitemTree().get(1);
        Workitem task = data.createWorkitem(Workitem.TASK_PREFIX, story);
        Workitem task1 = data.createWorkitem(Workitem.TASK_PREFIX, story1);
        assertEquals(story, task.parent);
        assertEquals(story1, task1.parent);

        assertEquals("", task.getPropertyAsString(Workitem.NAME_PROPERTY));
        assertEquals("", task1.getPropertyAsString(Workitem.NAME_PROPERTY));
        task.setProperty(Workitem.NAME_PROPERTY, "Task");
        task1.setProperty(Workitem.NAME_PROPERTY, "Task1");
        assertEquals("Task", task.getPropertyAsString(Workitem.NAME_PROPERTY));
        assertEquals("Task1", task1.getPropertyAsString(Workitem.NAME_PROPERTY));

        story = data.getWorkitemTree().get(0);
        assertEquals(1, story.children.size());
        task = story.children.get(0);
        assertEquals("Task", task.getPropertyAsString(Workitem.NAME_PROPERTY));

        story1 = data.getWorkitemTree().get(1);
        assertEquals(1, story1.children.size());
        task1 = story1.children.get(0);
        assertEquals("Task1", task1.getPropertyAsString(Workitem.NAME_PROPERTY));
}
    
    @Ignore("This test is integrational. It works with V1 server.")
    @Test
    public void testCreateAndGetWorkitem() throws Exception {
        final ApiDataLayer data = ApiDataLayer.getInstance();
        data.addProperty("Name", "Task", false);
        data.addProperty("Owners", "Task", true);
        data.addProperty("Status", "Task", true);
        data.connect(V1_PATH, V1_USER, V1_PASSWORD, false);
        final Workitem story = data.getWorkitemTree().get(0);
        final Workitem task = data.createWorkitem(Workitem.TASK_PREFIX, story);
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
        assertEquals(Workitem.TASK_PREFIX, task.getTypePrefix());
        assertTrue(task.hasChanges());
        assertFalse(task.isMine());
        assertFalse(task.isPropertyReadOnly(Workitem.NAME_PROPERTY));
        assertEquals("", task.getPropertyAsString(Workitem.NAME_PROPERTY));
        task.setProperty(Workitem.NAME_PROPERTY, "NewName53765");
        assertEquals("NewName53765", task.getPropertyAsString(Workitem.NAME_PROPERTY));
        assertEquals("", task.getPropertyAsString(Workitem.STATUS_PROPERTY));
        task.setProperty(Workitem.STATUS_PROPERTY, "TaskStatus:123");
        assertEquals("In Progress", task.getPropertyAsString(Workitem.STATUS_PROPERTY));
        
        Workitem story2 = data.getWorkitemTree().get(0);
        assertTrue(story2.children.contains(task));
    }
}
