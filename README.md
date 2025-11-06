🧩🎲 **15 Puzzle Game**🎲🧩

---

## 📖 15-spelet

Detta är ett **Java program** som implementerar det klassiska spelet *15 Pussel* men även känt som *Sliding Puzzle*.
Spelet låter användaren flytta numrerande brickor för att sortera dem i stigande ordning, men en tom ruta som flyttas runt.

Projektet använder sig av **Swing (JFrame/JPanel)** för det grafiska gränssnittet och spara **Highscores** lokalt i en textfil som är döpt efter *Highscore*.

Användaren kan välja **storlek** på brädet (2x2 till 8x8), se **antal drag** och **tid**, samt spara sina resultat i en **highscore lista**.

---

## ⚙️ Huvudfunktioner

- Grafiskt gränssnitt som visar numrerade brickor i spelet

- Klicka på bricka bredvid tom ruta flyttar den

- Ogiltigt klick (inte bredvid tom ruta) gör inget

- "Nytt spel" knapp som blandar brickorna slumpmässigt

- Möjlighet att välja brädstorlek (2x2 till 8x8)

- Undo/Redo av drag

- Räknar antal drag och tid i milisekunder som omvandlas till sekunder

- Visar meddelande "🎉 Grattis, du vann!" vid vinst

- Highscore lista över de bästa resultat (minst drag och tid)

- Sparar highscore i en "Highscore.txt"

- Möjlighet att spela igen eller avsluta efter den sköna, snabba vinsten

---

## 🧠 Programflöde 

1. 🚀**Start**
'GameFrame.java' startar program och
visar startbild vid 'StartPanel.java'.

2. 🧩**Välj spelstorlek**
En dialogruta frågar *"Vilken storlek vill du spela på? (2-8)"*
En exempel är '3' som ger ett 3x3 pussel

3. 🎮**Starta nytt spel**
'Board.java' initerar ett löst pussel och blandar det slumpmässigt.
'BoardPanel.java' ritar brickorna som knappar

4. 🖱️**Spelinteraktioner**
Användaren klickar på brickorna.
'GameController.java' kontrollerar giltig flytt (bredvid den tomma rutan).
Brädet uppdateras automatiskt via 'BoardPanel.refresh()'.

5. 🔄**Ångra/Gör om**
Användaren har en möjlighet att få klicka på "Ångra" eller "Gör om" för att flytta tillbaka brickorna.
Stack baserad implemenation ('undoStack' och 'redoStack').

6. 🏁**Vinst**
När brickorna ligger i ordning visas en dialogruta som står:
>🎉"Grattis, du löste spelet på X drag och Y sekunder!"
Spelaren har även alternativ att få vara med i en highscore listan.

7. 🏆**Highscore**
Sparar namn, antal drag och tid i 'highscore.txt' fil.
Visar de 10 bästa resultaten i en dialogruta.

8. 🔁**Spela igen eller avsluta**
Efter vinst får användaren frågan:

> "Vill du spela igen?"

> Ja -> Ny startbild

> Nej -> Programmet avslutas.


---

## 🧩 Klasser i projektet

| Klass | Beskrivning |

| **GameFrame.java** | Huvudfönster som startar hela spelet (JFrame) |

| **StartPanel.java** | Startbild med knapp för att börja spela |

| **Board.java** | Logiken för pusslet (flyttar, kontrollerar vinst) |

| **Tile.java** | Representerar en enskild bricka |

| **Move.java** | Sparar en flytt (från-rad, från-kolumn, till-rad, till-kolumn) |

| **BoardPanel.java** | Grafiskt bräde med knappar för varje ruta |

| **GameController.java** | Hanterar timer, drag, undo/redo och vinstlogik |

| **Highscore.java** | Sparar och läser highscore-lista från fil |


---


## ⚙️ Funktionert
- Klassisk 15-pussel-logik
- Spelplanens storlek hämtas automatiskt
- Fasta färger för spelplan och brickor
- Kommentarer i koden för tydlighet

---

## 🧑‍💻 Versionshantering
Projektet har utvecklats med Git genom feature-brancher (t.ex. `feature/comments`, `feature-tile-fix`),
som successivt har mergats in i `master` via pull requests.  
Detta syns i Git-historiken med `git log --graph --oneline --all`.

---

## ▶️ Körning
1. Klona projektet
2. Öppna i IntelliJ
3. Kör `GameFrame.java` för att starta spelet

---

## 💻 Om spelet

Spelet är inspirerat av det klassiska *15-pusslet*, där målet är att ordna brickorna i stigande ordning genom att flytta dem mot den tomma rutan.  
Det tränar logiskt tänkande, strategi och tålamod 🧠.

---

## 👨‍💻 Skapat av

**Alexander**, 2025  
🧩 *Projekt: 15-Pussel*  
📦 *Programmeringsspråk: Java Swing*  


