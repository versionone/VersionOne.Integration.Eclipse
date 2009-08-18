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
        properties.put(Workitem.IdProperty, false);
        properties.put(Workitem.DetailEstimateProperty, false);
        properties.put(Workitem.NameProperty, false);
        properties.put(Workitem.StatusProperty, true);
        properties.put(Workitem.EffortProperty, false);
        properties.put(Workitem.DoneProperty, false);
        properties.put(Workitem.DescriptionProperty, false);
        //properties.put(Workitem.ScheduleNameProperty, false);
        properties.put(Workitem.OwnersProperty, true);
        properties.put(Workitem.TodoProperty, false);
        for (Entry<String, Boolean> entry : properties.entrySet()) {
            dataLayer.addProperty(entry.getKey(), Workitem.DefectPrefix, entry.getValue());
            dataLayer.addProperty(entry.getKey(), Workitem.TestPrefix, entry.getValue());
            dataLayer.addProperty(entry.getKey(), Workitem.StoryPrefix, entry.getValue());
            dataLayer.addProperty(entry.getKey(), Workitem.TaskPrefix, entry.getValue());
        }
        dataLayer.addProperty(Workitem.NameProperty, Workitem.ProjectPrefix, false);
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
