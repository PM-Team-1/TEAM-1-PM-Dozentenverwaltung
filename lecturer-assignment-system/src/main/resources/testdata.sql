-- ============================================================
-- UMFANGREICHE UND REALISTISCHE TESTDATEN
-- Dozentenmanagementsystem - Provadis Hochschule
-- ============================================================
-- Daten für hochwertige Präsentation / Abgabe
-- Fokus: SoSe 26 (Sommersemester 2026)
-- Semester: SoSe 25, WiSe 25/26, SoSe 26, WiSe 26/27, SoSe 27, WiSe 27/28
-- ============================================================

PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;

-- Löschen aller bestehenden Daten
DELETE FROM lecturer_can_hold_course;
DELETE FROM lecturer_holds_course;
DELETE FROM lecturer;
DELETE FROM course;

-- ============================================================
-- 28 DOZENTEN MIT VERSCHIEDENEN PROFILEN
-- ============================================================

INSERT INTO lecturer (id, title, first_name, last_name, second_name, email, phone, is_extern, teaching_preference) VALUES
  -- Gruppe 1: Erfahrene Master-Spezialisten (Prof. + Dr., PREFER_MASTER)
  (1, 'PROFESSOR', 'Anna', 'Müller', 'Marie', 'anna.mueller@provadis.de', '+491711234567', 0, 'PREFER_MASTER'),
  (2, 'PROFESSOR', 'Bernd', 'Schmidt', 'Karl', 'bernd.schmidt@provadis.de', '+491721234568', 0, 'PREFER_MASTER'),
  (3, 'DOCTOR', 'Carla', 'Meier', 'Theresa', 'carla.meier@provadis.de', '+491731234569', 0, 'PREFER_MASTER'),
  (4, 'DOCTOR', 'Dieter', 'Fischer', NULL, 'd.fischer@provadis.de', '+491741234570', 1, 'PREFER_MASTER'),

  -- Gruppe 2: Externe Master-Experten (Dr., extern, ONLY_MASTER / PREFER_MASTER)
  (5, 'DOCTOR', 'Eva', 'Klein', NULL, 'eva.klein@external.org', '+491751234571', 1, 'ONLY_MASTER'),
  (6, 'PROFESSOR', 'Frank', 'Wagner', NULL, 'frank.wagner@provadis.de', '+491761234572', 0, 'PREFER_MASTER'),
  (7, 'DOCTOR', 'Gisela', 'Hartmann', 'Elisabeth', 'gisela.hartmann@external.org', '+491771234573', 1, 'ONLY_MASTER'),

  -- Gruppe 3: Bachelor-Spezialist:innen (Prof./Dr., PREFER_BACHELOR)
  (8, 'PROFESSOR', 'Heike', 'Becker', NULL, 'heike.becker@provadis.de', '+491781234574', 0, 'PREFER_BACHELOR'),
  (9, 'DOCTOR', 'Ines', 'Schreiber', NULL, 'ines.schreiber@provadis.de', '+491791234575', 0, 'PREFER_BACHELOR'),
  (10, 'PROFESSOR', 'Jan', 'Keller', NULL, 'jan.keller@provadis.de', '+491701234576', 0, 'PREFER_BACHELOR'),
  (11, 'DOCTOR', 'Katrin', 'Lorenz', NULL, 'k.lorenz@external.org', '+491721234577', 1, 'PREFER_BACHELOR'),

  -- Gruppe 4: Nur Bachelor (NO_TITLE/Dr., ONLY_BACHELOR)
  (12, 'NO_TITLE', 'Lukas', 'Neumann', NULL, 'l.neumann@provadis.de', '+491731234578', 0, 'ONLY_BACHELOR'),
  (13, 'NO_TITLE', 'Maja', 'Koch', NULL, 'maja.koch@provadis.de', '+491741234579', 0, 'ONLY_BACHELOR'),
  (14, 'NO_TITLE', 'Nils', 'Peters', NULL, 'n.peters@external.org', '+491751234580', 1, 'ONLY_BACHELOR'),
  (15, 'NO_TITLE', 'Olga', 'Brandt', NULL, 'olga.brandt@provadis.de', '+491761234581', 0, 'ONLY_BACHELOR'),
  (16, 'DOCTOR', 'Paul', 'Neumann', NULL, 'paul.neumann@provadis.de', '+491771234582', 0, 'ONLY_BACHELOR'),

  -- Gruppe 5: Nur Master-Spezialist:innen (Dr./Prof., ONLY_MASTER)
  (17, 'PROFESSOR', 'Quirin', 'Weber', NULL, 'q.weber@provadis.de', '+491781234583', 0, 'ONLY_MASTER'),
  (18, 'DOCTOR', 'Rita', 'Maier', NULL, 'r.maier@provadis.de', '+491791234584', 0, 'ONLY_MASTER'),
  (19, 'DOCTOR', 'Sven', 'Koch', NULL, 'sven.koch@external.org', '+491701234585', 1, 'ONLY_MASTER'),

  -- Gruppe 6: Flexibel / Alle Level (ALLES - generalistisch, verschiedene Titel)
  (20, 'PROFESSOR', 'Tina', 'Lang', NULL, 'tina.lang@provadis.de', '+491711234586', 0, 'ALLES'),
  (21, 'NO_TITLE', 'Uwe', 'Bauer', NULL, 'uwe.bauer@provadis.de', '+491721234587', 0, 'ALLES'),
  (22, 'DOCTOR', 'Veronika', 'Graf', NULL, 'veronika.graf@provadis.de', '+491731234588', 0, 'ALLES'),
  (23, 'NO_TITLE', 'Wolf', 'Kramer', NULL, 'w.kramer@external.org', '+491741234589', 1, 'ALLES'),
  (24, 'NO_TITLE', 'Xenia', 'Fuchs', NULL, 'x.fuchs@external.org', '+491751234590', 1, 'ALLES'),

  -- Gruppe 7: Neue/Spezialisierte Lehrbeauftragte (NO_TITLE, extern, spezialisiert)
  (25, 'NO_TITLE', 'Yannick', 'Zimmer', 'Paul', 'yannick.zimmer@freelance.de', '+491761234591', 1, 'ALLES'),
  (26, 'NO_TITLE', 'Zoe', 'Keller', NULL, 'zoe.keller@provadis.de', '+491771234592', 0, 'PREFER_MASTER'),
  (27, 'DOCTOR', 'Alex', 'Brand', NULL, 'alex.brand@provadis.de', '+491781234593', 0, 'PREFER_BACHELOR'),
  (28, 'PROFESSOR', 'Bella', 'Mueller', 'Sophia', 'bella.mueller@provadis.de', '+491791234594', 0, 'ALLES');

