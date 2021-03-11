package io.segmentme.analysis.service.segment.worm;

import java.util.function.Function;

public interface Worm<T> {

    void apply(T target, Object object);

    Boolean computeResult(String key, Function<String, Boolean> matchFunction);
}
