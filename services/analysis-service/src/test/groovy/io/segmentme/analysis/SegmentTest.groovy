package io.segmentme.analysis

import io.segmentme.analysis.service.converter.SegmentConverter
import io.segmentme.analysis.service.segment.worm.StatisticWorm


class SegmentTest extends BaseRuleTest {

    def setup() {
        saveAllSegments()
    }

    def cleanup() {
        deleteAllSegments()
    }

    def "analyse segment #name - should be #isMatch"() {
        given:
        def segmentDto = SegmentConverter.of(getSegmentsByName(name))
        and:
        def result = super.analysisService.analyze(context, Arrays.asList(SegmentConverter.of(segmentDto, null, null)), new StatisticWorm())
        expect:
        def singleResult = resultValue(name, result)
        singleResult.value == isMatch
        singleResult.name == name
        where:
        name                                 | isMatch
        "USER EMAIL IN SEGMENT"              | true
        "EMAIL_NOT_IN"                       | false
        "AGE_GT"                             | true
        "AGE_GTE"                            | true
        "BIRTH_DATE_LT"                      | true
        "BIRTH_DATE_LTE"                     | true
        "REGISTERED_DATE_IN_RANGE"           | true
        "REGISTERED_DATE_NOT_IN_RANGE"       | false
        "POSTAL_CODE_CONTAINS_ANY"           | true
        "FIRST_POSTAL_CODE_CONTAINS_ONLY"    | true
        "NOT_FIRST_POSTAL_CODE_CONTAINS_ANY" | false
        "SECOND_PHONE_CONTAINS_ONLY"         | true
        "SECOND_PHONE_CONTAINS_ONLY_SEGMENT" | false
    }
}