-- ============================================================
-- 48 VORLESUNGEN: BACHELOR UND MASTER GEMISCHT
-- ============================================================
-- Schwerpunkt: SoSe 26 (viele Vorlesungen in diesem Semester)
-- Bachelor: IDs 1–24 (verschiedene Themen)
-- Master: IDs 25–48 (spezialisierte Themen)

-- BACHELOR COURSES (is_master=0, 24 insgesamt)
INSERT INTO course (id, name, is_closed, is_master, semester) VALUES
  -- Grundlagen & Kernmodule (SoSe 26 + andere Semester)
  (1, 'Programmierung I', 0, 0, 'WiSe 25/26'),
  (2, 'Programmierung II', 0, 0, 'SoSe 26'),
  (3, 'Algorithmen und Datenstrukturen', 0, 0, 'SoSe 26'),
  (4, 'Lineare Algebra für Informatik', 0, 0, 'WiSe 26/27'),
  (5, 'Mathematik I', 0, 0, 'WiSe 25/26'),
  (6, 'Mathematik II', 0, 0, 'SoSe 25'),

  -- Kernbereich: Datenbanken & Systeme (verteilt auf mehrere Semester)
  (7, 'Datenbanken Grundlagen', 0, 0, 'WiSe 25/26'),
  (8, 'Datenbanksysteme I', 0, 0, 'SoSe 26'),
  (9, 'Betriebssysteme Grundlagen', 0, 0, 'WiSe 25/26'),
  (10, 'Betriebssysteme Vertiefung', 0, 0, 'SoSe 26'),

  -- Netzwerke & Kommunikation
  (11, 'Rechnernetze Grundlagen', 0, 0, 'WiSe 26/27'),
  (12, 'Netzwerkprotokolle', 0, 0, 'SoSe 26'),

  -- Software Engineering & Praktisches
  (13, 'Software Engineering Grundlagen', 0, 0, 'SoSe 26'),
  (14, 'Requirements Engineering', 0, 0, 'WiSe 26/27'),
  (15, 'Projektmanagement', 0, 0, 'SoSe 27'),
  (16, 'Qualitätssicherung und Testen', 0, 0, 'WiSe 25/26'),

  -- Sicherheit & Datenschutz
  (17, 'IT-Sicherheit Grundlagen', 0, 0, 'SoSe 26'),
  (18, 'Kryptographie', 0, 0, 'WiSe 26/27'),
  (19, 'Datenschutz und Compliance', 0, 0, 'SoSe 26'),

  -- Web & Frontend
  (20, 'Webentwicklung Frontend', 0, 0, 'SoSe 26'),
  (21, 'Webentwicklung Backend', 0, 0, 'WiSe 26/27'),
  (22, 'Mobile Development', 0, 0, 'SoSe 27'),

  -- Spezialthemen
  (23, 'Künstliche Intelligenz Grundlagen', 0, 0, 'WiSe 27/28'),
  (24, 'Wirtschaftsinformatik und E-Business', 0, 0, 'SoSe 26');

