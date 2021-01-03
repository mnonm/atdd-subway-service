package nextstep.subway.path.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LineOfStationInPath {
    private static final int MULTI_LINE_BOUNDARY = 1;

    private final List<Long> lineIds;

    public LineOfStationInPath(List<Long> lineIds) {
        this.lineIds = new ArrayList<>(lineIds);
    }

    public boolean isMultiLine() {
        return this.lineIds.size() > MULTI_LINE_BOUNDARY;
    }

    public List<Long> getSameLines(final LineOfStationInPath that) {
        return that.lineIds.stream()
                .filter(this.lineIds::contains)
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LineOfStationInPath that = (LineOfStationInPath) o;
        return this.lineIds.equals(that.lineIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineIds);
    }
}
