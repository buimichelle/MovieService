public class Star {

    private final String id;
    private final String name;
    private final int birthyear;


    public Star(String id, String name, int year) {
        this.name = name;
        this.id = id;
        this.birthyear = year;
    }

    public int getYear() {
        return birthyear;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String toString() {
        return "Star Name:" + getName() + ", " +
                "ID:" + getId() + ", " +
                "BirthYear:" + getYear() + ".";
    }
}
