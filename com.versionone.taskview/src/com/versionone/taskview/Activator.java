package com.versionone.taskview;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.versionone.taskview";

	// Filter Image Id
	public static final String FILTER_IMAGE_ID = "image.filter";
	public static final String TASK_IMAGE_ID = "image.task";
	public static final String REFRESH_IMAGE_ID = "image.refresh";
	
	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
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

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
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
        registry.put(REFRESH_IMAGE_ID, imageDescriptorFromPlugin(PLUGIN_ID, "icons/refresh.gif"));        
	}
	
}
