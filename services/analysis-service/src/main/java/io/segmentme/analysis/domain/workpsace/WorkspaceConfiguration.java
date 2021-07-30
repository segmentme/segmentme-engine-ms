package io.segmentme.analysis.domain.workpsace;

import io.segmentme.helpers.context.processor.ExtractorConfiguration;
import lombok.Data;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Arrays;
import java.util.List;

@Data
public class WorkspaceConfiguration implements ExtractorConfiguration {

    public static final List<String> DEFAULT_DATE_PATTERNS = Arrays.asList(
        DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern(),
        DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern(),
        DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern() + "'Z'",
        DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern(),
        DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern() + "'Z'",
        DateFormatUtils.ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT.getPattern(),
        DateFormatUtils.SMTP_DATETIME_FORMAT.getPattern());

    private List<String> knownDateFormats;

    @Override
    public List<String> getDateFormats() {
        return knownDateFormats;
    }
}
