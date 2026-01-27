package teameins.lecturerassignmentsystem.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Einsatz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate datumVon;
    private LocalDate datumBis;
    private String status;

    @OneToMany
    private List<Semester> semester;
    @ManyToOne
    private Dozent dozent;
    @ManyToOne
    private Vorlesung vorlesung;

    public Einsatz() {
    }

    public Einsatz(int id, LocalDate datumVon, LocalDate datumBis, String status, List<Semester> semester, Dozent dozent, Vorlesung vorlesung) {
        this.id = id;
        this.datumVon = datumVon;
        this.datumBis = datumBis;
        this.status = status;
        this.semester = semester;
        this.dozent = dozent;
        this.vorlesung = vorlesung;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDatumVon() {
        return datumVon;
    }

    public void setDatumVon(LocalDate datumVon) {
        this.datumVon = datumVon;
    }

    public LocalDate getDatumBis() {
        return datumBis;
    }

    public void setDatumBis(LocalDate datumBis) {
        this.datumBis = datumBis;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Semester> getSemester() {
        return semester;
    }

    public void setSemester(List<Semester> semester) {
        this.semester = semester;
    }

    public Dozent getDozent() {
        return dozent;
    }

    public void setDozent(Dozent dozent) {
        this.dozent = dozent;
    }

    public Vorlesung getVorlesung() {
        return vorlesung;
    }

    public void setVorlesung(Vorlesung vorlesung) {
        this.vorlesung = vorlesung;
    }
}
