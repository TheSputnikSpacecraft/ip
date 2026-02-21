# 🕵️‍♂️ Requiem (Detective Hart Edition) - User Guide

**Requiem** isn't just a chatbot; it's your partner on the force. Adopting the persona of **Detective Marty Hart** from *True Detective*, it treats every task like a case file and you as "Rust", his brilliant but troubled partner.

> *"Alright, Rust. What are we lookin' at today? Make it quick."*

![Ui](https://raw.githubusercontent.com/TheSputnikSpacecraft/ip/master/docs/Ui.png)

## 🚀 Quick Start

1.  **Prerequisites**: Ensure you have **Java 17** installed.
2.  **Download**: Get the latest release from the [releases page](https://github.com/TheSputnikSpacecraft/ip/releases).
3.  **Launch**: Run the application:
    ```bash
    java -jar ip.jar
    ```
4.  **Engage**: The GUI will launch. Hart is waiting.

---

## 📂 Features & Case Management

### 📜 Command Format
*   **UPPER_CASE** words are parameters you supply (e.g., `todo LOCATION`).
*   **Dates** must follow the strict `yyyy-MM-dd` protocol (e.g., `2023-10-15`).
*   **Extras**: Parameters can be in any order, but let's keep it clean.

---

### 1. 🆘 Viewing Help
Lost? Calling for backup? This pulls up the manual.

*   **Format:** `help`

---

### 2. 📝 Adding a Todo
For general leads and tasks without a ticking clock.

*   **Format:** `todo DESCRIPTION`
*   **Example:**
    *   `todo analyze crime scene photos`
    *   `todo question the witness`

---

### 3. ⏰ Adding a Deadline
For tasks that need to be wrapped up by a specific time. Don't let the trail go cold.

*   **Format:** `deadline DESCRIPTION /by DATE`
*   **Example:**
    *   `deadline submit report /by 2023-10-15`

---

### 4. 📅 Adding an Event
For stakeouts, court dates, or meetings. Things with a start and end.

*   **Format:** `event DESCRIPTION /from DATE /to DATE`
*   **Example:**
    *   `event stakeout /from 2023-10-15 /to 2023-10-16`

---

### 5. 📋 Listing Tasks
Review the case board. See everything we've got pinned up.

*   **Format:** `list`

---

### 6. ✅ Marking a Task as Done
Close the case. File it away.

*   **Format:** `mark INDEX`
*   **Example:** `mark 1`

---

### 7. 🔓 Marking a Task as Not Done
Re-opening a cold case? Fine, but make it count.

*   **Format:** `unmark INDEX`
*   **Example:** `unmark 1`

---

### 8. 🗑️ Deleting a Task
Tear it off the board. It's a dead end.

*   **Format:** `delete INDEX`
*   **Example:** `delete 2`

---

### 9. 🔍 Finding Tasks
Search the files for specific keywords.

*   **Format:** `find KEYWORD`
*   **Example:** `find report`
    *   *Returns matches like "file missing report" or "submit autopsy report"*

---

### 10. 👋 Exiting
Head home. Try not to overthink things while you're gone.

*   **Format:** `bye`

---

## ❓ FAQ

**Q: How do I save my data?**
> **A:** Requiem files the paperwork automatically in the `data` folder. No need to assist.

**Q: Can I edit the save file directly?**
> **A:** You can try, but if you mess up the format, Hart might drop all your cases and start fresh. **Backup strictly recommended.**

---

## 📑 Command Summary

| Action | Format | Example |
| :--- | :--- | :--- |
| **Help** | `help` | `help` |
| **List** | `list` | `list` |
| **Todo** | `todo DESCRIPTION` | `todo buy coffee` |
| **Deadline** | `deadline DESC /by DATE` | `deadline report /by 2023-01-01` |
| **Event** | `event DESC /from DATE /to DATE` | `event meeting /from 2023-01-01 /to 2023-01-02` |
| **Mark** | `mark INDEX` | `mark 1` |
| **Unmark** | `unmark INDEX` | `unmark 1` |
| **Delete** | `delete INDEX` | `delete 2` |
| **Find** | `find KEYWORD` | `find book` |
| **Exit** | `bye` | `bye` |
