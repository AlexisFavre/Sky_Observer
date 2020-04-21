package ch.epfl.rigel.gui;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Give a set of often used pre-created {@code TimeAccelerator}
 * (continuous and discrete)
 *
 * @author @author Augustin ALLARD (299918)
 */
public enum NamedTimeAccelerator {
    TIMES_1("1x", TimeAccelerator.continuous(1)),
    TIMES_30("30x", TimeAccelerator.continuous(30)),
    TIMES_300("300x", TimeAccelerator.continuous(300)),
    TIMES_3000("3000x", TimeAccelerator.continuous(3000)),
    DAY("jour", TimeAccelerator.discrete(60, Duration.parse("P1D"))),
    SIDEREAL_DAY("jour sidéral", TimeAccelerator.discrete(60, Duration.parse("PT23H56M04S")));

    private String name;
    private TimeAccelerator accelerator;

    NamedTimeAccelerator(String name, TimeAccelerator accelerator) {
        this.name = name;
        this.accelerator = accelerator;
    }

    /**
     *
     * @return the accelerator corresponding to the field
     */
    public TimeAccelerator getAccelerator() {
        return accelerator;
    }

    /**
     *
     * @return the name of the accelerator corresponding to the field
     */
    public String getName() {
        return name;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName();
    }
}
