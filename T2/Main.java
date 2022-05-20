//----------------------------------------------------------//
// 2º Trabalho de E.D.A. 2 (Problem: Hill, the Climber)     //
//                                                          //
// Engenharia Informática, Universidade de Évora            //
// Joana Carrasqueira (nº 48566) e João Condeço (nº 48976)  //
// Abril de 2022                                            //
//----------------------------------------------------------//

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ArrayList;
import java.util.Arrays;

class Main {
      //Calculo da distância entre dois pontos
      static double dist(int x1, int y1, int x2, int y2){
            return Math.sqrt(Math.pow(x1-x2, 2)+ Math.pow(y1-y2, 2));
      }

      public static void main(String[] args) throws NumberFormatException, IOException{

            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            String[] data = input.readLine().split(" ");                            
            int hp = Integer.parseInt(data[0]);                                           // => Number of Hold Points
            int height = Integer.parseInt(data[1]);                                       // => Altura do topo
            int nCases = Integer.parseInt(data[2]);                                       // => Número de testes
            
            Points arr[] = new Points[hp];                                                // => Array dos pontos obtidos no input

            //Recolha dos pontos
            for(int i = 0; i < hp; i++){
                  String[] coord = input.readLine().split(" ");
                  arr[i] = new Points(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]));
            }

            Arrays.sort(arr);                                                             // => Ordena os pontos por altura (y)

            int []ranges = new int[nCases];
            int max_range = Integer.MIN_VALUE;
            for(int i = 0; i< nCases; i++){
                  ranges[i] = Integer.parseInt(input.readLine());
                  if(ranges[i] > max_range){
                        max_range = ranges[i];
                  }
            }

            int range = max_range;
            
            //É criado um grafo não orientado pesado com os pontos
            Graph g = new Graph(hp);          
            for(int j = 0; j < hp; j++){
                  for(int k = j+1; k < hp && (arr[k].y <= (range + arr[j].y)); k++){
                        double d = dist(arr[j].x, arr[j].y , arr[k].x, arr[k].y);
                        if(d <= range /*&& arr[j].y != arr[k].y*/){  
                              g.addEdge(new Edge(j, d, arr[j].y) , new Edge(k, d, arr[k].y));
                        } 
                  } 
            } 

            for(int i = 0; i < nCases; i++){
                  range = ranges[i];

                  //Caso não seja necessário hold points para chegar ao topo
                  if(range >= height){
                        System.out.println(0);
                        continue;  
                  }

                  Hill theClimber = new Hill(g, height, range);
                  int n = -1;                                                             // => Número de pontos percorridos a partir de um determinado vértice
                  int min = Integer.MAX_VALUE;                                            // => Número minimo de pontos percorridos
                  boolean theWae = false;                                                 // => Se existe algum caminho
                  List<Integer> startingPoints = new ArrayList<Integer>();
                  boolean doBfs = true;

                  // Just one hold point
                  if((arr.length == 1) && (arr[0].y <= range) && ((arr[0].y + range) >= height)){
                        min = 1;
                        theWae = true;
                  }
                  else{
                        for(int j = 0; j < hp; j++){
                              if(arr[j].y <= range){
                                    if(arr[j].y + range >= height){
                                          min = 1;
                                          theWae = true;
                                          doBfs = false;
                                          break;
                                    }
                                    startingPoints.add(j);
                              }
                              else{break;}
                        }

                        // More than one hold point
                        if(doBfs){
                              n = theClimber.bfs( startingPoints );
                              if(n != -1){
                                    min = Math.min(min, n);
                                    theWae = true;
                              } 
                        }                                   
                  }

                  if(theWae){
                        System.out.println(min);
                  }
                  else{
                        System.out.println("unreachable");
                  }
            }
                      
            input.close();
      }           
}
 
//Representa um Ponto e as suas coordenadas
class Points implements Comparable<Points>{
      int x, y;
      public Points(int x, int y){
            this.x = x;
            this.y = y;
      }

      public int compareTo(Points p){
            if (this.y < p.y){
                  return -1;
            }
        
            if (this.y == p.y){
                  return 0;
            }
        
            return 1;
      }
}

//Representa a escalada
class Hill{
      Graph g;
      private int goal;                                           // => Objetivo do Hill
      private int range;                                          // => Capacidade de salto do Hill

      public static final int INFINITY = Integer.MAX_VALUE;
      public static final int NONE = -1;
      private static enum Colour { WHITE, GREY, BLACK };
      
      Hill(Graph g, int goal, int range){
            this.g = g; 
            this.goal = goal; 
            this.range = range;
      }


      public int bfs(List<Integer> startingPoints){
            Colour[] colour = new Colour[this.g.nodes];
            int[] d = new int[this.g.nodes];                      // => Distância de cada ponto à origem
            int[] heights = new int[this.g.nodes];
      
            for (int u = 0; u < this.g.nodes; u++){
                  colour[u] = Colour.WHITE;
                  d[u] = INFINITY;
            }
            
            for (int i : startingPoints) {
                  d[i] = 0;
                  colour[i] = Colour.GREY;
            }     

            Queue<Integer> Q = new LinkedList<>();
            for (Integer i : startingPoints) {
                  Q.add(i);
            }
            while (!Q.isEmpty()){
                  int u = Q.remove();
                  for (Edge v : this.g.adjacents[u]){
                        if (colour[v.dest()] == Colour.WHITE && (v.weight() <= this.range)){
                              colour[v.dest()] = Colour.GREY;
                              d[v.dest()] = d[u] + 1;
                              heights[v.dest()] = v.y();
                              Q.add(v.dest()); 

                              if(v.y() + this.range >= goal){
                                    return d[v.dest()] + 1;
                              }
                        }     
                  }
                  colour[u] = Colour.BLACK;
            }
            return -1;                                            // => Se não for encontrado nenhum ponto
      }
}

class Edge implements Comparable<Edge>{    
      private int dest;			// edge destination node
      private double weight;		// edge weight
      private int y;                // edge height
    
      public Edge(int dest, double weight, int y){
            this.dest = dest;
            this.weight = weight;
            this.y = y;
      }
    
      public int dest() {return dest;}
      public double weight() {return weight;}
      public int y() {return y;}

      public int compareTo(Edge anEdge){
            if (weight < anEdge.weight){
                  return -1;
            }

            if (weight == anEdge.weight){
                  return 0;
            }
      
            return 1;			
      }
}
                                      
class Graph {
      int nodes;
      List<Edge>[] adjacents; 
      @SuppressWarnings("unchecked")
      
      public Graph(int nodes){
          this.nodes = nodes;
          adjacents = new List[nodes];
          for (int i = 0; i < nodes; i++){
                adjacents[i] = new LinkedList<>();
            }
      }
      
      public void addEdge(Edge o, Edge v){
            adjacents[o.dest()].add(v);
            adjacents[v.dest()].add(o);
      }
}


