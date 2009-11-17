package com.versionone.common.sdk;


import com.versionone.apiclient.IAssetType;
import com.versionone.common.sdk.Workitem;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static com.versionone.common.sdk.WorkitemType.*;

import static org.junit.Assert.*;

/**
 * This is unit test and it must be run as JUnit test (NOT as JUnit Plug-in
 * Test).
 * 
 * @author rozhnev
 */
public class WorkitemTester implements IntegrationalTest {

    @Test
    public void StoryConstructorTest() {
        ApiDataLayer data = ApiDataLayer.getInstance();
        data.setShowAllTasks(true);
        IAssetType storyType = new AssetTypeMock(Story.name());
        AssetMock asset1 = new AssetMock(storyType);
        AssetMock asset11 = new AssetMock(storyType);
        asset1.children.add(asset11);
        Workitem item1 = new Workitem(asset1, null);
        Workitem item11 = new Workitem(asset11, item1);
        Assert.assertEquals(null, item1.parent);
        Assert.assertEquals(item1, item11.parent);
        Assert.assertTrue(item1.children.contains(item11));
        Assert.assertEquals(0, item11.children.size());
    }

    @Test
    public void ProjectConstructorTest() {
        ApiDataLayer data = ApiDataLayer.getInstance();
        data.setShowAllTasks(false);
        IAssetType prjType = new AssetTypeMock(Scope.name());
        AssetMock asset1 = new AssetMock(prjType);
        AssetMock asset11 = new AssetMock(prjType);
        asset1.children.add(asset11);
        Workitem item1 = new Workitem(asset1, null);
        Workitem item11 = new Workitem(asset11, item1);
        Assert.assertEquals(null, item1.parent);
        Assert.assertEquals(item1, item11.parent);
        Assert.assertTrue(item1.children.contains(item11));
        Assert.assertEquals(0, item11.children.size());
    }

    @Ignore("Intergational test")
    @Test
    public void testCreateChild() throws Exception {
        final ApiDataLayer data = ApiDataLayer.getInstance();
        data.addProperty("Name", Task, false);
        data.addProperty("Owners", Task, true);
        data.addProperty("Status", Task, true);
        data.connect(V1_PATH, V1_USER, V1_PASSWORD, false);
        Workitem story = data.getWorkitemTree().get(0);
        final Workitem test = story.createChild(Test);
        assertTrue(story.children.contains(test));
        assertEquals(story, test.parent);
        assertEquals(0, test.children.size());
        final Workitem task = story.createChild(Task);
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