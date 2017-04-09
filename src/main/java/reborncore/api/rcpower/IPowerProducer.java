package reborncore.api.rcpower;

/**
 * Created by Gigabit101 on 08/04/2017.
 */
public interface IPowerProducer
{
    int takePower(int amount, boolean simulated);
}
