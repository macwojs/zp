INSERT INTO role VALUES (1,'ROLE_ADMIN'),(2,'ROLE_USER'), (3, 'ROLE_MARSZALEK_SEJMU'), (4, 'ROLE_MARSZALEK_SENATU'), (5, 'ROLE_POSEL'), (6, 'ROLE_SENATOR'), (7, 'ROLE_PREZYDENT');

INSERT INTO "option" VALUES  (1, 'Tak'), (2, 'Nie'), (3, 'Za'), (4, 'Przeciw'), (5, 'Wstrzymał się');

INSERT INTO "function" VALUES    (1, 'Prezydent'), (2,'Premier'), (3,'Wicepremier'), (4, 'Minister'), (5,'Wiceminister'), (6,'Marszałek Sejmu'), (7,'Wicemarszałek Sejmu'),(8, 'Marszałek Senatu'), (9, 'Wicemarszałek Senatu');
INSERT INTO "documenttype" VALUES    (1,'Ustawa'), (2,'Rozporządzenie'), (3, 'Zarządzenie'), (4,'Uchwała'), (5,'Dyrektywa'), (6,'Poprawka do ustawy'), (7, 'Poprawka do uchwały');
INSERT INTO "documentstatus" VALUES   (1,'Głosowanie w Sejmie'), (2,'Głosowanie w Senacie'), (3,'Przyjęta'), (4,'Odrzucona'),  (5,'Pierwsze czytanie'), (6,'Drugie czytanie'), (7,'Trzecie czytanie'), (8,'Do zatwierdzenia przez Prezydenta'), (9, 'Do ponownego rozpatrzenia w Sejmie');


INSERT INTO "set" VALUES (1,'Tak/Nie'), (2,'Za/Przeciw/Wstrzymaj się');

INSERT INTO "optionset"("optionid", "setid") VALUES (1,1), (2,1), (3,2),(4,2),(5,2);

