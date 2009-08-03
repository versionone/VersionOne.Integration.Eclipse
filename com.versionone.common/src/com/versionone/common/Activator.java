package com.versionone.common;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.sun.xml.internal.fastinfoset.sax.Properties;
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
        dataLayer.connect("http://jsdksrv01:8080/VersionOne/", "admin", "admin", false);

        for (String key : Workitem.properties.keySet()) {
            dataLayer.addProperty(key, Workitem.DefectPrefix, Workitem.properties.get(key));
            dataLayer.addProperty(key, Workitem.TestPrefix, Workitem.properties.get(key));
            dataLayer.addProperty(key, Workitem.StoryPrefix, Workitem.properties.get(key));
            dataLayer.addProperty(key, Workitem.TaskPrefix, Workitem.properties.get(key));
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
