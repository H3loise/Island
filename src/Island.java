import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
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

        Parametre p =new Parametre();


        Parametre finalP = p;
        EventQueue.invokeLater(() -> {
                Modele modele = new Modele(finalP.names);
                Vue vue = new Vue(modele);
        });
    }
}




class Modele extends Observable{
public static final int tailleGrille=6;

    private Case[][] cases;
    ArrayList<Case> innondables;
    //Player p1;
    //Player p2;
    //Player p3;
    //Player p4;
    Player playerRound;
    int xHelico;
    int yHelico;
    ArrayList<Player> players;

    public Modele(ArrayList<String> names){
        cases=new Case[tailleGrille][tailleGrille];
        innondables=new ArrayList<>();
        players = new ArrayList<>();
        for(int i=0;i<names.size();i++){
            switch (i){
                case 0:
                    players.add(new Player(0,0,names.get(i),this));
                    //players.get(0).inventaireArtefact.add(new Artefact("Feu"));
                    //players.get(0).inventaireActionSpe.add(new ActionSpe("Sable"));
                    players.get(0).inventaireActionSpe.add(new ActionSpe("Helico"));
                    break;
                case 1:
                    players.add(new Player(5,0,names.get(i),this));
                    //players.get(1).inventaireArtefact.add(new Artefact("Eau"));
                    break;
                case 2:
                    players.add(new Player(5,5,names.get(i),this));
                    //players.get(2).inventaireArtefact.add(new Artefact("Terre"));
                    break;
                case 3:
                    players.add(new Player(0,5,names.get(i),this));
                    //players.get(3).inventaireArtefact.add(new Artefact("Air"));
                    break;
            }
        }
        String nomp1=new String("Player1");
        String nom2= new String("Player2");
        //p1=new Player(0,0,nomp1,this);
        //p2=new Player(5,5,nom2,this);
        //p3=new Player(5,0,"Player 3",this);
        //p4=new Player(0,5,"Player 4",this);
        playerRound=players.get(0);
        for(int i=0;i<tailleGrille;i++){
            for(int j=0;j<tailleGrille;j++) {
                Case c = new Case(this, i, j);
                cases[i][j] = c;
                innondables.add(c);
            }
        }
        int nbFeu=2;
        int nbEau=2;
        int nbTerre=2;
        int nbAir=2;
        int helico=1;
        Random random = new Random();
        while(nbFeu!=0 || nbEau!=0 || nbTerre!=0 || nbAir!=0 || helico!=0){
            int valx=random.nextInt(tailleGrille);
            int valy=random.nextInt(tailleGrille);
            if(nbFeu>0 && this.getCase(valx,valy).special.equals("None")){
                this.getCase(valx,valy).setSpecial("Feu");
                nbFeu--;
            }else{
                if(nbEau>0 && this.getCase(valx,valy).special.equals("None")){
                    this.getCase(valx,valy).setSpecial("Eau");
                    nbEau--;
                }else{
                    if(nbTerre>0 && this.getCase(valx,valy).special.equals("None")){
                        this.getCase(valx,valy).setSpecial("Terre");
                        nbTerre--;
                    }
                    else{
                        if (nbAir>0 && this.getCase(valx,valy).special.equals("None")){
                            this.getCase(valx,valy).setSpecial("Air");
                            nbAir--;
                        }
                        else{
                            if(helico>0 && this.getCase(valx,valy).special.equals("None")){
                                this.getCase(valx,valy).setSpecial("Helico");
                                helico--;
                                this.xHelico=valx;
                                this.yHelico=valy;
                            }
                        }
                    }
                }
            }

        }
    }


