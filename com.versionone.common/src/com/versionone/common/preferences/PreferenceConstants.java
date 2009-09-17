package com.versionone.common.preferences;

public class PreferenceConstants {

    /**
     * Is the integration enabled
     */
    public static final String P_ENABLED = "enabled";

    /**
     * Stores the complete server url
     */
    public static final String P_URL = "serverURL";

    /**
     * Stores the user id
     */
    public static final String P_USER = "userId";

    /**
     * Stores the password
     */
    public static final String P_PASSWORD = "credentials";

    /**
     * Determines if the parameters are valid
     */
    public static final String P_REQUIRESVALIDATION = "needsValidation";

    /**
     * Use Windows Integrated Authentication
     */
    public static final String P_INTEGRATED_AUTH = "useIntegratedAuth";

    /**
     * VersionOne token for user + url to VersionOne server for uniq value
     */
    public static final String P_MEMBER_TOKEN = "memberToken";

    /**
     * VersionONe token for the selected project
     */
    public static final String P_PROJECT_TOKEN = "projectToken";

    /**
     * These are the attributes we select from VersionOne for Tasks
     */
    public static final String P_ATTRIBUTE_SELECTION = "attributeSelection";

    /**
     * Filter for workitem (show only assigned task or all)
     */
    public static final String P_ONLY_USER_WORKITEMS = "onlyUserworkitems";

}
