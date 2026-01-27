package teameins.lecturerassignmentsystem.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Vorlesung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private boolean offen;
    private List<Vorlesungsqualifikation> vorlesungsqualifikationen;
    @ManyToOne
    private Fachbereich fachbereich;

    public Vorlesung() {
    }

    public Vorlesung(int id, String name, boolean offen, List<Vorlesungsqualifikation> vorlesungsqualifikationen, Fachbereich fachbereich) {
        this.id = id;
        this.name = name;
        this.offen = offen;
        this.vorlesungsqualifikationen = vorlesungsqualifikationen;
        this.fachbereich = fachbereich;
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

    public boolean isOffen() {
        return offen;
    }

    public void setOffen(boolean offen) {
        this.offen = offen;
    }

    public List<Vorlesungsqualifikation> getVorlesungsqualifikationen() {
        return vorlesungsqualifikationen;
    }

    public void setVorlesungsqualifikationen(List<Vorlesungsqualifikation> vorlesungsqualifikationen) {
        this.vorlesungsqualifikationen = vorlesungsqualifikationen;
    }

    public Fachbereich getFachbereich() {
        return fachbereich;
    }

    public void setFachbereich(Fachbereich fachbereich) {
        this.fachbereich = fachbereich;
    }
}
