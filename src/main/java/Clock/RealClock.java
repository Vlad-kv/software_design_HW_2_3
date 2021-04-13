package Clock;

import Clock.Clock;

import java.time.Instant;

public class RealClock implements Clock {
    @Override
    public Instant now() {
        return Instant.now();
    }
}
