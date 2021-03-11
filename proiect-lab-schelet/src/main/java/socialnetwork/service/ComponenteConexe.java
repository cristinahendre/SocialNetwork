package socialnetwork.service;
import java.io.*;
import java.util.*;

public class ComponenteConexe {

    private int noduri;
    private int muchii;
    private Map<Integer,
            HashSet<Integer>> adjMap; //lista de adiacenta
    private static Map<Integer,
            Integer> visited;


    /**
     *
     * @param noduri = numarul de noduri
     */
    ComponenteConexe(int noduri) {
        noduri = noduri;
        adjMap = new HashMap<Integer, HashSet<Integer>>();
        visited = new HashMap<Integer, Integer>(); //1 sau 0
        visited.forEach((x,y)->visited.put(x,0));
    }

    /**
    adauga o muchie noua in graf
     @param dreapta  - nodul de inceput
     @param stanga  -nodul de sfarsit
     */
    public void adaugaMuchie(int stanga, int dreapta) {
        adjMap.putIfAbsent(stanga,  new HashSet<Integer>());
        adjMap.putIfAbsent(dreapta, new HashSet<Integer>());
        adjMap.get(stanga).add(dreapta);
        adjMap.get(stanga).add(stanga);
        adjMap.get(dreapta).add(stanga);
        adjMap.get(dreapta).add(dreapta);
        visited.put(stanga, 0);
        visited.put(dreapta, 0);
    }


    /**
    face DFS - viziteaza nodurile
     @param nod  -nodul de inceput
     */
    private void findDFS(int nod) {

        visited.put(nod, 1);
        for (Integer vecin : adjMap.get(nod)) {
            if (visited.get(vecin) == 0) {
                findDFS(vecin);
            }
        }
    }

    /**
    calculeaza numarul de componente conexe
     @param edges - numarul de muchii
     @param vertices  - numar de noduri
     */
    public static int NrComponente(int edges, int vertices, ComponenteConexe ccc) {
        int cc = 0;
        for (Integer nod : visited.keySet()) {
            if (visited.get(nod) == 0) {
                ccc.findDFS(nod);
                cc++;
            }
        }
        return  cc;
    }

    public static int celMaiLungDrum(int muchii ,int noduri, ComponenteConexe ccc){
        int nrmuchii=0,aux=0;
        for (Integer nod : visited.keySet()) {
            aux++;
            if (visited.get(nod) == 0) {
                ccc.findDFS(nod);

            }
        }

        return  aux;
    }
}