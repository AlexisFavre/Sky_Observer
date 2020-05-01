package ch.epfl.rigel.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;


public class TimeAcceleratorTest{

    @Test
    public void testContinuous() throws Exception {
        ZonedDateTime initialTime = ZonedDateTime.parse("2020-04-17T21:00:00+00:00");
        ZonedDateTime laterTime = TimeAccelerator.continuous(300).adjust(initialTime, (long) (2.34 * 1e9));
        assertEquals(ZonedDateTime.parse("2020-04-17T21:11:42+00:00"), laterTime); 
    }

    @Test
    public void testDiscrete() throws Exception {
        ZonedDateTime initialTime = ZonedDateTime.parse("2020-04-20T21:00:00+00:00");
        ZonedDateTime laterTime = TimeAccelerator.discrete(10, Duration.parse("PT23H56M4S"))
  .adjust(initialTime, (long) (2.34 * 1e9));
  assertEquals(ZonedDateTime.parse("2020-05-13T19:29:32+00:00"), laterTime);     }

}
