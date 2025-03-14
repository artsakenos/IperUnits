
# Keyboard

Gestisce la simulazione di input da tastiera (click, scrittura, combinazioni di tasti) utilizzando la classe `Robot`.

## Comandi Principali

- **Click(int keyEvent, boolean down)**: Simula la pressione o rilascio di un tasto.
  - Esempio: `Click(KeyEvent.VK_A, true)` per premere "A".
  
- **Click(int keyEvent)**: Simula la pressione e il rilascio di un tasto.
  - Esempio: `Click(KeyEvent.VK_A)` per premere e rilasciare "A".

- **Click(int keyEvent1, int keyEvent2)**: Simula la pressione e il rilascio di due tasti (combinazioni).
  - Esempio: `Click(KeyEvent.VK_SHIFT, KeyEvent.VK_A)` per premere "Shift + A".

- **Click(char key)**: Simula la pressione di un tasto corrispondente a un carattere.
  - Esempio: `Click('A')` per premere "A".

- **Write(String text)**: Simula la digitazione di una stringa.
  - Esempio: `Write("Hello")` simula la scrittura della parola "Hello".

## Tasti Speciali

- **Combinazioni di tasti**:
  - KEYBOARD_CTRL_C := `[CONTROL]c[/CONTROL]` per "Ctrl + C".
  - KEYBOARD_CTRL_V := `[CONTROL]v[/CONTROL]` per "Ctrl + V".
  - KEYBOARD_NEW_BROWSER_TAB := `[CONTROL]t[/CONTROL]` per "Ctrl + T" (nuova scheda del browser).
  - KEYBOARD_CTRL_F4 := `[CONTROL][F4][/CONTROL]` per "Ctrl + F4" (chiusura scheda).

- **Altri tasti**: 
  - `[SHIFT]`, `[ALT]`, `[ENTER]`, `[BACK_SPACE]`, `[SPACE]`, ecc.

## Eccezioni
- **IllegalArgumentException**: Se un tasto non valido viene passato ai metodi `Click`.
- **AWTException**: Se non è possibile creare un'istanza di `Robot`.

## Esempio di utilizzo

```java
Keyboard.Write("Hello, [CONTROL]c[/CONTROL]!");
```

Simula la scrittura della stringa "Hello," seguita da "Ctrl + C".


# Humanizer

| **Comando**                      | **Descrizione**                                                                   | **Esempio** `Human.parseCommand("...");`   |
|----------------------------------|-----------------------------------------------------------------------------------|--------------------------------------------|
| **KEYBOARD TYPE {Stringa}**      | Simula la scrittura della stringa specificata.                                    | `KEYBOARD TYPE Hello`                      |
| **MOUSE MOVETO {x,y}**           | Muove il mouse lentamente alla posizione specificata.                             | `MOUSE MOVETO 100,200`                     |
| **MOUSE MOVETOR {x,y}**          | Muove rapidamente il mouse alla posizione specificata.                            | `MOUSE MOVETOR 100,200`                    |
| **MOUSE MOVEBY {dx,dy}**         | Muove il mouse relativamente alla posizione attuale.                              | `MOUSE MOVEBY 10,20`                       |
| **MOUSE CLICK {String}**         | Simula un clic del mouse, ad esempio "Left" o "Right".                            | `MOUSE CLICK Left`                         |
| **MOUSE WHEEL {delta}**          | Ruota la rotella del mouse in base al `delta` (+ per giù, - per su).              | `MOUSE WHEEL -1`                           |
| **SYSTEM WAIT {int}**            | Attende per un numero di millisecondi specificato.                                | `SYSTEM WAIT 1000`                         |
| **SYSTEM WAITCHANGE {x,y}**      | Attende che un pixel in posizione `{x,y}` cambi colore.                           | `SYSTEM WAITCHANGE 100,200`                |
| **SYSTEM WAITCOLOR {x,y,r,g,b}** | Attende che un pixel in posizione `{x,y}` diventi del colore specificato (r,g,b). | `SYSTEM WAITCOLOR 100,200,255,0,0`         |
| **SYSTEM EXECUTE {String}**      | Esegue il programma specificato dal percorso.                                     | `SYSTEM EXECUTE C:\\path\\to\\program.exe` |
| **SYSTEM START {String}**        | Esegue un programma su Windows utilizzando il comando `start`.                    | `SYSTEM START C:\\path\\to\\program.exe`   |
| **SYSTEM BEEP {freq}**           | Esegue un beep di sistema alla frequenza `freq`.                                  | `SYSTEM BEEP 1000`                         |
| **SYSTEM OPENFOLDER {String}**   | Apre una cartella specificata (funziona su Windows e Linux).                      | `SYSTEM OPENFOLDER C:\\path\\to\\folder`   |
| **SYSTEM OPENURL {String}**      | Apre l'URL specificato nel browser.                                               | `SYSTEM OPENURL http://example.com`        |