-- MASTER COURSES (is_master=1, 24 insgesamt)
INSERT INTO course (id, name, is_closed, is_master, semester) VALUES
  -- Vertiefte Systeme & Architektur
  (25, 'Advanced Software Engineering', 0, 1, 'SoSe 26'),
  (26, 'Softwarearchitektur Patterns', 0, 1, 'WiSe 26/27'),
  (27, 'Enterprise Architecture', 0, 1, 'SoSe 26'),
  (28, 'Cloud Computing und DevOps', 0, 1, 'SoSe 26'),
  (29, 'Verteilte Systeme', 0, 1, 'WiSe 26/27'),
  (30, 'Hochperformance Computing', 0, 1, 'SoSe 27'),

  -- Daten & AI
  (31, 'Machine Learning', 0, 1, 'SoSe 26'),
  (32, 'Deep Learning und Neural Networks', 0, 1, 'WiSe 26/27'),
  (33, 'Data Science', 0, 1, 'SoSe 26'),
  (34, 'Big Data Analytics', 0, 1, 'WiSe 26/27'),
  (35, 'Natural Language Processing', 0, 1, 'SoSe 27'),

  -- Sicherheit & Vertrauen
  (36, 'Cyber Security Advanced', 0, 1, 'SoSe 26'),
  (37, 'Blockchain und kryptographische Systeme', 0, 1, 'WiSe 27/28'),
  (38, 'Security Engineering', 0, 1, 'SoSe 27'),
  (39, 'Identität und Zugriffskontrolle', 0, 1, 'WiSe 26/27'),

  -- Spezialisierte Themen
  (40, 'Autonome Systeme und Robotik', 0, 1, 'WiSe 27/28'),
  (41, 'IoT und Embedded Systems', 0, 1, 'SoSe 27'),
  (42, 'Quantencomputing Grundlagen', 0, 1, 'WiSe 26/27'),
  (43, 'Formal Methods und Verification', 0, 1, 'SoSe 26'),
  (44, 'Advanced Compiler Design', 0, 1, 'WiSe 27/28'),
  (45, 'Echtzeitsysteme', 0, 1, 'SoSe 27'),
  (46, 'Künstliche Intelligenz im Business', 0, 1, 'SoSe 26'),
  (47, 'Human-Computer Interaction', 0, 1, 'WiSe 26/27'),
  (48, 'Forschungsprojekt - Spezialthema', 0, 1, 'SoSe 26');

-- ============================================================
-- LECTURER_CAN_HOLD_COURSE: UMFANGREICHE BEZIEHUNGEN
-- (bereits_gehalten, qualifikation, affinity)
-- ============================================================
-- Strategie:
-- - Jeder Dozent kann mehrere Vorlesungen halten (viele CanHold-Einträge)
-- - Realistische Kombinationen: priority + alreadyHeld + qualification
-- - Edge Cases: Vorlesungen nur mit unerfahrenen Dozenten
-- - Sehr differenzierte Affinity-Werte
-- IDs: 1–300 (umfangreiche Abdeckung)

-- Dozent 1: Anna Müller (PREFER_MASTER) - sehr erfahren, viele Master bereits gehalten
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (1, 'PROVADIS', 'IMMEDIATELY', 25, 1, 'HIGH'),
  (2, 'PROVADIS', 'IMMEDIATELY', 27, 1, 'HIGH'),
  (3, 'PROVADIS', 'IMMEDIATELY', 31, 1, 'HIGH'),
  (4, 'PROVADIS', 'IMMEDIATELY', 33, 1, 'HIGH'),
  (5, 'OTHER_SCHOOL', 'IMMEDIATELY', 36, 1, 'MEDIUM'),
  (6, 'NOT_YET_HELD', 'IMMEDIATELY', 43, 1, 'MEDIUM'),
  (7, 'NOT_YET_HELD', 'FOUR_WEEKS', 46, 1, 'MEDIUM'),
  (8, 'PROVADIS', 'IMMEDIATELY', 1, 1, 'MEDIUM');

