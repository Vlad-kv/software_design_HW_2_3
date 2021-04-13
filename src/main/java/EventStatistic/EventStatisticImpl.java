package EventStatistic;

import Clock.Clock;

import java.time.Instant;
import java.util.*;

public class EventStatisticImpl implements EventStatistic {
    final Clock clock;
    final TreeMap<String, Integer> eventCountByLastHour = new TreeMap<>();
    final LinkedList<Pair<String, Instant>> events = new LinkedList<>();

    public EventStatisticImpl(Clock clock) {
        this.clock = clock;
    }

    private void dropOldEvents() {
        Instant now = clock.now();
        while ((! events.isEmpty()) && (now.getEpochSecond() - events.getFirst().getSecond().getEpochSecond() >= 1000 * 60 * 60)) {
            eventCountByLastHour.replace(events.getFirst().getFirst(), eventCountByLastHour.get(events.getFirst().getFirst()) - 1);
            events.removeFirst();
        }
    }

    @Override
    public void incEvent(String name) {
        if (eventCountByLastHour.containsKey(name)) {
            eventCountByLastHour.replace(name, eventCountByLastHour.get(name) + 1);
        } else {
            eventCountByLastHour.put(name, 1);
        }
        Instant now = clock.now();
        assert ((events.isEmpty()) || (events.getLast().getSecond().getEpochSecond() <= now.getEpochSecond()));
        events.addLast(new Pair<>(name, clock.now()));
    }
    @Override
    public double getEventStatisticByName(String name) {
        dropOldEvents();
        if (eventCountByLastHour.containsKey(name)) {
            return eventCountByLastHour.get(name) / 60.0;
        } else {
            return 0;
        }
    }
    @Override
    public Collection<Pair<String, Double>> getAllEventStatistic() {
        dropOldEvents();
        ArrayList<Pair<String, Double>> res = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : eventCountByLastHour.entrySet()) {
            if (entry.getValue() > 0) {
                res.add(new Pair<>(entry.getKey(), entry.getValue() / 60.0));
            }
        }
        return res;
    }
    @Override
    public void printStatistic() {
        for (Pair<String, Double> p : getAllEventStatistic()) {
            System.out.println(p.getFirst() + " : " + p.getSecond());
        }
    }
}
