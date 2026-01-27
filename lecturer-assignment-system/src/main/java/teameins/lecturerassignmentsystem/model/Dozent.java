package teameins.lecturerassignmentsystem.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Dozent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String titel;
    private String vorname;
    private String nachname;
    private String email;
    private String telefon;
    private boolean intern;
    private String studienLevelPraeferenz;
    @OneToMany
    private List<Vorlesungsqualifikation> vorlesungsqualifikationen;
    @OneToMany
    private List<Einsatz> einsaetze;

    public Dozent() {
    }

    public Dozent(int id, String titel, String vorname, String nachname, String email, String telefon, boolean intern, String studienLevelPraeferenz, List<Vorlesungsqualifikation> vorlesungsqualifikationen, List<Einsatz> einsaetze) {
        this.id = id;
        this.titel = titel;
        this.vorname = vorname;
        this.nachname = nachname;
        this.email = email;
        this.telefon = telefon;
        this.intern = intern;
        this.studienLevelPraeferenz = studienLevelPraeferenz;
        this.vorlesungsqualifikationen = vorlesungsqualifikationen;
        this.einsaetze = einsaetze;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public boolean isIntern() {
        return intern;
    }

    public void setIntern(boolean intern) {
        this.intern = intern;
    }

    public String getStudienLevelPraeferenz() {
        return studienLevelPraeferenz;
    }

    public void setStudienLevelPraeferenz(String studienLevelPraeferenz) {
        this.studienLevelPraeferenz = studienLevelPraeferenz;
    }

    public List<Vorlesungsqualifikation> getVorlesungsqualifikationen() {
        return vorlesungsqualifikationen;
    }

    public void setVorlesungsqualifikationen(List<Vorlesungsqualifikation> vorlesungsqualifikationen) {
        this.vorlesungsqualifikationen = vorlesungsqualifikationen;
    }

    public List<Einsatz> getEinsaetze() {
        return einsaetze;
    }

    public void setEinsaetze(List<Einsatz> einsaetze) {
        this.einsaetze = einsaetze;
    }
}