-- Dozent 2: Bernd Schmidt (PREFER_MASTER) - Prof., vielseitig
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (9, 'PROVADIS', 'IMMEDIATELY', 26, 2, 'HIGH'),
  (10, 'PROVADIS', 'IMMEDIATELY', 28, 2, 'HIGH'),
  (11, 'PROVADIS', 'IMMEDIATELY', 29, 2, 'HIGH'),
  (12, 'OTHER_SCHOOL', 'IMMEDIATELY', 39, 2, 'HIGH'),
  (13, 'NOT_YET_HELD', 'IMMEDIATELY', 42, 2, 'MEDIUM'),
  (14, 'PROVADIS', 'IMMEDIATELY', 7, 2, 'MEDIUM');

-- Dozent 3: Carla Meier (PREFER_MASTER) - Spezialistin für Sicherheit
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (15, 'PROVADIS', 'IMMEDIATELY', 36, 3, 'HIGH'),
  (16, 'PROVADIS', 'IMMEDIATELY', 38, 3, 'HIGH'),
  (17, 'OTHER_SCHOOL', 'IMMEDIATELY', 37, 3, 'HIGH'),
  (18, 'NOT_YET_HELD', 'FOUR_WEEKS', 39, 3, 'MEDIUM'),
  (19, 'PROVADIS', 'IMMEDIATELY', 17, 3, 'HIGH'),
  (20, 'PROVADIS', 'IMMEDIATELY', 18, 3, 'MEDIUM');

-- Dozent 4: Dieter Fischer (extern, PREFER_MASTER) - spezialisiert auf Cloud
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (21, 'NOT_YET_HELD', 'IMMEDIATELY', 28, 4, 'HIGH'),
  (22, 'NOT_YET_HELD', 'IMMEDIATELY', 30, 4, 'MEDIUM'),
  (23, 'NOT_YET_HELD', 'FOUR_WEEKS', 41, 4, 'MEDIUM'),
  (24, 'OTHER_SCHOOL', 'IMMEDIATELY', 45, 4, 'LOW'),
  (25, 'NOT_YET_HELD', 'IMMEDIATELY', 2, 4, 'MEDIUM');

-- Dozent 5: Eva Klein (extern, ONLY_MASTER) - AI-Spezialistin, highly wanted
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (26, 'PROVADIS', 'IMMEDIATELY', 31, 5, 'HIGH'),
  (27, 'PROVADIS', 'IMMEDIATELY', 32, 5, 'HIGH'),
  (28, 'PROVADIS', 'IMMEDIATELY', 35, 5, 'HIGH'),
  (29, 'OTHER_SCHOOL', 'IMMEDIATELY', 34, 5, 'MEDIUM'),
  (30, 'NOT_YET_HELD', 'IMMEDIATELY', 46, 5, 'HIGH');

-- Dozent 6: Frank Wagner (PREFER_MASTER) - vielseitig, viel Provadis-Erfahrung
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (31, 'PROVADIS', 'IMMEDIATELY', 25, 6, 'MEDIUM'),
  (32, 'PROVADIS', 'IMMEDIATELY', 27, 6, 'MEDIUM'),
  (33, 'PROVADIS', 'IMMEDIATELY', 33, 6, 'HIGH'),
  (34, 'NOT_YET_HELD', 'IMMEDIATELY', 43, 6, 'MEDIUM'),
  (35, 'PROVADIS', 'IMMEDIATELY', 13, 6, 'HIGH'),
  (36, 'PROVADIS', 'IMMEDIATELY', 16, 6, 'MEDIUM');

-- Dozent 7: Gisela Hartmann (extern, ONLY_MASTER) - Spezialistin Formal Methods
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (37, 'NOT_YET_HELD', 'IMMEDIATELY', 43, 7, 'HIGH'),
  (38, 'NOT_YET_HELD', 'FOUR_WEEKS', 44, 7, 'HIGH'),
  (39, 'NOT_YET_HELD', 'IMMEDIATELY', 29, 7, 'MEDIUM'),
  (40, 'OTHER_SCHOOL', 'IMMEDIATELY', 42, 7, 'MEDIUM');

-- Dozent 8: Heike Becker (PREFER_BACHELOR) - Programmier-Spezialistin
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (41, 'PROVADIS', 'IMMEDIATELY', 1, 8, 'HIGH'),
  (42, 'PROVADIS', 'IMMEDIATELY', 2, 8, 'HIGH'),
  (43, 'PROVADIS', 'IMMEDIATELY', 3, 8, 'HIGH'),
  (44, 'PROVADIS', 'IMMEDIATELY', 5, 8, 'MEDIUM'),
  (45, 'NOT_YET_HELD', 'IMMEDIATELY', 20, 8, 'HIGH'),
  (46, 'OTHER_SCHOOL', 'IMMEDIATELY', 26, 8, 'LOW');

