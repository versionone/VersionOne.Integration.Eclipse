package com.versionone.taskview.views.properties;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.Activator;

@XmlRootElement(name = "Configuration")
public class Configuration {

    private static Configuration configuration;

    @XmlElement(name = "APIVersion")
    public final String apiVersion = "8.3";
    // private GridSettings gridSettings = new GridSettings();
    @XmlElement(name = "AssetDetail")
    public final AssetDetailSettings assetDetailSettings = new AssetDetailSettings();
    @XmlElement(name = "ProjectTree")
    public final ProjectTreeSettings projectTreeSettings = new ProjectTreeSettings();

    public static Configuration getInstance() {
        if (configuration == null) {
            InputStream stream = null;
            try {
                final Class<Configuration> thisClass = Configuration.class;
                JAXBContext jc = JAXBContext.newInstance(thisClass);

                Unmarshaller um = jc.createUnmarshaller();
                stream = thisClass.getResourceAsStream(thisClass.getSimpleName() + ".xml");
                configuration = (Configuration) um.unmarshal(stream);
            } catch (JAXBException e) {
                Activator.logError("Cannot load configuration", e);
                configuration = new Configuration(); 
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        // Do nothing
                    }
                }
            }
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

        @XmlElementWrapper(name = "TaskColumns")
        @XmlElement(name = "ColumnSetting")
        public final ColumnSetting[] taskColumns;
        @XmlElementWrapper(name = "StoryColumns")
        @XmlElement(name = "ColumnSetting")
        public final ColumnSetting[] storyColumns;
        @XmlElementWrapper(name = "TestColumns")
        @XmlElement(name = "ColumnSetting")
        public final ColumnSetting[] testColumns;
        @XmlElementWrapper(name = "DefectColumns")
        @XmlElement(name = "ColumnSetting")
        public final ColumnSetting[] defectColumns;

        public AssetDetailSettings() {
            taskColumns = new ColumnSetting[8];
            testColumns = new ColumnSetting[1];
            storyColumns = new ColumnSetting[1];
            defectColumns = new ColumnSetting[1];

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
            taskColumns[6] = new ColumnSetting("ColumnTitle'ID", STRING_TYPE, "Number", MAIN_CATEGORY, true, false);
            taskColumns[7] = new ColumnSetting("ColumnTitle'Effort", STRING_TYPE, "Actuals", MAIN_CATEGORY, false, true);

            storyColumns[0] = new ColumnSetting("ColumnTitle'Status", LIST_TYPE, "Status", EXTENDED_CATEGORY, false,
                    false);

            testColumns[0] = new ColumnSetting("ColumnTitle'Status", LIST_TYPE, "Status", EXTENDED_CATEGORY, false,
                    false);

            defectColumns[0] = new ColumnSetting("ColumnTitle'Status", LIST_TYPE, "Status", EXTENDED_CATEGORY, false,
                    false);
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

    public static class ProjectTreeSettings {

        @XmlElementWrapper(name = "Columns")
        @XmlElement(name = "ColumnSetting")
        public final ColumnSetting[] taskColumns;

        ProjectTreeSettings() {
            taskColumns = new ColumnSetting[0];
        }
    }

    public static class ColumnSetting {
        @XmlElement(name = "Name")
        public final String name;
        @XmlElement(name = "Category")
        public final String category;
        @XmlElement(name = "Type")
        public final String type;
        @XmlElement(name = "Attribute")
        public final String attribute;
        @XmlElement(name = "ReadOnly", defaultValue = "false")
        public final boolean readOnly;
        @XmlElement(name = "EffortTracking", defaultValue = "false")
        public final boolean effortTracking;
        @XmlElement(name = "Name", defaultValue = "100", required = false)
        public int width = 100;

        public ColumnSetting() {
            name = category = type = attribute = null;
            readOnly = effortTracking = false;
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