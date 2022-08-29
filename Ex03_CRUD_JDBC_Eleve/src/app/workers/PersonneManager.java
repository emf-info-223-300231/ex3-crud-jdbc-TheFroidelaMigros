/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.workers;

import app.beans.Personne;
import java.util.ArrayList;


/**
 *
 * @author SchabrunR
 */
public class PersonneManager {
    
    private int index = 0;
    private ArrayList<Personne> listePersonnes;
    
    
    public Personne courantPersonne(){
        return listePersonnes.get(index);
    }
    
    public Personne debutPersonne(){
        return listePersonnes.get(0);
    }
    
    public Personne finPersonne(){
        int t = listePersonnes.size();
        return listePersonnes.get(t);
    }
    
    public Personne precedentPersonne(){
        if(index >=1){
            index --;
        }
        return listePersonnes.get(index);
    }
    
    public Personne setPersonnes (ArrayList<Personne> listePersonnes){
        return listePersonnes.get(index);
    }
    
    public Personne suivantPersonne(){
        if(index < listePersonnes.size()){
            index ++;
        }
        return listePersonnes.get(index);
    }
    
}
