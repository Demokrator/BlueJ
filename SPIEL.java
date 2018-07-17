import ea.*;
import java.util.*;

/**
 * Beschreiben Sie hier die Klasse SPIEL.
 * 
 * @author Informatik-Kurs
 * @version 0.1
 */

public class SPIEL
extends Game
implements FallReagierbar, KollisionsReagierbar, Ticker, KlickReagierbar
{
    private HINDERNISSE[] hindernisse;
    private BODEN[] boden;
    private BODEN startBoden;
    private ArrayList <PROJEKTILE> projektile = new ArrayList<PROJEKTILE>();
    protected ArrayList <GEGNER> gegner = new ArrayList<GEGNER>();
    private Bild wallpaper;
    private Bild wallpaper2;
    private Text scoreAnzeige;
    private Maus maus;

    private SPIELER spieler;
    private SHOP shop;
    private DATENBANK datenbank;
    private BARRIERE barriere;
    //private HEATBAR heatbar; //zum schießen
    private HEATBARnew heatbarnew;
    private SOUND sound = new SOUND(); //sound sammlung laden
    private enum zustand{pause, spiel, shop}; //Erstellen des Datentyps "zustand"
    private zustand z;

    private int bodenZahl = 0;
    protected int gegnerZahl = 0;
    private int hindernisseZahl = 0;
    protected int projektilZahl = 0;
    protected int score = 100000;

    protected int shotHeat = 0; //kaufbar
    private int cooldown = 1;
    protected  int projektilVel = 0; //kaufbar
    protected  int projektilDamage = 0; //kaufbar

    private int bodenYdiff = 150;
    private int bodenXdiff = 200;
    private int bodenDeleteOffscreen = -100; //wenn x-wert überschritten --> löschen

    private int hindernissYdiff = 100;
    private int hindernissXdiff = 500;
    private int hindernissDeleteOffscreen = -400;  //wenn x-wert überschritten --> löschen

    private int gegnerYdiff = 150;
    private int gegnerXdiff = 250;
    private int gegnerDeleteOffscreen = -100;  //wenn x-wert überschritten --> löschen
    protected  int gegnerVel = 0; //kaufbar
    protected  int gegnerHp = 0; //kaufbar
    private int gegnerScore = 10;

    private int tickrate = 20; //ticker speed
    private int clock = 0;

    private Random zufall;
    private int Fensterbreite, Fensterhoehe;
    protected Knoten pro,geg,spl;

    private String achtBit, normal; //hintergrund 

    public SPIEL()
    {           
        //werte x, y, name, vollbild, exitOnEsc, fensterX, FensterY;
        super(1280, 720, "sidecroller beta", false, true, 0, 0);
        Fensterbreite = (int)fensterGroesse().breite; //nutzen um relativ zum fenster zu berechnen
        Fensterhoehe = (int)fensterGroesse().hoehe; //somit kann man Fenstergröße schnell ändern

        this.rechenintensiveArbeitSetzen(false);

        datenbank = new DATENBANK(this);
        datenbank.datenbankInitialisieren();
        datenbankAuslesen();

        zufall = new Random();
        z = zustand.spiel;

        achtBit = ("files" + pfadtrenner + "visual" + pfadtrenner + "wallpaper" + pfadtrenner + "8bit_dawn.jpg"); //pfad für jeweiliges bild
        normal = ("files" + pfadtrenner + "visual" + pfadtrenner + "wallpaper" + pfadtrenner + "hintergrund.jpg"); //als string speichern        
        wallpaper = new Bild(0 ,-250, achtBit);
        wallpaper2 = new Bild(wallpaper.normaleBreite(), -250, achtBit);

        //Tastatur
        this.tastenReagierbarAnmelden(this);

        pro = new Knoten();//Projektile
        geg = new Knoten();//Gegner
        spl = new Knoten();//Spieler + Boden

        //felder erstellen
        boden = new BODEN[20];
        hindernisse = new HINDERNISSE[10];

        //start objekte erzeugen
        heatbarnew = new HEATBARnew(Fensterbreite - 215, 20, this); //heatbar ohne hitze
        heatbarnew.wurzelEntfernen();
        

        startBoden = new BODEN(100,100,50,10);
        startBoden.getRechteck().passivMachen();

        scoreAnzeige = new Text(Fensterbreite - 200, 40, 30, "Score: " + score);

        maus = new Maus(3);
        mausAnmelden(maus);
        maus.klickReagierbarAnmelden(this);

        //spieler erzeugen
        spieler = new SPIELER(100,20,50,20);

        wurzel.add(wallpaper,wallpaper2, spl, scoreAnzeige, pro, geg);
        spl.add(spieler.spieler,startBoden.getRechteck());

        //ticker ist in spiel starten
        heatbarnew = new HEATBARnew(Fensterbreite - 215, 20, this);
        spielStarten();
    }

    public void tick() 
    {

        //neues adden falls nötig
        addNewBoden(bodenXdiff, bodenYdiff);
        addNewHindernisse(hindernissXdiff, hindernissYdiff);
        addNewGegner(gegnerXdiff, gegnerYdiff);

        //alles bewegen
        bodenBewegen(); 
        hindernisseBewegen();
        projektileBewegen();
        gegnerBewegen();
        hintergrund();

        //unnötiges löschen
        deleteOffScreenBoden();
        deleteOffScreenHindernisse();
        deleteOffScreenProjektile();
        deleteOffScreenGegner();

        //clock
        clockUpdate();
        startBodenLoeschen();

        //schuss kühlen
        cooldownShot(cooldown);

        spielerGegnerHit(); //Fehler
        projektilGegnerHit();
        gegnerAnimationStoppen(); //Fehler

        if(tasteGedrueckt(0)==true && z == zustand.spiel)
        {
            spieler.bewegen(-5, 0);
        }

        if(tasteGedrueckt(3)==true && z == zustand.spiel)
        {
            spieler.bewegen(5, 0);
        }

    }

    public void updateScore(int points)
    {
        score = score + points;
        scoreAnzeige.inhaltSetzen("Score: " + score);
    }

    public void datenbankAuslesen()
    {
        int[] daten = datenbank.DatenbankLesen();

        shotHeat = daten[0];
        projektilVel = daten[1];
        projektilDamage = daten[2];
        gegnerVel = daten[3];
        gegnerHp = daten[4];
    }

    public void datenbankSpeichern()
    {
        datenbank.DatenbankSchreiben();
    }

    public void spielStarten()
    {
        //Ticker
        //Alle 20 Millisekunden ein Tick

        z = zustand.pause;
        //warten(2000);
        spieler.spieler.aktivMachen();
        spieler.spieler.fallReagierbarAnmelden(this, Fensterhoehe);
        //spieler.spieler.heavyComputingSetzen(true); 
        z = zustand.spiel;
        //warten(5000);
        //startBoden.getRechteck().loeschen();
        //spl.entfernen(startBoden.getRechteck());

        this.tickerAnmelden(this, tickrate);
    }

    public void spielBeginnNeuStarten() //Veraendert 
    {

        z = zustand.pause;
        //spieler.spieler.passivMachen(); //auskommentiert
        //warten(30);
        if(startBoden == null)
        {
            startBoden = new BODEN(100,100,0,0);
            startBoden.getRechteck().passivMachen();
            wurzel.add(startBoden.getRechteck());
        }

        //warten(500);

        z = zustand.spiel;
        spieler.spieler.aktivMachen();
        spieler.spieler.fallReagierbarAnmelden(this, Fensterhoehe);

        clock = 0;

        //warten(5000);
        //startBoden.getRechteck().loeschen(); //geadded
        //wurzel.entfernen(startBoden.getRechteck());
    }

    public void clockUpdate()
    {
        if(clock < 10000)
        {   
            clock = clock + tickrate;
        }
    }

    public void startBodenLoeschen()
    {
        if(clock >= 5500 && startBoden != null)
        {
            startBoden.getRechteck().loeschen(); 
            startBoden.getRechteck().sichtbarSetzen(false);
            startBoden.getRechteck().neutralMachen();
            wurzel.entfernen(startBoden.getRechteck());
            startBoden = null;
        }
    }

    public void shopAufrufen()
    {
        //z = zustand.shop;
        tickerStoppen();
        shop = new SHOP(this);
    }

    public void shopAbmelden()
    {
        //z = zustand.spiel;
        shop.beenden();
        this.tickerAnmelden(this, tickrate);
        spielBeginnNeuStarten();
    }

    public void cooldownShot(int cool)
    {
        heatbarnew.cooldown(cool);
    }

    public void hintergrund() 
    {
        if(wallpaper.getX() <= -(wallpaper.normaleBreite()))
        {
            wallpaper.positionSetzen(wallpaper.normaleBreite(),-250);
        }

        else if(wallpaper2.getX() <= -(wallpaper2.normaleBreite()))
        {
            wallpaper2.positionSetzen(wallpaper2.normaleBreite(),-250);
        }
        else
        {            
            wallpaper.bewegen(-1f,0);
            wallpaper2.bewegen(-1f,0);
        }
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
            for(int i = 0; i < gegnerZahl; i++)
            {
                gegner.get(i).bewegen();
            }
        }
    }

    public void deleteOffScreenBoden()
    {
        if(bodenZahl >0 && boden[0].getX() < bodenDeleteOffscreen)
        {
            boden[0].getRechteck().loeschen();
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
            hindernisse[0].getRechteck().loeschen();
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
                projektile.get(0).getRechteck().loeschen();
                pro.entfernen(projektile.get(0).getRechteck()); 
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
                gegner.get(0).getRechteck().loeschen();
                geg.entfernen(gegner.get(0).getRechteck()); 
                gegner.remove(0);

                gegnerZahl--;
            }
        }
    }

    public void addNewBoden(int distanceToLast, int diff)
    {                     
        //distanceToLast = x-abstand zu letzter platte
        //diff = y-abstand zu letzter platte
        try
        {
            if(bodenZahl != 0)
            {
                if((boden[bodenZahl-1].getX() + boden[bodenZahl - 1].getBreite()) < Fensterbreite)
                {
                    boden[bodenZahl] = new BODEN(Fensterbreite + distanceToLast, createNoise(diff, (int)boden[bodenZahl-1].getY(), Fensterhoehe/2), 50, 10);
                    spl.add(boden[bodenZahl].getRechteck());
                    boden[bodenZahl].getRechteck().passivMachen();
                    bodenZahl++;               
                }
            }
            else //falls noch keine da 
            {
                boden[0] = new BODEN(Fensterbreite, Fensterhoehe/2 + 50, 50, 10);
                boden[0].getRechteck().passivMachen();
                spl.add(boden[0].getRechteck());
                bodenZahl++;
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
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
                hindernisse[hindernisseZahl] = new HINDERNISSE(Fensterbreite + distanceToLast, createNoise(diff, (int)hindernisse[hindernisseZahl-1].getY(), Fensterhoehe/3), 20, 20);
                spl.add(hindernisse[hindernisseZahl].getRechteck());
                hindernisse[hindernisseZahl].getRechteck().passivMachen();
                hindernisseZahl++;               
            }
        }
        else //falls noch keine da 
        {
            hindernisse[0] = new HINDERNISSE(Fensterbreite, 120,20,20);
            hindernisse[0].getRechteck().passivMachen();
            spl.add(hindernisse[0].getRechteck());
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
                gegner.add(gegnerZahl, new GEGNER(Fensterbreite + distanceToLast, createNoise(diff, (int)gegner.get(gegnerZahl-1).getY(), Fensterhoehe/2-50), 30, 10, gegnerVel, gegnerHp, gegnerScore));
                //kollisionsReagierbarAnmelden(this, spieler.spieler, gegner.get(gegnerZahl).getRechteck(), 1); //berührung mit spieler ID 1
                gegner.get(gegnerZahl).getRechteck().heavyComputingSetzen(true);
                geg.add(gegner.get(gegnerZahl).getRechteck());
                gegner.get(gegnerZahl).getRechteck().passivMachen();
                gegnerZahl++;               
            }
        }
        else    //falls keine gegner da neuen machen
        {
            gegner.add(0, new GEGNER(Fensterbreite + distanceToLast, Fensterhoehe/2, 30, 10, gegnerVel, gegnerHp, gegnerScore));
            //kollisionsReagierbarAnmelden(this, spieler.spieler, gegner.get(0).getRechteck(), 1); //berührung mit spieler ID 1
            gegner.get(0).getRechteck().heavyComputingSetzen(true);
            geg.add(gegner.get(0).getRechteck());
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
        try
        {
            if(heatbarnew.heat(heat) == true) //lässt nur schießen wenn nach schuss hitze unter 100 wäre
            {
                //wurzel.entfernen(heatbar.getRechteck());
                //wurzel.add(heatbar.heat(heat));

                int startx = (int)spieler.getX() + (int)spieler.getBreite();
                int starty = (int)spieler.getY() + (int)spieler.getHoehe() / 2;

                projektile.add(projektilZahl, new PROJEKTILE(startx, starty, projektilVel, projektilDamage));
                pro.add(projektile.get(projektilZahl).getRechteck());
                projektilZahl++;
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        } 
    }

    public void projektileBewegen()
    {
        if(projektile.size() != 0)
        {
            for(int i = 0; i < projektilZahl; i++)
            {
                projektile.get(i).bewegen();
            }
        }
    }

    public void spielerGegnerHit()
    {
        if(gegnerZahl != 0)
        {
            for(int i = 0; i < gegnerZahl; i++)
            {
                if(gegner.get(i).spielerSchneiden(spieler) == true)
                {
                    fallReagieren();
                    break;
                }
            }
        }
    }

    public void projektilGegnerHit()
    {   
        if(gegnerZahl != 0)
        {
            for(int i = 0; i < gegnerZahl; i++)
            {
                gegner.get(i).projektilSchneiden(projektile, projektilZahl, this);
            }
        }
    }

    public void gegnerAnimationStoppen()
    {
        if(gegnerZahl != 0)
        {
            for(int i = 0; i < gegnerZahl; i++)
            {
                if(gegner.get(i).animationStoppen() != null)
                {
                    wurzel.entfernen(gegner.get(i).animationStoppen().explosion);

                    gegnerZahl--;
                    gegner.remove(gegner.get(i).animationStoppen());
                }
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
        gegnerAnimationStoppen();

        //boden aus wurzel entfernen
        for(int i = 0; i < bodenZahl; i++)
        {
            boden[i].getRechteck().loeschen();
            spl.entfernen(boden[i].getRechteck());
        }
        //hindernisse aus wurzel entfernen
        for(int i = 0; i < hindernisseZahl; i++)
        {
            hindernisse[i].getRechteck().loeschen();
            spl.entfernen(hindernisse[i].getRechteck());
        }
        //projektile aus wurzel entfernen
        for(int i = 0; i < projektilZahl; i++)
        {
            projektile.get(i).getRechteck().loeschen();
            pro.entfernen(projektile.get(i).getRechteck());
        }  
        //gegner aus wurzel entfernen
        for(int i = 0; i < gegnerZahl; i++)
        {
            gegner.get(i).getRechteck().loeschen();
            geg.entfernen(gegner.get(i).getRechteck());
        }

        //felder leeren
        boden = new BODEN[20];
        hindernisse = new HINDERNISSE[10];
        projektile.clear();
        gegner.clear();

        //zahlen reset
        bodenZahl = 0;
        gegnerZahl = 0;
        hindernisseZahl = 0;
        projektilZahl = 0;

        //neu erzeugen     
        //wurzel.entfernen(heatbar.getRechteck());
        heatbarnew.wurzelEntfernen();
        heatbarnew = new HEATBARnew(Fensterbreite - 215, 20, this); //heat leiste leeren

        //wurzel.add(heatbar.getRechteck());
    }

    public void fallReagieren()
    {
        spieler.spieler.positionSetzen(100,20);
        manager.anhalten(this);                
        spielReset();
        manager.starten(this, tickrate);  
        spielBeginnNeuStarten();
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
                if(clock > 5000)
                {
                    shopAufrufen();
            }
                //startBoden.getRechteck().passivMachen();
                break;

                case 17: //taste "r"
                shoot(shotHeat);
                break;

            }
            // Tastenbelegung im shop
        }
        else 
        {

        } 
    }

    public void klickReagieren(Punkt p)
    {
        shoot(shotHeat);
    }
}
