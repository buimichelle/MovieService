public class Genre {

    private final String id;
    private final String name;


    public Genre(String id, String name) {
        this.name = name;
        this.id = id;

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String toString() {
        return "Genre Name:" + getName() + ", " +
                "ID:" + getId() + "." ;
    }
}
