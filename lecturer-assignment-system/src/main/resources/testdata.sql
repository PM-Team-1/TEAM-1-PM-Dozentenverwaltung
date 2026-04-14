-- Umfangreiche Testdaten für die SQLite-Datenbank
-- Enthält viele Einfügungen für Lecturer, Course und LecturerCanHoldCourse
-- Annahme: Hibernate verwendet die standardmäßige physical naming strategy,
-- daher sind die Tabellen snake_case: lecturer, course, lecturer_can_hold_course
-- Spalten sind ebenfalls snake_case aus den Feldnamen abgeleitet.

PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;

-- Vor dem Anlegen neuer Testdaten: alle bisherigen Daten löschen
-- Wichtig: Child-Tabellen zuerst löschen, damit FK-Constraints nicht verletzt werden.
DELETE FROM lecturer_can_hold_course;
DELETE FROM lecturer;
DELETE FROM course;

-- 30 Dozenten (verschiedene Präferenzen)
-- Preference enum values: ALLES, ONLY_MASTER, ONLY_BACHELOR, PREFER_MASTER, PREFER_BACHELOR
INSERT INTO lecturer (id, title, first_name, last_name, second_name, email, phone, is_extern, teaching_preference) VALUES
  (1, 'DOCTOR', 'Anna', 'Müller', 'Marie', 'anna.mueller@uni.de', '+491711234567', 0, 'PREFER_MASTER'),
  (2, 'PROFESSOR', 'Bernd', 'Schmidt', 'Karl', 'bernd.schmidt@uni.de', '+491721234568', 0, 'PREFER_MASTER'),
  (3, 'DOCTOR', 'Carla', 'Meier', 'Theresa', 'carla.meier@uni.de', '+491731234569', 0, 'PREFER_MASTER'),
  (4, 'DOCTOR', 'Dieter', 'Fischer', NULL, 'd.fischer@provadis.de', '+491741234570', 1, 'PREFER_MASTER'),
  (5, 'PROFESSOR', 'Eva', 'Klein', NULL, 'eva.klein@other.edu', '+491751234571', 1, 'PREFER_MASTER'),
  (6, 'DOCTOR', 'Frank', 'Wagner', NULL, 'frank.wagner@uni.de', '+491761234572', 0, 'PREFER_MASTER'),

  (7, 'PROFESSOR', 'Gerd', 'Hartmann', 'Heinrich', 'gerd.hartmann@uni.de', '+491771234573', 0, 'PREFER_BACHELOR'),
  (8, 'PROFESSOR', 'Heike', 'Becker', NULL, 'heike.becker@uni.de', '+491781234574', 1, 'PREFER_BACHELOR'),
  (9, 'DOCTOR', 'Ines', 'Schreiber', NULL, 'ines.schreiber@uni.de', '+491791234575', 0, 'PREFER_BACHELOR'),
  (10, 'PROFESSOR', 'Jan', 'Keller', NULL, 'jan.keller@uni.de', '+491701234576', 0, 'PREFER_BACHELOR'),
  (11, 'DOCTOR', 'Katrin', 'Lorenz', NULL, 'k.lorenz@other.edu', '+491721234577', 1, 'PREFER_BACHELOR'),
  (12, 'PROFESSOR', 'Lukas', 'Neumann', NULL, 'l.neumann@uni.de', '+491731234578', 0, 'PREFER_BACHELOR'),

  (13, 'DOCTOR', 'Maja', 'Koch', NULL, 'maja.koch@hs.de', '+491741234579', 0, 'ONLY_MASTER'),
  (14, 'DOCTOR', 'Nils', 'Peters', NULL, 'n.peters@provadis.de', '+491751234580', 1, 'ONLY_MASTER'),
  (15, 'PROFESSOR', 'Olga', 'Brandt', NULL, 'olga.brandt@uni.de', '+491761234581', 0, 'ONLY_MASTER'),
  (16, 'PROFESSOR', 'Paul', 'Neumann', NULL, 'paul.neumann@uni.de', '+491771234582', 1, 'ONLY_MASTER'),
  (17, 'NO_TITLE', 'Quirin', 'Weber', NULL, 'q.weber@uni.de', '+491781234583', 0, 'ONLY_MASTER'),
  (18, 'NO_TITLE', 'Rita', 'Maier', NULL, 'rita.maier@uni.de', '+491791234584', 0, 'ONLY_MASTER'),

  (19, 'NO_TITLE', 'Sven', 'Koch', NULL, 'sven.koch@uni.de', '+491701234585', 0, 'ONLY_BACHELOR'),
  (20, 'NO_TITLE', 'Tina', 'Lang', NULL, 'tina.lang@uni.de', '+491711234586', 0, 'ONLY_BACHELOR'),
  (21, 'NO_TITLE', 'Uwe', 'Bauer', NULL, 'uwe.bauer@uni.de', '+491721234587', 0, 'ONLY_BACHELOR'),
  (22, 'NO_TITLE', 'Veronika', 'Graf', NULL, 'veronika.graf@uni.de', '+491731234588', 0, 'ONLY_BACHELOR'),
  (23, 'DOCTOR', 'Wolf', 'Kramer', NULL, 'wolf.kramer@provadis.de', '+491741234589', 1, 'ONLY_BACHELOR'),
  (24, 'NO_TITLE', 'Xenia', 'Fuchs', NULL, 'xenia.fuchs@other.edu', '+491751234590', 1, 'ONLY_BACHELOR'),

  (25, 'PROFESSOR', 'Yann', 'Zimmer', 'Paul', 'yann.zimmer@uni.de', '+491761234591', 0, 'ALLES'),
  (26, 'NO_TITLE', 'Zoe', 'Keller', NULL, 'zoe.keller@uni.de', '+491771234592', 0, 'ALLES'),
  (27, 'NO_TITLE', 'Alex', 'Brand', NULL, 'alex.brand@uni.de', '+491781234593', 0, 'ALLES'),
  (28, 'DOCTOR', 'Bella', 'Kurz', NULL, 'bella.kurz@uni.de', '+491791234594', 0, 'ALLES'),
  (29, 'NO_TITLE', 'Cem', 'Alt', NULL, 'cem.alt@uni.de', '+491701234595', 0, 'ALLES'),
  (30, 'NO_TITLE', 'Dana', 'Neu', NULL, 'dana.neu@uni.de', '+491711234596', 0, 'ALLES');

