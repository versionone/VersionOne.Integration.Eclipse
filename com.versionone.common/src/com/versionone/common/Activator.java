package com.versionone.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.versionone.common.preferences.PreferenceConstants;
import com.versionone.common.preferences.PreferencePage;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.Workitem;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "com.versionone.common";

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
     * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        setAttributes();
    }

    private void setAttributes() throws Exception {
        ApiDataLayer dataLayer = ApiDataLayer.getInstance();
        Map<String, Boolean> properties = new HashMap<String, Boolean>();
        properties.put(Workitem.ID_PROPERTY, false);
        properties.put(Workitem.DETAIL_ESTIMATE_PROPERTY, false);
        properties.put(Workitem.NAME_PROPERTY, false);
        properties.put(Workitem.STATUS_PROPERTY, true);
        properties.put(Workitem.EFFORT_PROPERTY, false);
        properties.put(Workitem.DONE_PROPERTY, false);
        properties.put(Workitem.DESCRIPTION_PROPERTY, false);
        //properties.put(Workitem.ScheduleNameProperty, false);
        properties.put(Workitem.OWNERS_PROPERTY, true);
        properties.put(Workitem.TODO_PROPERTY, false);
        properties.put("Scope.Name", false);
        for (Entry<String, Boolean> entry : properties.entrySet()) {
            dataLayer.addProperty(entry.getKey(), Workitem.DEFECT_PREFIX, entry.getValue());
            dataLayer.addProperty(entry.getKey(), Workitem.TEST_PREFIX, entry.getValue());
            dataLayer.addProperty(entry.getKey(), Workitem.STORY_PREFIX, entry.getValue());
            dataLayer.addProperty(entry.getKey(), Workitem.TASK_PREFIX, entry.getValue());
        }
        dataLayer.addProperty(Workitem.NAME_PROPERTY, Workitem.PROJECT_PREFIX, false);
        connect();
    }
    
    /**
     * connect to the VersionOne server with current user preference
     * @throws Exception
     */
    public static void connect() {
        String path = PreferencePage.getPreferences().getString(PreferenceConstants.P_URL);
        String user = PreferencePage.getPreferences().getString(PreferenceConstants.P_USER);
        String password = PreferencePage.getPreferences().getString(PreferenceConstants.P_PASSWORD);
        boolean auth = Boolean.valueOf(PreferencePage.getPreferences().getBoolean(PreferenceConstants.P_INTEGRATED_AUTH));
        
        try {
            ApiDataLayer.getInstance().connect(path, user, password, auth);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ApiDataLayer.getInstance().setCurrentProjectId(PreferencePage.getPreferences().getString(PreferenceConstants.P_PROJECT_TOKEN));
        boolean showAllTask = PreferencePage.getPreferences().getInt(PreferenceConstants.P_WORKITEM_FILTER_SELECTION) == 1 ? false : true;
        ApiDataLayer.getInstance().setShowAllTasks(showAllTask);
    }
    
    public static void connect(String user, String password, String path, boolean auth) {
        try {
            ApiDataLayer.getInstance().connect(path, user, password, auth);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
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

    public static void logError(Throwable t) {
        logError("Unexpected Error", t);
    }

    public static void logError(String message, Throwable t) {
        log(IStatus.ERROR, IStatus.OK, message, t);
    }

    private static void log(int severity, int code, String message, Throwable t) {
        Activator.getDefault().getLog().log(createStatus(severity, code, message, t));

    }

    private static IStatus createStatus(int severity, int code, String message, Throwable t) {
        return new Status(severity, PLUGIN_ID, code, message, t);
    }
}
