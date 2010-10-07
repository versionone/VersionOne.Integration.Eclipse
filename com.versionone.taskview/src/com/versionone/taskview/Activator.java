package com.versionone.taskview;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.Entity;
import com.versionone.common.sdk.EntityType;
import com.versionone.common.sdk.Project;
import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.views.properties.Configuration;
import com.versionone.taskview.views.properties.Configuration.ColumnSetting;

import static com.versionone.common.sdk.EntityType.*;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "com.versionone.taskview";

    // Filter Image Id
    public static final String FILTER_IMAGE_ID = "image.filter";
    public static final String REFRESH_IMAGE_ID = "image.refresh";
    public static final String SAVE_IMAGE_ID = "image.save";
    public static final String FILTER_WORKITEM_IMAGE_ID = "image.workitemfilter";
    public static final String ADD_TASK_ID = "image.addtask";
    public static final String ADD_DEFECT_ID = "image.adddefect";
    public static final String ADD_TEST_ID = "image.addtest";
    public static final String ADD_STORY_ID = "image.addstory";

    public static final String WORKITEM_IMAGE_PREFIX = "image.";
    public static final String TASK_IMAGE_ID = "image.Task";
    public static final String STORY_IMAGE_ID = "image.Story";
    public static final String DEFECT_IMAGE_ID = "image.Defect";
    public static final String TEST_IMAGE_ID = "image.Test";

    // The shared instance
    private static Activator plugin;

    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        setAttributes();
        setDetailsAttributes();
        connect();
    }

    private void setDetailsAttributes() throws Exception {
        Configuration cfg = Configuration.getInstance();
        setAttributes(cfg.assetDetailSettings.defectColumns, Defect);
        setAttributes(cfg.assetDetailSettings.storyColumns, Story);
        setAttributes(cfg.assetDetailSettings.testColumns, Test);
        setAttributes(cfg.assetDetailSettings.taskColumns, Task);
        setAttributes(cfg.projectTreeSettings.projectColumns, Scope);
    }

    private static void setAttributes(ColumnSetting[] columns, EntityType type) {
        ApiDataLayer dataLayer = ApiDataLayer.getInstance();
        for (ColumnSetting entry : columns) {
            dataLayer.addProperty(entry.attribute, type, isListType(entry.type));
        }
    }

    private static boolean isListType(String type) {
        return type.equals(Configuration.AssetDetailSettings.LIST_TYPE)
                || type.equals(Configuration.AssetDetailSettings.MULTI_VALUE_TYPE);
    }

    private void setAttributes() throws Exception {
        final ApiDataLayer dataLayer = ApiDataLayer.getInstance();
        final Map<String, Boolean> properties = new HashMap<String, Boolean>();
        properties.put(Entity.ID_PROPERTY, false);
        properties.put(Entity.NAME_PROPERTY, false);
        properties.put(Workitem.DETAIL_ESTIMATE_PROPERTY, false);
        properties.put(Workitem.STATUS_PROPERTY, true);
        properties.put(Workitem.EFFORT_PROPERTY, false);
        properties.put(Workitem.DONE_PROPERTY, false);
        properties.put(Workitem.DESCRIPTION_PROPERTY, false);
        // properties.put(Workitem.ScheduleNameProperty, false);
        properties.put(Workitem.OWNERS_PROPERTY, true);
        properties.put(Workitem.TODO_PROPERTY, false);
        properties.put(Workitem.CHECK_QUICK_CLOSE_PROPERTY, false);
        properties.put(Workitem.CHECK_SIGNUP_PROPERTY, false);
        properties.put(Workitem.SCOPE_NAME_PROPERTY, false);
        properties.put(Workitem.ORDER_PROPERTY, false);

        for (Entry<String, Boolean> entry : properties.entrySet()) {
            for (EntityType type : EntityType.values()) {
                if (type.isWorkitem()) {
                    dataLayer.addProperty(entry.getKey(), type, entry.getValue());
                }
            }
        }
        dataLayer.addProperty(Project.NAME_PROPERTY, Scope, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    public static void connect() {
        com.versionone.common.Activator.connect();
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path
     * 
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public static void logError(Throwable t) {
        logError("Unexpected Error", t);
    }

    public static void logError(String message, Throwable t) {
        log(IStatus.ERROR, IStatus.OK, message, t);
    }

    public static void logInfo(String message) {
        log(IStatus.INFO, IStatus.OK, message, null);
    }

    public static void logWarning(String message) {
        log(IStatus.WARNING, IStatus.OK, message, null);
    }

    public static void logWarning(String message, Throwable t) {
        log(IStatus.WARNING, IStatus.OK, message, t);
    }

    private static void log(int severity, int code, String message, Throwable t) {
        Activator.getDefault().getLog().log(createStatus(severity, code, message, t));

    }

    private static IStatus createStatus(int severity, int code, String message, Throwable t) {
        return new Status(severity, PLUGIN_ID, code, message, t);
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry registry) {
        registry.put(FILTER_IMAGE_ID, imageDescriptorFromPlugin(PLUGIN_ID, "icons/filter.gif"));
        registry.put(TASK_IMAGE_ID, imageDescriptorFromPlugin(PLUGIN_ID, "icons/task.gif"));
        registry.put(DEFECT_IMAGE_ID, imageDescriptorFromPlugin(PLUGIN_ID, "icons/defect.gif"));
        registry.put(TEST_IMAGE_ID, imageDescriptorFromPlugin(PLUGIN_ID, "icons/test.gif"));
        registry.put(STORY_IMAGE_ID, imageDescriptorFromPlugin(PLUGIN_ID, "icons/story.gif"));
        registry.put(REFRESH_IMAGE_ID, imageDescriptorFromPlugin(PLUGIN_ID, "icons/refresh.gif"));
        registry.put(SAVE_IMAGE_ID, imageDescriptorFromPlugin(PLUGIN_ID, "icons/save.gif"));
        registry.put(FILTER_WORKITEM_IMAGE_ID, imageDescriptorFromPlugin(PLUGIN_ID, "icons/member.gif"));
        registry.put(ADD_TASK_ID, imageDescriptorFromPlugin(PLUGIN_ID, "icons/AddTask.gif"));
        registry.put(ADD_DEFECT_ID, imageDescriptorFromPlugin(PLUGIN_ID, "icons/AddDefect.gif"));
        registry.put(ADD_STORY_ID, imageDescriptorFromPlugin(PLUGIN_ID, "icons/AddStory.gif"));
        registry.put(ADD_TEST_ID, imageDescriptorFromPlugin(PLUGIN_ID, "icons/AddTest.gif"));
    }
}