    public boolean won(){
        if(players.get(0).x!=xHelico || players.get(0).y!=yHelico){
            return false;
        }

        for(int i=1;i<this.players.size();i++){
            if(players.get(i).x!=players.get(i-1).x || players.get(i).y!=players.get(i-1).y){
                return false;
            }
        }
            int feu = 0;
            int eau = 0;
            int terre = 0;
            int air = 0;
            for(int i=0;i<players.size();i++){
                for(int e=0;e<players.get(i).inventaireArtefact.size();e++){
                    if(players.get(i).inventaireArtefact.get(e).type.equals("Feu")){
                        feu++;
                    }
                    if(players.get(i).inventaireArtefact.get(e).type.equals("Eau")){
                        eau++;
                    }
                    if(players.get(i).inventaireArtefact.get(e).type.equals("Terre")){
                        terre++;
                    }
                    if(players.get(i).inventaireArtefact.get(e).type.equals("Air")){
                        air++;
                    }
                }
            }
            if(feu>0 && eau>0 && terre>0 && air>0){
                return true;
            }
        return false;
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

    public void getCle(){
        Random random=new Random();
        int nb= random.nextInt(4);
        switch(nb){
            case 1:
                this.getCase(this.playerRound.x,this.playerRound.y).prendLeau();
                break;
            case 2:
                int numbArtefact = random.nextInt(4);
                switch (numbArtefact){
                    case 0:
                        this.playerRound.addCle(new Cle("Feu"));
                        break;
                    case 1:
                        this.playerRound.addCle(new Cle("Eau"));
                        break;
                    case 2:
                        this.playerRound.addCle(new Cle("Terre"));
                        break;
                    case 3:
                        this.playerRound.addCle(new Cle("Air"));
                        break;
                }
                break;
            case 3:
                int numbAS = random.nextInt(2);
                switch (numbAS) {
                    case 0:
                        this.playerRound.addActionSpe(new ActionSpe("Helico"));
                        break;
                    case 1:
                        this.playerRound.addActionSpe(new ActionSpe("Sable"));
                        break;
                }
                break;

        }notifyObservers();
    }

    public void changePlayerRound(){
        if(playerRound.equals(this.players.get(0))){
            this.playerRound=this.players.get(1);
        }else {
            if (playerRound.equals(this.players.get(1))) {
                if(this.players.size()>2) {
                    this.playerRound = players.get(2);
                }else{
                    this.playerRound=players.get(0);
                }
            }else{
                if(playerRound.equals(players.get(2))){
                    if(players.size()>3) {
                        this.playerRound = players.get(3);
                    }else{
                        this.playerRound=players.get(0);
                    }
                }else{
                    this.playerRound=players.get(0);
                }
            }
        }
       // System.out.println(this.playerRound.nom);
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
    String special;
    String ActionSpe;

    public Case(Modele modele, int x, int y){
        this.modele=modele;
        this.etat=0;
        this.x=x;
        this.y=y;
        this.special=new String("None");
        this.ActionSpe=new String("None");
    }

    public void setSpecial(String s){
        this.special=s;
    }

    public void putSand(){
        this.ActionSpe="Sable";
    }

    public void putHelico(){
        this.ActionSpe="Helico";
    }

    public void putNone(){
        this.ActionSpe="None";
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
        Dimension d = new Dimension(450,450);
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

    private void paint(Graphics g, Case c, int x, int y) {
        if (this.modele.won()) {
            g.setColor(Color.BLACK);
            g.drawString("Vous avez gagné ! ",225,255);
        } else {
            switch (c.getEtat()) {
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
            g.fillRect(x, y, Taille, Taille);
            if (!c.special.equals("None")) {
                switch (c.special) {
                    case "Feu":
                        g.setColor(Color.RED);
                        break;
                    case "Eau":
                        g.setColor(Color.BLUE);
                        break;
                    case "Terre":
                        g.setColor(Color.GREEN);
                        break;
                    case "Air":
                        g.setColor(Color.LIGHT_GRAY);
                        break;
                    case "Helico":
                        g.setColor(Color.BLACK);
                        break;
                }
                g.fillRect(x, y, Taille / 4, Taille / 4);
            }

            if (c.getX() == this.modele.players.get(0).x && c.getY() == this.modele.players.get(0).y) {
                g.setColor(Color.YELLOW);
                g.fillOval(x, y, Taille, Taille);
            }
            if (c.getX() == this.modele.players.get(1).x && c.getY() == this.modele.players.get(1).y) {
                g.setColor(Color.RED);
                g.fillOval(x, y, Taille, Taille);
            }
            if (this.modele.players.size() > 2) {
                if (c.getX() == this.modele.players.get(2).x && c.getY() == this.modele.players.get(2).y) {
                    g.setColor(Color.GREEN);
                    g.fillOval(x, y, Taille, Taille);
                }
            }
            if (this.modele.players.size() > 3) {
                if (c.getX() == this.modele.players.get(3).x && c.getY() == this.modele.players.get(3).y) {
                    g.setColor(Color.MAGENTA);
                    g.fillOval(x, y, Taille, Taille);
                }
            }
            g.setColor(Color.BLACK);
            g.drawString(this.modele.playerRound.nom, 260, 20);
            g.drawString("move left :" + this.modele.playerRound.nbLeft, 260, 40);
            g.drawString("clés : ", 260, 60);
            for (int i = 0; i < this.modele.playerRound.inventaireCle.size(); i++) {
                switch (this.modele.playerRound.inventaireCle.get(i).type) {
                    case "Feu":
                        g.setColor(Color.RED);
                        g.fillRect(260 + 40 + i * 25, 45, 20, 20);
                        break;
                    case "Eau":
                        g.setColor(Color.BLUE);
                        g.fillRect(260 + 40 + i * 25, 45, 20, 20);
                        break;
                    case "Terre":
                        g.setColor(Color.GREEN);
                        g.fillRect(260 + 40 + i * 25, 45, 20, 20);
                        break;
                    case "Air":
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillRect(260 + 40 + i * 25, 45, 20, 20);
                        break;
                }
            }
            g.setColor(Color.BLACK);
            g.drawString("artefact :", 260, 80);
            for (int i = 0; i < this.modele.playerRound.inventaireArtefact.size(); i++) {
                switch (this.modele.playerRound.inventaireArtefact.get(i).type) {
                    case "Feu":
                        g.setColor(Color.RED);
                        g.fillRect(260 + 60 + i * 25, 70, 20, 20);
                        break;
                    case "Eau":
                        g.setColor(Color.BLUE);
                        g.fillRect(260 + 60 + i * 25, 70, 20, 20);
                        break;
                    case "Terre":
                        g.setColor(Color.GREEN);
                        g.fillRect(260 + 60 + i * 25, 70, 20, 20);
                        break;
                    case "Air":
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillRect(260 + 60 + i * 25, 70, 20, 20);
                        break;
                }
            }g.drawString("inventaire :",260,100);
            for (int i = 0; i < this.modele.playerRound.inventaireActionSpe.size(); i++) {
                switch (this.modele.playerRound.inventaireActionSpe.get(i).type){
                    case "Helico":
                        g.setColor(Color.BLACK);
                        g.fillRect(260+80+i*25,90,20,20);
                        break;
                    case "Sable":
                        g.setColor(Color.YELLOW);
                        g.fillRect(260+80+i*25,90,20,20);
                        break;
                }
            }
        }
    }
}

class VueCommandes extends JPanel{
    private Modele modele;

    public VueCommandes(Modele modele){
        this.modele=modele;
        Dimension d = new Dimension(450,450);
        this.setPreferredSize(d);
        JPanel p=new JPanel();
        p.setLayout(null);
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
        JButton artefact = new JButton("Artefact");
        this.add(artefact);
        artefact.addActionListener(ctrl);
        JButton cle = new JButton("Cle");
        this.add(cle);
        cle.addActionListener(ctrl);
        JLabel playerdon = new JLabel("nom player");
        JLabel cartedon=new JLabel("carte ");
        JTextField playerC =new JTextField(10);
        JTextField carte = new JTextField(10);
        JLabel sablex = new JLabel("x sable");
        JLabel sabley=new JLabel("y sable");
        JTextField xsable =new JTextField(8);
        JTextField ysable = new JTextField(8);
        ctrl.xSable=xsable;
        ctrl.ySable=ysable;
        ctrl.carte=carte;
        ctrl.player=playerC;
        this.add(playerdon);
        this.add(playerC);
        this.add(cartedon);
        this.add(carte);
        JButton echange = new JButton("Echange");
        echange.setBounds(10,250,10,10);
        this.add(echange);
        echange.addActionListener(ctrl);
        this.add(sablex);
        this.add(xsable);
        this.add(sabley);
        this.add(ysable);
        JButton sable = new JButton("Sable");
        this.add(sable);
        sable.addActionListener(ctrl);
        JLabel helicox=new JLabel("x helico");
        JLabel helicoy = new JLabel("y helico");
        JTextField xhelico =new JTextField(5);
        JTextField yhelico= new JTextField(5);
        ctrl.xHelico=xhelico;
        ctrl.yHelico=yhelico;
        JCheckBox people = new JCheckBox("prendre les autres");
        ctrl.box=people;
        this.add(helicox);
        this.add(xhelico);
        this.add(helicoy);
        this.add(yhelico);
        this.add(people);

        JButton helico = new JButton("Helico");
        this.add(helico);
        helico.addActionListener(ctrl);

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
    boolean sable;
    JTextField carte;
    JTextField player;
    JTextField xSable;
    JTextField ySable;
    JTextField xHelico;
    JTextField yHelico;
    JCheckBox box;

    public Controleur(Modele modele){
        this.modele=modele;
        this.assecher=false;
        this.sable=false;;
    }

    public void actionPerformed(ActionEvent e){
        //System.out.println("nombre de coups restant : "+this.modele.playerRound.nbLeft);
        if(e.getActionCommand().equals("Sable")){
            if(this.modele.getCase(Integer.valueOf(xSable.getText()),Integer.valueOf(ySable.getText())).canBeDried() && this.modele.playerRound.isInInventory("Sable")){
                System.out.println("can be dried");
                this.modele.getCase(Integer.valueOf(xSable.getText()),Integer.valueOf(ySable.getText())).seche();
                this.modele.playerRound.UseActionSpe("Sable");
            }
        }

        if(e.getActionCommand().equals("Helico")){
            if(this.modele.playerRound.isInInventory("Helico") && this.modele.getCase(Integer.valueOf(xHelico.getText()),Integer.valueOf(yHelico.getText())).canBeDried()){
                if(box.isSelected()){
                    System.out.println("select");
                    int x=modele.playerRound.x;
                    int y=modele.playerRound.y;
                    for(int i=0;i<this.modele.players.size();i++){
                        System.out.println("("+modele.players.get(i).x+modele.players.get(i).y+")  "+"("+modele.playerRound.x+modele.playerRound.y+")");
                        if(this.modele.players.get(i).x==x && this.modele.players.get(i).y==y){
                            System.out.println("dans la boucle");
                            this.modele.players.get(i).x=Integer.valueOf(xHelico.getText());
                            this.modele.players.get(i).y=Integer.valueOf(yHelico.getText());
                        }
                    }
                }else{
                    this.modele.playerRound.x=Integer.valueOf(xHelico.getText());
                    this.modele.playerRound.y=Integer.valueOf(yHelico.getText());
                }
                this.modele.playerRound.UseActionSpe("Helico");
            }else{
                this.modele.playerRound.nbLeft++;
            }
        }

        if(e.getActionCommand().equals("Echange")){
            for(int i=0;i<this.modele.players.size();i++){
                if(this.modele.players.get(i).nom.equals(player.getText()) && this.modele.playerRound.x==this.modele.players.get(i).x && this.modele.playerRound.y==this.modele.players.get(i).y){
                    System.out.println("meme case ");
                    if(this.modele.playerRound.isInKeys(carte.getText())){
                        this.modele.playerRound.giveCle(carte.getText());
                        this.modele.players.get(i).getCle(carte.getText());
                    }
                }
            }
        }

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
            if(e.getActionCommand().equals("Cle")){
                this.modele.getCle();
            }
            if(e.getActionCommand().equals("Artefact")){
                //System.out.println(this.modele.getCase(this.modele.playerRound.x,this.modele.playerRound.y).special);
                if(this.modele.playerRound.isInKeys(this.modele.getCase(this.modele.playerRound.x,this.modele.playerRound.y).special)){
                    this.modele.playerRound.getArtefact(this.modele.getCase(this.modele.playerRound.x,this.modele.playerRound.y).special);
                }else{
                    //System.out.println("artefact ");
                    this.modele.playerRound.nbLeft++;
                }
            }
            modele.playerRound.removeLeft();
        }else{
            if (e.getActionCommand().equals("fin de tour")) {
                this.modele.getCle();
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
    ArrayList<Artefact> inventaireArtefact;
    ArrayList<Cle> inventaireCle;
    ArrayList<ActionSpe> inventaireActionSpe;
    public Player(int x, int y, String nom, Modele modele){
        this.modele=modele;
        this.x=x;
        this.y=y;
        this.nbLeft=3;
        this.nom=nom;
        this.inventaireArtefact =new ArrayList<>();
        this.inventaireCle = new ArrayList<>();
        this.inventaireActionSpe = new ArrayList<>();
    }

    public void removeLeft(){
        this.nbLeft--;
    }

    public boolean isInKeys(String type){
        for(int i=0;i<this.inventaireCle.size();i++){
            if(this.inventaireCle.get(i).type.equals(type)){
                return true;
            }
        }
        return false;
    }


    public void getArtefact(String type){
        int index=-1;
        for(int i=0;i<this.inventaireCle.size();i++){
            if(this.inventaireCle.get(i).type.equals(type)){
                index=i;
            }
        }this.inventaireCle.remove(index);
        this.addArtefact(new Artefact(type));
    }

    public void giveCle(String type){
        int index=-1;
        for(int i=0;i<this.inventaireCle.size();i++){
            if(this.inventaireCle.get(i).type.equals(type)){
                index=i;
            }
        }this.inventaireCle.remove(index);
    }


    public boolean isInInventory(String type){
        for(int i=0;i<this.inventaireActionSpe.size();i++){
            if(this.inventaireActionSpe.get(i).type.equals(type)){
                return true;
            }
        }
        return false;
    }

    public void UseActionSpe(String type){
        int index=-1;
        for(int i=0;i<this.inventaireActionSpe.size();i++){
            if(this.inventaireActionSpe.get(i).type.equals(type)){
                index=i;
            }
        }this.inventaireActionSpe.remove(index);
    }

    public void getCle(String s){
        this.inventaireCle.add(new Cle(s));
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

    public void addArtefact(Artefact a){
        this.inventaireArtefact.add(a);
    }

    public void addCle(Cle c){
        this.inventaireCle.add(c);
    }

    public void addActionSpe(ActionSpe as){
        this.inventaireActionSpe.add(as);
    }

}

class Artefact{
    String type;

    public Artefact(String type){
        this.type=type;
    }
}

class Cle{
    String type;

    public Cle(String type){
        this.type=type;
    }
}

class ActionSpe{
    String type;

    public ActionSpe(String type){
        this.type=type;
    }
}


class Parametre implements ActionListener{
    int nbJoueurs;
    ArrayList<String> names;
    JTextField field1;
    JTextField field2;
    JTextField field3;
    JTextField field4;


    public Parametre(){
        int n;
        /**
         * JFrame framParam = new JFrame("Parameters");
        framParam.setSize(new Dimension(400,120));
        JLabel labelNb= new JLabel("Nombre de joueurs : ");
        framParam.add(labelNb);
        fieldNbPlayers=new JTextField(20);
        framParam.add(fieldNbPlayers);
        JButton suivantNbPlayers=new JButton("Next");
        framParam.add(suivantNbPlayers);
        framParam.setLayout(new FlowLayout());
        suivantNbPlayers.addActionListener(this);
        framParam.setVisible(true);
         **/
        names=new ArrayList<>();
        JFrame framParam = new JFrame("Parameters");
        framParam.setSize(new Dimension(400,200));
        JLabel label1= new JLabel("Nom player 1");
        JLabel labe2= new JLabel("Nom player 2");
        JLabel label3= new JLabel("Nom player 3");
        JLabel label4= new JLabel("Nom player 4");
        framParam.add(label1);
        field1=new JTextField(20);
        field2=new JTextField(20);
        field3=new JTextField(20);
        field4=new JTextField(20);
        framParam.add(field1);
        framParam.add(labe2);
        framParam.add(field2);
        framParam.add(label3);
        framParam.add(field3);
        framParam.add(label4);
        framParam.add(field4);
        JButton suivantNbPlayers=new JButton("Finish");
        framParam.add(suivantNbPlayers);
        framParam.setLayout(new FlowLayout());
        suivantNbPlayers.addActionListener(this);
        framParam.setVisible(true);
        System.out.println("ici");
        while(names.size()<2){
            System.out.println("boucle");
        }// boucle pour empecher le jeux de se lancer si nb players <2


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        names.clear();
        if(e.getActionCommand().equals("Finish")){
            if(!field1.getText().equals("")) {
                names.add(field1.getText());
            }if(!field2.getText().equals("")) {
                names.add(field2.getText());
            }
            if(!field3.getText().equals("")){
                names.add(field3.getText());
            }
            if(!field4.getText().equals("")){
                names.add(field4.getText());
            }
        }
    }
}



class Exchange implements ActionListener{
    JTextField fieldP;
    JTextField fieldC;
    String player;
    String carte;


    public Exchange(){
        JFrame framParam = new JFrame("Parameters");
        framParam.setSize(new Dimension(340,150));
        JLabel Player= new JLabel("Nom player ");
        JLabel Carte= new JLabel("Carte");
        framParam.add(Player);
        fieldC=new JTextField(20);
        fieldP=new JTextField(20);
        framParam.add(fieldP);
        framParam.add(Carte);
        framParam.add(fieldC);
        JButton finish=new JButton("Finish");
        framParam.add(finish);
        framParam.setLayout(new FlowLayout());
        finish.addActionListener(this);
        framParam.setVisible(true);
        carte="";
        //while(carte.equals("")){
            //System.out.println("lall");
        //}// boucle pour empecher le jeux de se lancer si nb players <2


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Finish")){
            if(!fieldC.getText().equals("") && !fieldP.getText().equals("")) {
                carte=fieldC.getText();
                player=fieldP.getText();
            }
        }
    }
}