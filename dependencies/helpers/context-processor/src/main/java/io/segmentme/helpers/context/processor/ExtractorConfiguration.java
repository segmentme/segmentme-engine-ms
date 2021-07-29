package io.segmentme.helpers.context.processor;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public interface ExtractorConfiguration {

    List<String> getDateFormats();

    default List<DateTimeFormatter> toDateFormatters(List<String> formats) {
        return formats
            .stream()
            .map(DateTimeFormatter::ofPattern)
            .map(it -> it.withZone(ZoneId.systemDefault()))
            .collect(Collectors.toList());
    }

}
