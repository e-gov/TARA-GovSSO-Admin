package ee.ria.tara.service.duration;

import jakarta.annotation.Nonnull;

import java.time.Duration;

record ParseTestCase(
        String name,
        String input,
        Duration output
) {

    @Override
    @Nonnull
    public String toString() {
        return name;
    }

}