-- Dozent 9: Ines Schreiber (PREFER_BACHELOR) - SE & Testen Spezialistin
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (47, 'PROVADIS', 'IMMEDIATELY', 13, 9, 'HIGH'),
  (48, 'PROVADIS', 'IMMEDIATELY', 16, 9, 'HIGH'),
  (49, 'PROVADIS', 'IMMEDIATELY', 14, 9, 'MEDIUM'),
  (50, 'NOT_YET_HELD', 'IMMEDIATELY', 17, 9, 'MEDIUM'),
  (51, 'OTHER_SCHOOL', 'IMMEDIATELY', 19, 9, 'MEDIUM');

-- Dozent 10: Jan Keller (PREFER_BACHELOR) - Datenbanken-Spezialist
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (52, 'PROVADIS', 'IMMEDIATELY', 7, 10, 'HIGH'),
  (53, 'PROVADIS', 'IMMEDIATELY', 8, 10, 'HIGH'),
  (54, 'PROVADIS', 'IMMEDIATELY', 9, 10, 'HIGH'),
  (55, 'NOT_YET_HELD', 'IMMEDIATELY', 4, 10, 'MEDIUM'),
  (56, 'NOT_YET_HELD', 'FOUR_WEEKS', 11, 10, 'LOW');

-- Dozent 11: Katrin Lorenz (extern, PREFER_BACHELOR) - Netzwerk-Spezialistin
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (57, 'OTHER_SCHOOL', 'IMMEDIATELY', 11, 11, 'HIGH'),
  (58, 'NOT_YET_HELD', 'IMMEDIATELY', 12, 11, 'HIGH'),
  (59, 'NOT_YET_HELD', 'IMMEDIATELY', 18, 11, 'MEDIUM'),
  (60, 'NOT_YET_HELD', 'FOUR_WEEKS', 9, 11, 'MEDIUM');

-- Dozent 12: Lukas Neumann (ONLY_BACHELOR) - Algebra & Mathematik
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (61, 'PROVADIS', 'IMMEDIATELY', 4, 12, 'HIGH'),
  (62, 'PROVADIS', 'IMMEDIATELY', 5, 12, 'HIGH'),
  (63, 'PROVADIS', 'IMMEDIATELY', 6, 12, 'HIGH'),
  (64, 'NOT_YET_HELD', 'IMMEDIATELY', 1, 12, 'MEDIUM'),
  (65, 'NOT_YET_HELD', 'IMMEDIATELY', 3, 12, 'MEDIUM');

-- Dozent 13: Maja Koch (ONLY_BACHELOR) - Backend & Netzwerk
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (66, 'PROVADIS', 'IMMEDIATELY', 7, 13, 'HIGH'),
  (67, 'PROVADIS', 'IMMEDIATELY', 8, 13, 'HIGH'),
  (68, 'PROVADIS', 'IMMEDIATELY', 12, 13, 'HIGH'),
  (69, 'NOT_YET_HELD', 'IMMEDIATELY', 21, 13, 'MEDIUM');

-- Dozent 14: Nils Peters (extern, ONLY_BACHELOR) - Web & Mobile
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (70, 'NOT_YET_HELD', 'IMMEDIATELY', 20, 14, 'HIGH'),
  (71, 'NOT_YET_HELD', 'IMMEDIATELY', 21, 14, 'HIGH'),
  (72, 'NOT_YET_HELD', 'IMMEDIATELY', 22, 14, 'HIGH'),
  (73, 'NOT_YET_HELD', 'FOUR_WEEKS', 2, 14, 'MEDIUM');

-- Dozent 15: Olga Brandt (ONLY_BACHELOR) - Betriebssysteme & OS-Spezialistin
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (74, 'PROVADIS', 'IMMEDIATELY', 9, 15, 'HIGH'),
  (75, 'PROVADIS', 'IMMEDIATELY', 10, 15, 'HIGH'),
  (76, 'NOT_YET_HELD', 'IMMEDIATELY', 11, 15, 'MEDIUM'),
  (77, 'NOT_YET_HELD', 'IMMEDIATELY', 4, 15, 'MEDIUM');

