package io.segmentme.helpers.context.processor.helper;

import io.segmentme.helpers.context.processor.ExtractorConfiguration;
import io.segmentme.helpers.context.processor.NodeDescriptor;
import io.segmentme.helpers.context.processor.SchemaDescriptor;
import io.segmentme.models.shared.analysis.SchemaNodeType;
import lombok.Data;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class ExtractorConfigurationHelper {

    public SchemaDescriptor<NodeDescriptorImpl> getSchemaDescriptor() {
        SchemaDescriptorImpl schema = new SchemaDescriptorImpl();

        NodeDescriptorImpl rootNode = new NodeDescriptorImpl();
        schema.setRootNode(rootNode);

        NodeDescriptorImpl user = new NodeDescriptorImpl();
        user.setPath("user");
        user.setName("user");
        user.setType(SchemaNodeType.OBJECT);

        NodeDescriptorImpl userDetails = new NodeDescriptorImpl();
        userDetails.setPath("user.details");
        userDetails.setName("details");
        userDetails.setType(SchemaNodeType.OBJECT);
        user.setNodes(Arrays.asList(userDetails));

        NodeDescriptorImpl birthDate = new NodeDescriptorImpl();
        birthDate.setPath("user.details.birthDate");
        birthDate.setName("birthDate");
        birthDate.setType(SchemaNodeType.DATE);
        userDetails.setNodes(Arrays.asList(birthDate));


        NodeDescriptorImpl objectArrays = new NodeDescriptorImpl();
        objectArrays.setPath("objectArrays");
        objectArrays.setName("objectArrays");
        objectArrays.setType(SchemaNodeType.ARRAY);
        objectArrays.setSubType(SchemaNodeType.OBJECT);

        NodeDescriptorImpl details = new NodeDescriptorImpl();
        details.setPath("objectArrays.dateTime");
        details.setName("dateTime");
        details.setType(SchemaNodeType.DATE);
        objectArrays.setNodes(Arrays.asList(details));


        rootNode.setNodes(Arrays.asList(user, objectArrays));

        return schema;
    }

    public ExtractorConfiguration defaultWorkspaceConfiguration() {
        return () -> Arrays.asList(
            DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern(),
            DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern(),
            DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern() + "'Z'",
            DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern(),
            DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern() + "'Z'",
            DateFormatUtils.ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT.getPattern(),
            DateFormatUtils.SMTP_DATETIME_FORMAT.getPattern());
    }

    @Data
    public static class SchemaDescriptorImpl implements SchemaDescriptor<NodeDescriptorImpl> {
        private NodeDescriptorImpl rootNode;
    }


    @Data
    public static class NodeDescriptorImpl implements NodeDescriptor<NodeDescriptorImpl> {
        private List<NodeDescriptorImpl> nodes;
        private String name;
        private String path;
        private SchemaNodeType type;
        private SchemaNodeType subType;

    }
}