-- 20 Vorlesungen: 1-10 Bachelor (is_master=0), 11-20 Master (is_master=1)
INSERT INTO course (id, name, is_closed, is_master, semester) VALUES
  (1, 'Programmieren I', 0, 0, 'WiSe 24/25'),
  (2, 'Mathematik für Informatik', 0, 0, 'WiSe 24/25'),
  (3, 'Datenbanken Grundlagen', 0, 0, 'SoSe 25'),
  (4, 'Betriebssysteme Grundlagen', 0, 0, 'SoSe 25'),
  (5, 'Softwareengineering Grundlagen', 0, 0, 'WiSe 25/26'),
  (6, 'Netzwerkgrundlagen', 0, 0, 'WiSe 25/26'),
  (7, 'Programmierung II', 0, 0, 'SoSe 26'),
  (8, 'IT-Sicherheit Grundlagen', 0, 0, 'SoSe 26'),
  (9, 'Datenstrukturen und Algorithmen I', 0, 0, 'WiSe 26/27'),
  (10, 'Webtechnologien', 0, 0, 'WiSe 26/27'),
  (11, 'Algorithmen Fortgeschritten', 0, 1, 'SoSe 27'),
  (12, 'Maschinelles Lernen', 0, 1, 'SoSe 27'),
  (13, 'Verteilte Systeme Fortgeschritten', 0, 1, 'WiSe 27/28'),
  (14, 'Softwarearchitektur', 0, 1, 'WiSe 27/28'),
  (15, 'Datenbanksysteme Fortgeschritten', 0, 1, 'SoSe 28'),
  (16, 'IT-Sicherheitsmanagement', 0, 1, 'SoSe 28'),
  (17, 'Fortgeschrittene Programmierung', 0, 1, 'WiSe 28/29'),
  (18, 'Cloud Computing', 0, 1, 'WiSe 28/29'),
  (19, 'AI Systems', 0, 1, 'SoSe 29'),
  (20, 'Software Projektarbeit', 0, 1, 'SoSe 29');