-- Dozent 16: Paul Neumann (ONLY_BACHELOR) - Sicherheit & Datenschutz
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (78, 'PROVADIS', 'IMMEDIATELY', 17, 16, 'HIGH'),
  (79, 'PROVADIS', 'IMMEDIATELY', 19, 16, 'HIGH'),
  (80, 'PROVADIS', 'IMMEDIATELY', 18, 16, 'HIGH'),
  (81, 'NOT_YET_HELD', 'IMMEDIATELY', 13, 16, 'MEDIUM');

-- Dozent 17: Quirin Weber (ONLY_MASTER) - Fortgeschrittene Architekturen
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (82, 'PROVADIS', 'IMMEDIATELY', 25, 17, 'HIGH'),
  (83, 'PROVADIS', 'IMMEDIATELY', 26, 17, 'HIGH'),
  (84, 'PROVADIS', 'IMMEDIATELY', 29, 17, 'HIGH'),
  (85, 'NOT_YET_HELD', 'IMMEDIATELY', 47, 17, 'MEDIUM');

-- Dozent 18: Rita Maier (ONLY_MASTER) - Data Science Spezialisten
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (86, 'PROVADIS', 'IMMEDIATELY', 31, 18, 'HIGH'),
  (87, 'PROVADIS', 'IMMEDIATELY', 33, 18, 'HIGH'),
  (88, 'PROVADIS', 'IMMEDIATELY', 34, 18, 'HIGH'),
  (89, 'NOT_YET_HELD', 'IMMEDIATELY', 35, 18, 'MEDIUM');

-- Dozent 19: Sven Koch (extern, ONLY_MASTER) - Blockchain Spezialist
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (90, 'NOT_YET_HELD', 'IMMEDIATELY', 37, 19, 'HIGH'),
  (91, 'NOT_YET_HELD', 'IMMEDIATELY', 39, 19, 'HIGH'),
  (92, 'OTHER_SCHOOL', 'IMMEDIATELY', 36, 19, 'MEDIUM'),
  (93, 'NOT_YET_HELD', 'FOUR_WEEKS', 43, 19, 'MEDIUM');

-- Dozent 20: Tina Lang (ALLES) - Generalistisch, sehr vielseitig
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (94, 'PROVADIS', 'IMMEDIATELY', 1, 20, 'HIGH'),
  (95, 'PROVADIS', 'IMMEDIATELY', 7, 20, 'HIGH'),
  (96, 'PROVADIS', 'IMMEDIATELY', 25, 20, 'MEDIUM'),
  (97, 'PROVADIS', 'IMMEDIATELY', 28, 20, 'MEDIUM'),
  (98, 'NOT_YET_HELD', 'IMMEDIATELY', 35, 20, 'MEDIUM'),
  (99, 'NOT_YET_HELD', 'IMMEDIATELY', 13, 20, 'HIGH');

-- Dozent 21: Uwe Bauer (ALLES) - Versatil, viele Jahre
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (100, 'PROVADIS', 'IMMEDIATELY', 2, 21, 'HIGH'),
  (101, 'PROVADIS', 'IMMEDIATELY', 8, 21, 'HIGH'),
  (102, 'PROVADIS', 'IMMEDIATELY', 20, 21, 'HIGH'),
  (103, 'NOT_YET_HELD', 'IMMEDIATELY', 31, 21, 'MEDIUM'),
  (104, 'NOT_YET_HELD', 'IMMEDIATELY', 33, 21, 'MEDIUM');

-- Dozent 22: Veronika Graf (ALLES) - SE & Quality
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (105, 'PROVADIS', 'IMMEDIATELY', 13, 22, 'HIGH'),
  (106, 'PROVADIS', 'IMMEDIATELY', 16, 22, 'HIGH'),
  (107, 'PROVADIS', 'IMMEDIATELY', 25, 22, 'MEDIUM'),
  (108, 'NOT_YET_HELD', 'IMMEDIATELY', 26, 22, 'MEDIUM'),
  (109, 'NOT_YET_HELD', 'FOUR_WEEKS', 14, 22, 'MEDIUM');

-- Dozent 23: Wolf Kramer (extern, ALLES) - IoT & Embedded
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (110, 'NOT_YET_HELD', 'IMMEDIATELY', 41, 23, 'HIGH'),
  (111, 'NOT_YET_HELD', 'IMMEDIATELY', 45, 23, 'HIGH'),
  (112, 'NOT_YET_HELD', 'FOUR_WEEKS', 22, 23, 'MEDIUM'),
  (113, 'NOT_YET_HELD', 'IMMEDIATELY', 12, 23, 'MEDIUM');

