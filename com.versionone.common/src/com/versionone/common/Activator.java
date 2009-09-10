package com.versionone.common;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.versionone.common.preferences.PreferenceConstants;
import com.versionone.common.preferences.PreferencePage;
import com.versionone.common.sdk.ApiDataLayer;

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
    }
    
    /**
     * connect to the VersionOne server with current user preference
     * @throws Exception
     */
    public static void connect() {
        final IPreferenceStore pref = PreferencePage.getPreferences();
        String path = pref.getString(PreferenceConstants.P_URL);
        String user = pref.getString(PreferenceConstants.P_USER);
        String password = pref.getString(PreferenceConstants.P_PASSWORD);
        boolean auth = pref.getBoolean(PreferenceConstants.P_INTEGRATED_AUTH);
        
        connect(user, password, path, auth);
        ApiDataLayer.getInstance().setCurrentProjectId(pref.getString(PreferenceConstants.P_PROJECT_TOKEN));
    }
    
    public static void connect(String user, String password, String path, boolean auth) {
        try {
            ApiDataLayer.getInstance().connect(path, user, password, auth);
        } catch (Exception e) {
            Activator.logError(e);
        }
        boolean showAllTask = PreferencePage.getPreferences().getInt(PreferenceConstants.P_WORKITEM_FILTER_SELECTION) == 1 ? false : true;
        ApiDataLayer.getInstance().showAllTasks = showAllTask;        
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
