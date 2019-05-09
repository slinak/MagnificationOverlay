package camp.scottlikesto.www.magnificationoverlay;

public class Scale {
    public int magnification;
    public double length;
    public String unit;

    public Scale(int _mag, double _length, String _unit) {
        magnification = _mag;
        length = _length;
        unit = _unit;
    }
}
