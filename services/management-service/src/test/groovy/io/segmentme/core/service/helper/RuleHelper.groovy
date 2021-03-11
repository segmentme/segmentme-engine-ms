package io.segmentme.core.service.helper


import io.segmentme.models.shared.analysis.AggregationType

class RuleHelper {

    static def fillRule(Object segment, Map args = [:]) {
        segment.aggregation = args['aggregation'] ?: AggregationType.AND
        segment.name = args['name'] ?: UUID.randomUUID().toString()
        segment.conditions = args['conditions']
        segment.matchResult = args['matchResult'] ?: true
        return segment
    }
}
