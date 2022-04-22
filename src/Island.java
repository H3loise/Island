import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

interface Observer {

    public void update();

}

abstract class Observable {

    private ArrayList<Observer> observers;
    public Observable() {
    this.observers = new ArrayList<Observer>();
    }
    public void addObserver(Observer o) {
    observers.add(o);
    }

    public void notifyObservers() {
    for(Observer o : observers) {
        o.update();
    }
    }
}

public class Island {

    public static void main(String[] args) {

    EventQueue.invokeLater(() -> {

                Modele modele = new Modele();
                Vue vue = new Vue(modele);
        });
    }
}




class Modele extends Observable{
public static final int tailleGrille=6;

    private Case[][] cases;
    ArrayList<Case> innondables;
    Player p1;
    Player p2;
    Player p3;
    Player p4;
    Player playerRound;

    public Modele(){
        cases=new Case[tailleGrille][tailleGrille];
        innondables=new ArrayList<>();
        String nomp1=new String("Player1");
        String nom2= new String("Player2");
        p1=new Player(0,0,nomp1,this);
        p2=new Player(5,5,nom2,this);
        p3=new Player(5,0,"Player 3",this);
        p4=new Player(0,5,"Player 4",this);
        playerRound=p1;
        for(int i=0;i<tailleGrille;i++){
            for(int j=0;j<tailleGrille;j++) {
                Case c = new Case(this, i, j);
                cases[i][j] = c;
                innondables.add(c);
            }
        }
    }

    public void innonde(){
        Random random=new Random();
        int nb;
        for(int i=0;i<3;i++){
            nb=random.nextInt(this.innondables.size());
            innondables.get(nb).prendLeau();
            if(innondables.get(nb).getEtat()==2){
                innondables.remove(nb);
            }
        }
        notifyObservers();

    }

    public void changePlayerRound(){
        if(playerRound.equals(p1)){
            this.playerRound=p2;
        }else {
            if (playerRound.equals(p2)) {
                this.playerRound = p3;
            }else{
                if(playerRound.equals(p3)){
                    this.playerRound=p4;
                }else{
                    this.playerRound=p1;
                }
            }
        }
        System.out.println(this.playerRound.nom);
        notifyObservers();
    }


    public void moveRight(){
        if(this.playerRound.x<tailleGrille-1 && this.getCase(this.playerRound.x+1,this.playerRound.y).getEtat()!=2){
            playerRound.x+=1;
        }else {
            playerRound.nbLeft++;
        }notifyObservers();
    }

    public void moveLeft(){
        if(this.playerRound.x>0){
            playerRound.x-=1;
        }notifyObservers();
    }

    public void moveUp(){
        if(this.playerRound.y>0){
            playerRound.y-=1;
        }
        notifyObservers();
    }

    public void moveDown(){
        if(this.playerRound.y<tailleGrille-1){
            playerRound.y+=1;
        }
        notifyObservers();
    }

    public Case getCase (int x, int y){
        return this.cases[x][y];
    }
            
}

class Case{
    private Modele modele;
    private int etat;
    private final int x,y;
    
    public Case(Modele modele, int x, int y){
        this.modele=modele;
        this.etat=0;
        this.x=x;
        this.y=y;
    }

    public int getEtat(){
        return this.etat;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }
    
    public void prendLeau(){
        switch (this.etat){
            case 0:
                this.etat=1;
                break;
            case 1:
                this.etat=2;
                break;
        }
    }

    public void seche(){
        this.etat=0;
    }
    
    public boolean estInnondable(){
        return(this.etat==0 || this.etat==1);
    }

    public boolean canBeDried(){
        return this.etat==0 || this.etat==1;
    }

    
}

class Vue{
    private JFrame frame;
    private VueCarte carte;
    VueCommandes commandes;
    //VueInfo info;
    
