package com.versionone.common.sdk;

import com.versionone.apiclient.IAssetType;
import com.versionone.common.sdk.Workitem;

import org.junit.Assert;

public class WorkitemTester {

    @org.junit.Test
    public void StoryConstructorTest(){
        ApiDataLayer data = ApiDataLayer.getInstance();
        data.showAllTasks = true;
        IAssetType storyType = new AssetTypeMock(Workitem.StoryPrefix);
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

    @org.junit.Test
    public void ProjectConstructorTest(){
        ApiDataLayer data = ApiDataLayer.getInstance();
        data.showAllTasks = false;
        IAssetType prjType = new AssetTypeMock(Workitem.ProjectPrefix);
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
