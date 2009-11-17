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
public class WorkitemTester {

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
}