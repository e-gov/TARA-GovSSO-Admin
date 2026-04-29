package ee.ria.tara.duration;

import java.time.Duration;

public interface DurationFormat {

    String format(Duration duration);

    Duration parse(String formatted);

}