-- Dozent 24: Xenia Fuchs (extern, ALLES) - HCI & Frontend
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (114, 'NOT_YET_HELD', 'IMMEDIATELY', 20, 24, 'HIGH'),
  (115, 'NOT_YET_HELD', 'IMMEDIATELY', 47, 24, 'HIGH'),
  (116, 'NOT_YET_HELD', 'FOUR_WEEKS', 21, 24, 'MEDIUM'),
  (117, 'NOT_YET_HELD', 'IMMEDIATELY', 22, 24, 'MEDIUM');

-- Dozent 25: Yannick Zimmer (extern, ALLES) - Freelancer, viele Themen
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (118, 'NOT_YET_HELD', 'FOUR_WEEKS', 3, 25, 'MEDIUM'),
  (119, 'NOT_YET_HELD', 'IMMEDIATELY', 25, 25, 'MEDIUM'),
  (120, 'NOT_YET_HELD', 'IMMEDIATELY', 28, 25, 'MEDIUM'),
  (121, 'NOT_YET_HELD', 'OVER_FOUR_WEEKS', 35, 25, 'LOW');

-- Dozent 26: Zoe Keller (PREFER_MASTER) - Neue Dozentin, sehr motiviert, noch unerfahren
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (122, 'NOT_YET_HELD', 'FOUR_WEEKS', 27, 26, 'HIGH'),
  (123, 'NOT_YET_HELD', 'IMMEDIATELY', 46, 26, 'MEDIUM'),
  (124, 'NOT_YET_HELD', 'FOUR_WEEKS', 32, 26, 'MEDIUM');

-- Dozent 27: Alex Brand (PREFER_BACHELOR) - Neue Dozentin in Bachelor
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (125, 'NOT_YET_HELD', 'FOUR_WEEKS', 2, 27, 'HIGH'),
  (126, 'NOT_YET_HELD', 'IMMEDIATELY', 13, 27, 'MEDIUM'),
  (127, 'NOT_YET_HELD', 'IMMEDIATELY', 20, 27, 'MEDIUM'),
  (128, 'NOT_YET_HELD', 'FOUR_WEEKS', 3, 27, 'MEDIUM');

-- Dozent 28: Bella Mueller (ALLES) - Erfahrene Generalistin
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (129, 'PROVADIS', 'IMMEDIATELY', 24, 28, 'HIGH'),
  (130, 'PROVADIS', 'IMMEDIATELY', 5, 28, 'MEDIUM'),
  (131, 'PROVADIS', 'IMMEDIATELY', 46, 28, 'MEDIUM'),
  (132, 'PROVADIS', 'IMMEDIATELY', 43, 28, 'MEDIUM'),
  (133, 'OTHER_SCHOOL', 'IMMEDIATELY', 40, 28, 'MEDIUM');

-- ============================================================
-- SONDERFÄLLE FÜR REALISTISCHE EDGE CASES
-- ============================================================

-- Kurse, für die KEIN Dozent vorhanden ist (Edge Case für Reports)
-- Beispiele: Neue Spezialvorlesung mit minimaler Nachfrage
-- (Quantencomputing, Forschungsprojekt - nur vereinzelt mit unerfahrenen Dozenten)

-- Kurs 42 (Quantencomputing): Nur ein sehr unerfahrener Dozent (Yannick, zu viel Ramp-up)
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (134, 'NOT_YET_HELD', 'OVER_FOUR_WEEKS', 42, 25, 'LOW');

-- Kurs 40 (Autonome Systeme): Nur externe unerfahrene Dozenten
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (135, 'NOT_YET_HELD', 'FOUR_WEEKS', 40, 23, 'MEDIUM'),
  (136, 'NOT_YET_HELD', 'OVER_FOUR_WEEKS', 40, 24, 'LOW');

-- Kurs 44 (Advanced Compiler Design): Spezialisiert, wenige Dozenten
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (137, 'NOT_YET_HELD', 'IMMEDIATELY', 44, 7, 'HIGH'),
  (138, 'NOT_YET_HELD', 'OVER_FOUR_WEEKS', 44, 5, 'LOW');

-- Kurs 48 (Forschungsprojekt): Nur neue/unerfahrene Dozenten
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (139, 'NOT_YET_HELD', 'FOUR_WEEKS', 48, 25, 'MEDIUM'),
  (140, 'NOT_YET_HELD', 'IMMEDIATELY', 48, 26, 'MEDIUM');

-- Kurs 47 (HCI): Spezialisiert, nur wenige bekannt
INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  (141, 'PROVADIS', 'IMMEDIATELY', 47, 17, 'HIGH'),
  (142, 'NOT_YET_HELD', 'IMMEDIATELY', 47, 24, 'HIGH');

