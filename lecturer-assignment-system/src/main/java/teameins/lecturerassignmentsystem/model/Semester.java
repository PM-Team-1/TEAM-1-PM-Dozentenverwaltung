package teameins.lecturerassignmentsystem.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private LocalDate startDatum;
    private LocalDate endDatum;
    private boolean aktiv;
    @OneToMany
    private List<Einsatz> einsaetze;

    public Semester() {
    }

    public Semester(int id, String name, LocalDate startDatum, LocalDate endDatum, boolean aktiv, List<Einsatz> einsaetze) {
        this.id = id;
        this.name = name;
        this.startDatum = startDatum;
        this.endDatum = endDatum;
        this.aktiv = aktiv;
        this.einsaetze = einsaetze;
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

    public LocalDate getStartDatum() {
        return startDatum;
    }

    public void setStartDatum(LocalDate startDatum) {
        this.startDatum = startDatum;
    }

    public LocalDate getEndDatum() {
        return endDatum;
    }

    public void setEndDatum(LocalDate endDatum) {
        this.endDatum = endDatum;
    }

    public boolean isAktiv() {
        return aktiv;
    }

    public void setAktiv(boolean aktiv) {
        this.aktiv = aktiv;
    }

    public List<Einsatz> getEinsaetze() {
        return einsaetze;
    }

    public void setEinsaetze(List<Einsatz> einsaetze) {
        this.einsaetze = einsaetze;
    }
}
