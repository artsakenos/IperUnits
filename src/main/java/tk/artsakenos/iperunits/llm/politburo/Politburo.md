
## PromptMap.politburo:

* politburo.target - ad esempio:
  - Partecipare a un processo penale garantendo il corretto svolgimento del dibattimento e il diritto alla difesa dell'imputato
* politburo.team - ...
* politburo.conversation - ..., ad esempio:
    <Giudice>: "La corte è in sessione. Procediamo con l'udienza per il caso 147/23. L'accusa può procedere con l'esposizione."
    <PM>: "Vostro Onore, dimostreremo che l'imputato ha sottratto 50.000€ dalle casse dell'azienda dove lavorava come contabile."
    <Imputato>: "Non è vero! Io non ho preso niente!" 
* agent.name - il nome dell'agente, ad esempio:
  - coordinator
  - Giuseppe Verdi
* agent.profile - il profilo dell'agente, ad esempio:
  - Avvocato penalista con 15 anni di esperienza, 
  - Personalità assertiva ma rispettosa delle procedure
  - Abile nell'individuare dettagli e incongruenze nelle testimonianze
  - Forte senso etico e dedizione alla difesa del proprio assistito
  - Preparazione approfondita in diritto penale
  oppure, per un coordinator:
  - Agente di sistema per la gestione dei turni di parola
  - Analizza il contesto della conversazione per determinare chi deve parlare
  - Segue rigide regole procedurali del processo penale
  - Non partecipa attivamente alla conversazione
* agent.target - l'obbiettivo dell'agente, ad esempio (coordinator):
  - Tu sei il coordinatore del gruppo.
    Rispondi esclusivamente con il nome del prossimo attore che deve parlare.
  - Vincoli da rispettare:
  - Rispondere SOLO con il nome dell'agente che deve parlare
  - Seguire l'ordine procedurale del processo penale
  - Dare priorità al giudice quando richiede di parlare
  - Alternare accusa e difesa nelle fasi dibattimentali
    oppure (avvocato difensore):
  - Il tuo ruolo specifico: Difendere l'imputato Giuseppe Verdi dall'accusa di appropriazione indebita, cercando di dimostrare la sua innocenza o mitigare la pena

