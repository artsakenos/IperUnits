
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

