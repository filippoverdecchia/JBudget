# JBudget119474

Applicazione Java per la gestione del bilancio familiare sviluppata per il corso
di Modellazione e Gestione della Conoscenza / Programmazione Avanzata (UNICAM).

## Funzionalità
- Inserimento movimenti con tag gerarchici
- Movimenti programmati con rate mensili
- Scadenzario
- Statistiche per periodo
- Persistenza JSON
- Interfaccia JavaFX

## Avvio

### Da terminale
./gradlew run

### Da IDE
Eseguire la classe:
it.unicam.cs.mpgc.jbudget119474.gui.MainFX

## Organizzazione del progetto
- model: entità di dominio
- repository: gestione stato
- persistence: salvataggio/caricamento JSON
- stats: query statistiche
- gui: interfaccia grafica

## Note
Il file dati.json non è incluso nella repository e viene creato a runtime.