-- ============================================================
-- LECTURER_HOLDS_COURSE: TATSÄCHLICHE ZUORDNUNGEN FÜR SoSe 26
-- Fokus: SoSe 26 ist gut besetzt, viele Kurse haben Dozenten
-- ============================================================
-- Strategie für SoSe 26:
-- - Die meisten Kurse im SoSe 26 haben einen Dozenten
-- - Manche Dozenten halten mehrere Kurse
-- - Ein paar wenige Kurse sind unbesetzt (strategisch für Reports)

INSERT INTO lecturer_holds_course (id, course_id, lecturer_id) VALUES
  -- Bachelor SoSe 26 (Kurse 2, 3, 8, 10, 12, 13, 20, 24) - KURSE 17, 19 UNBESETZT
  (1, 2, 8),        -- Programmierung II -> Heike Becker
  (2, 3, 10),       -- Algorithmen -> Jan Keller
  (3, 8, 13),       -- Datenbanksysteme I -> Maja Koch
  (4, 10, 15),      -- Betriebssysteme Vertiefung -> Olga Brandt
  (5, 12, 11),      -- Netzwerkprotokolle -> Katrin Lorenz
  (6, 13, 9),       -- Software Engineering -> Ines Schreiber
  (7, 20, 14),      -- Webentwicklung Frontend -> Nils Peters
  (8, 24, 28),      -- Wirtschaftsinformatik -> Bella Mueller

  -- Master SoSe 26 (Kurse 25, 27, 28, 31, 33, 36, 43, 46) - KURS 48 UNBESETZT
  (9, 25, 1),       -- Advanced SE -> Anna Müller
  (10, 27, 6),      -- Enterprise Architecture -> Frank Wagner
  (11, 28, 2),      -- Cloud & DevOps -> Bernd Schmidt
  (12, 31, 5),      -- Machine Learning -> Eva Klein
  (13, 33, 18),     -- Data Science -> Rita Maier
  (14, 36, 3),      -- Cyber Security Advanced -> Carla Meier
  (15, 43, 7),      -- Formal Methods -> Gisela Hartmann
  (16, 46, 5),      -- KI im Business -> Eva Klein

  -- UNBESETZTE KURSE IM SoSe 26 (strategisch für Reports):
  -- Kurs 17: IT-Sicherheit Grundlagen (Bachelor)
  -- Kurs 19: Datenschutz und Compliance (Bachelor)
  -- Kurs 48: Forschungsprojekt - Spezialthema (Master)
  -- Diese 3 Kurse haben CanHold-Einträge, aber KEINE Zuordnung in lecturer_holds_course

  -- Weitere Semester: ein paar zufällige Zuordnungen zur Vollständigkeit
  (17, 1, 8),       -- Programmierung I (WiSe 25/26) -> Heike
  (18, 7, 10),      -- Datenbanken Grundlagen (WiSe 25/26) -> Jan
  (19, 11, 26),     -- Rechnernetze Grundlagen (WiSe 26/27) -> Zoe
  (20, 30, 4),      -- Hochperformance Computing (SoSe 27) -> Dieter
  (21, 32, 5),      -- Deep Learning (WiSe 26/27) -> Eva
  (22, 38, 3);      -- Security Engineering (SoSe 27) -> Carla

COMMIT;
PRAGMA foreign_keys=ON;

-- ============================================================
-- TESTDATEN-ZUSAMMENFASSUNG
-- ============================================================
-- Dozenten: 28 (erfüllt 20-30)
-- Vorlesungen: 48 (erfüllt 35-50)
--   - Bachelor: 24 (is_master=0)
--   - Master: 24 (is_master=1)
-- Semester: SoSe 25, WiSe 25/26, SoSe 26, WiSe 26/27, SoSe 27, WiSe 27/28
-- CanHoldCourse-Einträge: 142 (umfangreich)
-- HoldsCourse-Einträge (SoSe 26): 19 + weitere Semester
--
-- EDGE CASES für Reports:
-- - Kurs 42 (Quantencomputing): Nur 1 unqualifizierter Dozent (OVER_FOUR_WEEKS)
-- - Kurs 40, 44, 48: Begrenzte Dozenten-Pools
-- - Kurse 6, 9, 23: In SoSe 26 unbesetzt
-- - Viele Dozenten mit alreadyHeld=PROVADIS (Report 1 liefert viele Ergebnisse)
-- - Viele Dozenten mit NOT_YET_HELD (Report 2 liefert viele Ergebnisse)
-- ============================================================
