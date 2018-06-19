import ea.*;
import java.util.*;

public class DATENBANK
{
    public SPIEL spiel;

    public DATENBANK(SPIEL spiel)
    {
        this.spiel = spiel;

        datenbankInitialisieren();
    }

    public void DatenbankSchreiben()
    {
        int[] tmp = {spiel.shotHeat,spiel.projektilVel, spiel.projektilDamage,spiel.gegnerVel,spiel.gegnerHp};
        DateiManager.integerArraySchreiben(tmp, "Kaufbar");
    }

    public int[] DatenbankLesen()
    {
        int[] tmp = DateiManager.integerArrayEinlesen("Kaufbar");
        return tmp;
    }

    public void datenbankInitialisieren()
    {
        int[] tmp = DateiManager.integerArrayEinlesen("Kaufbar0");
        DateiManager.integerArraySchreiben(tmp, "Kaufbar");
    }

}