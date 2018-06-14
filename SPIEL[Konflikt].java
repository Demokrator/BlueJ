import ea.*;
import java.util.*;

//Test
/**
 * Beschreiben Sie hier die Klasse SPIEL.
 * 
 * @author Informatik-Kurs
 * @version 0.1
 */

public class SPIEL
extends Game
implements FallReagierbar, KollisionsReagierbar, Ticker
{
    private HINDERNISSE[] hindernisse;
    private PROJEKTILE[] projektile;
    private GEGNER[] gegner;
    private BODEN[] boden;

    private SPIELER spieler;
    private BARRIERE barriere;
    private SHOP shop;
    private HEATBAR heatbar; //zum schießen

    private int bodenZahl = 1; //1 da von anfang an einer da ist
    private int gegnerZahl = 0;
    private int hindernisseZahl =1; //1 da von anfang an eins da ist
    private int projektilZahl = 0;
    private Random zufall;

    private int Fensterbreite, Fensterhoehe;

    private Knoten k1,k2;

    public SPIEL()
    {
        //werte x, y, name, vollbild, exitOnEsc, fensterX, FensterY;
        super(1280, 720, "sidecroller beta", false, true, 0, 0);
        Fensterbreite = (int)fensterGroesse().breite; //nutzen um relativ zum fenster zu berechnen
        Fensterhoehe = (int)fensterGroesse().hoehe; //somit kann man Fenstergröße schnell ändern

        zufall = new Random();

        //Tastatur
        this.tastenReagierbarAnmelden(this);

        //Ticker
        //Alle 20 Millisekunden ein Tick
        this.tickerAnmelden(this, 20); 

        k1 = new Knoten();//Spieler
        k2 = new Knoten();//rendern

        //felder erstellen
        boden = new BODEN[100];
        hindernisse = new HINDERNISSE[100];
        projektile = new PROJEKTILE[100];
        gegner = new GEGNER[100];

        //start objekte erzeugen
        boden[0] = new BODEN(Fensterbreite, Fensterhoehe/2 + 50, 50, 10);
        boden[0].getRechteck().passivMachen();
        
        hindernisse[0] = new HINDERNISSE(Fensterbreite, 120,20,20);
        
        heatbar = new HEATBAR(Fensterbreite - 150, 50, 0, 20); //heatbar ohne hitze
        
 
        //spieler erzeugen
        spieler = new SPIELER(Fensterbreite - 100,0,50,20);
        spieler.spieler.aktivMachen();
        spieler.spieler.fallReagierbarAnmelden(this, Fensterhoehe);
        
        wurzel.add(boden[0].getRechteck(), spieler.spieler);

    }
    
    public void tick() 
    {
        //alles bewegen
        bodenBewegen(); 
        hindernisseBewegen();
        projektileBewegen();
        
        //neues adden falls nötig
        addNewBoden(100, 150);
        addNewHindernisse(500,100);
        
        //unnötiges löschen
        deleteOffScreenBoden();
        deleteOffScreenHindernisse();
        deleteOffScreenProjektile();
    }

    public void bodenBewegen()
    {
        for(int i = 0; i < bodenZahl; i++)
        {
            boden[i].bewegen(-4, 0);
        }
    }

    public void hindernisseBewegen()
    {
        for(int i = 0; i < hindernisseZahl; i++)
        {
            hindernisse[i].bewegen(-4, 0);
        }
    }

    public void deleteOffScreenBoden()
    {
        if(boden[0].getX() < -400)
        {
            wurzel.entfernen(boden[0].getRechteck());          
         
            //nachrücken
            for(int i = 0; i < bodenZahl; i++)
            {
                boden[i] = boden [i + 1];
            }  
            
            boden[bodenZahl] = null;
            bodenZahl--;
        }

    }

    public void deleteOffScreenHindernisse()
    {
        if(hindernisse[0].getX() < -400)
        {
            wurzel.entfernen(hindernisse[0].getRechteck());  
            
            //nachrücken
            for(int i = 0; i < hindernisseZahl; i++)
            {
                hindernisse[i] = hindernisse[i + 1];
            }

            hindernisse[hindernisseZahl] = null;
            hindernisseZahl--;
        }
    }
    
    public void deleteOffScreenProjektile()
    {
        if(projektilZahl != 0)
        {
            if(projektile[0].getX() > Fensterbreite + 200)
            {
                wurzel.entfernen(projektile[0].getRechteck());  
            
                //nachrücken
                for(int i = 0; i < hindernisseZahl; i++)
                {
                    projektile[i] = projektile[i + 1];
                }

                projektile[projektilZahl] = null;
                projektilZahl--;
            }
        }
    }

    public void addNewBoden(int distanceToLast, int diff)
    {                     
        //distanceToLast = x-abstand zu letzter platte
        //diff = y-abstand zu letzter platte

        if((boden[bodenZahl-1].getX() + boden[bodenZahl - 1].getBreite()) < Fensterbreite)
        {
            boden[bodenZahl] = new BODEN(Fensterbreite + distanceToLast, createNoise(diff, (int)boden[bodenZahl-1].getY(), Fensterhoehe/2), 50, 10);
            wurzel.add(boden[bodenZahl].getRechteck());
            boden[bodenZahl].getRechteck().passivMachen();
            bodenZahl++;               
        }
    }

    public void addNewHindernisse(int distanceToLast, int diff)
    {
        //distanceToLast = x-abstand zu letzter platte
        //diff = y-abstand zu letzter platte
        
        if((hindernisse[hindernisseZahl-1].getX() + hindernisse[hindernisseZahl - 1].getBreite()) < Fensterbreite)
        {
            hindernisse[hindernisseZahl] = new HINDERNISSE(Fensterbreite + distanceToLast, createNoise(diff, (int)hindernisse[hindernisseZahl-1].getY(), Fensterhoehe/2), 20, 20);
            wurzel.add(hindernisse[hindernisseZahl].getRechteck());
            hindernisse[hindernisseZahl].getRechteck().passivMachen();
            hindernisseZahl++;               
        }
    }

    public int createNoise(int diff, int lastY, int line)
    {
        int highestY = lastY - diff; //obere Grenze

        int aboveLast = (zufall.nextInt(diff) + highestY); //bereich über letztem wert
        int belowLast = (zufall.nextInt(diff) + lastY);    //bereich unter letztem wert

        if(lastY < line) //verhindern dass linie bild verlässt --> bewegt sich immer um line
        {
            return belowLast;
        }
        else
        {
            return aboveLast;
        }
    }
    
    public void shoot()
    {
        int startx = (int)spieler.getX() + (int)spieler.getBreite();
        int starty = (int)spieler.getY() + (int)spieler.getHoehe() / 2;
        
        projektile[projektilZahl] = new PROJEKTILE(startx, starty, 10, 4, 9);
        wurzel.add(projektile[projektilZahl].getRechteck());
        projektilZahl++;
    }
    
    public void projektileBewegen()
    {
        if(projektilZahl != 0)
        {
            for(int i = 0; i < projektilZahl; i++)
            {
                projektile[i].bewegen();
            }
        }
    }

    public void kollision(int code)
    {
        
    }
    
    public void spielReset()
    {
        //boden aus wurzel
        for(int i = 0; i < bodenZahl; i++)
        {
            wurzel.entfernen(boden[i].getRechteck());
        }
        //hindernisse aus wurzel
        for(int i = 0; i < hindernisseZahl; i++)
        {
            wurzel.entfernen(hindernisse[i].getRechteck());
        }
        //projektile aus wurzel
        for(int i = 0; i < projektilZahl; i++)
        {
            wurzel.entfernen(projektile[i].getRechteck());
        }
        //gegner aus wurzel
        for(int i = 0; i < gegnerZahl; i++)
        {
            wurzel.entfernen(gegner[i].getRechteck());
        }
        
        //felder leeren
        boden = new BODEN[100];
        hindernisse = new HINDERNISSE[100];
        projektile = new PROJEKTILE[100];
        gegner = new GEGNER[100];
        
        //zahlen reset
        bodenZahl = 1;
        gegnerZahl = 0;
        hindernisseZahl =1;
        projektilZahl = 0;
        
        //neu erzeugen
        boden[0] = new BODEN(Fensterbreite, Fensterhoehe/2 + 50, 50, 10);
        boden[0].getRechteck().passivMachen();
        
        hindernisse[0] = new HINDERNISSE(Fensterbreite, 120,20,20);
        hindernisse[0].getRechteck().passivMachen();
        
        wurzel.add(boden[0].getRechteck(), hindernisse[0].getRechteck());
    }

    public void fallReagieren()
    {
        manager.anhalten(this);        
        spielReset();
        warten(1000);
        spieler.spieler.positionSetzen(Fensterbreite - 20, Fensterhoehe/2);
        manager.starten(this, 20);
    }

    public void tickerIntervallSetzen(int ms) 
    {
        this.tickerAbmelden(this);
        this.tickerAnmelden(this, ms);
    }

    public void tickerStoppen() 
    {
        this.tickerAbmelden(this);
    }

    public void tickerNeuStarten(int ms) 
    {
        this.tickerAbmelden(this);
        this.tickerAnmelden(this, ms);
    }
    
        public void tasteReagieren(int code)
    {
        switch(code)
        {
            case 0: //links "a"
            spieler.bewegen(-25, 0);
            break;

            case 3: //rechts "d"
            spieler.bewegen(25, 0);
            break;

            case 22: //oben "w"

            break;

            case 18: //unten "s"

            break;

            case 30: //springen "leer"
            spieler.sprung(10);
            break;

            case 29: //pfeiltaste links
            cam.verschieben(-20, 0);
            break;

            case 27: //pfeiltaste rechts
            cam.verschieben(20, 0);
            break;

            case 26: //pfeiltaste oben
            cam.verschieben(0, -20);
            break;

            case 28: //pfeiltaste unten
            cam.verschieben(0, 20);
            break;

            case 25: //taste "z"

            break;

            case 17: //taste "r"
                shoot();
            break;
        }
    } 
}
