package teameins.lecturerassignmentsystem.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Fachbereich {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    @OneToMany
    List<Vorlesung> vorlesungen;

    public Fachbereich() {
    }

    public Fachbereich(int id, String name, List<Vorlesung> vorlesungen) {
        this.id = id;
        this.name = name;
        this.vorlesungen = vorlesungen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Vorlesung> getVorlesungen() {
        return vorlesungen;
    }

    public void setVorlesungen(List<Vorlesung> vorlesungen) {
        this.vorlesungen = vorlesungen;
    }
}
