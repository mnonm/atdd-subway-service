package nextstep.subway.line.domain.section;

import nextstep.subway.exception.SectionException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public void addLineStation(Section section) {
        validation(section);

        sections.stream()
                .filter(it -> it.getUpStation() == section.getUpStation())
                .findFirst()
                .ifPresent(it -> it.updateUpStation(section.getDownStation(), section.getDistance()));

        sections.stream()
                .filter(it -> it.getDownStation() == section.getDownStation())
                .findFirst()
                .ifPresent(it -> it.updateDownStation(section.getUpStation(), section.getDistance()));

        sections.add(section);
    }

    private void validation(Section section) {
        validateDuplicateSection(section);
        validateNoneMatchSection(section);
    }

    private void validateDuplicateSection(Section section) {
        List<Station> stations = getStations();
        boolean isUpStationExisted = stations.stream().anyMatch(it -> it == section.getUpStation());
        boolean isDownStationExisted = stations.stream().anyMatch(it -> it == section.getDownStation());

        if (!stations.isEmpty() && isUpStationExisted && isDownStationExisted) {
            throw new SectionException("이미 등록된 구간 입니다.");
        }
    }

    private void validateNoneMatchSection(Section section) {
        List<Station> stations = getStations();
        boolean isMatchUpStation = stations.stream().noneMatch(it -> it == section.getUpStation());
        boolean isMatchDownStation = stations.stream().noneMatch(it -> it == section.getDownStation());

        if (!stations.isEmpty() && isMatchUpStation && isMatchDownStation) {
            throw new SectionException("연결되는 구간이 없습니다.");
        }
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Arrays.asList();
        }

        Set<Station> result = new LinkedHashSet<>();

        Optional<Section> first = sections.stream()
                .filter(f -> !firstStation(f.getUpStation()))
                .findFirst();

        while (first.isPresent()) {
            Section station = first.get();
            result.add(first.get().getUpStation());
            result.add(first.get().getDownStation());
            first = sections.stream()
                    .filter(f -> f.getUpStation() == station.getDownStation())
                    .findFirst();
        }
        return new ArrayList<>(result);
    }

    private boolean firstStation(Station station) {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList())
                .contains(station);
    }

    public List<Section> getSections() {
        return sections;
    }
}
