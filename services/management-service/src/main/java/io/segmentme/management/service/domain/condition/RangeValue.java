package io.segmentme.management.service.domain.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RangeValue implements Comparable<Object> {

    private Object min;

    private Object max;

    @Override
    public int compareTo(Object o) {

        boolean comparedMin = ((Comparable<Object>) min).compareTo(o) <= 0;
        boolean comparedMax = ((Comparable<Object>) max).compareTo(o) >= 0;

        if (comparedMin && comparedMax) {
            return 0;
        } else if (comparedMin) {
            return 1;
        } else {
            return -1;
        }
    }

}