package com.versionone.common.sdk;

import static org.junit.Assert.*;

import com.versionone.apiclient.IAssetType;
import com.versionone.common.sdk.Workitem;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This is unit test and it must be run as JUnit test 
 * (NOT as JUnit Plug-in Test).
 * 
 * @author rozhnev
 */
public class WorkitemTester {

    @Test
    public void StoryConstructorTest() {
        ApiDataLayer data = ApiDataLayer.getInstance();
        data.showAllTasks = true;
        IAssetType storyType = new AssetTypeMock(Workitem.STORY_PREFIX);
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
        data.showAllTasks = false;
        IAssetType prjType = new AssetTypeMock(Workitem.PROJECT_PREFIX);
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

    @Ignore("Not yet implemented")
    @Test
    public void testVirtualStory() throws DataLayerException {
        final IAssetType storyType = new AssetTypeMock(Workitem.STORY_PREFIX);
        final Workitem story = new Workitem(new AssetMock(storyType), null);
        final Workitem test = story.createChild(Workitem.TEST_PREFIX);
        assertTrue(story.children.contains(test));
        assertEquals(story, test.parent);
        assertEquals(0, test.children.size());
        final Workitem task = story.createChild(Workitem.TASK_PREFIX);
        assertTrue(story.children.contains(task));
        assertEquals(story, test.parent);
        assertEquals(0, test.children.size());
        
        try {
            story.createChild(Workitem.STORY_PREFIX);
            fail("Story allow to create child story");
        } catch (IllegalArgumentException e) {
            // Do nothing
        }
        try {
            story.createChild(Workitem.DEFECT_PREFIX);
            fail("Story allow to create child defect");
        } catch (IllegalArgumentException e) {
            // Do nothing
        }
        try {
            story.createChild(Workitem.PROJECT_PREFIX);
            fail("Story allow to create child project");
        } catch (IllegalArgumentException e) {
            // Do nothing
        }
        try {
            story.createChild("Wrong");
            fail("Story allow to call createChild with wrong type");
        } catch (IllegalArgumentException e) {
            // Do nothing
        }
    }
    
    @Ignore("Not yet implemented")
    @Test
    public void testVirtualDefect() throws DataLayerException {
        final IAssetType storyType = new AssetTypeMock(Workitem.DEFECT_PREFIX);
        final Workitem story = new Workitem(new AssetMock(storyType), null);
        final Workitem test = story.createChild(Workitem.TEST_PREFIX);
        assertTrue(story.children.contains(test));
        assertEquals(story, test.parent);
        assertEquals(0, test.children.size());
        final Workitem task = story.createChild(Workitem.TASK_PREFIX);
        assertTrue(story.children.contains(task));
        assertEquals(story, test.parent);
        assertEquals(0, test.children.size());
        
        try {
            story.createChild(Workitem.STORY_PREFIX);
            fail("Defect allow to create child story");
        } catch (IllegalArgumentException e) {
            // Do nothing
        }
        try {
            story.createChild(Workitem.DEFECT_PREFIX);
            fail("Defect allow to create child defect");
        } catch (IllegalArgumentException e) {
            // Do nothing
        }
        try {
            story.createChild(Workitem.PROJECT_PREFIX);
            fail("Defect allow to create child project");
        } catch (IllegalArgumentException e) {
            // Do nothing
        }
        try {
            story.createChild("Wrong");
            fail("Defect allow to call createChild with wrong type");
        } catch (IllegalArgumentException e) {
            // Do nothing
        }
    }
}