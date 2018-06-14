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
    private BODEN[] boden;
    private BODEN startBoden;
    private ArrayList <PROJEKTILE> projektile = new ArrayList<PROJEKTILE>();
    private ArrayList<GEGNER> gegner = new ArrayList<GEGNER>();

    private SPIELER spieler;
    private BARRIERE barriere;
    private SHOP shop;
    private HEATBAR heatbar; //zum schießen
    private SOUND sound = new SOUND(); //sound sammlung laden
    private enum zustand{pause, spiel, shop}; //Erstellen des Datentyps "zustand"
    private zustand z;

    private int bodenZahl = 0;
    private int gegnerZahl = 0;
    private int hindernisseZahl =0;
    private int projektilZahl = 0;

    private int shotHeat = 20;
    private int cooldown = 1;
    private int projektilVel = 9;
    private int projektilDamage = 50;

    private int bodenYdiff = 150;
    private int bodenXdiff = 100;
    private int bodenDeleteOffscreen = -400; //wenn x-wert überschritten --> löschen

    private int hindernissYdiff = 100;
    private int hindernissXdiff = 500;
    private int hindernissDeleteOffscreen = -400;  //wenn x-wert überschritten --> löschen

    private int gegnerYdiff = 150;
    private int gegnerXdiff = 250;
    private int gegnerDeleteOffscreen = -100;  //wenn x-wert überschritten --> löschen
    private int gegnerVel = 5;
    private int gegnerHp = 100;

    private int tickrate = 20; //ticker speed

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
        z = zustand.spiel;

        //Tastatur
        this.tastenReagierbarAnmelden(this);

        //Ticker
        //Alle 20 Millisekunden ein Tick
        this.tickerAnmelden(this, tickrate); 

        k1 = new Knoten();//Spieler
        k2 = new Knoten();//rendern

        //felder erstellen
        boden = new BODEN[100];
        hindernisse = new HINDERNISSE[100];

        //start objekte erzeugen
        heatbar = new HEATBAR(Fensterbreite - 150, 20, 0, 10); //heatbar ohne hitze

        startBoden = new BODEN(100,100,50,10);
        startBoden.getRechteck().passivMachen();

        //spieler erzeugen
        spieler = new SPIELER(100,20,50,20);
        //spieler.spieler.aktivMachen();
        //spieler.spieler.fallReagierbarAnmelden(this, Fensterhoehe);
        //spieler.spieler.heavyComputingSetzen(true);

        wurzel.add(spieler.spieler, heatbar.getRechteck(), startBoden.getRechteck());

        spielStarten();

        this.rechenintensiveArbeitSetzen(true);
    }

    public void tick() 
    {
        //alles bewegen
        bodenBewegen(); 
        hindernisseBewegen();
        projektileBewegen();
        gegnerBewegen();

        //neues adden falls nötig
        addNewBoden(bodenXdiff, bodenYdiff);
        addNewHindernisse(hindernissXdiff, hindernissYdiff);
        addNewGegner(gegnerXdiff, gegnerYdiff);

        //unnötiges löschen
        deleteOffScreenBoden();
        deleteOffScreenHindernisse();
        deleteOffScreenProjektile();
        deleteOffScreenGegner();

        //schuss kühlen
        cooldownShot(cooldown);

        //spielerGegnerHit();
        projektilGegnerHit();

        if(tasteGedrueckt(0)==true && z == zustand.spiel)
        {
            spieler.bewegen(-5, 0);
        }

        if(tasteGedrueckt(3)==true && z == zustand.spiel)
        {
            spieler.bewegen(5, 0);
        }
    }

    public void spielStarten()
    {
        z = zustand.pause;
        warten(5000);
        spieler.spieler.aktivMachen();
        spieler.spieler.fallReagierbarAnmelden(this, Fensterhoehe);
        spieler.spieler.heavyComputingSetzen(true); 
        z = zustand.spiel;
        warten(5000);
        wurzel.entfernen(startBoden.getRechteck());
    }

    public void spielBeginnNeuStarten()
    {
        z= zustand.pause;
        spieler.spieler.passivMachen(); 
        startBoden = new BODEN(100,100,50,10);
        startBoden.getRechteck().passivMachen();
        warten(500);
        spieler.spieler.aktivMachen();
        spieler.spieler.fallReagierbarAnmelden(this, Fensterhoehe);
        z = zustand.spiel;
        warten(500);
        wurzel.entfernen(startBoden.getRechteck());
    }

    public void cooldownShot(int cool)
    {
        wurzel.entfernen(heatbar.getRechteck());

        wurzel.add(heatbar.cooldown(cool));
    }

    public void bodenBewegen()
    {
        if(bodenZahl != 0)
        {
            for(int i = 0; i < bodenZahl; i++)
            {
                boden[i].bewegen(-4, 0);
            }
        }
    }

    public void hindernisseBewegen()
    {
        if(hindernisseZahl != 0)
        {
            for(int i = 0; i < hindernisseZahl; i++)
            {
                hindernisse[i].bewegen(-4, 0);
            }
        }
    }

    public void gegnerBewegen()
    {
        if(gegnerZahl != 0)
        {
            for(GEGNER gegner: gegner)
            {
                gegner.bewegen();
            }
        }
    }

    public void deleteOffScreenBoden()
    {
        if(boden[0].getX() < bodenDeleteOffscreen)
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
        if(hindernisse[0].getX() < hindernissDeleteOffscreen)
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
            if(projektile.get(0).getX() > Fensterbreite + 200)
            {
                wurzel.entfernen(projektile.get(0).getRechteck()); 
                projektile.remove(0);

                projektilZahl--;
            }
        }
    }

    public void deleteOffScreenGegner()
    {
        if(gegnerZahl != 0)
        {
            if(gegner.get(0).getX() <  gegnerDeleteOffscreen)
            {
                wurzel.entfernen(gegner.get(0).getRechteck()); 
                gegner.remove(0);

                gegnerZahl--;
            }
        }
    }

    public void addNewBoden(int distanceToLast, int diff)
    {                     
        //distanceToLast = x-abstand zu letzter platte
        //diff = y-abstand zu letzter platte

        if(bodenZahl != 0)
        {
            if((boden[bodenZahl-1].getX() + boden[bodenZahl - 1].getBreite()) < Fensterbreite)
            {
                boden[bodenZahl] = new BODEN(Fensterbreite + distanceToLast, createNoise(diff, (int)boden[bodenZahl-1].getY(), Fensterhoehe/2), 50, 10);
                wurzel.add(boden[bodenZahl].getRechteck());
                boden[bodenZahl].getRechteck().passivMachen();
                bodenZahl++;               
            }
        }
        else //falls noch keine da 
        {
            boden[0] = new BODEN(Fensterbreite, Fensterhoehe/2 + 50, 50, 10);
            boden[0].getRechteck().passivMachen();
            wurzel.add(boden[0].getRechteck());
            bodenZahl++;
        }
    }

    public void addNewHindernisse(int distanceToLast, int diff)
    {
        //distanceToLast = x-abstand zu letzter platte
        //diff = y-abstand zu letzter platte

        if(hindernisseZahl != 0)
        {
            if((hindernisse[hindernisseZahl-1].getX() + hindernisse[hindernisseZahl - 1].getBreite()) < Fensterbreite)
            {
                hindernisse[hindernisseZahl] = new HINDERNISSE(Fensterbreite + distanceToLast, createNoise(diff, (int)hindernisse[hindernisseZahl-1].getY(), Fensterhoehe/2), 20, 20);
                wurzel.add(hindernisse[hindernisseZahl].getRechteck());
                hindernisse[hindernisseZahl].getRechteck().passivMachen();
                hindernisseZahl++;               
            }
        }
        else //falls noch keine da 
        {
            hindernisse[0] = new HINDERNISSE(Fensterbreite, 120,20,20);
            hindernisse[0].getRechteck().passivMachen();
            wurzel.add(hindernisse[0].getRechteck());
            hindernisseZahl++;
        }
    }

    public void addNewGegner(int distanceToLast, int diff)
    {
        //distanceToLast = x-abstand zu letzter platte
        //diff = y-abstand zu letzter platte

        if(gegnerZahl != 0)
        {
            if((gegner.get(gegnerZahl-1).getX() + gegner.get(gegnerZahl-1).getBreite()) < Fensterbreite)
            {
                gegner.add(gegnerZahl, new GEGNER(Fensterbreite + distanceToLast, createNoise(diff, (int)gegner.get(gegnerZahl-1).getY(), Fensterhoehe/2), 30, 10, gegnerVel, gegnerHp));
                //kollisionsReagierbarAnmelden(this, spieler.spieler, gegner.get(gegnerZahl).getRechteck(), 1); //berührung mit spieler ID 1
                gegner.get(gegnerZahl).getRechteck().heavyComputingSetzen(true);
                wurzel.add(gegner.get(gegnerZahl).getRechteck());
                gegner.get(gegnerZahl).getRechteck().passivMachen();
                gegnerZahl++;               
            }
        }
        else    //falls keine gegner da neuen machen
        {
            gegner.add(0, new GEGNER(Fensterbreite + distanceToLast, Fensterhoehe/2, 30, 10, gegnerVel, gegnerHp));
            //kollisionsReagierbarAnmelden(this, spieler.spieler, gegner.get(0).getRechteck(), 1); //berührung mit spieler ID 1
            gegner.get(0).getRechteck().heavyComputingSetzen(true);
            wurzel.add(gegner.get(0).getRechteck());
            gegnerZahl++;
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

    public void shoot(int heat)
    {
        if(heatbar.getHeat() < 100 - heat) //lässt nur schießen wenn nach schuss hitze unter 100 wäre
        {
            wurzel.entfernen(heatbar.getRechteck());
            wurzel.add(heatbar.heat(heat));

            int startx = (int)spieler.getX() + (int)spieler.getBreite();
            int starty = (int)spieler.getY() + (int)spieler.getHoehe() / 2;

            projektile.add(projektilZahl,new PROJEKTILE(startx, starty, 10, 4, projektilVel, projektilDamage));
            wurzel.add(projektile.get(projektilZahl).getRechteck());
            projektilZahl++;
        }
    }

    public void projektileBewegen()
    {
        if(projektile.size() != 0)
        {
            for(PROJEKTILE projektile: projektile)
            {
                projektile.bewegen();
            }
        }
    }

    public void spielerGegnerHit()
    {
        if(gegnerZahl != 0)
        {
            for(GEGNER gegner: gegner)
            {
                if(gegner.spielerSchneiden(spieler) == true)
                {
                    fallReagieren();
                }
            }
        }
    }

    public void projektilGegnerHit()
    {
        if(gegnerZahl != 0)
        {
            for(GEGNER gegner: gegner)
            {
                gegner.projektilSchneiden(projektile, projektilZahl, this);
            }
        }
    }

    public void kollision(int code)
    {
        switch(code)
        {
            case 1:
            fallReagieren();
            break;
        }
    }

    public void spielReset()
    {
        //boden aus wurzel entfernen
        for(int i = 0; i < bodenZahl; i++)
        {
            wurzel.entfernen(boden[i].getRechteck());
        }
        //hindernisse aus wurzel entfernen
        for(int i = 0; i < hindernisseZahl; i++)
        {
            wurzel.entfernen(hindernisse[i].getRechteck());
        }
        //projektile aus wurzel entfernen
        for(PROJEKTILE projektile: projektile)
        {
            wurzel.entfernen(projektile.getRechteck());
        }  
        //gegner aus wurzel entfernen
        for(GEGNER gegner: gegner)
        {
            wurzel.entfernen(gegner.getRechteck());
        }

        //felder leeren
        boden = new BODEN[100];
        hindernisse = new HINDERNISSE[100];
        projektile.clear();
        gegner.clear();

        //zahlen reset
        bodenZahl = 0;
        gegnerZahl = 0;
        hindernisseZahl = 0;
        projektilZahl = 0;

        //neu erzeugen      
        heatbar = new HEATBAR(Fensterbreite - 150, 20, 0, 10); //heat leiste leeren

        wurzel.add(heatbar.getRechteck());
    }

    public void fallReagieren()
    {
        spieler.spieler.positionSetzen(100,20);
        manager.anhalten(this);                
        spielReset();
        spielBeginnNeuStarten();
        //warten(1000);
        manager.starten(this, tickrate);    
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
        //Tastenbelegung im Spiel
        if(z == zustand.spiel)
        {
            switch(code)
            {

                case 0: //links "a"
                //spieler.bewegen(-25, 0);
                break;

                case 3: //rechts "d"
                //spieler.bewegen(25, 0);
                break;

                case 22: //oben "w"

                break;

                case 18: //unten "s"

                break;

                case 30: //springen "leer"                   
                spieler.sprung(10);
                //sound.playSound("mariojump");  //funktioniert allerdings sehr spät und SEHR LAUT
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
                shoot(shotHeat);
                break;

            }
            // Tastenbelegung im shop
        }
    } 
}
