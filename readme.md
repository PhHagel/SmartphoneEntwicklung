# PHANDAM-App

## 🩺 Projektbeschreibung

Die **PHANDAM-App** ist Teil eines Projekts im Rahmen des Studiengangs Informatik an der Hochschule Kempten. Sie ergänzt das Zusammenspiel von Hard- und Software im Empfangsbereich der „Arztpraxis XYZ“ und dient als benutzerfreundliche Schnittstelle zur Aufnahme und Weiterleitung von Bild- und Audiodaten.

### Hauptfunktionen:

- Aufnahme von Foto- und Audiodateien
- Weiterleitung der Dateien an einen zentralen Server
- Sprachgesteuerte Interaktionen
- Terminvereinbarung durch Sprachaufnahme
- Erfassung von Neukunden durch Sprachaufnahme

---

## ⚙️ Verwendete Technologien

- **Programmiersprache:** Java
- **Buildsystem:** Gradle
- **Entwicklungsumgebung:** Android Studio
- **Android-Version:** NUR API Level 33 (Android 13)
- **Zusätzliche Bibliotheken:** Diverse Android- und Netzwerk-Bibliotheken (nicht einzeln aufgeführt)

---

## 🛠️ Installation

Vor der Installation sollte geprüft werden, ob die richtige IP-Adresse des Servers in der App konfiguriert ist, da sich diese von der Nutzungsumgebung der App unterscheiden kann.

Die App ist nicht über den Google Play Store verfügbar. Die Installation erfolgt manuell über Android Studio:

1. Aktiviere **Entwickleroptionen** auf deinem Android-Gerät.
2. Aktiviere **USB-Debugging** in den Entwickleroptionen.
3. Verbinde das Smartphone per USB mit deinem Computer.
4. Öffne das Projekt in **Android Studio**.
5. Klicke auf **Run** (▶️), um die App auf dem verbundenen Gerät zu installieren.
6. Bestätige ggf. den Zugriff des Computers auf das Gerät.

---

## ▶️ Start und Verwendung

Nach erfolgreicher Installation:

- Die App erscheint im App-Menü und kann dort gestartet werden.
- Die App wird durch den Nutzer gesteuert und bietet dabei eine sprachgestützte Benutzerführung.
- Die App dient als Ein-/Ausgabe-Modul für das Gesamtsystem und reagiert automatisch auf Nutzereingaben und Nachrichten vom Server.

---

## 📱 Systemvoraussetzungen

- **Android-Version:** Genau Android 13 (API-Level 33) – spätere oder frühere Versionen werden nicht unterstützt.
- **Hardwareanforderungen:**
  - Frontkamera (Displayseite)
  - Mikrofon
  - Lautsprecher
- **App-Berechtigungen (beim Start bestätigen):**
  - Kamera (für Bildaufnahmen)
  - Mikrofon (für Audioaufnahmen)
  - Speicherzugriff (zum Speichern von Mediendateien)
  - Internetzugang (für Serverkommunikation)
  - Netzwerkstatus (zur Verbindungsprüfung)
  - Vibration (für haptisches Feedback)

---

## 👨‍💻 Autor

Dieses Projekt wurde im Rahmen eines Hochschulprojekts an der **Hochschule Kempten** entwickelt von:

**Philipp Hagel**

---

## 📄 Lizenz

*Nur für akademische Zwecke. Keine öffentliche Lizenzierung vorgesehen.*

