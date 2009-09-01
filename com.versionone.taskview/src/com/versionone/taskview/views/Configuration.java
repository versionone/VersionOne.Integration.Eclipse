package com.versionone.taskview.views;

import com.versionone.common.sdk.Workitem;

public class Configuration {

    private static final String CONFIGURATION_FILE = "configuration.xml";

    // private GridSettings gridSettings = new GridSettings();
    public final AssetDetailSettings assetDetailSettings;
    public String apiVersion = "8.3";
    private static Configuration configuration;

    public Configuration() {
        // TODO temporary
        assetDetailSettings = new AssetDetailSettings();
    }

    public static Configuration getInstance() {
        if (configuration == null) {
            // TODO load from XML
            configuration = new Configuration();

//          JAXBContext jc = JAXBContext.newInstance(Configuration.class);
        }
        return configuration;
    }

    public static class AssetDetailSettings {

        public static final String EXTENDED_CATEGORY = "Extended";
        public static final String MAIN_CATEGORY = "Main";

        public static final String LIST_TYPE = "List";
        public static final String MULTI_VALUE_TYPE = "Multi";
        public static final String RICH_TEXT_TYPE = "RichText";
        public static final String STRING_TYPE = "String";

        public final ColumnSetting[] taskColumns;
        public final ColumnSetting[] storyColumns;
        public final ColumnSetting[] testColumns;
        public final ColumnSetting[] defectColumns;
        public final ColumnSetting[] projectColumns;

        public AssetDetailSettings() {
            // TODO temporary
            taskColumns = new ColumnSetting[7];
            testColumns = new ColumnSetting[1];
            storyColumns = new ColumnSetting[1];
            defectColumns = new ColumnSetting[1];
            projectColumns = new ColumnSetting[0];

            taskColumns[0] = new ColumnSetting("ColumnTitle'Title", STRING_TYPE, "Name", MAIN_CATEGORY, false, false);
            taskColumns[1] = new ColumnSetting("ColumnTitle'Description", RICH_TEXT_TYPE, "Description", MAIN_CATEGORY,
                    false, false);
            taskColumns[2] = new ColumnSetting("ColumnTitle'Project", STRING_TYPE, "Scope.Name", MAIN_CATEGORY, false,
                    false);
            taskColumns[3] = new ColumnSetting("ColumnTitle'Parent", STRING_TYPE, "Parent.Name", MAIN_CATEGORY, false,
                    false);
            taskColumns[4] = new ColumnSetting("ColumnTitle'Owner", MULTI_VALUE_TYPE, "Owners", EXTENDED_CATEGORY,
                    false, false);
            taskColumns[5] = new ColumnSetting("ColumnTitle'Status", LIST_TYPE, "Status", EXTENDED_CATEGORY, false,
                    false);
            taskColumns[6] = new ColumnSetting("ColumnTitle'ID", STRING_TYPE, "Number", MAIN_CATEGORY, true,
                    false);
            
            storyColumns[0] = new ColumnSetting("ColumnTitle'Status", LIST_TYPE, "Status", EXTENDED_CATEGORY, false, false);
            
            testColumns[0] = new ColumnSetting("ColumnTitle'Status", LIST_TYPE, "Status", EXTENDED_CATEGORY, false, false);
            
            defectColumns[0] = new ColumnSetting("ColumnTitle'Status", LIST_TYPE, "Status", EXTENDED_CATEGORY, false, false);

        }

        public ColumnSetting[] getColumns(String type) {
            if (type.equals(Workitem.STORY_PREFIX)) {
                return storyColumns;
            } else if (type.equals(Workitem.TASK_PREFIX)) {
                return taskColumns;
            } else if (type.equals(Workitem.DEFECT_PREFIX)) {
                return defectColumns;
            } else if (type.equals(Workitem.TEST_PREFIX)) {
                return testColumns;
            } else if (type.equals(Workitem.PROJECT_PREFIX)) {
                return testColumns;
            } else {
                throw new IllegalArgumentException("Unknown type: " + type);
            }
        }
    }

    public static class ColumnSetting {
        public String name;
        public String type;
        public String attribute;
        public String category;
        public boolean readOnly;
        public boolean effortTracking;
        public int width = 100;

        public ColumnSetting() {
        }
        
        ColumnSetting(String name, String type, String attribute, String category, boolean readOnly,
                boolean effortTracking) {
            super();
            this.name = name;
            this.type = type;
            this.attribute = attribute;
            this.category = category;
            this.readOnly = readOnly;
            this.effortTracking = effortTracking;
        }
    }
}