    public Vue(Modele modele){
        frame=new JFrame();
        frame.setTitle("Forbidden Island");
        frame.setLayout(new FlowLayout());
        carte=new VueCarte(modele);
        frame.add(carte);
        commandes=new VueCommandes(modele);
        frame.add(commandes);
        //info=new VueInfo(modele);
        //frame.add(info);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

class VueCarte extends JPanel implements Observer{
    private Modele modele;
    private final static int Taille = 40;
    
    public VueCarte(Modele modele){
        this.modele=modele;
        modele.addObserver(this);
        //this.setPreferredSize(Taille*Modele.tailleGrille,Taille*Modele.tailleGrille);
        Dimension d = new Dimension(350,350);
        this.setPreferredSize(d);
    }
    
    public void update() {
        repaint();
    }
    
    public void paintComponent(Graphics g){
        super.repaint();
        for(int i=0;i<Modele.tailleGrille;i++){
            for(int j=0;j<Modele.tailleGrille;j++){
                paint(g,modele.getCase(i,j),i*Taille+i*2,j*Taille+j*2);
            }
        }
    }
    
    private void paint(Graphics g, Case c, int x, int y){
        switch(c.getEtat()){
                case 0:
                g.setColor(Color.WHITE);
                break;
                case 1:
                g.setColor(Color.CYAN);
                break;
                case 2:
                g.setColor(Color.BLUE);
                break;
        }
        g.fillRect(x,y,Taille,Taille);
        if(c.getX()==this.modele.p1.x && c.getY()==this.modele.p1.y){
            g.setColor(Color.YELLOW);
            g.fillOval(x,y,Taille,Taille);
        }if(c.getX()==this.modele.p2.x && c.getY()==this.modele.p2.y){
            g.setColor(Color.RED);
            g.fillOval(x,y,Taille,Taille);
        }if(c.getX()==this.modele.p3.x && c.getY()==this.modele.p3.y){
            g.setColor(Color.GREEN);
            g.fillOval(x,y,Taille,Taille);
        }if(c.getX()==this.modele.p4.x && c.getY()==this.modele.p4.y){
            g.setColor(Color.MAGENTA);
            g.fillOval(x,y,Taille,Taille);
        }
        g.setColor(Color.BLACK);
        g.drawString(this.modele.playerRound.nom,260,20);
        g.drawString("move left :"+this.modele.playerRound.nbLeft,260,40);
    }
}

class VueCommandes extends JPanel{
    private Modele modele;
    
    public VueCommandes(Modele modele){
        this.modele=modele;
        JButton buttonAvance = new JButton("fin de tour");
        this.add(buttonAvance);
        Controleur ctrl = new Controleur(modele);
        buttonAvance.addActionListener(ctrl);
        JButton up=new JButton("^");
        this.add(up);
        up.addActionListener(ctrl);
        JButton right=new JButton(">");
        this.add(right);
        right.addActionListener(ctrl);
        JButton left = new JButton("<");
        this.add(left);
        left.addActionListener(ctrl);
        JButton down = new JButton("u");
        this.add(down);
        down.addActionListener(ctrl);
        JButton here = new JButton("here");
        this.add(here);
        here.addActionListener(ctrl);
        JButton assecher= new JButton("Assecher");
        this.add(assecher);
        assecher.addActionListener(ctrl);

    }
}
/**
class VueInfo extends JPanel implements Observer{
    private Modele modele;

    public VueInfo(Modele modele){
        this.modele=modele;
        JLabel player=new JLabel(modele.playerRound.nom);
        JLabel moveLeft=new JLabel("move left :"+modele.playerRound.nbLeft);
        this.add(player);
        this.add(moveLeft);
    }

    public void update(){

    }

}
 **/

class Controleur implements ActionListener {
    Modele modele;
    boolean assecher;
    
    public Controleur(Modele modele){
        this.modele=modele;
        this.assecher=false;
    }

    public void actionPerformed(ActionEvent e){
        System.out.println("nombre de coups restant : "+this.modele.playerRound.nbLeft);
        if(e.getActionCommand().equals("Assecher")){
            this.assecher=true;
            this.modele.playerRound.nbLeft++;
        }
        if(this.modele.playerRound.nbLeft>0 && !e.getActionCommand().equals("fin de tour")) {
            if (e.getActionCommand().equals(">")) {
                if(assecher && this.modele.playerRound.Dryable(this.modele.playerRound.x+1,this.modele.playerRound.y)){
                    this.modele.getCase(this.modele.playerRound.x+1,this.modele.playerRound.y).seche();
                    this.assecher=false;
                }else {
                    modele.moveRight();
                }
            }

            if(e.getActionCommand().equals("here")){
                if(assecher && this.modele.playerRound.Dryable(this.modele.playerRound.x,this.modele.playerRound.y)){
                    this.modele.getCase(this.modele.playerRound.x,this.modele.playerRound.y).seche();
                    this.assecher=false;
                }else{
                    this.modele.playerRound.nbLeft++;
                }
            }

            if (e.getActionCommand().equals("<")) {
                if(assecher && this.modele.playerRound.Dryable(this.modele.playerRound.x-1,this.modele.playerRound.y)){
                    this.modele.getCase(this.modele.playerRound.x-1,this.modele.playerRound.y).seche();
                    this.assecher=false;
                }else {
                    modele.moveLeft();
                }
            }
            if (e.getActionCommand().equals("^")) {
                if(assecher && this.modele.playerRound.Dryable(this.modele.playerRound.x,this.modele.playerRound.y-1)){
                    this.modele.getCase(this.modele.playerRound.x,this.modele.playerRound.y-1).seche();
                    this.assecher=false;
                }else {
                    modele.moveUp();
                }
            }
            if (e.getActionCommand().equals("u")) {
                if(assecher && this.modele.playerRound.Dryable(this.modele.playerRound.x,this.modele.playerRound.y+1)){
                    this.modele.getCase(this.modele.playerRound.x,this.modele.playerRound.y+1).seche();
                    this.assecher=false;
                }else {
                    modele.moveDown();
                }
            }
            modele.playerRound.removeLeft();
        }else{
            if (e.getActionCommand().equals("fin de tour")) {
                this.modele.innonde();
                this.modele.playerRound.nbLeft=3;
                this.modele.changePlayerRound();
            }
        }
    }
}

class Player{
    Modele modele;
    int x;
    int y;
    String nom;
    int nbLeft;
    public Player(int x, int y, String nom, Modele modele){
        this.modele=modele;
        this.x=x;
        this.y=y;
        this.nbLeft=3;
        this.nom=nom;
    }

    public void removeLeft(){
        this.nbLeft--;
    }

    /**public ArrayList<Case> Dryable(){
        ArrayList<Case> t = new ArrayList<>();
        if(this.x>0 && this.modele.getCase(this.x-1,y).canBeDried()){
            t.add(this.modele.getCase(this.x-1,this.y));
        }
        if(this.x<modele.tailleGrille && this.modele.getCase(this.x+1,y).canBeDried()){
            t.add(this.modele.getCase(this.x+1,y));
        }
        if(this.y>0 && this.modele.getCase(this.x,y-1).canBeDried()){
            t.add(this.modele.getCase(this.x,this.y-1));
        }
        if(this.y<this.modele.tailleGrille && this.modele.getCase(this.x,y+1).canBeDried()){
            t.add(this.modele.getCase(this.x,this.y+1));
        }
        return t;
    }**/

    public boolean Dryable(int x, int y){
        return (x>=0 && y>=0 && x<this.modele.tailleGrille && y<this.modele.tailleGrille && this.modele.getCase(x,y).canBeDried());
    }

}