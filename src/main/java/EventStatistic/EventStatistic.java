package EventStatistic;

import java.util.Collection;

public interface EventStatistic {
    void incEvent(String name);
    double getEventStatisticByName(String name);
    Collection<Pair<String, Double>> getAllEventStatistic();
    void printStatistic();
}
