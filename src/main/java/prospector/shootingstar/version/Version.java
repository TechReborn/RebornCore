package prospector.shootingstar.version;

public class Version {
    public static final Version NULL_VERSION = new Version(0, 0, 0);

    public final int major;
    public final int minor;
    public final int patch;

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }
}
