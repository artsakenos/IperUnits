
# Introduzione

[![](https://jitpack.io/v/artsakenos/IperUnits.svg)](https://jitpack.io/#artsakenos/IperUnits)

**IperUnits** ([JavaDocs](https://artsakenos.github.io/ultra_lib_docs/IperUnits/index.html)) è una libreria Java multi purpose, progettata per soddisfare esigenze applicative.
Include una serie di strumenti generici per semplificare lo sviluppo di applicazioni.

## Caratteristiche principali:

- **Audio Sampling**: Funzionalità per la gestione e l'analisi di campioni audio e sequenze midi.
- **Gestione Database**: Supporto per Sqlite, H2, file CSV, e modelli di tabelle.
- **Gestione File e Directory**: Utility per lavorare con file, directory, archivi ZIP e operazioni di parsing.
- **Gestione Form e Grafica Swing**: Strumenti per la creazione e gestione di interfacce utente (UI) con Swing, e utility per migliorare l’esperienza dell'utente.
- **Gestione Inizializzatori e Proprietà**: Sistemi per gestire configurazioni e proprietà di applicazioni in modo semplice anche su cloud.
- **Modelli LLM e Agenti**: Integrazione con modelli linguistici di grandi dimensioni (LLM), agenti intelligenti, routing e retrieval-augmented generation (RAG).
- **Serializzazione e Modellazione JSON**: Supporto per la serializzazione, la modellazione di oggetti JSON, cifratura e bitwise operations.
- **Manipolazione Stringhe e Normalizzazione**: Funzionalità per formattare, normalizzare e manipolare stringhe, inclusi grammatica, sillabazione, tokenizzazione, formattazione date.
- **Utility di Sistema**: Strumenti per timer, profiler, interazione con l’ambiente umano e integrazione con la Java PowerShell. Sistema di gestione eventi e periferiche hardware.
- **Estensioni per Collezioni e Tipi di Dati**: Astrazione di liste, mappe e tipi di dati personalizzati.
- **Librerie Web**: Strumenti per client-server, blockchain, Delgado, utility web e gestione di archivi online.


## Ecosistema

L'ecosistema comprende un insieme di librerie progettate per rispondere a diverse necessità nello sviluppo di applicazioni,
dalla gestione di dati e risorse multimediali alla creazione di applicazioni intelligenti.
Ogni modulo offre funzionalità specifiche che possono essere facilmente integrate in progetti Java o Android.

* **UltraAnalysis** ([docs](https://artsakenos.github.io/ultra_lib_docs/UltraAnalysis/index.html)) - Classificatori bayesiani, feature selectors, analisi del testo, POS tagging, generazione grafi, wrapper per social network.
* **UltraHttp** ([docs](https://artsakenos.github.io/ultra_lib_docs/UltraHttp/index.html)) - Client-server, wrapper per CMS, suite di plugin per l'estensione dei microservizi e db noSQL, API per Google e Delgado.
* **UltraNeuro** - Comprende **UltraNeuroApps** e **AppsDL** per deep learning e reti neurali.
* **UltraImage** - Libreria per elaborazione e analisi di immagini.
* **UltraCMS** ([docs](https://artsakenos.github.io/ultra_lib_docs/UltraCMS/index.html)) - Framework per CMS, con sistema di template e supporto plugin.
* **UltraHook** - Strumenti di hooking e gestione di eventi di sistema, delle applicazioni, del clibpboard.
* **IperLibroid** ([docs](https://artsakenos.github.io/ultra_lib_docs/IperLibroid/index.html)) - Librerie general purpose per lo sviluppo Android.


## Applicazioni

Alcune applicazioni costruite sopra le librerie, sono ad esempio:
* IperApps
* UltraServices


# Utilizzo
Le IperUnits possono essere
1. Distribuite come jar. Compilate con Maven,
   per la build di un fat jar, viene usato il plugin maven-assembly-plugin
   (vedi tutto il <build> dal pom.xml, con la main class).
2. Incorporate dal mio virtual maven repository:
   Aggiungere il repository
```xml    
<repositories>
    <repository>
        <id>ultra-mvn-repo</id>
        <name>UltraMaven</name>
        <url>
            https://artsakenos.github.io/ultra_maven/
        </url>
    </repository>
</repositories>
```
e aggiungere le dipendenze (anche di una delle sotto librerie disponibili)
```xml    
<dependencies>
    <dependency>
        <groupId>tk.artsakenos</groupId>
        <artifactId>UltraCMS</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency> 
</dependencies>
```

3. Incorporate tramite JitPack:
   Aggiungere il repository
```xml    
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```
e aggiungere le dipendenze
```xml    
<dependencies>
  <dependency>
    <groupId>com.github.artsakenos</groupId>
    <artifactId>IperUnits</artifactId>
    <version>2024.11</version>
  </dependency>
</dependencies>
```
4. Tramite il Github Packaging - Forse in futuro.


## Testing
Per l'utilizzo di alcune funzionalità e per il testing utilizzare il file di configurazione infodev.properties con tutte le credenziali necessarie.
Si puó creare un file default mediante test TestSuperProperties.



# Notes, Credits, and Todo

**JavaDocs** -
Inserire i parametri: -Xdoclint:none -encoding UTF-8 -charset UTF-8

**Release Tagging**
- Aggiorna il JitPack shield
- git commit -am "Release 2024.03" && git push origin main
- git tag -a 2024.03 -m "Release 2024.03" && git push origin 2024.03
- Check on [JitPack](https://jitpack.io/#artsakenos/IperUnits) - (pom.xml)[https://jitpack.io/com/github/artsakenos/IperUnits/2024.03/pom.xml]


## Java Shell

[1]-> /classpath .\dist\JavaTest.jar
|  Path '.\dist\JavaTest.jar' added to classpath
[1]-> import javatest.JavaTest;
[2]-> JavaTest jt = new JavaTest();
|  jt ==> javatest.JavaTest@2de8284b
[3]-> jt.main(new String[]{});
[4]-> (my method just prints something on the terminal output)

## Build a Fat Jar

```xml    
<build>
    <plugins>

        <plugin>
            <artifactId>maven-assembly-plugin</artifactId>
            <configuration>
                <archive>
                    <manifest>
                        <mainClass>tk.artsakenos.iperunits.APPS.Apps_Starter</mainClass>
                    </manifest>
                </archive>
                <descriptorRefs>
                    <descriptorRef>jar-with-dependencies</descriptorRef>
                </descriptorRefs>
            </configuration>
            <executions>
                <execution>
                    <id>make-assembly</id> <!-- this is used for inheritance merges -->
                    <phase>package</phase> <!-- bind to the packaging phase -->
                    <goals>
                        <goal>single</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>

    </plugins>
</build>
```
