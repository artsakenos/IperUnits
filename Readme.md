# Introduzione

[![](https://jitpack.io/v/artsakenos/IperUnits.svg)](https://jitpack.io/#artsakenos/IperUnits)

**IperUnits** ([JavaDocs](https://artsakenos.github.io/ultra_lib_docs/IperUnits/index.html)) è una libreria Java multi-purpose,
una suite di strumenti progettata per soddisfare le esigenze applicative e semplificare l'integrazione di funzionalità di base.

## Caratteristiche principali:

- **Audio Sampling**: Gestione completa di campioni audio e sequenze MIDI.
- **Gestione Database**: Supporto per Sqlite, H2, file CSV e modelli di tabelle.
- **Gestione File e Directory**: Utility per operazioni su file, directory, archivi ZIP e parsing avanzato.
- **Gestione Form e Grafica Swing**: Suite completa per la creazione e gestione di interfacce utente con Swing.
- **Gestione Inizializzatori e Proprietà**: Sistema avanzato per la gestione di configurazioni e proprietà applicative, con supporto cloud.
- **[Modelli LLM e Agenti](./src/main/java/tk/artsakenos/iperunits/llm/Readme.md)**: 
    Integrazione con Large Language Models (LLM), 
    [agenti intelligenti](https://infodev.wordpress.com/2023/11/15/ai-agents/), routing e Retrieval-Augmented Generation (RAG).
- **Serializzazione e Modellazione JSON**: Framework completo per serializzazione, modellazione JSON, cifratura e operazioni bitwise.
- **Manipolazione Stringhe e Normalizzazione**: Strumenti avanzati per elaborazione di stringhe, grammatica, sillabazione, tokenizzazione e formattazione date.
- **[Utility di Sistema](./src/main/java/tk/artsakenos/iperunits/system/Readme.md)**: 
    Suite di strumenti per timing, profiling, interazione con l'ambiente e integrazione PowerShell.
    Sistema completo per gestione eventi e periferiche.
- **Estensioni per Collezioni e Tipi di Dati**: Framework esteso per la gestione di liste, mappe e tipi personalizzati.
- **Librerie Web**: Strumenti completi per operazioni client-server, blockchain, Delgado e gestione archivi online.
- **GUIs**: Some ready to use graphics user interface, come Humanizer. Ricorda di impostare `-Djava.awt.headless=false` nelle chiamate da headless applications.

## Ecosistema

L'ecosistema comprende un insieme di librerie progettate per rispondere a diverse esigenze nello sviluppo di applicazioni,
dalla gestione di dati e risorse multimediali alla creazione di applicazioni intelligenti.
Ogni modulo offre funzionalità specifiche integrabili in progetti Java o Android.

Molte librerie sono state reingegnerizzate, o deprecate in favore di nuove tecnologie
(ad esempio UltraNeuro utilizza versioni obsolete di TensorFlow e tecniche superate,
mentre UltraImage si basa su tecniche di Pattern Recognition e versioni di OpenCV da aggiornare).
Queste verranno progressivamente incorporate nelle IperUnits o riscritte all'interno di nuove librerie,
integrate negli UltraServices o in nuove applicazioni.

* **UltraAnalysis** ([docs](https://artsakenos.github.io/ultra_lib_docs/UltraAnalysis/index.html)) - Classificatori bayesiani, feature selectors, analisi del testo, POS tagging, generazione grafi, wrapper per social network. See e.g., [UltraGram](https://infodev.wordpress.com/2018/04/05/ultracms-uscenno-release/), [SuperReddit](https://infodev.wordpress.com/2017/01/20/esperimenti-con-reddit/), [FacebookPromoter](https://infodev.wordpress.com/2015/07/10/facebook-promoter/).
* **UltraHttp** ([docs](https://artsakenos.github.io/ultra_lib_docs/UltraHttp/index.html)) - Client-server, wrapper per CMS, suite di plugin per l'estensione dei microservizi e db noSQL, API per Google e Delgado.
* **UltraNeuro** - Comprende **UltraNeuroApps** e **AppsDL** per deep learning e reti neurali (see, e.g., [Borgs](https://infodev.wordpress.com/2018/04/15/borgs/), [Java AI Libraries](https://infodev.wordpress.com/2018/04/10/testing-some-java-ai-libraries/)).
* **UltraImage** - Libreria per elaborazione e analisi di immagini (see, e.g., [UltraGram](https://infodev.wordpress.com/2020/12/05/ultragram-bot-intergeptor-deluxe-2020/), [UltraCam - CodaDellOcchio](https://infodev.wordpress.com/2013/11/05/ultracam/)).
* **UltraCMS** ([docs](https://artsakenos.github.io/ultra_lib_docs/UltraCMS/index.html)) - ([article](https://infodev.wordpress.com/2018/04/05/ultracms-uscenno-release/)) Framework per CMS, con sistema di template e supporto plugin, see, e.g., CrisiDiGoverno.
* **UltraHook** - Strumenti di hooking e gestione di eventi di sistema, delle applicazioni, del clibpboard.
* **IperLibroid** ([docs](https://artsakenos.github.io/ultra_lib_docs/IperLibroid/index.html)) - Librerie [IperLibroid](https://github.com/artsakenos/Iperoid) general purpose per lo sviluppo Android e SwissArmyKnife App Iperoid.

E altre librerie e applicazioni:
* **UltraWemo** ([github](https://github.com/artsakenos/UltraWemo)) - allows you to control your WeMo Devices with a pure Java library with no dependencies.
* **UltraChess** - wrapper di Stockfish in Java
* **IperApps** - Uan suite di micro servizi per il sistema operativo
* [**UltraServices**](https://github.com/artsakenos/UltraServices) - Una suite di servizi e framework applicativi (per Java, Python, JS, React) containerizzata e distribution ready.


# Utilizzo
Le IperUnits possono essere
1. Distribuite come jar. Compilate con Maven,
   per la build viene usato il plugin maven-assembly-plugin, vedi `pom.xml::build`.
2. Incorporate dal mio virtual maven repository:
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
aggiungendo le dipendenze (anche di una delle sotto librerie disponibili)
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
```xml    
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```
aggiungendo le dipendenze
```xml    
<dependencies>
   <dependency>
      <groupId>com.github.artsakenos</groupId>
      <artifactId>IperUnits</artifactId>
      <version>2024.03</version>
   </dependency>
</dependencies>
```
4. Tramite il Github Packaging - Forse in futuro.


## Testing
Per l'utilizzo di alcune funzionalità e per il testing utilizzare il file di configurazione infodev.properties con tutte le credenziali necessarie.
Si puó creare un file default mediante test TestSuperProperties.


# Notes, Credits, and Todo

**JavaDocs** -
Compilare con i parametri:
`--Xdoclint:none -encoding UTF-8 -charset UTF-8 -doctitle "IperUnits ver. 2022.01" -windowtitle "IperUnits Libraries"`

**Release Tagging**

1. Aggiorna la versione su `pom.xml`
2. Commit the new release
    - `git add -A`
    - `git commit -am "Release 2025.03"`
    - `git push origin main`
    - `git tag -a 2025.03 -m "Release 2025.03"`
    - `git push origin 2025.03`
3. Check on [JitPack](https://jitpack.io/#artsakenos/IperUnits) 
   the corresponding [pom.xml](https://jitpack.io/com/github/artsakenos/IperUnits/2024.03/pom.xml)


## Java Shell

```bash
[1]-> /classpath .\dist\JavaTest.jar
|  Path '.\dist\JavaTest.jar' added to classpath
[1]-> import javatest.JavaTest;
[2]-> JavaTest jt = new JavaTest();
|  jt ==> javatest.JavaTest@2de8284b
[3]-> jt.main(new String[]{});
[4]-> (my method just prints something on the terminal output)
```


## Fat Jar Release

Aggiungere il maven-assembly-plugin, modificare la main class.

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
