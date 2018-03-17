package tacchella.arduini.com.time_waiting_counter;

import java.util.ArrayList;

/**
 * Created by alessio on 17/03/18.
 */

public class ChartEntry {
    private float time;
    private float speed;

    public void setTime(float time) {
        this.time = time;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getTime() {
        return time;
    }

    public float getSpeed() {
        return speed;
    }

    public ChartEntry(float time, float speed) {
        this.time = time;
        this.speed = speed;
    }
}
