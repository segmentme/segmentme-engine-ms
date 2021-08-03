package io.segmentme.core.service.converter

import io.segmentme.analysis.dto.conditions.ArrayConditionDto
import io.segmentme.analysis.dto.conditions.SingleConditionDto
import io.segmentme.analysis.dto.segment.SegmentDto
import io.segmentme.management.service.converter.SegmentConverter
import io.segmentme.management.service.domain.condition.ArrayCondition
import io.segmentme.management.service.domain.condition.SingleCondition
import io.segmentme.management.service.domain.segment.Segment
import spock.lang.Specification

import static io.segmentme.core.service.helper.ConditionHelper.fillCondition
import static io.segmentme.core.service.helper.RuleHelper.fillRule
import static io.segmentme.models.shared.analysis.ConditionType.*
import static java.util.UUID.randomUUID

class SegmentConverterTest extends Specification {

    def "Analysis rule converting: #values isDto: #isDto"() {
        given:
        def source = createRule(isDto, values)
        expect:
        def target = isDto ? SegmentConverter.of(source, randomUUID().toString(), randomUUID().toString()) : SegmentConverter.of(source)
        target.matchResult == source.matchResult
        target.aggregation == source.aggregation
        target.conditions.size() == 1
        def convertedCondition = target.conditions[0]
        def condition = source.conditions[0]
        convertedCondition.type == condition.type
        where:
        values                                                                                                                                 | isDto
        ['value': true, 'conditions': List.of(fillCondition(new ArrayConditionDto(), true, IN))]       | true
        ['value': false, 'conditions': List.of(fillCondition(new ArrayConditionDto(), true, IN))]                                              | true
        ['value': true, 'conditions': List.of(fillCondition(new ArrayCondition(), true, IN))]                                                  | false
        ['value': false, 'conditions': List.of(fillCondition(new ArrayCondition(), true, IN))]                                                 | false
        ['value': "JSON_VALUE", 'conditions': List.of(fillCondition(new SingleConditionDto(), 1, LT))] | true
        ['value': "JSON_VALUE", 'conditions': List.of(fillCondition(new SingleConditionDto(), 2, LTE))]                                        | true
        ['value': "JSON_VALUE", 'conditions': List.of(fillCondition(new SingleCondition(), 1, LT))]                                            | false
        ['value': "JSON_VALUE", 'conditions': List.of(fillCondition(new SingleCondition(), 2, LTE))]                                           | false
        ['value'        : true, 'conditions': List.of(fillCondition(new SingleConditionDto(), 1, LT)),
         'analysisRules': List.of(createRule(true, ['value': true, 'conditions': List.of(fillCondition(new ArrayConditionDto(), true, IN))]))] | true
        ['value'        : true, 'conditions': List.of(fillCondition(new SingleCondition(), 1, LT)),
         'analysisRules': List.of(createRule(false, ['value': true, 'conditions': List.of(fillCondition(new ArrayCondition(), true, IN))]))]   | false
    }

    private def createRule(boolean isDto, Map values) {
        fillRule(isDto ? new SegmentDto() : new Segment(), values)
    }
}