-- LecturerCanHoldCourse: erzeugt nach Präferenzlogik
-- already_held: PROVADIS, OTHER_SCHOOL, NOT_YET_HELD
-- qualification: IMMEDIATELY, FOUR_WEEKS, OVER_FOUR_WEEKS
-- affinity: individuell / zufällig je Zuordnung (LOW/MEDIUM/HIGH)

INSERT INTO lecturer_can_hold_course (id, already_held, qualification, course_id, lecturer_id, affinity) VALUES
  -- IDs start at 1 und erhöhen fortlaufend; für Übersicht gruppiert pro Lecturer
  -- Lecturer 1..6: PREFER_MASTER -> geben je 4 Master (prio=1) und 2 Bachelor (prio=0)
  (1, 'NOT_YET_HELD', 'IMMEDIATELY', 11, 1, 'HIGH'),
  (2, 'NOT_YET_HELD', 'FOUR_WEEKS', 12, 1, 'MEDIUM'),
  (3, 'OTHER_SCHOOL', 'IMMEDIATELY', 13, 1, 'LOW'),
  (4, 'PROVADIS', 'IMMEDIATELY', 14, 1, 'HIGH'),
  (5, 'NOT_YET_HELD', 'IMMEDIATELY', 1, 1, 'MEDIUM'),
  (6, 'PROVADIS', 'FOUR_WEEKS', 2, 1, 'LOW'),

  (7, 'NOT_YET_HELD', 'IMMEDIATELY', 11, 2, 'LOW'),
  (8, 'OTHER_SCHOOL', 'FOUR_WEEKS', 15, 2, 'HIGH'),
  (9, 'NOT_YET_HELD', 'IMMEDIATELY', 16, 2, 'MEDIUM'),
  (10, 'NOT_YET_HELD', 'OVER_FOUR_WEEKS', 17, 2, 'HIGH'),
  (11, 'NOT_YET_HELD', 'IMMEDIATELY', 3, 2, 'MEDIUM'),
  (12, 'PROVADIS', 'IMMEDIATELY', 4, 2, 'LOW'),

  (13, 'OTHER_SCHOOL', 'IMMEDIATELY', 12, 3, 'MEDIUM'),
  (14, 'NOT_YET_HELD', 'IMMEDIATELY', 13, 3, 'LOW'),
  (15, 'NOT_YET_HELD', 'FOUR_WEEKS', 18, 3, 'HIGH'),
  (16, 'PROVADIS', 'IMMEDIATELY', 19, 3, 'MEDIUM'),
  (17, 'NOT_YET_HELD', 'IMMEDIATELY', 5, 3, 'HIGH'),
  (18, 'OTHER_SCHOOL', 'FOUR_WEEKS', 6, 3, 'LOW'),

  (19, 'PROVADIS', 'IMMEDIATELY', 11, 4, 'HIGH'),
  (20, 'PROVADIS', 'IMMEDIATELY', 12, 4, 'LOW'),
  (21, 'NOT_YET_HELD', 'IMMEDIATELY', 14, 4, 'MEDIUM'),
  (22, 'OTHER_SCHOOL', 'FOUR_WEEKS', 20, 4, 'HIGH'),
  (23, 'NOT_YET_HELD', 'IMMEDIATELY', 7, 4, 'LOW'),
  (24, 'PROVADIS', 'IMMEDIATELY', 8, 4, 'MEDIUM'),

  (25, 'NOT_YET_HELD', 'IMMEDIATELY', 15, 5, 'LOW'),
  (26, 'NOT_YET_HELD', 'FOUR_WEEKS', 16, 5, 'MEDIUM'),
  (27, 'OTHER_SCHOOL', 'IMMEDIATELY', 17, 5, 'HIGH'),
  (28, 'NOT_YET_HELD', 'IMMEDIATELY', 18, 5, 'MEDIUM'),
  (29, 'NOT_YET_HELD', 'IMMEDIATELY', 9, 5, 'HIGH'),
  (30, 'PROVADIS', 'IMMEDIATELY', 10, 5, 'LOW'),

  (31, 'NOT_YET_HELD', 'IMMEDIATELY', 11, 6, 'MEDIUM'),
  (32, 'NOT_YET_HELD', 'IMMEDIATELY', 12, 6, 'HIGH'),
  (33, 'OTHER_SCHOOL', 'IMMEDIATELY', 13, 6, 'LOW'),
  (34, 'PROVADIS', 'IMMEDIATELY', 14, 6, 'HIGH'),
  (35, 'NOT_YET_HELD', 'OVER_FOUR_WEEKS', 2, 6, 'MEDIUM'),
  (36, 'OTHER_SCHOOL', 'FOUR_WEEKS', 3, 6, 'LOW'),

  -- Lecturer 7..12: PREFER_BACHELOR -> geben je 4 Bachelor (prio=1) und 2 Master (prio=0)
  (37, 'NOT_YET_HELD', 'IMMEDIATELY', 1, 7, 'MEDIUM'),
  (38, 'PROVADIS', 'FOUR_WEEKS', 2, 7, 'LOW'),
  (39, 'OTHER_SCHOOL', 'IMMEDIATELY', 3, 7, 'HIGH'),
  (40, 'NOT_YET_HELD', 'IMMEDIATELY', 4, 7, 'MEDIUM'),
  (41, 'NOT_YET_HELD', 'IMMEDIATELY', 11, 7, 'HIGH'),
  (42, 'PROVADIS', 'FOUR_WEEKS', 12, 7, 'LOW'),

  (43, 'NOT_YET_HELD', 'IMMEDIATELY', 5, 8, 'LOW'),
  (44, 'NOT_YET_HELD', 'FOUR_WEEKS', 6, 8, 'MEDIUM'),
  (45, 'OTHER_SCHOOL', 'IMMEDIATELY', 7, 8, 'HIGH'),
  (46, 'PROVADIS', 'IMMEDIATELY', 8, 8, 'LOW'),
  (47, 'NOT_YET_HELD', 'IMMEDIATELY', 13, 8, 'MEDIUM'),
  (48, 'NOT_YET_HELD', 'FOUR_WEEKS', 14, 8, 'HIGH'),

  (49, 'OTHER_SCHOOL', 'IMMEDIATELY', 9, 9, 'MEDIUM'),
  (50, 'NOT_YET_HELD', 'IMMEDIATELY', 10, 9, 'LOW'),
  (51, 'PROVADIS', 'FOUR_WEEKS', 1, 9, 'HIGH'),
  (52, 'NOT_YET_HELD', 'IMMEDIATELY', 2, 9, 'MEDIUM'),
  (53, 'NOT_YET_HELD', 'IMMEDIATELY', 15, 9, 'HIGH'),
  (54, 'OTHER_SCHOOL', 'FOUR_WEEKS', 16, 9, 'LOW'),

  (55, 'NOT_YET_HELD', 'IMMEDIATELY', 3, 10, 'LOW'),
  (56, 'NOT_YET_HELD', 'FOUR_WEEKS', 4, 10, 'MEDIUM'),
  (57, 'PROVADIS', 'IMMEDIATELY', 5, 10, 'HIGH'),
  (58, 'OTHER_SCHOOL', 'IMMEDIATELY', 6, 10, 'LOW'),
  (59, 'NOT_YET_HELD', 'IMMEDIATELY', 17, 10, 'MEDIUM'),
  (60, 'NOT_YET_HELD', 'FOUR_WEEKS', 18, 10, 'HIGH'),

  -- Lecturer 13..18: ONLY_MASTER -> nur masterkurse
  (61, 'NOT_YET_HELD', 'IMMEDIATELY', 11, 13, 'HIGH'),
  (62, 'PROVADIS', 'FOUR_WEEKS', 12, 13, 'LOW'),
  (63, 'OTHER_SCHOOL', 'IMMEDIATELY', 13, 13, 'MEDIUM'),
  (64, 'NOT_YET_HELD', 'IMMEDIATELY', 14, 13, 'HIGH'),
  (65, 'NOT_YET_HELD', 'IMMEDIATELY', 15, 13, 'LOW'),
  (66, 'PROVADIS', 'IMMEDIATELY', 16, 13, 'MEDIUM'),

  (67, 'NOT_YET_HELD', 'IMMEDIATELY', 12, 14, 'LOW'),
  (68, 'OTHER_SCHOOL', 'FOUR_WEEKS', 13, 14, 'HIGH'),
  (69, 'NOT_YET_HELD', 'IMMEDIATELY', 14, 14, 'MEDIUM'),
  (70, 'PROVADIS', 'IMMEDIATELY', 15, 14, 'LOW'),
  (71, 'NOT_YET_HELD', 'IMMEDIATELY', 16, 14, 'HIGH'),
  (72, 'OTHER_SCHOOL', 'OVER_FOUR_WEEKS', 17, 14, 'MEDIUM'),

  (73, 'NOT_YET_HELD', 'IMMEDIATELY', 11, 15, 'MEDIUM'),
  (74, 'NOT_YET_HELD', 'FOUR_WEEKS', 12, 15, 'HIGH'),
  (75, 'OTHER_SCHOOL', 'IMMEDIATELY', 13, 15, 'LOW'),
  (76, 'PROVADIS', 'IMMEDIATELY', 14, 15, 'MEDIUM'),
  (77, 'NOT_YET_HELD', 'IMMEDIATELY', 18, 15, 'LOW'),
  (78, 'NOT_YET_HELD', 'OVER_FOUR_WEEKS', 19, 15, 'HIGH'),

  (79, 'NOT_YET_HELD', 'IMMEDIATELY', 11, 16, 'LOW'),
  (80, 'PROVADIS', 'FOUR_WEEKS', 12, 16, 'MEDIUM'),
  (81, 'OTHER_SCHOOL', 'IMMEDIATELY', 13, 16, 'HIGH'),
  (82, 'NOT_YET_HELD', 'IMMEDIATELY', 14, 16, 'LOW'),
  (83, 'NOT_YET_HELD', 'IMMEDIATELY', 15, 16, 'MEDIUM'),
  (84, 'PROVADIS', 'IMMEDIATELY', 20, 16, 'HIGH'),

  -- Lecturer 19..24: ONLY_BACHELOR -> nur bachelorkurse
  (85, 'NOT_YET_HELD', 'IMMEDIATELY', 1, 19, 'HIGH'),
  (86, 'NOT_YET_HELD', 'FOUR_WEEKS', 2, 19, 'LOW'),
  (87, 'OTHER_SCHOOL', 'IMMEDIATELY', 3, 19, 'MEDIUM'),
  (88, 'PROVADIS', 'IMMEDIATELY', 4, 19, 'HIGH'),
  (89, 'NOT_YET_HELD', 'IMMEDIATELY', 5, 19, 'LOW'),
  (90, 'OTHER_SCHOOL', 'FOUR_WEEKS', 6, 19, 'MEDIUM'),

  (91, 'NOT_YET_HELD', 'IMMEDIATELY', 7, 20, 'LOW'),
  (92, 'PROVADIS', 'FOUR_WEEKS', 8, 20, 'MEDIUM'),
  (93, 'NOT_YET_HELD', 'IMMEDIATELY', 9, 20, 'HIGH'),
  (94, 'OTHER_SCHOOL', 'IMMEDIATELY', 10, 20, 'LOW'),
  (95, 'NOT_YET_HELD', 'IMMEDIATELY', 1, 20, 'MEDIUM'),
  (96, 'PROVADIS', 'FOUR_WEEKS', 2, 20, 'HIGH'),

  (97, 'NOT_YET_HELD', 'IMMEDIATELY', 3, 21, 'MEDIUM'),
  (98, 'OTHER_SCHOOL', 'FOUR_WEEKS', 4, 21, 'LOW'),
  (99, 'NOT_YET_HELD', 'IMMEDIATELY', 5, 21, 'HIGH'),
  (100, 'PROVADIS', 'IMMEDIATELY', 6, 21, 'MEDIUM'),
  (101, 'NOT_YET_HELD', 'IMMEDIATELY', 7, 21, 'LOW'),
  (102, 'OTHER_SCHOOL', 'FOUR_WEEKS', 8, 21, 'HIGH'),

  (103, 'NOT_YET_HELD', 'IMMEDIATELY', 9, 22, 'HIGH'),
  (104, 'PROVADIS', 'FOUR_WEEKS', 10, 22, 'MEDIUM'),
  (105, 'NOT_YET_HELD', 'IMMEDIATELY', 1, 22, 'LOW'),
  (106, 'OTHER_SCHOOL', 'IMMEDIATELY', 2, 22, 'HIGH'),
  (107, 'NOT_YET_HELD', 'IMMEDIATELY', 3, 22, 'MEDIUM'),
  (108, 'PROVADIS', 'FOUR_WEEKS', 4, 22, 'LOW'),

  -- Lecturer 25..30: ALLES -> meist NULL priorities, aber einige Ausnahmen
  (109, 'NOT_YET_HELD', 'IMMEDIATELY', 1, 25, 'LOW'),
  (110, 'OTHER_SCHOOL', 'FOUR_WEEKS', 11, 25, 'HIGH'),
  (111, 'PROVADIS', 'IMMEDIATELY', 5, 25, 'MEDIUM'),
  (112, 'NOT_YET_HELD', 'IMMEDIATELY', 12, 25, 'LOW'),
  (113, 'NOT_YET_HELD', 'FOUR_WEEKS', 7, 25, 'HIGH'),
  (114, 'OTHER_SCHOOL', 'IMMEDIATELY', 15, 25, 'MEDIUM'),

  (115, 'PROVADIS', 'IMMEDIATELY', 2, 26, 'MEDIUM'),
  (116, 'NOT_YET_HELD', 'FOUR_WEEKS', 3, 26, 'HIGH'),
  (117, 'OTHER_SCHOOL', 'IMMEDIATELY', 13, 26, 'LOW'),
  (118, 'NOT_YET_HELD', 'IMMEDIATELY', 6, 26, 'LOW'),
  (119, 'NOT_YET_HELD', 'IMMEDIATELY', 14, 26, 'MEDIUM'),
  (120, 'PROVADIS', 'FOUR_WEEKS', 8, 26, 'HIGH'),

  (121, 'NOT_YET_HELD', 'IMMEDIATELY', 4, 27, 'HIGH'),
  (122, 'OTHER_SCHOOL', 'FOUR_WEEKS', 9, 27, 'LOW'),
  (123, 'PROVADIS', 'IMMEDIATELY', 10, 27, 'MEDIUM'),
  (124, 'NOT_YET_HELD', 'IMMEDIATELY', 11, 27, 'HIGH'),
  (125, 'NOT_YET_HELD', 'FOUR_WEEKS', 18, 27, 'LOW'),
  (126, 'OTHER_SCHOOL', 'IMMEDIATELY', 20, 27, 'MEDIUM'),

  (127, 'NOT_YET_HELD', 'IMMEDIATELY', 1, 28, 'MEDIUM'),
  (128, 'NOT_YET_HELD', 'FOUR_WEEKS', 2, 28, 'LOW'),
  (129, 'PROVADIS', 'IMMEDIATELY', 3, 28, 'HIGH'),
  (130, 'OTHER_SCHOOL', 'IMMEDIATELY', 14, 28, 'LOW'),
  (131, 'NOT_YET_HELD', 'IMMEDIATELY', 7, 28, 'HIGH'),
  (132, 'NOT_YET_HELD', 'FOUR_WEEKS', 16, 28, 'MEDIUM'),

  (133, 'PROVADIS', 'IMMEDIATELY', 5, 29, 'LOW'),
  (134, 'NOT_YET_HELD', 'FOUR_WEEKS', 6, 29, 'HIGH'),
  (135, 'OTHER_SCHOOL', 'IMMEDIATELY', 11, 29, 'MEDIUM'),
  (136, 'NOT_YET_HELD', 'IMMEDIATELY', 12, 29, 'LOW'),
  (137, 'NOT_YET_HELD', 'FOUR_WEEKS', 17, 29, 'MEDIUM'),
  (138, 'PROVADIS', 'IMMEDIATELY', 19, 29, 'HIGH'),

  (139, 'NOT_YET_HELD', 'IMMEDIATELY', 8, 30, 'HIGH'),
  (140, 'OTHER_SCHOOL', 'FOUR_WEEKS', 9, 30, 'LOW'),
  (141, 'PROVADIS', 'IMMEDIATELY', 13, 30, 'MEDIUM'),
  (142, 'NOT_YET_HELD', 'IMMEDIATELY', 14, 30, 'HIGH'),
  (143, 'NOT_YET_HELD', 'IMMEDIATELY', 20, 30, 'LOW'),
  (144, 'OTHER_SCHOOL', 'FOUR_WEEKS', 10, 30, 'MEDIUM');

COMMIT;
PRAGMA foreign_keys=ON;

-- Ende der umfangreichen Testdaten
