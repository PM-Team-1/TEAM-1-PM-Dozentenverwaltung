package teameins.lecturerassignmentsystem.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Vorlesungsqualifikation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String qualifikationstufe;
    private String erfahrung;
    private LocalDate erstellungsDatum;
    @ManyToOne
    private Dozent dozent;
    @ManyToOne
    private Vorlesung vorlesung;

    public Vorlesungsqualifikation() {
    }

    public Vorlesungsqualifikation(int id, String qualifikationstufe, String erfahrung, LocalDate erstellungsDatum, Dozent dozent, Vorlesung vorlesung) {
        this.id = id;
        this.qualifikationstufe = qualifikationstufe;
        this.erfahrung = erfahrung;
        this.erstellungsDatum = erstellungsDatum;
        this.dozent = dozent;
        this.vorlesung = vorlesung;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQualifikationstufe() {
        return qualifikationstufe;
    }

    public void setQualifikationstufe(String qualifikationstufe) {
        this.qualifikationstufe = qualifikationstufe;
    }

    public String getErfahrung() {
        return erfahrung;
    }

    public void setErfahrung(String erfahrung) {
        this.erfahrung = erfahrung;
    }

    public LocalDate getErstellungsDatum() {
        return erstellungsDatum;
    }

    public void setErstellungsDatum(LocalDate erstellungsDatum) {
        this.erstellungsDatum = erstellungsDatum;
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
