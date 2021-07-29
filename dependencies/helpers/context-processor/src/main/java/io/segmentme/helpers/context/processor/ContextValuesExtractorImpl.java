package io.segmentme.helpers.context.processor;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.models.shared.analysis.SchemaNodeType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.IteratorUtils;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static io.segmentme.models.shared.analysis.SchemaNodeType.getPossibleSchemaNodeTypes;

@RequiredArgsConstructor
@Slf4j
public class ContextValuesExtractorImpl implements ContextValuesExtractor {


    @Override
    public <T extends SchemaDescriptor> ContextValueHolder<T> extractValues(JsonNode rawContext, T schemaDescriptor, ExtractorConfiguration workspaceConfiguration) {
        List<DateTimeFormatter> dateFormats = Optional.ofNullable(workspaceConfiguration)
            .map(it -> it.toDateFormatters(it.getDateFormats()))
            .orElseGet(ArrayList::new);


        ContextValueHolder<T> context = new ContextValueHolder<>();
        context.setValues(new HashMap<>());
        context.setSchema(schemaDescriptor);
        rawContext.fields().forEachRemaining(it -> {
            List<NodeDescriptor> schemaNodes = Optional.ofNullable(schemaDescriptor).map(SchemaDescriptor::getRootNode).map(NodeDescriptor::getNodes).orElse(null);
            buildValuesMap(it.getKey(), it.getValue(), schemaNodes, context.getValues(), dateFormats);
        });
        return context;
    }

    private void buildValuesMap(String path, JsonNode value, List<NodeDescriptor> schemaNodes, Map<String, Object> values, List<DateTimeFormatter> dateFormats) {
        Optional<NodeDescriptor> NodeDescriptor = Optional.ofNullable(schemaNodes).orElseGet(ArrayList::new).stream().filter(it -> it.getName().equalsIgnoreCase(path)).findFirst();
        getNodeValue(value, NodeDescriptor.orElseGet(() -> new NodeDescriptorProxy().setPath(path)
            .setType(getPossibleSchemaNodeTypes(value.getNodeType()).get(0))), values, dateFormats);
    }


    private void getNodeValue(JsonNode value, NodeDescriptor nodeDescriptor, Map<String, Object> values, List<DateTimeFormatter> dateFormats) {
        SchemaNodeType resolvedType = getPotentialSchemaNodeType(value, nodeDescriptor.getType());
        switch (resolvedType) {
            case STRING, NUMBER, BOOLEAN, DATE -> values.put(nodeDescriptor.getPath(), getComparableValue(value, resolvedType, dateFormats));
            case OBJECT -> value.fields().forEachRemaining(objectField -> collectObjectValues(nodeDescriptor, values, objectField, dateFormats));
            case ARRAY -> resolveArrayItems(nodeDescriptor.getPath(), nodeDescriptor, value, values, dateFormats);
            default -> log.debug("Unexpected node type: {}", resolvedType);
        }
    }

    private SchemaNodeType getPotentialSchemaNodeType(JsonNode value, SchemaNodeType NodeDescriptor) {
        return Optional.ofNullable(NodeDescriptor).orElseGet(() -> getPossibleSchemaNodeTypes(value.getNodeType()).get(0));
    }

    private void resolveArrayItems(String path, NodeDescriptor<?> nodeDescriptor, JsonNode array, Map<String, Object> values, List<DateTimeFormatter> dateFormats) {
        List<JsonNode> arrayItems = IteratorUtils.toList(array.elements());
        Map<String, List<Object>> objects = new HashMap<>();
        arrayItems.forEach(element -> {
            SchemaNodeType subtype = getPotentialSchemaNodeType(element, nodeDescriptor.getSubType());
            switch (subtype) {
                case STRING, NUMBER, BOOLEAN, DATE -> {
                    putValue(objects, path, getComparableValue(element, subtype, dateFormats));
                }
                case OBJECT -> {
                    Map<String, Object> objectValues = new HashMap<>();
                    element.fields().forEachRemaining(objectField -> collectObjectValues(nodeDescriptor, objectValues, objectField, dateFormats));
                    objectValues.forEach((key, value) -> putValue(objects, key, value));
                }
                default -> log.debug("Unexpected node type: {}", subtype);
            }
        });
        values.putAll(objects);
    }

    private void collectObjectValues(NodeDescriptor nodeDescriptor, Map<String, Object> objectValues, Map.Entry<String, JsonNode> objectField, List<DateTimeFormatter> dateFormats) {
        buildValuesMap(Optional.ofNullable(nodeDescriptor.getNodes()).map(subnodes -> objectField.getKey()).orElse(nodeDescriptor.getPath() + "." + objectField.getKey()), objectField.getValue(), nodeDescriptor.getNodes(), objectValues, dateFormats);
    }

    private void putValue(Map<String, List<Object>> objects, String key, Object value) {
        List<Object> collectedValues = objects.getOrDefault(key, new ArrayList<>());
        collectedValues.add(value);
        objects.putIfAbsent(key, collectedValues);
    }

    public Comparable<?> getComparableValue(JsonNode value, SchemaNodeType type, List<DateTimeFormatter> dateFormats) {
        return switch (type) {
            case STRING -> value.asText();
            case NUMBER -> Optional.ofNullable(value.numberValue()).map(Number::doubleValue).orElse(null);
            case BOOLEAN -> value.booleanValue();
            case DATE -> {
                Optional<Instant> resolve = DateResolver.resolve(value.asText(), dateFormats);
                if (resolve.isPresent()) {
                    yield resolve.get();
                } else {
                    yield value.asText();
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }


    @Data
    private static class NodeDescriptorProxy implements NodeDescriptor {
        private String path;

        private SchemaNodeType type;

        @Override
        public List<NodeDescriptor> getNodes() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }


        @Override
        public SchemaNodeType getSubType() {
            return null;
        }

    }
}
