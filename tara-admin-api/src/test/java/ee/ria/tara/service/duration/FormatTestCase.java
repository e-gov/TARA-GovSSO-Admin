package ee.ria.tara.service.duration;

import jakarta.annotation.Nonnull;

import java.time.Duration;

record FormatTestCase(
        String name,
        Duration input,
        String output
) {

    @Override
    @Nonnull
    public String toString() {
        return name;
    }
}
