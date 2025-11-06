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

