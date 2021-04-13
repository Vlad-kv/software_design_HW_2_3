import EventStatistic.EventStatisticImpl;
import EventStatistic.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class EventStatisticTest {
    static final long startTime = 0;

    private static ArrayList<Pair<String, Double>> createList(String []names, Double []values) {
        assert (names.length == values.length);
        ArrayList<Pair<String, Double>> res = new ArrayList<>();

        for (int i = 0; i < names.length; i++) {
            res.add(new Pair<>(names[i], values[i]));
        }
        return res;
    }

    private static boolean isEqual(Collection<Pair<String, Double>> c1, Collection<Pair<String, Double>> c2) {
        Iterator<Pair<String, Double>> it1 = c1.iterator();
        Iterator<Pair<String, Double>> it2 = c2.iterator();

        while (it1.hasNext() && (it2.hasNext())) {
            Pair<String, Double> p1 = it1.next();
            Pair<String, Double> p2 = it2.next();

            if ((! p1.getFirst().equals(p2.getFirst())) || (! p1.getSecond().equals(p2.getSecond()))) {
                return false;
            }
        }
        return ((! it1.hasNext()) && (! it2.hasNext()));
    }

    @Test
    public void test_1() {
        SetableClock clock = new SetableClock(Instant.ofEpochSecond(startTime));
        EventStatisticImpl eventStatistic = new EventStatisticImpl(clock);

        Assert.assertEquals(0, eventStatistic.getEventStatisticByName("event_1"), 0);
        Assert.assertTrue(isEqual(new ArrayList<>(), eventStatistic.getAllEventStatistic()));

        eventStatistic.incEvent("event 1");

        Assert.assertEquals(1 / 60.0, eventStatistic.getEventStatisticByName("event 1"), 1e-6);
        Assert.assertEquals(0, eventStatistic.getEventStatisticByName("event 2"), 1e-6);

        Assert.assertTrue(isEqual(createList(new String[]{"event 1"}, new Double[]{1 / 60.0}),
                                  eventStatistic.getAllEventStatistic()));

        clock.setNow(Instant.ofEpochSecond(startTime + 1000));
        eventStatistic.incEvent("event 2");


        Assert.assertEquals(1 / 60.0, eventStatistic.getEventStatisticByName("event 1"), 1e-6);
        Assert.assertEquals(1 / 60.0, eventStatistic.getEventStatisticByName("event 2"), 1e-6);

        Assert.assertTrue(isEqual(createList(new String[]{"event 1", "event 2"}, new Double[]{1 / 60.0, 1 / 60.0}),
                                  eventStatistic.getAllEventStatistic()));

        clock.setNow(Instant.ofEpochSecond(startTime + 2000));
        eventStatistic.incEvent("event 1");

        clock.setNow(Instant.ofEpochSecond(startTime + 3000));
        eventStatistic.incEvent("event 1");

        clock.setNow(Instant.ofEpochSecond(startTime + 4000));
        eventStatistic.incEvent("event 1");

        clock.setNow(Instant.ofEpochSecond(startTime + 5000));
        eventStatistic.incEvent("event 2");

        Assert.assertEquals(4 / 60.0, eventStatistic.getEventStatisticByName("event 1"), 1e-6);
        Assert.assertEquals(2 / 60.0, eventStatistic.getEventStatisticByName("event 2"), 1e-6);

        Assert.assertTrue(isEqual(createList(new String[]{"event 1", "event 2"}, new Double[]{4 / 60.0, 2 / 60.0}),
                                  eventStatistic.getAllEventStatistic()));

        clock.setNow(Instant.ofEpochSecond(startTime + 60 * 60 * 1000 - 1));

        Assert.assertTrue(isEqual(createList(new String[]{"event 1", "event 2"}, new Double[]{4 / 60.0, 2 / 60.0}),
                eventStatistic.getAllEventStatistic()));

        clock.setNow(Instant.ofEpochSecond(startTime + 60 * 60 * 1000));

        Assert.assertTrue(isEqual(createList(new String[]{"event 1", "event 2"}, new Double[]{3 / 60.0, 2 / 60.0}),
                eventStatistic.getAllEventStatistic()));

        clock.setNow(Instant.ofEpochSecond(startTime + (60 * 60 + 1) * 1000));

        Assert.assertEquals(3 / 60.0, eventStatistic.getEventStatisticByName("event 1"), 1e-6);
        Assert.assertEquals(1 / 60.0, eventStatistic.getEventStatisticByName("event 2"), 1e-6);

        clock.setNow(Instant.ofEpochSecond(startTime + (60 * 60 + 4) * 1000));

        Assert.assertTrue(isEqual(createList(new String[]{"event 2"}, new Double[]{1 / 60.0}),
                eventStatistic.getAllEventStatistic()));

        clock.setNow(Instant.ofEpochSecond(startTime + (60 * 60 + 5) * 1000));
        Assert.assertTrue(isEqual(new ArrayList<>(), eventStatistic.getAllEventStatistic()));
    }

    @Test
    public void test_2() {
        SetableClock clock = new SetableClock(Instant.ofEpochSecond(startTime));
        EventStatisticImpl eventStatistic = new EventStatisticImpl(clock);

        eventStatistic.incEvent("e_1");
        eventStatistic.incEvent("e_1");
        eventStatistic.incEvent("e_1");
        eventStatistic.incEvent("e_1");

        eventStatistic.incEvent("e_2");
        eventStatistic.incEvent("e_2");

        eventStatistic.incEvent("e_3");
        eventStatistic.incEvent("e_4");

        eventStatistic.incEvent("e_5");
        eventStatistic.incEvent("e_5");
        eventStatistic.incEvent("e_5");

        Assert.assertTrue(isEqual(createList(new String[]{"e_1", "e_2", "e_3", "e_4", "e_5"}, new Double[]{4 / 60.0, 2 / 60.0, 1 / 60.0, 1 / 60.0, 3 / 60.0}),
                eventStatistic.getAllEventStatistic()));

        eventStatistic.printStatistic();

        clock.setNow(Instant.ofEpochSecond(startTime + 60 * 60 * 1000 - 1));
        Assert.assertTrue(isEqual(createList(new String[]{"e_1", "e_2", "e_3", "e_4", "e_5"}, new Double[]{4 / 60.0, 2 / 60.0, 1 / 60.0, 1 / 60.0, 3 / 60.0}),
                eventStatistic.getAllEventStatistic()));

        clock.setNow(Instant.ofEpochSecond(startTime + 60 * 60 * 1000));
        Assert.assertTrue(isEqual(createList(new String[]{}, new Double[]{}),
                eventStatistic.getAllEventStatistic()));
    }
}
